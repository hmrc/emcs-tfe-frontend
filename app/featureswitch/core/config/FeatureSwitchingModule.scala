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

import featureswitch.core.models.FeatureSwitch
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}

import javax.inject.Singleton

@Singleton
class FeatureSwitchingModule extends Module with FeatureSwitchRegistry {

  val switches: Seq[FeatureSwitch] = Seq(StubGetTraderKnownFacts, ReturnToLegacy, DenyDutyPaidUsers)

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    Seq(
      bind[FeatureSwitchRegistry].to(this).eagerly()
    )
  }
}

case object StubGetTraderKnownFacts extends FeatureSwitch {
  override val configName: String = "features.stub-get-trader-known-facts"
  override val displayName: String = "Use stub to get trader known facts"
}

case object ReturnToLegacy extends FeatureSwitch {
  override val configName: String = "features.returnToLegacy"
  override val displayName: String = "Return the User to the Legacy EMCS service"
}

case object DenyDutyPaidUsers extends FeatureSwitch {
  override val configName: String = "features.denyDutyPaidUsers"
  override val displayName: String = "Deny access to duty paid users"
}
