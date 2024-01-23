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

import cats.data.EitherT
import config.ErrorHandler
import connectors.emcsTfe.GetMovementListConnector
import connectors.referenceData.GetExciseProductCodesConnector
import controllers.predicates.{AuthAction, AuthActionHelper, DataRetrievalAction}
import forms.ViewAllMovementsFormProvider
import models.MovementListSearchOptions.DEFAULT_MAX_ROWS
import models.requests.DataRequest
import models.response.ErrorResponse
import models.{MovementFilterStatusOption, MovementListSearchOptions, MovementSearchSelectOption, MovementSortingSelectOption}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.MovementPaginationHelper
import viewmodels.helpers.SelectItemHelper
import views.html.viewAllMovements.ViewAllMovements

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ViewAllMovementsController @Inject()(mcc: MessagesControllerComponents,
                                           getMovementListConnector: GetMovementListConnector,
                                           getExciseProductCodesConnector: GetExciseProductCodesConnector,
                                           view: ViewAllMovements,
                                           errorHandler: ErrorHandler,
                                           val auth: AuthAction,
                                           val getData: DataRetrievalAction,
                                           paginationHelper: MovementPaginationHelper,
                                           formProvider: ViewAllMovementsFormProvider
                                          )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with AuthActionHelper with I18nSupport {
  def onPageLoad(ern: String, searchOptions: MovementListSearchOptions): Action[AnyContent] = {
    authorisedWithData(ern).async { implicit request =>
      println(scala.Console.YELLOW + "query string in onPageLoad = " + request.queryString + scala.Console.RESET)
      renderView(Ok, ern, searchOptions)
    }
  }

  def onSubmit(ern: String, searchOptions: MovementListSearchOptions): Action[AnyContent] =
    authorisedWithData(ern).async { implicit request =>
      formProvider().bindFromRequest().fold(
        _ => renderView(BadRequest, ern, searchOptions),
        value => {
          println(scala.Console.YELLOW + "value in onSubmit = " + value + scala.Console.RESET)

          Future(Redirect(routes.ViewAllMovementsController.onPageLoad(ern, value)))
        }
      )
    }

  private def renderView(status: Status, ern: String, searchOptions: MovementListSearchOptions)(implicit request: DataRequest[_]) = {
    val result: EitherT[Future, ErrorResponse, Result] = for {
      movementList <- EitherT(getMovementListConnector.getMovementList(ern, Some(searchOptions)))
      epcs <- EitherT(getExciseProductCodesConnector.getExciseProductCodes())
      exciseProductCodeOptions = MovementListSearchOptions.CHOOSE_PRODUCT_CODE +: epcs
    } yield {

        val pageCount = {
          if (movementList.count == 0) {
            1
          } else if (movementList.count % DEFAULT_MAX_ROWS != 0) {
            (movementList.count / DEFAULT_MAX_ROWS) + 1
          } else {
            movementList.count / DEFAULT_MAX_ROWS
          }
        }
        if (searchOptions.index <= 0 || searchOptions.index > pageCount) {
          Redirect(routes.ViewAllMovementsController.onPageLoad(ern, MovementListSearchOptions()))
        } else {
          val movementStatusItems = MovementFilterStatusOption.selectItems(searchOptions.movementStatus)

          status(view(
            form = formProvider().fill(searchOptions),
            action = routes.ViewAllMovementsController.onSubmit(ern, searchOptions),
            ern = ern,
            movements = movementList.movements,
            sortSelectItems = MovementSortingSelectOption.constructSelectItems(Some(searchOptions.sortBy.code)),
            searchSelectItems = MovementSearchSelectOption.constructSelectItems(searchOptions.searchKey.map(_.code)),
            movementStatusItems = movementStatusItems,
            exciseProductCodeSelectItems = SelectItemHelper.constructSelectItems(exciseProductCodeOptions, None, searchOptions.exciseProductCode),
            pagination = paginationHelper.constructPagination(pageCount, ern, searchOptions)
          ))
        }

    }

    result.leftMap {
      _ => InternalServerError(errorHandler.internalServerErrorTemplate)
    }.merge
  }
}
