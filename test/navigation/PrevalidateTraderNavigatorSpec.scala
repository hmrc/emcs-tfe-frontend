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

import base.SpecBase
import models.{CheckMode, NormalMode}
import pages.Page
import pages.prevalidateTrader._

class PrevalidateTraderNavigatorSpec extends SpecBase {

  val navigator = new PrevalidateTraderNavigator

  "in Normal mode" when {

    "unknown page" must {

      "go from a page that doesn't exist in the route map to AddToList" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          // TODO: update route
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }
    }

    "PrevalidateConsigneeTraderIdentificationPage" must {

      "go to PrevalidateAddExciseProductCodePage" in {

        val userAnswers = emptyUserAnswers.set(PrevalidateConsigneeTraderIdentificationPage, "AB123456")

        navigator.nextPage(PrevalidateConsigneeTraderIdentificationPage, NormalMode, userAnswers) mustBe
          // TODO: update route
          testOnly.controllers.routes.UnderConstructionController.onPageLoad()
      }
    }
  }

  "in Check mode" must {
    "go to PrevalidateAddToListPage" in {
      case object UnknownPage extends Page
      navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
        // TODO: update route
        testOnly.controllers.routes.UnderConstructionController.onPageLoad()
    }
  }
}
