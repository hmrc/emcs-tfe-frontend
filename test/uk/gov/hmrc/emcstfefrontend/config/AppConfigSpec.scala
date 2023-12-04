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

package uk.gov.hmrc.emcstfefrontend.config

import uk.gov.hmrc.emcstfefrontend.featureswitch.core.config.{FeatureSwitching, ReturnToLegacy, StubGetTraderKnownFacts}
import uk.gov.hmrc.emcstfefrontend.fixtures.BaseFixtures
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec

class AppConfigSpec
  extends UnitSpec
    with FeatureSwitching
    with BaseFixtures {

  lazy val config = app.injector.instanceOf[AppConfig]

  "AppConfig" when {

    ".deskproName must be emcstfe" in {
      config.deskproName shouldBe "emcstfe"
    }

    ".feedbackFrontendSurveyUrl() must handoff to feedback frontend with the correct URL" in {
      config.feedbackFrontendSurveyUrl shouldBe s"http://localhost:9514/feedback/${config.deskproName}"
    }

    ".emcsTfeBaseUrl() must return correct URL" in {
      config.emcsTfeBaseUrl shouldBe s"http://localhost:8311/emcs-tfe"
    }

    ".emcsTfeHomeUrl()" when {

      "ReturnToLegacy is enabled" when {

        "an ERN is supplied" must {

          "return to the legacy URL including the ERN" in {
            enable(ReturnToLegacy)
            config.emcsTfeHomeUrl(Some(testErn)) shouldBe s"http://localhost:8080/emcs/trader/$testErn"
          }
        }

        "an ERN is NOT supplied" must {

          "return to the legacy URL without the ERN" in {
            enable(ReturnToLegacy)
            config.emcsTfeHomeUrl(None) shouldBe s"http://localhost:8080/emcs/trader"
          }
        }
      }

      "ReturnToLegacy is disabled" must {

        "return to the new URL" in {
          disable(ReturnToLegacy)
          config.emcsTfeHomeUrl(None) shouldBe s"http://localhost:8310/emcs-tfe"
        }
      }
    }

    ".traderKnownFactsReferenceDataBaseUrl" when {

      "StubGetTraderKnownFacts is enabled" must {

        "return to the legacy URL" in {
          enable(StubGetTraderKnownFacts)
          config.traderKnownFactsReferenceDataBaseUrl shouldBe s"http://localhost:8309/emcs-tfe-reference-data"
        }
      }

      "StubGetTraderKnownFacts is disabled" must {

        "return to the new URL" in {
          disable(StubGetTraderKnownFacts)
          config.traderKnownFactsReferenceDataBaseUrl shouldBe s"http://localhost:8312/emcs-tfe-reference-data"
        }
      }
    }
  }
}
