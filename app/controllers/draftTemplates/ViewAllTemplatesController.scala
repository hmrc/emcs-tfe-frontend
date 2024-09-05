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
import models.common.DestinationType
import models.draftTemplates.Template
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewmodels.PaginationHelper
import views.html.ViewAllTemplatesView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ViewAllTemplatesController @Inject()(mcc: MessagesControllerComponents,
                                           view: ViewAllTemplatesView,
                                           val auth: AuthAction,
                                           val getData: DataRetrievalAction,
                                           paginationHelper: PaginationHelper
                                          )(implicit val executionContext: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc) with AuthActionHelper with I18nSupport {

  val dummyTemplates: Seq[Template] = Seq(
    Template("1", "Template 1", DestinationType.TaxWarehouse, Some("GB001234567890")),
    Template("2", "Template 2", DestinationType.TaxWarehouse, Some("GB001234567890")),
    Template("3", "Template 3", DestinationType.TaxWarehouse, Some("XI001234567890")),
    Template("4", "Template 4", DestinationType.TaxWarehouse, Some("IE001234567890")),
    Template("5", "Template 5", DestinationType.TaxWarehouse, None),
    Template("6", "Template 6", DestinationType.Export, Some("GB001234567890")),
    Template("7", "Template 7", DestinationType.Export, Some("GB001234567890")),
    Template("8", "Template 8", DestinationType.Export, Some("XI001234567890")),
    Template("9", "Template 9", DestinationType.Export, Some("IE001234567890")),
    Template("10", "Template 10", DestinationType.Export, None)
  )

  val totalNumberOfTemplates = 45

  def onPageLoad(exciseRegistrationNumber: String, page: Option[Int]): Action[AnyContent] = {
    authorisedDataRequestAsync(exciseRegistrationNumber) { implicit request =>
      ifCanAccessDraftTemplates(exciseRegistrationNumber) {
        val pagination = paginationHelper.constructPaginationForDraftTemplates(exciseRegistrationNumber, page.getOrElse(1), totalNumberOfTemplates)

        Future.successful(
          Ok(view(dummyTemplates, pagination))
        )
      }
    }
  }

}
