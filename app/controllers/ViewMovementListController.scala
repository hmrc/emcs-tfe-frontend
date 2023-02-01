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

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import config.ErrorHandler
import connectors.emcsTfe.GetMovementListConnector
import controllers.predicates.AuthAction
import views.html.ViewMovementListPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ViewMovementListController @Inject()(mcc: MessagesControllerComponents,
                                           connector: GetMovementListConnector,
                                           viewMovementListPage: ViewMovementListPage,
                                           errorHandler: ErrorHandler,
                                           authAction: AuthAction
                                          )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) {

  def viewMovementList(exciseRegistrationNumber: String): Action[AnyContent] = authAction.async { implicit request =>
    authAction.checkErnMatchesRequest(exciseRegistrationNumber) {
      connector.getMovementList(exciseRegistrationNumber).map {
        case Right(movementList) => Ok(viewMovementListPage(exciseRegistrationNumber, movementList))
        case Left(_) => InternalServerError(errorHandler.internalServerErrorTemplate)
      }
    }
  }
}