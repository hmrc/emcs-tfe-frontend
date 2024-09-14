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

import config.Constants.{TFE_OLD_DRAFT_TEMPLATE_NAME, TFE_UPDATED_DRAFT_TEMPLATE_NAME}
import controllers.predicates.{AuthAction, AuthActionHelper, DataRetrievalAction}
import forms.draftTemplates.RenameTemplateFormProvider
import models.draftTemplates.Template
import models.requests.DataRequest
import play.api.data.{Form, FormError}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.DraftTemplatesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.draftTemplates.RenameTemplateView

import java.time.Instant
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RenameTemplateController @Inject()(mcc: MessagesControllerComponents,
                                         view: RenameTemplateView,
                                         val auth: AuthAction,
                                         val getData: DataRetrievalAction,
                                         service: DraftTemplatesService,
                                         formProvider: RenameTemplateFormProvider
                                          )(implicit val executionContext: ExecutionContext)
  extends FrontendController(mcc) with AuthActionHelper with I18nSupport {

  def onPageLoad(ern: String, id: String): Action[AnyContent] = {
    authorisedDataRequestAsync(ern) { implicit request =>
      renderView(Ok, ern, id, formProvider())
    }
  }

  def onSubmit(ern: String, id: String): Action[AnyContent] = {
    authorisedDataRequestAsync(ern) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors => {
          renderView(BadRequest, ern, id, formWithErrors)
        },
        newTemplateName => {
          service.doesExist(ern, newTemplateName).flatMap {
            case true => renderView(BadRequest, ern, id, formProvider().withError(FormError("value","renameTemplate.error.notUnique")))
            case false => service.getTemplate(ern, id).flatMap{
              case Some(template) =>
                val updatedTemplate = Template(template.ern, templateId = template.templateId, templateName = newTemplateName, data = template.data, lastUpdated = Instant.now())
                service.set(ern, id, updatedTemplate).map{
                  case newTemplate => Redirect(controllers.draftTemplates.routes.ViewAllTemplatesController.onPageLoad(ern, None)).flashing(TFE_UPDATED_DRAFT_TEMPLATE_NAME -> newTemplate.templateName, TFE_OLD_DRAFT_TEMPLATE_NAME -> template.templateName)
                  case _ => Redirect(routes.ViewAllTemplatesController.onPageLoad(ern, None))
                }
              case _ => Future(Redirect(routes.ViewAllTemplatesController.onPageLoad(ern, None)))
              }

          }
        }
      )
    }
  }

  private def renderView(
                          status: Status,
                          ern: String,
                          id: String,
                          form: Form[String]
                        )(implicit request: DataRequest[_]): Future[Result] = {
    service.getTemplate(ern, id).map {
      case Some(template) => status(view(form, controllers.draftTemplates.routes.RenameTemplateController.onSubmit(ern, id), template))
      case _ => Redirect(routes.ViewAllTemplatesController.onPageLoad(ern, None))

    }
  }
}
