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

import config.ErrorHandler
import controllers.predicates.{AuthAction, AuthActionHelper, DataRetrievalAction}
import models.EventTypes
import models.EventTypes._
import models.common.AcceptMovement
import models.requests.DataRequest
import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import models.response.emcsTfe.reportOfReceipt.IE818ItemModelWithCnCodeInformation
import models.response.emcsTfe.{GetMovementResponse, IE881ItemModelWithCnCodeInformation}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{GetCnCodeInformationService, GetDocumentTypesService, GetMovementHistoryEventsService, GetMovementService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Logging
import views.html.events.HistoryEventView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ViewEventController @Inject()(mcc: MessagesControllerComponents,
                                    val auth: AuthAction,
                                    val getData: DataRetrievalAction,
                                    getMovementHistoryEventsService: GetMovementHistoryEventsService,
                                    getMovementService: GetMovementService,
                                    getCnCodeInformationService: GetCnCodeInformationService,
                                    getDocumentTypesService: GetDocumentTypesService,
                                    view: HistoryEventView,
                                    errorHandler: ErrorHandler
                                   )(implicit val executionContext: ExecutionContext)
  extends FrontendController(mcc) with AuthActionHelper with I18nSupport with Logging {

  def movementCreated(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE801)

  def movementUpdated(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE801)

  def changeDestinationDue(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE802)

  def reportReceiptDue(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE802)

  def movementDestinationDue(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE802)

  def movementSplit(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE803)

  def movementDiverted(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE803)

  def movementCancelled(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE810)

  def reportReceiptSubmitted(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE818)

  def alertRejectionSubmitted(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE819)

  def movementAcceptedCustoms(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE829)

  def movementRejectedCustoms(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE839)

  def manualClosureOfMovement(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE905)

  def explanationDelaySubmitted(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE837)

  def changeDestinationSubmitted(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE813)

  def movementIntercepted(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE807)

  def shortageExcessSubmitted(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE871)

  def manualClosureResponse(ern: String, arc: String, eventId: Int): Action[AnyContent] =
    onPageLoad(ern, arc, eventId, IE881)

  private def onPageLoad(ern: String, arc: String, eventId: Int, eventType: EventTypes): Action[AnyContent] = {
    authorisedDataRequestAsync(ern) { implicit request =>
      withHistoryEvent(ern, arc, eventType, eventId) { event =>
        getDocumentTypesService.getDocumentTypes().flatMap { documentType =>
          getMovementService.getMovement(ern = ern, arc = arc, sequenceNumber = Some(event.sequenceNumber)).flatMap {
            movement =>
              withIE818ItemModelWithCnCodeInformation(eventType, movement) {
                ie818ItemModelWithCnCodeInformation =>
                  withIE881ItemModelWithCnCodeInformation(eventType, movement) {
                    ie881ItemModelWithCnCodeInformation =>
                      Ok(view(event, movement, ie818ItemModelWithCnCodeInformation, documentType, ie881ItemModelWithCnCodeInformation))
                  }
              }
          }
        }
      }
    }
  }
  private def withIE818ItemModelWithCnCodeInformation(eventType: EventTypes, movement: GetMovementResponse)
                                                     (f: Seq[IE818ItemModelWithCnCodeInformation] => Future[Result])
                                                     (implicit request: DataRequest[_]): Future[Result] = {
    val ie818ItemModelWithCnCodeInformationFuture: Future[Seq[IE818ItemModelWithCnCodeInformation]] = if (eventType == IE818) {
      (for {
        reportOfReceipt <- movement.reportOfReceipt
        if reportOfReceipt.acceptMovement != AcceptMovement.Satisfactory
      } yield {
        getCnCodeInformationService.getCnCodeInformation(movement.items).map {
          _.flatMap {
            case (item, information) => reportOfReceipt.individualItems.collect {
              case rorItem if rorItem.eadBodyUniqueReference == item.itemUniqueReference =>
                IE818ItemModelWithCnCodeInformation(rorItem, information)
            }
          }
        }
      }).getOrElse(Future.successful(Seq.empty))
    } else {
      Future.successful(Seq.empty)
    }
    ie818ItemModelWithCnCodeInformationFuture.flatMap(f)
  }


  private def withIE881ItemModelWithCnCodeInformation(eventType: EventTypes, movement: GetMovementResponse)
                                                     (f: Seq[IE881ItemModelWithCnCodeInformation] => Result)
                                                     (implicit request: DataRequest[_]): Future[Result] = {
    val ie881ItemModelWithCnCodeInformationFuture: Future[Seq[IE881ItemModelWithCnCodeInformation]] = if (eventType == IE881) {
      (for {
        manualClosureResponse <- movement.manualClosureResponse
        manualClosure <- manualClosureResponse.bodyManualClosure
      } yield {
        getCnCodeInformationService.getCnCodeInformation(movement.items).map {
          _.flatMap {
            case (item, information) => manualClosure.collect {
              case closureItem if closureItem.bodyRecordUniqueReference == item.itemUniqueReference =>
                IE881ItemModelWithCnCodeInformation(closureItem, information)
            }
          }
        }
      }).getOrElse(Future.successful(Seq.empty))
    } else {
      Future.successful(Seq.empty)
    }
    ie881ItemModelWithCnCodeInformationFuture.map(f)
  }


  private def withHistoryEvent(ern: String, arc: String, eventType: EventTypes, eventId: Int)
                              (f: MovementHistoryEvent => Future[Result])(implicit request: DataRequest[_]): Future[Result] =

    getMovementHistoryEventsService.getMovementHistoryEvents(ern, arc).flatMap {
      case events if events.nonEmpty =>
        events.find(e => e.eventId == eventId && e.eventType == eventType) match {
          case Some(matchedEvent) =>
            f(matchedEvent)
          case None =>
            logger.warn(s"[withHistoryEvent] - Unable to find the movement history for event id $eventId")
            errorHandler.notFoundTemplate.map(NotFound(_))
        }

      case _ =>
        logger.warn(s"[withHistoryEvent] - There are no movement history events for ERN:$ern, ARC:$arc")
        errorHandler.notFoundTemplate.map(NotFound(_))
      }

}
