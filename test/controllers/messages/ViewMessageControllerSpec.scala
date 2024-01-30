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
import controllers.predicates.{FakeAuthAction, FakeDataRetrievalAction}
import fixtures.MessagesFixtures
import fixtures.messages.EN
import mocks.services.MockGetMessagesService
import models.messages.{MessageCache, MessagesSearchOptions}
import models.requests.DataRequest
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}
import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, redirectLocation, status}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.messages.ViewMessage

import java.time.Instant
import scala.concurrent.Future

class ViewMessageControllerSpec extends SpecBase with MessagesFixtures with FakeAuthAction with MockGetMessagesService {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(EN.lang))

  lazy val view = app.injector.instanceOf[ViewMessage]

  lazy val controller: ViewMessageController = new ViewMessageController(
    mcc = app.injector.instanceOf[MessagesControllerComponents],
    auth = FakeSuccessAuthAction,
    getData = new FakeDataRetrievalAction(testMinTraderKnownFacts, testMessageStatistics),
    getMessagesService = mockGetMessagesService,
    view = view
  )

  val testMessageId = 1234

  val testMessageFromCache = MessageCache(
    ern = testErn,
    message = message1.copy(uniqueMessageIdentifier = testMessageId),
    lastUpdated = Instant.now
  )

  "GET" when {
    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

    implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

    "service call to get message returns a Some(MessageCache)" should {
      "render the view" in {
        MockGetMessagesService
          .getMessage(testErn, testMessageId)
          .returns(Future.successful(Some(testMessageFromCache)))

        val result: Future[Result] = controller.onPageLoad(testErn, testMessageId)(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsString(result) shouldBe view(testMessageFromCache).toString()
      }
    }

    "service call to get message returns a None" should {
      "redirect back to the messages inbox" in {
        MockGetMessagesService
          .getMessage(testErn, testMessageId)
          .returns(Future.successful(None))

        val result: Future[Result] = controller.onPageLoad(testErn, testMessageId)(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions()).url)
      }
    }
  }

}
