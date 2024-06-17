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
import controllers.predicates.{BetaAllowListActionImpl, FakeAuthAction, FakeDataRetrievalAction}
import fixtures.messages.EN
import fixtures.{GetMovementResponseFixtures, GetSubmissionFailureMessageFixtures, MessagesFixtures}
import mocks.config.MockAppConfig
import mocks.connectors.MockBetaAllowListConnector
import mocks.services.{MockDeleteMessageService, MockDraftMovementService, MockGetMessagesService, MockGetMovementService}
import models.messages.{MessageCache, MessagesSearchOptions}
import models.requests.DataRequest
import models.response.emcsTfe.messages.DeleteMessageResponse
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}
import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, redirectLocation, status}
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier
import views.html.messages.ViewMessageView

import java.time.Instant
import scala.concurrent.Future

class ViewMessageControllerSpec extends SpecBase
  with MessagesFixtures
  with FakeAuthAction
  with MockGetMessagesService
  with MockGetMovementService
  with MockDeleteMessageService
  with MockDraftMovementService
  with GetSubmissionFailureMessageFixtures
  with GetMovementResponseFixtures
  with MockAppConfig
  with MockBetaAllowListConnector {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(EN.lang))

  lazy val view = app.injector.instanceOf[ViewMessageView]

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

  class Test(navHubEnabled: Boolean = true, messageInboxEnabled: Boolean = true) {

    lazy val betaAllowListAction = new BetaAllowListActionImpl(
      betaAllowListConnector = mockBetaAllowListConnector,
      errorHandler = errorHandler,
      config = mockAppConfig
    )

    lazy val controller: ViewMessageController = new ViewMessageController(
      mcc = app.injector.instanceOf[MessagesControllerComponents],
      auth = FakeSuccessAuthAction,
      getData = new FakeDataRetrievalAction(testMinTraderKnownFacts, Some(testMessageStatistics)),
      betaAllowList = betaAllowListAction,
      getMessagesService = mockGetMessagesService,
      getMovementService = mockGetMovementService,
      draftMovementService = mockDraftMovementService,
      deleteMessageService = mockDeleteMessagesService,
      view = view,
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

  "user is on the private beta list" when {
    "GET /trader/:ern/message/:uniqueMessageIdentifier/view" when {

      "service call to get message returns a Some(MessageCache)" should {
        "render the view" in new Test {
          MockGetMessagesService
            .getMessage(testErn, testMessageId)
            .returns(Future.successful(Some(testMessageFromCache)))

          val result: Future[Result] = controller.onPageLoad(testErn, testMessageId)(fakeRequest)

          status(result) shouldBe Status.OK
          contentAsString(result) shouldBe view(testMessageFromCache, None).toString()
        }
      }

      "service call to get message returns a Some(MessageCache) with an IE871 message type" should {
        "render the view" in new Test {

          val testMessageFromCacheWithIE871MessageType = MessageCache(
            ern = testErn,
            message = message1.copy(uniqueMessageIdentifier = testMessageId, messageType = "IE871"),
            errorMessage = Some(GetSubmissionFailureMessageResponseFixtures.getSubmissionFailureMessageResponseModel),
            lastUpdated = Instant.now
          )

          MockGetMessagesService
            .getMessage(testErn, testMessageId)
            .returns(Future.successful(Some(testMessageFromCacheWithIE871MessageType)))

          MockGetMovementService
            .getRawMovement(testErn, arc = "ARC1001", testMessageFromCacheWithIE871MessageType.message.sequenceNumber)
            .returns(Future.successful(getMovementResponseModel))

          val result: Future[Result] = controller.onPageLoad(testErn, testMessageId)(fakeRequest)

          status(result) shouldBe Status.OK
          contentAsString(result) shouldBe view(testMessageFromCacheWithIE871MessageType, None).toString()
        }
      }

      "service call to get message returns a None" should {
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
    "GET /trader/:ern/message/:uniqueMessageIdentifier/draft-movement" when {

      "the message ID relates to a movement submission in error" should {

        "call the draft movement service to delete the message and redirect to CaM to 'revive' the draft" in new Test {

          MockGetMessagesService
            .getMessage(testErn, testMessageId)
            .returns(Future.successful(Some(testMessageFromCache)))

          MockDeleteMessagesService
            .deleteMessage(testErn, testMessageId)
            .returns(Future.successful(DeleteMessageResponse(recordsAffected = 1)))

          MockDraftMovementService
            .putErrorMessagesAndMarkMovementAsDraft(testErn, GetSubmissionFailureMessageResponseFixtures.getSubmissionFailureMessageResponseModel)
            .returns(Future.successful(Some(testDraftId)))

          val result: Future[Result] = controller.removeMessageAndRedirectToDraftMovement(testErn, testMessageId)(fakeRequest)

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result).value shouldBe appConfig.emcsTfeCreateMovementTaskListUrl(testErn, testDraftId)
        }
      }

      "the message ID does not relate to a movement submission in error" should {

        "return a Not Found response" in new Test {

          MockGetMessagesService
            .getMessage(testErn, testMessageId)
            .returns(Future.successful(Some(testMessageFromCache.copy(errorMessage = None))))

          val result: Future[Result] = controller.removeMessageAndRedirectToDraftMovement(testErn, testMessageId)(fakeRequest)

          status(result) shouldBe Status.NOT_FOUND
        }
      }

      "no message ID can be found" should {

        "return a Not Found response" in new Test {

          MockGetMessagesService
            .getMessage(testErn, testMessageId)
            .returns(Future.successful(None))

          val result: Future[Result] = controller.removeMessageAndRedirectToDraftMovement(testErn, testMessageId)(fakeRequest)

          status(result) shouldBe Status.NOT_FOUND
        }
      }

      "no records were deleted" should {

        "still call the draft movement errors setup as message may have already been removed from core" in new Test {

          MockGetMessagesService
            .getMessage(testErn, testMessageId)
            .returns(Future.successful(Some(testMessageFromCache)))

          MockDeleteMessagesService
            .deleteMessage(testErn, testMessageId)
            .returns(Future.successful(DeleteMessageResponse(recordsAffected = 0)))

          MockDraftMovementService
            .putErrorMessagesAndMarkMovementAsDraft(testErn, GetSubmissionFailureMessageResponseFixtures.getSubmissionFailureMessageResponseModel)
            .returns(Future.successful(Some(testDraftId)))

          val result: Future[Result] = controller.removeMessageAndRedirectToDraftMovement(testErn, testMessageId)(fakeRequest)

          status(result) shouldBe Status.SEE_OTHER
          redirectLocation(result).value shouldBe appConfig.emcsTfeCreateMovementTaskListUrl(testErn, testDraftId)
        }
      }

      "one of the backend calls to 'revive' the movement fails" should {

        "return an ISE response" in new Test {

          MockGetMessagesService
            .getMessage(testErn, testMessageId)
            .returns(Future.successful(Some(testMessageFromCache)))

          MockDeleteMessagesService
            .deleteMessage(testErn, testMessageId)
            .returns(Future.successful(DeleteMessageResponse(recordsAffected = 1)))

          MockDraftMovementService
            .putErrorMessagesAndMarkMovementAsDraft(testErn, GetSubmissionFailureMessageResponseFixtures.getSubmissionFailureMessageResponseModel)
            .returns(Future.successful(None))

          val result: Future[Result] = controller.removeMessageAndRedirectToDraftMovement(testErn, testMessageId)(fakeRequest)

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }
    }
  }

  "user is NOT on the private beta list" when {
    "GET /trader/:ern/message/:uniqueMessageIdentifier/view" should {
      "redirect back to legacy" in new Test(messageInboxEnabled = false) {
        val result: Future[Result] = controller.onPageLoad(testErn, testMessageId)(fakeRequest)
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some("http://localhost:8080/emcs/trader/GBWKTestErn/messages")
      }
    }

    "GET /trader/:ern/message/:uniqueMessageIdentifier/draft-movement" should {
      "redirect back to legacy" in new Test(messageInboxEnabled = false) {
        val result: Future[Result] = controller.removeMessageAndRedirectToDraftMovement(testErn, testMessageId)(fakeRequest)
        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some("http://localhost:8080/emcs/trader/GBWKTestErn/messages")
      }
    }
  }

}
