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

package config

import base.SpecBase
import featureswitch.core.config.FeatureSwitching
import fixtures.BaseFixtures

class AppConfigSpec
  extends SpecBase
    with FeatureSwitching
    with BaseFixtures {

  lazy val config = app.injector.instanceOf[AppConfig]

  "AppConfig" when {

    ".deskproName must be emcstfe" in {
      config.deskproName mustBe "emcstfe"
    }

    ".feedbackFrontendSurveyUrl() must handoff to feedback frontend with the correct URL" in {
      config.feedbackFrontendSurveyUrl mustBe s"http://localhost:9514/feedback/${config.deskproName}"
    }

    ".emcsTfeBaseUrl() must return correct URL" in {
      config.emcsTfeBaseUrl mustBe s"http://localhost:8311/emcs-tfe"
    }

    ".emcsTfeHomeUrl" must {

      "return to home" in {
        config.emcsTfeHomeUrl mustBe controllers.routes.IndexController.exciseNumber().url
      }
    }

    ".traderKnownFactsBaseUrl" must {
        "return to the correct URL" in {
          config.traderKnownFactsBaseUrl mustBe s"http://localhost:8311/emcs-tfe/trader-known-facts"
      }
    }

    ".emcsTfeCreateMovementChangeDeferredMovementUrl" must {
      "return to the correct URL" in {
        config.emcsTfeChangeDraftDeferredMovementUrl(testErn, testDraftId) mustBe
          s"http://localhost:8314/emcs/create-movement/trader/$testErn/draft/$testDraftId/info/deferred-movement/change"
      }
    }
  }
}
