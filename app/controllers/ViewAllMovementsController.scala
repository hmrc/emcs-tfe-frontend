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
import connectors.emcsTfe.GetMovementListConnector
import controllers.predicates.AuthAction
import forms.ViewAllMovementsFormProvider
import models.MovementListSearchOptions.DEFAULT_MAX_ROWS
import models.auth.UserRequest
import models.{MovementListSearchOptions, MovementSortingSelectOption}
import play.api.i18n.Messages
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.MovementPaginationHelper
import views.html.viewAllMovements.ViewAllMovements

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ViewAllMovementsController @Inject()(mcc: MessagesControllerComponents,
                                           connector: GetMovementListConnector,
                                           view: ViewAllMovements,
                                           errorHandler: ErrorHandler,
                                           authAction: AuthAction,
                                           paginationHelper: MovementPaginationHelper,
                                           formProvider: ViewAllMovementsFormProvider,
                                          )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) {
  def onPageLoad(ern: String, searchOptions: MovementListSearchOptions): Action[AnyContent] =
    authAction(ern).async { implicit request =>
      renderView(Ok, ern, searchOptions)
    }

  def onSubmit(ern: String, searchOptions: MovementListSearchOptions): Action[AnyContent] =
    authAction(ern).async { implicit request =>
      formProvider().bindFromRequest().fold(
        _ => renderView(BadRequest, ern, searchOptions),
        value => Future(Redirect(routes.ViewAllMovementsController.onPageLoad(ern, value)))
      )
    }

  private def renderView(status: Status, ern: String, searchOptions: MovementListSearchOptions)(implicit request: UserRequest[_], messages: Messages) = {
    connector.getMovementList(ern, Some(searchOptions)).map {
      case Right(movementList) =>

        val pageCount = if(movementList.count % DEFAULT_MAX_ROWS != 0)
          (movementList.count / DEFAULT_MAX_ROWS) + 1
        else
          movementList.count / DEFAULT_MAX_ROWS
        if (searchOptions.index <= 0 || searchOptions.index > pageCount)
          Redirect(routes.ViewAllMovementsController.onPageLoad(ern, MovementListSearchOptions()))
        else {
          status(view(
            form = formProvider(),
            action = routes.ViewAllMovementsController.onSubmit(ern, searchOptions),
            ern = ern,
            movements = movementList.movements,
            selectItems = MovementSortingSelectOption.constructSelectItems(Some(searchOptions.sortBy.code)),
            pagination = paginationHelper.constructPagination(pageCount, ern, searchOptions)
          ))
        }

      case Left(_) =>
        InternalServerError(errorHandler.internalServerErrorTemplate)
    }
  }
}