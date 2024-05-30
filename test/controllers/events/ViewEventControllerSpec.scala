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

package controllers.events

import base.SpecBase
import controllers.predicates.{FakeAuthAction, FakeBetaAllowListAction, FakeDataRetrievalAction}
import fixtures.messages.EN
import fixtures.{GetMovementResponseFixtures, MessagesFixtures}
import mocks.config.MockAppConfig
import mocks.services.{MockGetMovementHistoryEventsService, MockGetMovementService}
import models.EventTypes.{IE801, IE819}
import models.requests.DataRequest
import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.status
import uk.gov.hmrc.http.HeaderCarrier
import views.html.events.HistoryEventView

import scala.concurrent.Future

class ViewEventControllerSpec
  extends SpecBase
  with MessagesFixtures
  with FakeAuthAction
  with MockAppConfig
  with MockGetMovementHistoryEventsService
  with MockGetMovementService
  with GetMovementResponseFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(EN.lang))

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

  lazy val view = app.injector.instanceOf[HistoryEventView]

  class Test {

    lazy val controller: ViewEventController = new ViewEventController(
      mcc = app.injector.instanceOf[MessagesControllerComponents],
      auth = FakeSuccessAuthAction,
      getData = new FakeDataRetrievalAction(testMinTraderKnownFacts, testMessageStatistics),
      betaAllowList = new FakeBetaAllowListAction,
      getMovementHistoryEventsService = mockGetMovementHistoryEventsService,
      getMovementService = mockGetMovementService,
      view = view,
      errorHandler = errorHandler
    )(ec, appConfig)
  }

  ".movementCreated" must {

      val ie801Event = MovementHistoryEvent(
        eventType = IE801,
        eventDate = "2024-12-04T17:00:00", // hash code then bit shifted right = 853932155
        sequenceNumber = 1,
        messageRole = 0,
        upstreamArc = None
      )

      "render a view" in new Test {
        val testHistoryEvents = getMovementHistoryEventsModel :+ ie801Event
        val testMovement = getMovementResponseModel.copy(eventHistorySummary = Some(testHistoryEvents))

        MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc).returns(Future.successful(testHistoryEvents))
        MockGetMovementService.getMovement(testErn, testArc, Some(1)).returns(Future.successful(testMovement))

        val result: Future[Result] = controller.movementCreated(testErn, testArc, 853932155)(fakeRequest)

        status(result) shouldBe Status.OK
      }

      "return a 404 not found" when {
        "when the wrong event id is requested" in new Test {
          val testHistoryEvents = getMovementHistoryEventsModel :+ ie801Event

          MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc).returns(Future.successful(testHistoryEvents))

          val result: Future[Result] = controller.movementCreated(testErn, testArc, 1234567)(fakeRequest)

          status(result) shouldBe Status.NOT_FOUND
        }
        "when the matching event is not an IE801 event" in new Test {
          val testHistoryEvents = getMovementHistoryEventsModel :+ ie801Event.copy(eventType = IE819)

          MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc).returns(Future.successful(testHistoryEvents))

          val result: Future[Result] = controller.movementCreated(testErn, testArc, 1234567)(fakeRequest)

          status(result) shouldBe Status.NOT_FOUND
        }
        "when there are no history events for the movement" in new Test {
          val testHistoryEvents = Seq[MovementHistoryEvent]().empty

          MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc).returns(Future.successful(testHistoryEvents))

          val result: Future[Result] = controller.movementCreated(testErn, testArc, 1234567)(fakeRequest)

          status(result) shouldBe Status.NOT_FOUND
        }
      }

  }

}