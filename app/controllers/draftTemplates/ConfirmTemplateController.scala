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
import forms.draftTemplates.ConfirmTemplateFormProvider
import models.draftTemplates.Template
import models.requests.DataRequest
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{DraftTemplatesService, GetCnCodeInformationService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.draftTemplates.ConfirmTemplateView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmTemplateController @Inject()(mcc: MessagesControllerComponents,
                                          val auth: AuthAction,
                                          val getData: DataRetrievalAction,
                                          service: DraftTemplatesService,
                                          cnCodeService: GetCnCodeInformationService,
                                          view: ConfirmTemplateView,
                                          form: ConfirmTemplateFormProvider
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
                createDraftEntryAndRedirect(exciseRegistrationNumber, template)
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


  private def redirectToAllTemplatesView(exciseRegistrationNumber: String): Result =
    Redirect(controllers.draftTemplates.routes.ViewAllTemplatesController.onPageLoad(exciseRegistrationNumber, None))


  private def renderView(status: Status, template: Template, form: Form[Boolean])(implicit request: DataRequest[_]): Future[Result] =
    cnCodeService.getCnCodeInformationForTemplateItems(template.items).map { itemsWithCnCodeInfo =>
      status(view(form, template, itemsWithCnCodeInfo))
    }

  private def createDraftEntryAndRedirect(ern: String, template: Template)(implicit request: DataRequest[_]): Future[Result] =
    service.createDraftMovement(ern, template.templateId).map { response =>
      Redirect(appConfig.emcsTfeChangeDraftDeferredMovementUrl(ern, response.createdDraftId))
    }
  
}
