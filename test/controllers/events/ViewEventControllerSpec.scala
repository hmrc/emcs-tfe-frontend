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
import mocks.services.{MockGetCnCodeInformationService, MockGetDocumentTypesService, MockGetMovementHistoryEventsService, MockGetMovementService}
import models.{DocumentType, EventTypes}
import models.common.AcceptMovement
import models.common.UnitOfMeasure.Kilograms
import models.requests.DataRequest
import models.response.emcsTfe.GetMovementResponse
import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import models.response.referenceData.CnCodeInformation
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc._
import play.api.test.FakeRequest
import play.api.test.Helpers.status
import uk.gov.hmrc.http.HeaderCarrier
import utils.DateUtils
import views.html.events.HistoryEventView

import scala.concurrent.Future

// scalastyle:off magic.number
class ViewEventControllerSpec
  extends SpecBase
    with MessagesFixtures
    with FakeAuthAction
    with MockAppConfig
    with MockGetMovementHistoryEventsService
    with MockGetCnCodeInformationService
    with MockGetMovementService
    with GetMovementResponseFixtures
    with GetMovementHistoryEventsResponseFixtures
    with DateUtils
    with MockGetDocumentTypesService {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(EN.lang))

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

  lazy val view = app.injector.instanceOf[HistoryEventView]

  lazy val controller: ViewEventController = new ViewEventController(
    mcc = app.injector.instanceOf[MessagesControllerComponents],
    auth = FakeSuccessAuthAction,
    getData = new FakeDataRetrievalAction(Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
    betaAllowList = new FakeBetaAllowListAction,
    getMovementHistoryEventsService = mockGetMovementHistoryEventsService,
    getMovementService = mockGetMovementService,
    getCnCodeInformationService = mockGetCnCodeInformationService,
    view = view,
    errorHandler = errorHandler,
    getDocumentTypesService = mockGetDocumentTypesService
  )(ec, appConfig)


  private def renderASuccessfulEventView(
                                          event: MovementHistoryEvent,
                                          movementResponseModel: GetMovementResponse,
                                          controllerMethod: => Future[Result],
                                          optMock: () => Unit = () => ()
                                        ): Unit = {
    "render a view" in {
      val testHistoryEvents = getMovementHistoryEventsModel.filterNot(_.eventType == event.eventType) :+ event
      val testMovement = movementResponseModel.copy(eventHistorySummary = Some(testHistoryEvents))

      MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc).returns(Future.successful(testHistoryEvents))
      MockGetMovementService.getMovement(testErn, testArc, Some(event.sequenceNumber)).returns(Future.successful(testMovement))
      MockGetDocumentTypesService.getDocumentTypes().returns(Future.successful(Seq(DocumentType("1","Document type description"), DocumentType("2", "Document type description 2"))))
      optMock()

      val result: Future[Result] = controllerMethod

      status(result) shouldBe Status.OK
    }
  }

  private def handle404s(event: MovementHistoryEvent, controllerMethod: => Future[Result]): Unit = {
    "return a 404 not found" when {
      "when the wrong event id is requested" in {
        val testHistoryEvents = getMovementHistoryEventsModel.filterNot(_.eventType == event.eventType) :+ event.copy(eventDate = parseDateTime("1999-12-31T23:59:59"))

        MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc).returns(Future.successful(testHistoryEvents))

        val result: Future[Result] = controllerMethod

        status(result) shouldBe Status.NOT_FOUND
      }
      s"when the matching event is not an ${simpleName(event.eventType)} event" in {
        val otherEventTypes: Seq[EventTypes] = EventTypes.values.filterNot(_ == event.eventType)
        val testHistoryEvents = getMovementHistoryEventsModel.filterNot(_.eventType == event.eventType) :+ event.copy(eventType = otherEventTypes.head)

        MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc).returns(Future.successful(testHistoryEvents))

        val result: Future[Result] = controllerMethod

        status(result) shouldBe Status.NOT_FOUND
      }
      "when there are no history events for the movement" in {
        val testHistoryEvents = Seq[MovementHistoryEvent]().empty

        MockGetMovementHistoryEventsService.getMovementHistoryEvents(testErn, testArc).returns(Future.successful(testHistoryEvents))

        val result: Future[Result] = controllerMethod

        status(result) shouldBe Status.NOT_FOUND
      }
    }
  }

  case class TestFixtureModel(methodName: String, event: MovementHistoryEvent, method: Int => Action[AnyContent])

  Seq[TestFixtureModel](
    TestFixtureModel(
      methodName = ".movementCreated",
      event = ie801Event,
      method = (id: Int) => controller.movementCreated(testErn, testArc, id)
    ),
    TestFixtureModel(
      methodName = ".movementUpdated",
      event = ie801Event.copy(sequenceNumber = 2, isFirstEventTypeInHistory = false),
      method = (id: Int) => controller.movementUpdated(testErn, testArc, id)
    ),
    TestFixtureModel(
      methodName = ".changeDestinationDue",
      event = ie802ChangeDestinationEvent,
      method = (id: Int) => controller.changeDestinationDue(testErn, testArc, id)
    ),
    TestFixtureModel(
      methodName = ".reportReceiptDue",
      event = ie802EventReportOfReceipt,
      method = (id: Int) => controller.reportReceiptDue(testErn, testArc, id)
    ),
    TestFixtureModel(
      methodName = ".movementDestinationDue",
      event = ie802MovementDestinationEvent,
      method = (id: Int) => controller.movementDestinationDue(testErn, testArc, id)
    ),
    TestFixtureModel(
      methodName = ".movementSplit",
      event = ie803MovementSplitEvent,
      method = (id: Int) => controller.movementSplit(testErn, testArc, id)
    ),
    TestFixtureModel(
      methodName = ".movementDiverted",
      event = ie803MovementDiversionEvent,
      method = (id: Int) => controller.movementDiverted(testErn, testArc, id)
    ),
    TestFixtureModel(
      methodName = ".alertRejectionSubmitted",
      event = ie819AlertEvent,
      method = (id: Int) => controller.alertRejectionSubmitted(testErn, testArc, id)
    ),
    TestFixtureModel(
      methodName = ".movementAcceptedCustoms",
      event = ie829MovementAcceptedCustomsEvent,
      method = (id: Int) => controller.movementAcceptedCustoms(testErn, testArc, id)
    ),
    TestFixtureModel(
      methodName = ".movementRejectedCustoms",
      event = ie839MovementRejectedCustomsEvent,
      method = (id: Int) => controller.movementRejectedCustoms(testErn, testArc, id)
    ),
    TestFixtureModel(
      methodName = ".manualClosureOfMovement",
      event = ie905ManualClosureResponseEvent,
      method = (id: Int) => controller.manualClosureOfMovement(testErn, testArc, id)
    ),
    TestFixtureModel(
      methodName = ".movementCancelled",
      event = ie810MovementCancelledEvent,
      method = (id: Int) => controller.movementCancelled(testErn, testArc, id)
    ),
    TestFixtureModel(
      methodName = ".changeDestinationSubmitted",
      event = ie813ChangeDestinationEvent,
      method = (id: Int) => controller.changeDestinationSubmitted(testErn, testArc, id)
    ),
    TestFixtureModel(
      methodName = ".movementIntercepted (interrupted)",
      event = ie807MovementInterruptedEvent,
      method = (id: Int) => controller.movementIntercepted(testErn, testArc, id)
    ),
    TestFixtureModel(
      methodName = ".shortageExcessSubmitted",
      event = ie871ShortageOrEccessEvent,
      method = (id: Int) => controller.shortageExcessSubmitted(testErn, testArc, id)
    ),
  ).foreach { case TestFixtureModel(methodName, event, method) =>

    s"calling $methodName" must {
      renderASuccessfulEventView(event, getMovementResponseModel, method(event.eventId)(fakeRequest))
      handle404s(event, method(event.eventId)(fakeRequest))
    }
  }

  ".reportReceiptSubmitted" when {
    "acceptMovement is Satisfactory" must {
      val model = getMovementResponseModel.copy(
        reportOfReceipt = Some(reportOfReceiptResponse.copy(acceptMovement = AcceptMovement.Satisfactory))
      )
      renderASuccessfulEventView(ie818Event, model, controller.reportReceiptSubmitted(testErn, testArc, 853932155)(fakeRequest))
      handle404s(ie818Event, controller.reportReceiptSubmitted(testErn, testArc, 853932155)(fakeRequest))
    }
    AcceptMovement.values.filterNot(_ == AcceptMovement.Satisfactory).foreach { acceptMovement =>
      s"acceptMovement is $acceptMovement" must {
        val model = getMovementResponseModel.copy(
          reportOfReceipt = Some(reportOfReceiptResponse.copy(acceptMovement = acceptMovement))
        )
        renderASuccessfulEventView(
          ie818Event,
          model,
          controller.reportReceiptSubmitted(testErn, testArc, 853932155)(fakeRequest),
          () => MockGetCnCodeInformationService.getCnCodeInformation(getMovementResponseModel.items).returns(Future.successful(Seq(
            item1WithWineAndPackaging -> CnCodeInformation(
              cnCode = "T400",
              cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
              exciseProductCode = "24029000",
              exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
              unitOfMeasure = Kilograms
            ),
            item2WithWineAndPackaging -> CnCodeInformation(
              cnCode = "T400",
              cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
              exciseProductCode = "24029000",
              exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
              unitOfMeasure = Kilograms
            )
          )))
        )
        handle404s(ie818Event, controller.reportReceiptSubmitted(testErn, testArc, 853932155)(fakeRequest))
      }
    }
  }

  ".manualClosureResponse" when {
    "acceptMovement is Satisfactory" must {
      renderASuccessfulEventView(ie881ManualClosureResponseEvent, getMovementResponseModel, controller.manualClosureResponse(testErn, testArc, 853932155)(fakeRequest),
        () => MockGetCnCodeInformationService.getCnCodeInformation(getMovementResponseModel.items).returns(Future.successful(Seq(
          item1WithWineAndPackaging -> CnCodeInformation(
            cnCode = "T400",
            cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
            exciseProductCode = "24029000",
            exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
            unitOfMeasure = Kilograms
          )
        )))
      )
      handle404s(ie881ManualClosureResponseEvent, controller.manualClosureResponse(testErn, testArc, 853932155)(fakeRequest))
    }
  }
}
