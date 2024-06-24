/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers.messages

import base.SpecBase
import config.SessionKeys
import controllers.predicates.{BetaAllowListActionImpl, FakeAuthAction, FakeDataRetrievalAction}
import fixtures.messages.EN
import fixtures.{GetSubmissionFailureMessageFixtures, MessagesFixtures}
import forms.DeleteMessageFormProvider
import mocks.config.MockAppConfig
import mocks.connectors.MockBetaAllowListConnector
import mocks.services.{MockDeleteMessageService, MockGetMessagesService, MockGetMovementService}
import models.messages.{MessageCache, MessagesSearchOptions}
import models.requests.DataRequest
import models.response.emcsTfe.messages.DeleteMessageResponse
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}
import pages.{ViewAllMessagesPage, ViewMessagePage}
import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, redirectLocation, status}
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.helpers.messages.{DeleteMessageHelper, MessagesHelper}
import views.html.messages.DeleteMessageView

import java.time.Instant
import scala.concurrent.Future

class DeleteMessageControllerSpec extends SpecBase
  with MessagesFixtures
  with FakeAuthAction
  with MockGetMessagesService
  with MockGetMovementService
  with MockDeleteMessageService
  with GetSubmissionFailureMessageFixtures
  with MockAppConfig
  with MockBetaAllowListConnector {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(EN.lang))

  lazy val formProvider = new DeleteMessageFormProvider

  lazy val view = app.injector.instanceOf[DeleteMessageView]

  lazy val messagesHelper = new MessagesHelper()

  class Test(navHubEnabled: Boolean = true, messageInboxEnabled: Boolean = true) {
    lazy val betaAllowListAction = new BetaAllowListActionImpl(
      betaAllowListConnector = mockBetaAllowListConnector,
      errorHandler = errorHandler,
      config = mockAppConfig
    )

    lazy val controller: DeleteMessageController = new DeleteMessageController(
      mcc = app.injector.instanceOf[MessagesControllerComponents],
      auth = FakeSuccessAuthAction,
      getData = new FakeDataRetrievalAction(testMinTraderKnownFacts, Some(testMessageStatistics)),
      betaAllowList = betaAllowListAction,
      getMessagesService = mockGetMessagesService,
      deleteMessageService = mockDeleteMessagesService,
      formProvider = formProvider,
      view = view,
      deleteMessageHelper = new DeleteMessageHelper(messagesHelper),
      messagesHelper = messagesHelper,
      errorHandler = errorHandler
    )(ec, appConfig)

    MockedAppConfig.betaAllowListCheckingEnabled.repeat(2).returns(true)
    MockBetaAllowListConnector.check(testErn, "tfeNavHub").returns(Future.successful(Right(navHubEnabled)))
    MockBetaAllowListConnector.check(testErn, "tfeMessageInbox").returns(Future.successful(Right(messageInboxEnabled)))
  }

  val testMessageId = 1234

  val testMessageFromCache = MessageCache(
    ern = testErn,
    message = message1.copy(uniqueMessageIdentifier = testMessageId),
    errorMessage = Some(GetSubmissionFailureMessageResponseFixtures.getSubmissionFailureMessageResponseModel),
    lastUpdated = Instant.now
  )

  "GET" when {
    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
      FakeRequest("GET", "/")
        .withSession(SessionKeys.FROM_PAGE -> ViewMessagePage.toString)

    implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

    "user is on the private beta list" should {

      "MockGetMessagesService returns a message" should {
        "render the view" in new Test {
          MockGetMessagesService
            .getMessage(testErn, testMessageId)
            .returns(Future.successful(Some(testMessageFromCache)))

          val result: Future[Result] = controller.onPageLoad(testErn, testMessageId)(fakeRequest)

          status(result) shouldBe Status.OK
          contentAsString(result) shouldBe
            view(
              testMessageFromCache.message,
              formProvider(),
              routes.ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions()).url,
              ViewMessagePage
            ).toString()
        }
      }

      "MockGetMessagesService does not return a message" should {
        "redirect back to the messages inbox" in new Test {
          MockGetMessagesService
            .getMessage(testErn, testMessageId)
            .returns(Future.successful(None))

          val result: Future[Result] = controller.onPageLoad(testErn, testMessageId)(fakeRequest)

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result) shouldBe Some(routes.ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions()).url)
        }
      }

    }

    "user is NOT on the private beta list" should {
      "redirect back to legacy"  in new Test(messageInboxEnabled = false) {
        val result: Future[Result] = controller.onPageLoad(testErn, testMessageId)(fakeRequest)
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some("http://localhost:8080/emcs/trader/GBWKTestErn/messages")
      }
    }

  }

  "POST" when {
    "user is on the private beta list" should {

      "the user does not select a form option" in new Test {
        val formKeyValue = "value" -> ""

        val fakeRequest =
          FakeRequest("POST", "/")
            .withFormUrlEncodedBody(formKeyValue)
            .withSession(SessionKeys.FROM_PAGE -> ViewAllMessagesPage.toString)

        implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

        MockGetMessagesService
          .getMessage(testErn, testMessageId)
          .returns(Future.successful(Some(testMessageFromCache)))

        val boundForm = formProvider().bind(Map(formKeyValue))

        val result: Future[Result] = controller.onSubmit(testErn, testMessageId)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsString(result) shouldBe
          view(
            testMessageFromCache.message,
            boundForm,
            routes.ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions()).url,
            ViewAllMessagesPage
          ).toString()
      }

      "the user has arrived from the ViewAllMessagesPage, and selects 'No, return to all messages'" in new Test {
        val fakeRequest =
          FakeRequest("POST", "/")
            .withFormUrlEncodedBody(data = "value" -> "false")
            .withSession(SessionKeys.FROM_PAGE -> ViewAllMessagesPage.toString)

        val result: Future[Result] = controller.onSubmit(testErn, testMessageId)(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions()).url)
      }

      "the user has arrived from the ViewMessagesPage, and selects 'No, return to message'" in new Test {
        val fakeRequest =
          FakeRequest("POST", "/")
            .withFormUrlEncodedBody(data = "value" -> "false")
            .withSession(SessionKeys.FROM_PAGE -> ViewMessagePage.toString)

        val result: Future[Result] = controller.onSubmit(testErn, testMessageId)(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ViewMessageController.onPageLoad(testErn, testMessageId).url)
      }

      "the user selects 'Yes, delete this message', and the message is deleted" in new Test {
        MockGetMessagesService
          .getMessage(testErn, testMessageId)
          .returns(Future.successful(Some(testMessageFromCache)))

        MockDeleteMessagesService
          .deleteMessage(testErn, testMessageId)
          .returns(Future.successful(DeleteMessageResponse(recordsAffected = 1)))

        val fakeRequest =
          FakeRequest("POST", "/")
            .withFormUrlEncodedBody(data = "value" -> "true")
            .withSession(SessionKeys.FROM_PAGE -> ViewMessagePage.toString)

        val result: Future[Result] = controller.onSubmit(testErn, testMessageId)(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions()).url)
      }

      "the user selects 'Yes, delete this message', and the message is not deleted (still redirect as desired outcome is already achieved)" in new Test {
        MockGetMessagesService
          .getMessage(testErn, testMessageId)
          .returns(Future.successful(Some(testMessageFromCache)))

        MockDeleteMessagesService
          .deleteMessage(testErn, testMessageId)
          .returns(Future.successful(DeleteMessageResponse(recordsAffected = 0)))

        val fakeRequest =
          FakeRequest("POST", "/")
            .withFormUrlEncodedBody(data = "value" -> "true")
            .withSession(SessionKeys.FROM_PAGE -> ViewMessagePage.toString)

        val result: Future[Result] = controller.onSubmit(testErn, testMessageId)(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions()).url)
      }

      "the user selects 'Yes, delete this message', and the message is not in the cache anymore. Then redirect back to where they came from." in new Test {
        MockGetMessagesService
          .getMessage(testErn, testMessageId)
          .returns(Future.successful(None))

        val fakeRequest =
          FakeRequest("POST", "/")
            .withFormUrlEncodedBody(data = "value" -> "true")
            .withSession(SessionKeys.FROM_PAGE -> ViewMessagePage.toString)

        val result: Future[Result] = controller.onSubmit(testErn, testMessageId)(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ViewMessageController.onPageLoad(testErn, 1234).url)
      }

    }

    "user is NOT on the private beta list" should {
      "redirect back to legacy" in new Test(messageInboxEnabled = false) {

        val fakeRequest =
          FakeRequest("POST", "/")
            .withFormUrlEncodedBody(data = "value" -> "true")
            .withSession(SessionKeys.FROM_PAGE -> ViewMessagePage.toString)

        val result: Future[Result] = controller.onSubmit(testErn, testMessageId)(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some("http://localhost:8080/emcs/trader/GBWKTestErn/messages")
      }
    }
  }

}
