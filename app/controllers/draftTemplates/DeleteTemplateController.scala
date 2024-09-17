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
import config.Constants.TFE_DELETED_DRAFT_TEMPLATE_ID
import controllers.predicates.{AuthAction, AuthActionHelper, DataRetrievalAction}
import forms.draftTemplates.DeleteTemplateFormProvider
import models.draftTemplates.Template
import models.requests.DataRequest
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.DraftTemplatesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.draftTemplates.DeleteTemplateView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeleteTemplateController @Inject() (mcc: MessagesControllerComponents,
                                          val auth: AuthAction,
                                          val getData: DataRetrievalAction,
                                          service: DraftTemplatesService,
                                          view: DeleteTemplateView,
                                          form: DeleteTemplateFormProvider
                                         )(implicit val executionContext: ExecutionContext, appConfig: AppConfig)
  extends FrontendController(mcc)
    with AuthActionHelper
    with I18nSupport {

  def onPageLoad(exciseRegistrationNumber: String, templateId: String): Action[AnyContent] = {
    authorisedDataRequestAsync(exciseRegistrationNumber) { implicit request =>
      ifCanAccessDraftTemplates(exciseRegistrationNumber) {
        withTemplate(exciseRegistrationNumber, templateId) { template =>
          renderView(Ok, template, form())
        }
      }
    }
  }

  def onSubmit(exciseRegistrationNumber: String, templateId: String): Action[AnyContent] = {
    authorisedDataRequestAsync(exciseRegistrationNumber) { implicit request =>
      ifCanAccessDraftTemplates(exciseRegistrationNumber) {
        withTemplate(exciseRegistrationNumber, templateId) { template =>
          form().bindFromRequest().fold(
            formWithErrors =>
              renderView(BadRequest, template, formWithErrors),
            value =>
              if (value) {
                service.delete(exciseRegistrationNumber, template.templateId).map { _ =>
                  redirectToAllTemplatesView(exciseRegistrationNumber)
                    .flashing(TFE_DELETED_DRAFT_TEMPLATE_ID -> template.templateName)
                }
              } else {
                Future.successful(redirectToAllTemplatesView(exciseRegistrationNumber))
              }
          )
        }
      }
    }
  }

  private def withTemplate(exciseRegistrationNumber: String, templateId: String)
                              (f: Template => Future[Result])(implicit request: DataRequest[_]): Future[Result] =

    service.getTemplate(exciseRegistrationNumber, templateId).flatMap {
      case Some(template) => f(template)
      case None =>
        logger.warn(s"[withTemplate] - Unable to find template with ID: $templateId for ERN: $exciseRegistrationNumber")
        Future.successful(redirectToAllTemplatesView(exciseRegistrationNumber))
    }

  private def redirectToAllTemplatesView(exciseRegistrationNumber: String): Result = {
    Redirect(controllers.draftTemplates.routes.ViewAllTemplatesController.onPageLoad(exciseRegistrationNumber, None))
  }

  private def renderView(status: Status, template: Template, form: Form[Boolean])(implicit request: DataRequest[_]): Future[Result] = {
    Future.successful(status(view(form, template)))
  }
  
}
