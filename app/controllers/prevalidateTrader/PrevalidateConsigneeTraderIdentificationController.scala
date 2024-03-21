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

package controllers.prevalidateTrader

import controllers.predicates._
import forms.PrevalidateConsigneeTraderIdentificationFormProvider
import models.NormalMode
import models.requests.UserAnswersRequest
import navigation.PrevalidateTraderNavigator
import pages.prevalidateTrader.PrevalidateConsigneeTraderIdentificationPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.PrevalidateTraderUserAnswersService
import views.html.prevalidateTrader.PrevalidateConsigneeTraderIdentificationView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PrevalidateConsigneeTraderIdentificationController @Inject()(override val controllerComponents: MessagesControllerComponents,
                                                                   val auth: AuthAction,
                                                                   val getData: DataRetrievalAction,
                                                                   val betaAllowList: BetaAllowListAction,
                                                                   val userAnswersAction: PrevalidateTraderDataRetrievalAction,
                                                                   val userAnswersService: PrevalidateTraderUserAnswersService,
                                                                   val navigator: PrevalidateTraderNavigator,
                                                                   formProvider: PrevalidateConsigneeTraderIdentificationFormProvider,
                                                                   view: PrevalidateConsigneeTraderIdentificationView
                                                       )(implicit val executionContext: ExecutionContext) extends BasePrevalidateNavigationController with AuthActionHelper with I18nSupport {

  def onPageLoad(ern: String): Action[AnyContent] =
    (authorisedWithData(ern) andThen userAnswersAction) { implicit request =>
      Ok(renderView(fillForm(PrevalidateConsigneeTraderIdentificationPage, formProvider())))
    }

  def onSubmit(ern: String): Action[AnyContent] =
    (authorisedWithData(ern) andThen userAnswersAction).async { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(renderView(formWithErrors))),
        value => saveAndRedirect(PrevalidateConsigneeTraderIdentificationPage, value, NormalMode)
      )
    }

  private def renderView(form: Form[String])(implicit request: UserAnswersRequest[_]) = {
    view(
      form = form,
      action = controllers.prevalidateTrader.routes.PrevalidateConsigneeTraderIdentificationController.onSubmit(request.ern)
    )
  }

}
