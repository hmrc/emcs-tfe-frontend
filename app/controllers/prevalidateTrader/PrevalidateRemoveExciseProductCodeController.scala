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


import controllers.BaseNavigationController
import controllers.predicates._
import forms.prevalidateTrader.PrevalidateRemoveExciseProductCodeFormProvider
import models.{Index, NormalMode}
import models.requests.UserAnswersRequest
import navigation.PrevalidateTraderNavigator
import pages.prevalidateTrader.PrevalidateEPCPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import play.twirl.api.HtmlFormat
import queries.PrevalidateTraderEPCCount
import services.PrevalidateTraderUserAnswersService
import views.html.prevalidateTrader.PrevalidateTraderRemoveExciseProductCodeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PrevalidateRemoveExciseProductCodeController @Inject()(override val controllerComponents: MessagesControllerComponents,
                                                             val auth: AuthAction,
                                                             val getData: DataRetrievalAction,
                                                             val betaAllowList: BetaAllowListAction,
                                                             val requireData: PrevalidateTraderDataRetrievalAction,
                                                             val userAnswersService: PrevalidateTraderUserAnswersService,
                                                             val navigator: PrevalidateTraderNavigator,
                                                             formProvider: PrevalidateRemoveExciseProductCodeFormProvider,
                                                             view: PrevalidateTraderRemoveExciseProductCodeView
                                                            )(implicit val executionContext: ExecutionContext) extends BaseNavigationController with AuthActionHelper with I18nSupport {

  def onPageLoad(ern: String, idx: Index): Action[AnyContent] =
    (authorisedWithData(ern) andThen requireData) { implicit request =>
      validateIndex(idx) {
        val exciseProductCodeSelected = request.userAnswers.get(PrevalidateEPCPage(idx)).get
        Ok(renderView(idx, exciseProductCodeSelected.code, formProvider(exciseProductCodeSelected.code)))
      }
    }

  def onSubmit(ern: String, idx: Index): Action[AnyContent] =
    (authorisedWithData(ern) andThen requireData).async { implicit request =>
      validateIndexAsync(idx) {
        val exciseProductCodeSelected = request.userAnswers.get(PrevalidateEPCPage(idx)).get
        formProvider(exciseProductCodeSelected.code).bindFromRequest().fold(
          formWithErrors => Future.successful(BadRequest(renderView(idx, exciseProductCodeSelected.code, formWithErrors))),
          handleAnswerRemovalAndRedirect(_, ern, idx)
        )
      }
    }

  private def renderView(idx: Index, exciseProductCodeSelected: String, form: Form[Boolean])(implicit request: UserAnswersRequest[_]): HtmlFormat.Appendable = {
    view(
      form = form,
      action = controllers.prevalidateTrader.routes.PrevalidateRemoveExciseProductCodeController.onSubmit(request.ern, idx),
      exciseProductCode = exciseProductCodeSelected
    )
  }

  private def handleAnswerRemovalAndRedirect(shouldRemoveEPC: Boolean, ern: String, index: Index)
                                            (implicit request: UserAnswersRequest[_]): Future[Result] = {
    if (shouldRemoveEPC) {
      val cleansedAnswers = request.userAnswers.remove(PrevalidateEPCPage(index))

      val onwardRoute = cleansedAnswers.get(PrevalidateTraderEPCCount) match {
        case Some(count) if count >= 1 => routes.PrevalidateAddToListController.onPageLoad(ern)
        case _ => routes.PrevalidateExciseProductCodeController.onPageLoad(ern, Index(0), NormalMode)
      }
      userAnswersService.set(cleansedAnswers).map(_ => Redirect(onwardRoute))
    } else {
      Future(Redirect(routes.PrevalidateAddToListController.onPageLoad(ern)))
    }
  }

  private def validateIndex(index: Index)(onSuccess: => Result)(implicit request: UserAnswersRequest[_]): Result = {
    super.validateIndex(PrevalidateTraderEPCCount, index)(
      onSuccess,
      Redirect(controllers.prevalidateTrader.routes.PrevalidateAddToListController.onPageLoad(request.ern))
    )
  }

  private def validateIndexAsync(index: Index)(onSuccess: => Future[Result])(implicit request: UserAnswersRequest[_]): Future[Result] = {
    super.validateIndex(PrevalidateTraderEPCCount, index)(
      onSuccess,
      Future.successful(Redirect(controllers.prevalidateTrader.routes.PrevalidateAddToListController.onPageLoad(request.ern)))
    )
  }
}
