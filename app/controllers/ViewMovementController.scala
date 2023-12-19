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

import config.ErrorHandler
import controllers.predicates.{AuthAction, AuthActionHelper, DataRetrievalAction}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.GetMovementService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels._
import viewmodels.helpers.{TimelineHelper, ViewMovementHelper}
import views.html.viewMovement.ViewMovementPage

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ViewMovementController @Inject()(mcc: MessagesControllerComponents,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       getMovementService: GetMovementService,
                                       viewMovementPage: ViewMovementPage,
                                       errorHandler: ErrorHandler,
                                       helper: ViewMovementHelper,
                                       timelineHelper: TimelineHelper
                                      )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with I18nSupport with AuthActionHelper {

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

    authorisedDataRequestAsync(exciseRegistrationNumber) { implicit request =>
      getMovementService.getMovement(exciseRegistrationNumber, arc).map { movement =>
        Ok(viewMovementPage(
          ern = exciseRegistrationNumber,
          arc = arc,
          isConsignor = exciseRegistrationNumber == movement.consignorTrader.traderExciseNumber,
          subNavigationTabs = SubNavigationTab.values,
          currentSubNavigationTab = currentSubNavigationTab,
          movementTabBody = helper.movementCard(currentSubNavigationTab, movement),
          historyEvents = movement.eventHistorySummary
            .map(timelineHelper.timeline(_))
            .getOrElse(Seq.empty[TimelineEvent])
        ))
      } recover {
        case _ =>
          InternalServerError(errorHandler.standardErrorTemplate())
      }
    }
}
