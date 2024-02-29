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

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.mvc.RequestHeader
import controllers.routes
import featureswitch.core.config._
import models.MovementFilterUndischargedOption.Undischarged
import models.MovementListSearchOptions
import models.messages.MessagesSearchOptions
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.net.URLEncoder
import scala.annotation.unused
import scala.concurrent.duration.Duration

@Singleton
class AppConfig @Inject()(servicesConfig: ServicesConfig, configuration: Configuration) extends FeatureSwitching {

  override val config: AppConfig = this

  lazy val host: String = configuration.get[String]("host")
  lazy val deskproName: String = configuration.get[String]("deskproName")

  lazy val timeout: Int = configuration.get[Int]("timeout-dialog.timeout")
  lazy val countdown: Int = configuration.get[Int]("timeout-dialog.countdown")
  lazy val signOutUrl: String = configuration.get[String]("urls.signOut")

  lazy val emcsTfeHomeUrl: String = routes.IndexController.exciseNumber().url

  lazy val exciseHelplineUrl: String = configuration.get[String]("urls.exciseHelpline")

  lazy val recoverableErrorCodes: Seq[String] = configuration.get[Seq[String]]("messages.recoverableErrorCodes")

  def emcsTfeListMovementsUrl(ern: String): String = routes.ViewAllMovementsController.onPageLoad(ern, MovementListSearchOptions()).url

  // TODO: update with new URL when MOV01 filters are created
  def emcsTfeDraftMovementsUrl(@unused ern: String): String = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url

  def emcsTfeMessagesUrl(ern: String): String = controllers.messages.routes.ViewAllMessagesController.onPageLoad(ern, MessagesSearchOptions()).url

  def emcsTfeUndischargedMovementsUrl(ern: String): String =
    routes.ViewAllMovementsController.onPageLoad(ern, MovementListSearchOptions( undischargedMovements = Some(Undischarged) )).url

  private lazy val contactHost = configuration.get[String]("contact-frontend.host")

  def getFeatureSwitchValue(feature: String): Boolean = configuration.get[Boolean](feature)

  def betaBannerFeedbackUrl(implicit request: RequestHeader): String =
    s"$contactHost/contact/beta-feedback?service=$deskproName&backUrl=${URLEncoder.encode(host + request.uri, "UTF-8")}"

  private lazy val feedbackFrontendHost: String = configuration.get[String]("feedback-frontend.host")

  lazy val feedbackFrontendSurveyUrl: String = s"$feedbackFrontendHost/feedback/$deskproName"

  private def emcsTfeService: String = servicesConfig.baseUrl("emcs-tfe")

  def emcsTfeBaseUrl: String = s"$emcsTfeService/emcs-tfe"

  def referenceDataBaseUrl: String = servicesConfig.baseUrl("emcs-tfe-reference-data") + "/emcs-tfe-reference-data"

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

  def europaCheckLink: String =
    servicesConfig.getString("urls.europaCheckLink")

  def signUpBetaFormUrl: String = configuration.get[String]("urls.signupBetaForm")

  private def traderKnownFactsReferenceDataService: String =
    if (isEnabled(StubGetTraderKnownFacts)) {
      servicesConfig.baseUrl("emcs-tfe-reference-data-stub")
    } else {
      servicesConfig.baseUrl("emcs-tfe-reference-data")
    }

  def traderKnownFactsReferenceDataBaseUrl: String = s"$traderKnownFactsReferenceDataService/emcs-tfe-reference-data"

  lazy val tradeTariffCommoditiesUrl: String = configuration.get[String]("urls.tradeTariffCommodities")
  def getUrlForCommodityCode(code: String): String = s"$tradeTariffCommoditiesUrl/${code}00"

  def denyDutyPaidUsers: Boolean = isEnabled(DenyDutyPaidUsers)

  def messagesCacheTtl: Duration = Duration(configuration.get[String]("mongodb.messages.TTL"))
  def messagesReplaceIndexes(): Boolean = configuration.get[Boolean]("mongodb.messages.replaceIndexes")

  def betaAllowListCheckingEnabled: Boolean = isEnabled(CheckBetaAllowList)

  def betaCheckServiceName: String = configuration.get[String]("beta.serviceName")
}
