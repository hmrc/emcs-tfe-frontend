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
import pages.prevalidateTrader.{PrevalidateConsigneeTraderIdentificationPage, PrevalidateEPCPage}
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
          //TODO: Update to route to Add to List page when built PVT-04
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }

    case PrevalidateEPCPage(idx) => (userAnswers: UserAnswers) =>
      //TODO: Update to route to Add to List page when built PVT-04
      testOnly.controllers.routes.UnderConstructionController.onPageLoad()

    case _ => (userAnswers: UserAnswers) =>
      routes.PrevalidateTraderStartController.onPageLoad(userAnswers.ern)
  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case _ => (userAnswers: UserAnswers) =>
      //TODO: Update to route to Add to List page when built PVT-04
      testOnly.controllers.routes.UnderConstructionController.onPageLoad()
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}
