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

package controllers.draftTemplates

import config.AppConfig
import controllers.predicates.{AuthAction, AuthActionHelper, DataRetrievalAction}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.DraftTemplatesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.PaginationHelper
import views.html.ViewAllTemplatesView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ViewAllTemplatesController @Inject()(mcc: MessagesControllerComponents,
                                           view: ViewAllTemplatesView,
                                           val auth: AuthAction,
                                           val getData: DataRetrievalAction,
                                           service: DraftTemplatesService,
                                           paginationHelper: PaginationHelper
                                          )(implicit val executionContext: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc) with AuthActionHelper with I18nSupport {

  val DEFAULT_MAX_ROWS = 10

  val totalNumberOfTemplates = 30

  def onPageLoad(exciseRegistrationNumber: String, page: Option[Int]): Action[AnyContent] = {
    authorisedDataRequestAsync(exciseRegistrationNumber) { implicit request =>
      ifCanAccessDraftTemplates(exciseRegistrationNumber) {
        service.list(exciseRegistrationNumber, page.getOrElse(1)).map {
          templates =>
            val pageCount: Int = paginationHelper.calculatePageCount(totalNumberOfTemplates, DEFAULT_MAX_ROWS)

            val currentPage = page.getOrElse(1)

            if (currentPage <= 0 || currentPage > pageCount) {
              // if page number is invalid - lower than '1' or higher than the calculated max page count
              Redirect(controllers.draftTemplates.routes.ViewAllTemplatesController.onPageLoad(exciseRegistrationNumber, None))
            } else {
              val pagination = paginationHelper.constructPaginationForDraftTemplates(exciseRegistrationNumber, page.getOrElse(1), pageCount)

              Ok(view(templates, pagination))
            }
        }
      }
    }

  }
}
