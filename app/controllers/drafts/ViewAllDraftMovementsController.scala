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

package controllers.drafts

import cats.data.EitherT
import config.{AppConfig, ErrorHandler}
import connectors.emcsTfe.GetDraftMovementsConnector
import connectors.referenceData.GetExciseProductCodesConnector
import controllers.predicates.{AuthAction, AuthActionHelper, DataRetrievalAction}
import forms.ViewAllDraftMovementsFormProvider
import models.draftMovements.GetDraftMovementsSearchOptions.DEFAULT_MAX_ROWS
import models.draftMovements.{DraftMovementSortingSelectOption, GetDraftMovementsSearchOptions}
import models.requests.DataRequest
import models.response.ErrorResponse
import models.response.emcsTfe.draftMovement.GetDraftMovementsResponse
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.draftMovements.DraftMovementsPaginationHelper
import viewmodels.helpers.SelectItemHelper
import views.html.viewAllDrafts.ViewAllDraftMovementsView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ViewAllDraftMovementsController @Inject()(mcc: MessagesControllerComponents,
                                                getDraftMovementsConnector: GetDraftMovementsConnector,
                                                view: ViewAllDraftMovementsView,
                                                getExciseProductCodesConnector: GetExciseProductCodesConnector,
                                                errorHandler: ErrorHandler,
                                                val auth: AuthAction,
                                                val getData: DataRetrievalAction,
                                                formProvider: ViewAllDraftMovementsFormProvider,
                                                paginationHelper: DraftMovementsPaginationHelper
                                               )(implicit val executionContext: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc) with AuthActionHelper with I18nSupport {

  def onPageLoad(ern: String, searchOptions: GetDraftMovementsSearchOptions): Action[AnyContent] = {
    authorisedDataRequestAsync(ern) { implicit request =>
      renderView(Ok, ern, searchOptions, formProvider())
    }
  }

  def onSubmit(ern: String, searchOptions: GetDraftMovementsSearchOptions): Action[AnyContent] = {
    authorisedDataRequestAsync(ern) { implicit request =>
      formProvider().bindFromRequest().fold(
        renderView(BadRequest, ern, searchOptions, _),
        value => Future(Redirect(routes.ViewAllDraftMovementsController.onPageLoad(ern, value)))
      )
    }
  }

  private def renderView(
                          status: Status,
                          ern: String,
                          searchOptions: GetDraftMovementsSearchOptions,
                          form: Form[GetDraftMovementsSearchOptions]
                        )(implicit request: DataRequest[_]): Future[Result] = {

    val result: EitherT[Future, ErrorResponse, Result] = for {
      draftMovements <- EitherT(getDraftMovementsConnector.getDraftMovements(ern, Some(searchOptions)))
      exciseCodes <- EitherT(getExciseProductCodesConnector.getExciseProductCodes())
      exciseCodesWithDefault = GetDraftMovementsSearchOptions.CHOOSE_PRODUCT_CODE +: exciseCodes
    } yield {

      val formToRender = if (form.hasErrors) form else form.fill(searchOptions)

      val pageCount: Int = calculatePageCount(draftMovements)

      if (searchOptions.index <= 0 || searchOptions.index > pageCount) {
        Redirect(routes.ViewAllDraftMovementsController.onPageLoad(ern, searchOptions.copy(index = 1)))
      } else {
        status(view(
          form = formToRender,
          action = routes.ViewAllDraftMovementsController.onSubmit(ern, searchOptions),
          ern = ern,
          movements = draftMovements.paginatedDrafts,
          sortSelectItems = DraftMovementSortingSelectOption.constructSelectItems(Some(searchOptions.sortBy.code)),
          exciseItems = SelectItemHelper.constructSelectItems(exciseCodesWithDefault, None, searchOptions.exciseProductCode),
          pagination = paginationHelper.constructPagination(pageCount, ern, searchOptions),
          totalMovements = draftMovements.count,
          currentFilters = searchOptions
        ))
      }
    }

    result.value.flatMap {
      case Right(value) => Future.successful(value)
      case Left(_) => errorHandler.internalServerErrorTemplate.map(InternalServerError(_))
    }
  }

  private def calculatePageCount(movementList: GetDraftMovementsResponse): Int = movementList.count match {
    case 0 => 1
    case count if count % DEFAULT_MAX_ROWS != 0 => (movementList.count / DEFAULT_MAX_ROWS) + 1
    case _ => movementList.count / DEFAULT_MAX_ROWS
  }
}
