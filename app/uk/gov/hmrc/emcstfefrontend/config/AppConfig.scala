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

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class AppConfig @Inject()(servicesConfig: ServicesConfig, config: Configuration) {

  lazy val host: String = config.get[String]("host")

  lazy val deskproName: String = config.get[String]("deskproName")

  private lazy val contactHost = config.get[String]("contact-frontend.host")

  def betaBannerFeedbackUrl(implicit request: RequestHeader): String =
    s"$contactHost/contact/beta-feedback?service=$deskproName&backUrl=${SafeRedirectUrl(host + request.uri).encodedUrl}"

  private lazy val feedbackFrontendHost: String = config.get[String]("feedback-frontend.host")

  lazy val feedbackFrontendSurveyUrl: String = s"$feedbackFrontendHost/feedback/$deskproName"

  private def emcsTfeService: String = servicesConfig.baseUrl("emcs-tfe")

  def emcsTfeBaseUrl: String = s"$emcsTfeService/emcs-tfe"

  def referenceDataBaseUrl: String = servicesConfig.baseUrl("emcs-tfe-reference-data") + "/emcs-tfe-reference-data"

  def welshLanguageSupportEnabled: Boolean = config.getOptional[Boolean]("features.welsh-language-support").getOrElse(false)

  def loginUrl: String = servicesConfig.getString("urls.login")

  def loginContinueUrl: String = servicesConfig.getString("urls.loginContinue")

  def emcsTfeReportAReceiptUrl(ern: String, arc: String): String =
    servicesConfig.getString("urls.emcsTfeReportAReceipt") + s"/trader/$ern/movement/$arc"

  def emcsTfeExplainDelayUrl(ern: String, arc: String): String =
    servicesConfig.getString("urls.emcsTfeExplainDelay") + s"/trader/$ern/movement/$arc"

  def emcsTfeExplainShortageOrExcessUrl(ern: String, arc: String): String =
    servicesConfig.getString("urls.emcsTfeExplainShortageOrExcess") + s"/trader/$ern/movement/$arc"

  def emcsTfeCancelMovementUrl(ern: String, arc: String): String =
    servicesConfig.getString("urls.emcsTfeCancelMovement") + s"/trader/$ern/movement/$arc"

  def emcsTfeChangeDestinationUrl(ern: String, arc: String): String =
    servicesConfig.getString("urls.emcsTfeChangeDestination") + s"/trader/$ern/movement/$arc"

  def emcsTfeAlertOrRejectionUrl(ern: String, arc: String): String =
    servicesConfig.getString("urls.emcsTfeAlertRejection") + s"/trader/$ern/movement/$arc"

  def emcsTfeCreateMovementUrl(ern: String): String =
    servicesConfig.getString("urls.emcsTfeCreateMovement") + s"/trader/$ern"
}
