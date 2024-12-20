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
import connectors.referenceData.{GetExciseProductCodesConnector, GetMemberStatesConnector}
import controllers.predicates.{AuthAction, AuthActionHelper, DataRetrievalAction}
import forms.ViewAllMovementsFormProvider
import models.MovementListSearchOptions.DEFAULT_MAX_ROWS
import models.requests.DataRequest
import models.response.emcsTfe.GetMovementListResponse
import models.response.{ErrorResponse, NotFoundError}
import models.{MovementFilterDirectionOption, MovementFilterStatusOption, MovementListSearchOptions, MovementSearchSelectOption, MovementSortingSelectOption}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.PaginationHelper
import viewmodels.helpers.SelectItemHelper
import views.html.viewAllMovements.ViewAllMovementsView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ViewAllMovementsController @Inject()(mcc: MessagesControllerComponents,
                                           getMovementListConnector: GetMovementListConnector,
                                           getExciseProductCodesConnector: GetExciseProductCodesConnector,
                                           getMemberStatesConnector: GetMemberStatesConnector,
                                           view: ViewAllMovementsView,
                                           errorHandler: ErrorHandler,
                                           val auth: AuthAction,
                                           val getData: DataRetrievalAction,
                                           paginationHelper: PaginationHelper,
                                           formProvider: ViewAllMovementsFormProvider
                                          )(implicit val executionContext: ExecutionContext)
  extends FrontendController(mcc) with AuthActionHelper with I18nSupport {

  def onPageLoad(ern: String, searchOptions: MovementListSearchOptions): Action[AnyContent] = {
    authorisedDataRequestAsync(ern) { implicit request =>
      renderView(Ok, ern, searchOptions, isInitialView)
    }
  }

  def onSubmit(ern: String, searchOptions: MovementListSearchOptions): Action[AnyContent] =
    authorisedDataRequestAsync(ern) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors => {
          val modifiedFormWithErrors = formWithErrors.copy(errors = formWithErrors.errors.map {
            case fe if fe.message.contains("searchValue") => fe.copy(key = "searchValue")
            case fe if fe.message.contains("searchKey") => fe.copy(key = "searchKey")
            case fe => fe
          })
          renderView(BadRequest, ern, searchOptions, isInitialView = false, modifiedFormWithErrors)
        },
        value => Future(Redirect(routes.ViewAllMovementsController.onPageLoad(ern, value)))
      )
    }

  private def renderView(
                          status: Status,
                          ern: String,
                          searchOptions: MovementListSearchOptions,
                          isInitialView: Boolean,
                          form: Form[MovementListSearchOptions] = formProvider()
                        )(implicit request: DataRequest[_]): Future[Result] = {

    val result: EitherT[Future, ErrorResponse, Result] = for {
      movementList <- EitherT(getMovementListConnector.getMovementList(ern, Some(searchOptions)).map {
        case Left(NotFoundError) => Right(GetMovementListResponse(Seq(), 0))
        case value => value
      })
      epcs <- EitherT(getExciseProductCodesConnector.getExciseProductCodes())
      exciseProductCodeOptions = MovementListSearchOptions.CHOOSE_PRODUCT_CODE +: epcs
      countries <- EitherT(getMemberStatesConnector.getMemberStates())
      selectCountryOptions = MovementListSearchOptions.CHOOSE_COUNTRY +: countries.sortBy(_.displayName)
    } yield {

      val pageCount: Int = paginationHelper.calculatePageCount(movementList.count, DEFAULT_MAX_ROWS)

      if (searchOptions.index <= 0 || searchOptions.index > pageCount) {
        // if page number is invalid - lower than '1' or higher than the calculated max page count
        Redirect(routes.ViewAllMovementsController.onPageLoad(ern, MovementListSearchOptions()))
      } else {
        val movementStatusItems = MovementFilterStatusOption.selectItems(searchOptions.movementStatus)
        val formToRender = if (form.hasErrors) form else form.fill(searchOptions)

        status(view(
          form = formToRender,
          action = routes.ViewAllMovementsController.onSubmit(ern, searchOptions),
          ern = ern,
          movementListResponse = movementList,
          sortSelectItems = MovementSortingSelectOption.constructSelectItems(Some(searchOptions.sortBy.code)),
          searchSelectItems = MovementSearchSelectOption.constructSelectItems(formToRender),
          movementStatusItems = movementStatusItems,
          exciseProductCodeSelectItems = SelectItemHelper.constructSelectItems(exciseProductCodeOptions, None, searchOptions.exciseProductCode),
          countrySelectItems = SelectItemHelper.constructSelectItems(selectCountryOptions, None, searchOptions.countryOfOrigin),
          pagination = paginationHelper.constructPaginationForAllMovements(pageCount, ern, searchOptions),
          directionFilterOption = searchOptions.traderRole.getOrElse(MovementFilterDirectionOption.All),
          totalMovements = movementList.count,
          currentFilters = searchOptions,
          isInitialView = isInitialView
        ))
      }
    }

    result.value.flatMap {
      case Right(value) => Future.successful(value)
      case Left(_) => errorHandler.internalServerErrorTemplate.map(InternalServerError(_))
    }
  }

  private[controllers] def isInitialView(implicit request: DataRequest[_]): Boolean = {
    val url = routes.ViewAllMovementsController.onPageLoad(request.ern, MovementListSearchOptions()).url.split("\\?")(0)
    request.headers.get("referer").forall(!_.contains(url))
  }

}
