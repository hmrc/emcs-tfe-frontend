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

package controllers.prevalidateTrader

import config.AppConfig
import controllers.BaseNavigationController
import controllers.helpers.BetaChecks
import controllers.predicates._
import forms.prevalidate.PrevalidateAddToListFormProvider
import models.requests.UserAnswersRequest
import models.{Index, NormalMode}
import navigation.PrevalidateTraderNavigator
import pages.prevalidateTrader.{PrevalidateAddToListPage, PrevalidateAddedProductCodesPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.PrevalidateTraderEPCCount
import services.PrevalidateTraderUserAnswersService
import viewmodels.helpers.PrevalidateAddToListHelper
import views.html.prevalidateTrader.PrevalidateAddToListView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PrevalidateAddToListController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                override val userAnswersService: PrevalidateTraderUserAnswersService,
                                                override val betaAllowList: BetaAllowListAction,
                                                override val navigator: PrevalidateTraderNavigator,
                                                override val auth: AuthAction,
                                                override val getData: DataRetrievalAction,
                                                val requireData: PrevalidateTraderDataRetrievalAction,
                                                formProvider: PrevalidateAddToListFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: PrevalidateAddToListView
                                              )(implicit val executionContext: ExecutionContext, appConfig: AppConfig)
  extends BaseNavigationController
    with AuthActionHelper
    with BetaChecks {

  def onPageLoad(ern: String): Action[AnyContent] =
    (authorisedWithBetaGuardData(ern, preValidateBetaGuard(ern)) andThen requireData).async { implicit request =>
      withAtLeastOneItem {
        val form = onMax(maxF = None, notMaxF = Some(fillForm(PrevalidateAddToListPage, formProvider())))
        Future.successful(renderView(Ok, form))
      }
    }

  def onSubmit(ern: String): Action[AnyContent] =
    (authorisedWithBetaGuardData(ern, preValidateBetaGuard(ern)) andThen requireData).async { implicit request =>
      onMax(
        maxF = Future.successful(Redirect(navigator.nextPage(
          page = PrevalidateAddToListPage,
          mode = NormalMode,
          userAnswers = request.userAnswers
        ))),
        notMaxF =
          formProvider().bindFromRequest().fold(
            formWithErrors => Future.successful(renderView(BadRequest, Some(formWithErrors))),
            handleSubmissionRedirect
          )
      )
    }

  private def renderView(status: Status, form: Option[Form[_]])(implicit request: UserAnswersRequest[_]): Result =
    status(view(
      formOpt = form,
      onSubmitCall = routes.PrevalidateAddToListController.onSubmit(request.ern),
      addedEpcs = PrevalidateAddToListHelper.addedEpcs()
    ))

  private def handleSubmissionRedirect(implicit request: UserAnswersRequest[_]): Boolean => Future[Result] = {
    case true =>
      userAnswersService.set(request.userAnswers.remove(PrevalidateAddToListPage)).map { _ =>
        Redirect(navigator.nextPage(
          page = PrevalidateAddToListPage,
          mode = NormalMode,
          userAnswers = request.userAnswers.set(PrevalidateAddToListPage, true)
        ))
      }
    case value =>
      saveAndRedirect(PrevalidateAddToListPage, value, NormalMode)
  }

  private def onMax[T](maxF: => T, notMaxF: => T)(implicit request: UserAnswersRequest[_]): T =
    request.userAnswers.get(PrevalidateTraderEPCCount) match {
      case Some(value) if value >= PrevalidateAddedProductCodesPage.MAX => maxF
      case _ => notMaxF
    }

  private def withAtLeastOneItem(f: => Future[Result])(implicit request: UserAnswersRequest[_]): Future[Result] =
    request.userAnswers.get(PrevalidateTraderEPCCount) match {
      case Some(value) if value > 0 => f
      case _ => Future.successful(Redirect(routes.PrevalidateExciseProductCodeController.onPageLoad(request.ern, Index(0), NormalMode)))
    }
}
