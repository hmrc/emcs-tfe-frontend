/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers

import config.{AppConfig, ErrorHandler}
import controllers.predicates.{AuthAction, AuthActionHelper, BetaAllowListAction, DataRetrievalAction}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.GetMovementService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import utils.Logging
import viewmodels._
import viewmodels.helpers.{TimelineHelper, ViewMovementHelper}
import views.html.viewMovement.{PrintMovementView, ViewMovementView}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ViewMovementController @Inject()(mcc: MessagesControllerComponents,
                                       val auth: AuthAction,
                                       val getData: DataRetrievalAction,
                                       val betaAllowList: BetaAllowListAction,
                                       getMovementService: GetMovementService,
                                       view: ViewMovementView,
                                       printMovementView: PrintMovementView,
                                       errorHandler: ErrorHandler,
                                       helper: ViewMovementHelper,
                                       timelineHelper: TimelineHelper
                                      )(implicit val executionContext: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc)
    with I18nSupport with AuthActionHelper with Logging {

  def viewMovementOverview(exciseRegistrationNumber: String, arc: String): Action[AnyContent] =
    viewMovement(exciseRegistrationNumber, arc, Overview)

  def viewMovementMovement(exciseRegistrationNumber: String, arc: String): Action[AnyContent] =
    viewMovement(exciseRegistrationNumber, arc, Movement)

  def viewMovementDelivery(exciseRegistrationNumber: String, arc: String): Action[AnyContent] =
    viewMovement(exciseRegistrationNumber, arc, Delivery)

  def viewMovementGuarantor(exciseRegistrationNumber: String, arc: String): Action[AnyContent] =
    viewMovement(exciseRegistrationNumber, arc, Guarantor)

  def viewMovementTransport(exciseRegistrationNumber: String, arc: String): Action[AnyContent] =
    viewMovement(exciseRegistrationNumber, arc, Transport)

  def viewMovementItems(exciseRegistrationNumber: String, arc: String): Action[AnyContent] =
    viewMovement(exciseRegistrationNumber, arc, Items)

  def viewMovementDocuments(exciseRegistrationNumber: String, arc: String): Action[AnyContent] =
    viewMovement(exciseRegistrationNumber, arc, Documents)

  private def viewMovement(
                            exciseRegistrationNumber: String,
                            arc: String,
                            currentSubNavigationTab: SubNavigationTab
                          ): Action[AnyContent] =

    authorisedDataRequestAsync(exciseRegistrationNumber, viewMovementBetaGuard(exciseRegistrationNumber, arc)) { implicit request =>
      getMovementService.getLatestMovementForLoggedInUser(exciseRegistrationNumber, arc).flatMap { movement =>
        helper.movementCard(Some(currentSubNavigationTab), movement).map { movementCard =>

          Ok(view(
            ern = exciseRegistrationNumber,
            arc = arc,
            movement = movement,
            subNavigationTabs = SubNavigationTab.values,
            currentSubNavigationTab = currentSubNavigationTab,
            movementTabBody = movementCard,
            historyEvents = movement.eventHistorySummary
              .map(timelineHelper.timeline(_))
              .getOrElse(Seq.empty[TimelineEvent])
          ))
        }
      } recover {
        case e =>
          logger.warn(s"[onPageLoad][$exciseRegistrationNumber][$arc][$currentSubNavigationTab] Unexpected exception thrown of type ${e.getClass.getSimpleName.stripSuffix("$")}. Message: ${e.getMessage}")
          InternalServerError(errorHandler.standardErrorTemplate())
      }
    }

  def printMovement(exciseRegistrationNumber: String, arc: String): Action[AnyContent] =
    authorisedDataRequestAsync(exciseRegistrationNumber, viewMovementBetaGuard(exciseRegistrationNumber, arc)) { implicit request =>
      getMovementService.getLatestMovementForLoggedInUser(exciseRegistrationNumber, arc).flatMap { movement =>
        helper.movementCard(None, movement).map { movementCard =>
          Ok(printMovementView(
            ern = exciseRegistrationNumber,
            arc = arc,
            movement = movement,
            movementBody = movementCard
          ))
        }
      } recover {
        case e =>
          logger.warn(s"[onPageLoad][$exciseRegistrationNumber][$arc][Overview] Unexpected exception thrown of type ${e.getClass.getSimpleName.stripSuffix("$")}. Message: ${e.getMessage}")
          InternalServerError(errorHandler.standardErrorTemplate())
      }
    }
}
