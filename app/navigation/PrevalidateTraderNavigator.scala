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

import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.prevalidateTrader.PrevalidateConsigneeTraderIdentificationPage
import play.api.mvc.Call

import javax.inject.Inject

class PrevalidateTraderNavigator @Inject() extends BaseNavigator {

  private[navigation] val normalRoutes: Page => UserAnswers => Call = {
    case PrevalidateConsigneeTraderIdentificationPage => (userAnswers: UserAnswers) =>
      testOnly.controllers.routes.UnderConstructionController.onPageLoad()
    case _ => (userAnswers: UserAnswers) =>
      //TODO: Route to Add to List page
      testOnly.controllers.routes.UnderConstructionController.onPageLoad()
  }

  private[navigation] val checkRouteMap: Page => UserAnswers => Call = {
    case _ => (userAnswers: UserAnswers) =>
      //TODO: Route to Add to List pages
      testOnly.controllers.routes.UnderConstructionController.onPageLoad()
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}
