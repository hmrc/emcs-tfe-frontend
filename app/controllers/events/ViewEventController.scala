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

import config.{AppConfig, ErrorHandler}
import controllers.helpers.BetaChecks
import controllers.predicates.{AuthAction, AuthActionHelper, BetaAllowListAction, DataRetrievalAction}
import models.EventTypes
import models.EventTypes._
import models.requests.DataRequest
import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{GetMovementHistoryEventsService, GetMovementService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Logging
import views.html.events.HistoryEventView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ViewEventController @Inject()(mcc: MessagesControllerComponents,
                                    val auth: AuthAction,
                                    val getData: DataRetrievalAction,
                                    val betaAllowList: BetaAllowListAction,
                                    getMovementHistoryEventsService: GetMovementHistoryEventsService,
                                    getMovementService: GetMovementService,
                                    view: HistoryEventView,
                                    errorHandler: ErrorHandler
                                   )(implicit val executionContext: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc)
    with AuthActionHelper
    with I18nSupport
    with Logging
    with BetaChecks {

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

  private def onPageLoad(ern: String, arc: String, eventId: Int, eventType: EventTypes): Action[AnyContent] = {
    authorisedDataRequestAsync(ern, viewMovementBetaGuard(ern, arc)) { implicit request =>
      withHistoryEvent(ern, arc, eventType, eventId) { event =>
        getMovementService.getMovement(ern = ern, arc = arc, sequenceNumber = Some(event.sequenceNumber)).map {
          movement =>
            Ok(view(event, movement))
        }
      }
    }
  }


  private def withHistoryEvent(ern:String, arc: String, eventType: EventTypes, eventId: Int)
                      (f: MovementHistoryEvent => Future[Result])(implicit request: DataRequest[_]): Future[Result] =

    getMovementHistoryEventsService.getMovementHistoryEvents(ern, arc).flatMap {
      case events if events.nonEmpty =>
        events.find(e => e.eventId == eventId && e.eventType == eventType) match {
          case Some(matchedEvent) =>
            f(matchedEvent)
          case None =>
            logger.warn(s"[withHistoryEvent] - Unable to find the movement history for event id $eventId")
            Future.successful( NotFound(errorHandler.notFoundTemplate) )
        }

      case _ =>
        logger.warn(s"[withHistoryEvent] - There are no movement history events for ERN:$ern, ARC:$arc")
        Future.successful( NotFound(errorHandler.notFoundTemplate) )
    }

}
