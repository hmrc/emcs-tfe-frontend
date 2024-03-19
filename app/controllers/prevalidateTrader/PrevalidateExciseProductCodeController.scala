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

import controllers.predicates._
import forms.prevalidate.PrevalidateExciseProductCodeFormProvider
import models.requests.UserAnswersRequest
import models.{ExciseProductCode, Index, Mode}
import navigation.PrevalidateTraderNavigator
import pages.prevalidateTrader.{PrevalidateAddedProductCodesPage, PrevalidateEPCPage}
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.{PrevalidateTraderAddedValues, PrevalidateTraderEPCCount}
import services.{GetExciseProductCodesService, PrevalidateTraderUserAnswersService}
import viewmodels.helpers.SelectItemHelper
import views.html.prevalidateTrader.PrevalidateExciseProductCodeView

import javax.inject.Inject
import scala.concurrent.Future

class PrevalidateExciseProductCodeController @Inject()(
                                                        override val messagesApi: MessagesApi,
                                                        override val userAnswersService: PrevalidateTraderUserAnswersService,
                                                        override val navigator: PrevalidateTraderNavigator,
                                                        override val auth: AuthAction,
                                                        override val betaAllowList: BetaAllowListAction,
                                                        override val getData: DataRetrievalAction,
                                                        requireData: PrevalidateTraderDataRetrievalAction,
                                                        formProvider: PrevalidateExciseProductCodeFormProvider,
                                                        val controllerComponents: MessagesControllerComponents,
                                                        exciseProductCodesService: GetExciseProductCodesService,
                                                        view: PrevalidateExciseProductCodeView
                                           ) extends BasePrevalidateNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, idx: Index, mode: Mode): Action[AnyContent] =
    (authorisedWithData(ern) andThen requireData).async { implicit request =>
      validateIndexAsync(idx) {
        exciseProductCodesService.getExciseProductCodes().flatMap { exciseProductCodes =>
          renderView(Ok, formProvider(exciseProductCodes), idx, exciseProductCodes, mode)
        }
      }
    }

  def onSubmit(ern: String, idx: Index, mode: Mode): Action[AnyContent] =
    (authorisedWithData(ern) andThen requireData).async { implicit request =>
      validateIndexAsync(idx) {
        exciseProductCodesService.getExciseProductCodes().flatMap { exciseProductCodes =>
          formProvider(exciseProductCodes).bindFromRequest().fold(
            renderView(BadRequest, _, idx, exciseProductCodes, mode),
            epc =>
              saveAndRedirect(PrevalidateEPCPage(idx), exciseProductCodes.find(_.code == epc).get, mode)
          )
        }
      }
    }

  override def validateIndexAsync(idx: Index)(f: => Future[Result])(implicit request: UserAnswersRequest[_]): Future[Result] =
    validateIndexForJourneyEntry(PrevalidateTraderEPCCount, idx, PrevalidateAddedProductCodesPage.MAX)(
      onSuccess = f,
      onFailure = Future.successful(Redirect(routes.PrevalidateTraderStartController.onPageLoad(request.ern)))
    )

  private def renderView(status: Status, form: Form[_], idx: Index, exciseProductCodes: Seq[ExciseProductCode], mode: Mode)
                        (implicit request: UserAnswersRequest[_]): Future[Result] = {
    val existingAnswer = request.userAnswers.get(PrevalidateEPCPage(idx))
    val codesToHide = request.userAnswers.get(PrevalidateTraderAddedValues).toList.flatten.filterNot(existingAnswer.map(_.code).contains)
    val selectItems = SelectItemHelper.constructSelectItems(
      selectOptions = exciseProductCodes.filterNot(code => codesToHide.contains(code.code)),
      defaultTextMessageKey = Some("prevalidateTrader.exciseProductCode.select.defaultValue"),
      existingAnswer = existingAnswer.map(_.code),
      withEpcDescription = true
    )
    Future.successful(status(view(
      form = form,
      action = routes.PrevalidateExciseProductCodeController.onSubmit(request.ern, idx, mode),
      selectOptions = selectItems,
      indexOfDocument = idx
    )))
  }
}
