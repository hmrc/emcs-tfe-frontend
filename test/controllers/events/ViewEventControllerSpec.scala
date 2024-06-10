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
import fixtures.{GetMovementHistoryEventsResponseFixtures, GetMovementResponseFixtures, MessagesFixtures}
import mocks.config.MockAppConfig
import mocks.services.{MockGetMovementHistoryEventsService, MockGetMovementService}
import models.EventTypes
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
  with GetMovementResponseFixtures
  with GetMovementHistoryEventsResponseFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(EN.lang))

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

  lazy val view = app.injector.instanceOf[HistoryEventView]

  lazy val controller: ViewEventController = new ViewEventController(
    mcc = app.injector.instanceOf[MessagesControllerComponents],
    auth = FakeSuccessAuthAction,
    getData = new FakeDataRetrievalAction(testMinTraderKnownFacts, Some(testMessageStatistics)),
    betaAllowList = new FakeBetaAllowListAction,
    getMovementHistoryEventsService = mockGetMovementHistoryEventsService,
    getMovementService = mockGetMovementService,
    view = view,
    errorHandler = errorHandler
  )(ec, appConfig)


  private def renderASuccessfulEventView(event: MovementHistoryEvent, controllerMethod: () => Future[Result]): Unit = {
    "render a view" in {
      val testHistoryEvents = getMovementHistoryEventsModel :+ event
      val testMovement = getMovementResponseModel.copy(eventHistorySummary = Some(testHistoryEvents))

      MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc).returns(Future.successful(testHistoryEvents))
      MockGetMovementService.getMovement(testErn, testArc, Some(event.sequenceNumber)).returns(Future.successful(testMovement))

      val result: Future[Result] = controllerMethod()

      status(result) shouldBe Status.OK
    }
  }

  private def handle404s(event: MovementHistoryEvent, controllerMethod: () => Future[Result]): Unit = {
    "return a 404 not found" when {
      "when the wrong event id is requested" in {
        val testHistoryEvents = getMovementHistoryEventsModel :+ event.copy(eventDate = "1999-12-31T23:59:59")

        MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc).returns(Future.successful(testHistoryEvents))

        val result: Future[Result] = controllerMethod()

        status(result) shouldBe Status.NOT_FOUND
      }
      s"when the matching event is not an ${simpleName(event.eventType)} event" in {
        val otherEventTypes: Seq[EventTypes] = EventTypes.values.filterNot(_ == event.eventType)
        val testHistoryEvents = getMovementHistoryEventsModel :+ event.copy(eventType = otherEventTypes.head)

        MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc).returns(Future.successful(testHistoryEvents))

        val result: Future[Result] = controllerMethod()

        status(result) shouldBe Status.NOT_FOUND
      }
      "when there are no history events for the movement" in {
        val testHistoryEvents = Seq[MovementHistoryEvent]().empty

        MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc).returns(Future.successful(testHistoryEvents))

        val result: Future[Result] = controllerMethod()

        status(result) shouldBe Status.NOT_FOUND
      }
    }
  }

  ".movementCreated" must {
    renderASuccessfulEventView(ie801Event, () => controller.movementCreated(testErn, testArc, 853932155)(fakeRequest))
    handle404s(ie801Event, () => controller.movementCreated(testErn, testArc, 853932155)(fakeRequest))
  }

  ".movementUpdated" must {
    val testEvent = ie801Event.copy(sequenceNumber = 2, isFirstEventTypeInHistory = false)

    renderASuccessfulEventView(testEvent, () => controller.movementUpdated(testErn, testArc, 853932155)(fakeRequest))
    handle404s(testEvent, () => controller.movementUpdated(testErn, testArc, 853932155)(fakeRequest))
  }

  ".changeDestinationDue" must {
    renderASuccessfulEventView(ie802ChangeDestinationEvent, () => controller.changeDestinationDue(testErn, testArc, 853932155)(fakeRequest))
    handle404s(ie802ChangeDestinationEvent, () => controller.changeDestinationDue(testErn, testArc, 853932155)(fakeRequest))
  }

  ".reportReceiptDue" must {
    renderASuccessfulEventView(ie802EventReportOfReceipt, () => controller.reportReceiptDue(testErn, testArc, 853932155)(fakeRequest))
    handle404s(ie802EventReportOfReceipt, () => controller.reportReceiptDue(testErn, testArc, 853932155)(fakeRequest))
  }

  ".movementDestinationDue" must {
    renderASuccessfulEventView(ie802MovementDestinationEvent, () => controller.movementDestinationDue(testErn, testArc, 853932155)(fakeRequest))
    handle404s(ie802MovementDestinationEvent, () => controller.movementDestinationDue(testErn, testArc, 853932155)(fakeRequest))
  }

}
