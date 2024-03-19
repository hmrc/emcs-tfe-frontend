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

import controllers.predicates.{AuthAction, AuthActionHelper, BetaAllowListAction, DataRetrievalAction}
import forms.PrevalidateConsigneeTraderIdentificationFormProvider
import models.requests.DataRequest
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.prevalidateTrader.PrevalidateConsigneeTraderIdentificationView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PrevalidateConsigneeTraderIdentificationController @Inject()(mcc: MessagesControllerComponents,
                                                                   val auth: AuthAction,
                                                                   val getData: DataRetrievalAction,
                                                                   val betaAllowList: BetaAllowListAction,
                                                                   formProvider: PrevalidateConsigneeTraderIdentificationFormProvider,
                                                                   view: PrevalidateConsigneeTraderIdentificationView
                                                       )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with AuthActionHelper with I18nSupport {

  def onPageLoad(ern: String): Action[AnyContent] =
    authorisedDataRequest(ern) { implicit request =>
      Ok(renderView())
    }

  def onSubmit(ern: String): Action[AnyContent] =
    authorisedDataRequestAsync(ern) { implicit request =>
      formProvider().bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(renderView(formWithErrors))),
        _ => Future.successful(Redirect(testOnly.controllers.routes.UnderConstructionController.onPageLoad()))
      )
    }

  private def renderView(form: Form[_] = formProvider())(implicit request: DataRequest[_]) = {
    view(
      form = form,
      action = controllers.prevalidateTrader.routes.PrevalidateConsigneeTraderIdentificationController.onSubmit(request.ern)
    )
  }

}
