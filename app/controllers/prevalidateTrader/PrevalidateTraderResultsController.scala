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
import models.requests.UserAnswersRequest
import models.{Index, NormalMode}
import navigation.PrevalidateTraderNavigator
import pages.prevalidateTrader.PrevalidateAddedProductCodesPage
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import queries.PrevalidateTraderEPCCount
import services.{PrevalidateTraderService, PrevalidateTraderUserAnswersService}
import views.html.prevalidateTrader.PrevalidateTraderResultsView

import javax.inject.Inject


class PrevalidateTraderResultsController @Inject()(
                                                    override val messagesApi: MessagesApi,
                                                    override val userAnswersService: PrevalidateTraderUserAnswersService,
                                                    override val betaAllowList: BetaAllowListAction,
                                                    override val navigator: PrevalidateTraderNavigator,
                                                    override val auth: AuthAction,
                                                    override val getData: DataRetrievalAction,
                                                    val prevalidateTraderService: PrevalidateTraderService,
                                                    val requireData: PrevalidateTraderDataRetrievalAction,
                                                    val controllerComponents: MessagesControllerComponents,
                                                    val view: PrevalidateTraderResultsView
                                                  ) extends BaseNavigationController with AuthActionHelper {
  def onPageLoad(ern: String): Action[AnyContent] =
    (authorisedWithData(ern) andThen requireData).async { implicit request =>

      val addItemCall = onMax(
        routes.PrevalidateAddToListController.onPageLoad(request.ern),
        routes.PrevalidateExciseProductCodeController.onPageLoad(request.ern, Index(request.userAnswers.get(PrevalidateTraderEPCCount).getOrElse(0)), NormalMode)
      )

      prevalidateTraderService.prevalidate(request.ern).map { prevalidateResult =>
        Ok(view(
          ernOpt = None,
          addCodeCall = addItemCall,
          approved = Seq.empty,
          notApproved = Seq.empty
        ))
      }
    }

  private def onMax[T](maxF: => T, notMaxF: => T)(implicit request: UserAnswersRequest[_]): T =
    request.userAnswers.get(PrevalidateTraderEPCCount) match {
      case Some(value) if value >= PrevalidateAddedProductCodesPage.MAX => maxF
      case _ => notMaxF
    }
}
