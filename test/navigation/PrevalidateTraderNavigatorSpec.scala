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

package navigation

import base.SpecBase
import controllers.prevalidateTrader.routes
import fixtures.ExciseProductCodeFixtures
import models.prevalidate.PrevalidateTraderModel
import models.{CheckMode, NormalMode}
import pages.Page
import pages.prevalidateTrader.{PrevalidateAddToListPage, PrevalidateConsigneeTraderIdentificationPage, PrevalidateEPCPage}

class PrevalidateTraderNavigatorSpec extends SpecBase with ExciseProductCodeFixtures {

  val navigator = new PrevalidateTraderNavigator

  "in Normal mode" when {

    "unknown page" must {

      "go from a page that doesn't exist to the Prevalidate Start Page" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          routes.PrevalidateTraderStartController.onPageLoad(testErn)
      }
    }

    "PrevalidateConsigneeTraderIdentificationPage" must {

      "when no EPCs have been added yet" must {

        "go to PrevalidateAddExciseProductCodePage" in {

          val userAnswers = emptyUserAnswers.set(PrevalidateConsigneeTraderIdentificationPage, PrevalidateTraderModel(ern = "AB123456", entityGroup = testEntityGroup))

          navigator.nextPage(PrevalidateConsigneeTraderIdentificationPage, NormalMode, userAnswers) mustBe
            routes.PrevalidateExciseProductCodeController.onPageLoad(testErn, testIndex1, NormalMode)
        }
      }

      "when at least one EPC code has been added" must {

        "go to PrevalidateAddToListPage" in {

          val userAnswers = emptyUserAnswers
            .set(PrevalidateConsigneeTraderIdentificationPage, PrevalidateTraderModel(ern = "AB123456", entityGroup = testEntityGroup))
            .set(PrevalidateEPCPage(testIndex1), beerExciseProductCode)

          navigator.nextPage(PrevalidateConsigneeTraderIdentificationPage, NormalMode, userAnswers) mustBe
            routes.PrevalidateAddToListController.onPageLoad(testErn)
        }
      }

      "for the PrevalidateEPCPage" must {

        "go to the Add to List page" in {

          navigator.nextPage(PrevalidateEPCPage(testIndex1), NormalMode, emptyUserAnswers) mustBe
            routes.PrevalidateAddToListController.onPageLoad(testErn)
        }
      }

      "for the PrevalidateAddToListPage" when {

        "answer is `true`" must {

          "go to the next idx for the PrevalidateEPCPage" in {

            val userAnswers = emptyUserAnswers
              .set(PrevalidateAddToListPage, true)
              .set(PrevalidateEPCPage(testIndex1), beerExciseProductCode)

            navigator.nextPage(PrevalidateAddToListPage, NormalMode, userAnswers) mustBe
              routes.PrevalidateExciseProductCodeController.onPageLoad(testErn, testIndex2, NormalMode)
          }
        }

        "answer is `false`" must {

          "go to the result page PVT-05" in {

            val userAnswers = emptyUserAnswers
              .set(PrevalidateAddToListPage, false)
              .set(PrevalidateEPCPage(testIndex1), beerExciseProductCode)

            navigator.nextPage(PrevalidateAddToListPage, NormalMode, userAnswers) mustBe
              routes.PrevalidateTraderResultsController.onPageLoad(testErn)
          }
        }
      }
    }

    "in Check mode" must {

      "go from a page that doesn't exist in the edit route map to Add to List page" in {

        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
          routes.PrevalidateAddToListController.onPageLoad(testErn)
      }

      "for the PrevalidateEPCPage" must {

        "go to the Add to List page" in {

          navigator.nextPage(PrevalidateEPCPage(testIndex1), CheckMode, emptyUserAnswers) mustBe
            routes.PrevalidateAddToListController.onPageLoad(testErn)
        }
      }
    }
  }
}
