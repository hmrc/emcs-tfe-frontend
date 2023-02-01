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

import com.google.inject.ImplementedBy
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@ImplementedBy(classOf[AppConfigImpl])
trait AppConfig {
  def referenceDataBaseUrl: String
  def emcsTfeBaseUrl: String

  def welshLanguageSupportEnabled: Boolean
  def getReferenceDataStubFeatureSwitch(): Boolean

  def loginUrl: String
  def loginContinueUrl: String
}

@Singleton
class AppConfigImpl @Inject()(val servicesConfig: ServicesConfig, val config: Configuration) extends AppConfig {

  private def referenceDataService: String = servicesConfig.baseUrl("reference-data")
  def referenceDataBaseUrl: String = s"$referenceDataService/emcs-tfe-reference-data"

  private def emcsTfeService: String = servicesConfig.baseUrl("emcs-tfe")
  def emcsTfeBaseUrl: String = s"$emcsTfeService/emcs-tfe"

  def welshLanguageSupportEnabled: Boolean = config.getOptional[Boolean]("features.welsh-language-support").getOrElse(false)

  def getReferenceDataStubFeatureSwitch(): Boolean = servicesConfig.getBoolean("feature-switch.enable-reference-data-stub-source")

  def loginUrl: String = servicesConfig.getString("urls.login")

  def loginContinueUrl: String = servicesConfig.getString("urls.loginContinue")
}
