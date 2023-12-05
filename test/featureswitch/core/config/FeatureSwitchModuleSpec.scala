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

package featureswitch.core.config

import base.SpecBase
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder
class FeatureSwitchModuleSpec extends SpecBase {

  object TestFeatureSwitchRegistry extends FeatureSwitchingModule()

  "FeatureSwitchRegistry" when {

    "contain the feature switches for the Business Verification and Companies House stubs" in {

      TestFeatureSwitchRegistry.switches mustBe Seq(StubGetTraderKnownFacts, ReturnToLegacy)
    }

    "asdfdsa" in {

      val test = Configuration("blah" -> "blah")
      val environment = new GuiceApplicationBuilder().environment

      val stuff = TestFeatureSwitchRegistry.bindings(environment, test)

      val aMagicKey = stuff.head.key



      println(aMagicKey)

      assert(true)

    }

  }
}