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

package navigation

import controllers.prevalidateTrader.routes
import models.{CheckMode, Index, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.prevalidateTrader.{PrevalidateAddToListPage, PrevalidateConsigneeTraderIdentificationPage, PrevalidateEPCPage}
import play.api.mvc.Call
import queries.PrevalidateTraderEPCCount

import javax.inject.Inject

class PrevalidateTraderNavigator @Inject() extends BaseNavigator {

  private[navigation] val normalRoutes: Page => UserAnswers => Call = {

    case PrevalidateConsigneeTraderIdentificationPage => (userAnswers: UserAnswers) =>
      userAnswers.get(PrevalidateTraderEPCCount) match {
        case None | Some(0) =>
          routes.PrevalidateExciseProductCodeController.onPageLoad(userAnswers.ern, Index(0), NormalMode)
        case _ =>
          routes.PrevalidateAddToListController.onPageLoad(userAnswers.ern)
      }

    case PrevalidateEPCPage(_) => (userAnswers: UserAnswers) =>
      routes.PrevalidateAddToListController.onPageLoad(userAnswers.ern)

    case PrevalidateAddToListPage => (userAnswers: UserAnswers) =>
      userAnswers.get(PrevalidateAddToListPage) match {
        case Some(true) =>
          routes.PrevalidateExciseProductCodeController.onPageLoad(userAnswers.ern, Index(userAnswers.get(PrevalidateTraderEPCCount).getOrElse(0)), NormalMode)
        case _ =>
          routes.PrevalidateTraderResultsController.onPageLoad(userAnswers.ern)
      }

    case _ => (userAnswers: UserAnswers) =>
      routes.PrevalidateTraderStartController.onPageLoad(userAnswers.ern)
  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case _ => (userAnswers: UserAnswers) =>
      routes.PrevalidateAddToListController.onPageLoad(userAnswers.ern)
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}
