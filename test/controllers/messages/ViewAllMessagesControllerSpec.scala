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
import config.ErrorHandler
import controllers.predicates.{FakeAuthAction, FakeDataRetrievalAction}
import fixtures.MessagesFixtures
import mocks.services.MockGetMessagesService
import models.messages.MessagesSearchOptions
import models.response.MessagesException
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.status
import uk.gov.hmrc.http.HeaderCarrier
import views.html.messages.ViewAllMessages

import scala.concurrent.Future

class ViewAllMessagesControllerSpec extends SpecBase with MessagesFixtures with FakeAuthAction with MockGetMessagesService {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  lazy val view = app.injector.instanceOf[ViewAllMessages]

  lazy val controller: ViewAllMessagesController = new ViewAllMessagesController(
    mcc = app.injector.instanceOf[MessagesControllerComponents],
    auth = FakeSuccessAuthAction,
    getData = new FakeDataRetrievalAction(testMinTraderKnownFacts, testMessageStatistics),
    getMessagesService = mockGetMessagesService,
    view = view,
    app.injector.instanceOf[ErrorHandler]
  )

  "GET" when {

    "service call to get messages is successful" should {
      "show the correct view" in {
        implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

        val searchOptions = MessagesSearchOptions()

        MockGetMessagesService
          .getMessages(testErn, Some(searchOptions))
          .returns(Future.successful(getMessageResponse))

        val result: Future[Result] = controller.onPageLoad(testErn, searchOptions)(fakeRequest)

        status(result) shouldBe Status.OK
      }
    }

    "service call to get messages is un-successful" should {
      "return 500 response" in {
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
