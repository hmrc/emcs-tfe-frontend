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
import config.{ErrorHandler, SessionKeys}
import controllers.predicates.{FakeAuthAction, FakeDataRetrievalAction}
import fixtures.MessagesFixtures
import fixtures.messages.EN
import mocks.config.MockAppConfig
import mocks.services.MockGetMessagesService
import models.messages.{MessagesSearchOptions, MessagesSortingSelectOption}
import models.requests.DataRequest
import models.response.MessagesException
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}
import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, redirectLocation, status}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.messages.ViewAllMessagesView

import scala.concurrent.Future

class ViewAllMessagesControllerSpec
  extends SpecBase
    with MessagesFixtures with FakeAuthAction with MockGetMessagesService with MockAppConfig {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(EN.lang))

  lazy val view = app.injector.instanceOf[ViewAllMessagesView]

  trait Test {
    lazy val controller: ViewAllMessagesController = new ViewAllMessagesController(
      mcc = app.injector.instanceOf[MessagesControllerComponents],
      auth = FakeSuccessAuthAction,
      getData = new FakeDataRetrievalAction(Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
      getMessagesService = mockGetMessagesService,
      view = view,
      app.injector.instanceOf[ErrorHandler]
    )(ec)
  }


  "GET" when {
    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

    implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

    "service call to get messages is successful" should {

      "redirect to the index 1 when current index is below the minimum" in new Test {
        val searchOptions = MessagesSearchOptions(index = 0)

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions(index = 1)).url)
      }

      "show the correct view with an index of 1" in new Test {
        val numberOfMessages = 30

        val messagesResponse = constructMessageResponse(numberOfMessages)

        val search = MessagesSearchOptions(index = 1)

        MockGetMessagesService
          .getMessages(testErn, Some(search))
          .returns(Future.successful(messagesResponse))

        val result: Future[Result] = controller.onPageLoad(testErn, search)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsString(result) shouldBe view(
          sortSelectItems = MessagesSortingSelectOption.constructSelectItems(Some(search.sortBy.code)),
          allMessages = messagesResponse.messages,
          totalNumberOfPages = 3,
          searchOptions = search
        ).toString()
      }

      "show the correct view with an index of 2" in new Test {
        val numberOfMessages = 30

        val messagesResponse = constructMessageResponse(numberOfMessages)

        val search = MessagesSearchOptions(index = 2)

        MockGetMessagesService
          .getMessages(testErn, Some(search))
          .returns(Future.successful(messagesResponse))

        val result: Future[Result] = controller.onPageLoad(testErn, search)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsString(result) shouldBe view(
          sortSelectItems = MessagesSortingSelectOption.constructSelectItems(Some(search.sortBy.code)),
          allMessages = messagesResponse.messages,
          totalNumberOfPages = 3,
          searchOptions = search
        ).toString()
      }

      "show the correct view with an index of 3" in new Test {
        val numberOfMessages = 30

        val messagesResponse = constructMessageResponse(numberOfMessages)

        val search = MessagesSearchOptions(index = 3)

        MockGetMessagesService
          .getMessages(testErn, Some(search))
          .returns(Future.successful(messagesResponse))

        val result: Future[Result] = controller.onPageLoad(testErn, search)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsString(result) shouldBe view(
          sortSelectItems = MessagesSortingSelectOption.constructSelectItems(Some(search.sortBy.code)),
          allMessages = messagesResponse.messages,
          totalNumberOfPages = 3,
          searchOptions = search
        ).toString()
      }

      "show the correct view when message count is 1 above a multiple of the pageCount" in new Test {
        val numberOfMessages = 31

        val messagesResponse = constructMessageResponse(numberOfMessages)

        val search = MessagesSearchOptions(index = 4)

        MockGetMessagesService
          .getMessages(testErn, Some(search))
          .returns(Future.successful(messagesResponse))

        val result: Future[Result] = controller.onPageLoad(testErn, search)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsString(result) shouldBe view(
          sortSelectItems = MessagesSortingSelectOption.constructSelectItems(Some(search.sortBy.code)),
          allMessages = messagesResponse.messages,
          totalNumberOfPages = 4,
          searchOptions = search
        ).toString()
      }

      "show the correct view when message count is 1 below a multiple of the pageCount" in new Test {
        val numberOfMessages = 29

        val messagesResponse = constructMessageResponse(numberOfMessages)

        val search = MessagesSearchOptions(index = 3)

        MockGetMessagesService
          .getMessages(testErn, Some(search))
          .returns(Future.successful(messagesResponse))

        val result: Future[Result] = controller.onPageLoad(testErn, search)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsString(result) shouldBe view(
          sortSelectItems = MessagesSortingSelectOption.constructSelectItems(Some(search.sortBy.code)),
          allMessages = messagesResponse.messages,
          totalNumberOfPages = 3,
          searchOptions = search
        ).toString()
      }

      "show the correct view when there are no messages available" in new Test {
        val numberOfMessages = 0

        val messagesResponse = constructMessageResponse(numberOfMessages)

        val search = MessagesSearchOptions()

        MockGetMessagesService
          .getMessages(testErn, Some(search))
          .returns(Future.successful(messagesResponse))

        val result: Future[Result] = controller.onPageLoad(testErn, search)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsString(result) shouldBe view(
          sortSelectItems = MessagesSortingSelectOption.constructSelectItems(Some(search.sortBy.code)),
          allMessages = messagesResponse.messages,
          totalNumberOfPages = 1,
          searchOptions = search
        ).toString()
      }

      "redirect to the index 1 when current index is above the maximum" in new Test {
        val numberOfMessages = 30

        val messagesResponse = constructMessageResponse(numberOfMessages)

        val search = MessagesSearchOptions(index = 4)

        MockGetMessagesService
          .getMessages(testErn, Some(search))
          .returns(Future.successful(messagesResponse))

        val result: Future[Result] = controller.onPageLoad(testErn, search)(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions(index = 1)).url)
      }

      "show deleted message success banner, if the DELETED_MESSAGE_DESCRIPTION_KEY session variable is set" in new Test {
        val deletedMessageDescriptionKey = "messages.IE837.true.1.description"

        implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] =
          FakeRequest("GET", "/")
            .withFlash(SessionKeys.DELETED_MESSAGE_DESCRIPTION_KEY -> deletedMessageDescriptionKey)

        val messagesResponse = constructMessageResponse(numberOfMessages = 0)
        val search = MessagesSearchOptions()

        MockGetMessagesService
          .getMessages(testErn, Some(search))
          .returns(Future.successful(messagesResponse))

        val result: Future[Result] = controller.onPageLoad(testErn, search)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsString(result) shouldBe view(
          sortSelectItems = MessagesSortingSelectOption.constructSelectItems(Some(search.sortBy.code)),
          allMessages = messagesResponse.messages,
          totalNumberOfPages = 1,
          searchOptions = search,
          maybeDeletedMessageDescriptionKey = Some(deletedMessageDescriptionKey)
        ).toString()
      }
    }

    "service call to get messages is un-successful" should {
      "return 500 response" in new Test {
        implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

        val searchOptions = MessagesSearchOptions()

        MockGetMessagesService
          .getMessages(testErn, Some(searchOptions))
          .returns(Future.failed(MessagesException("bang")))

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}
