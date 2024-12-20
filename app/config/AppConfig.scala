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

import controllers.routes
import featureswitch.core.config._
import models.MovementFilterUndischargedOption.Undischarged
import models.MovementListSearchOptions
import models.draftMovements.GetDraftMovementsSearchOptions
import models.messages.MessagesSearchOptions
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}
import scala.concurrent.duration.Duration

// scalastyle:off
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

  def emcsTfeViewAllTemplatesUrl(ern: String): String = controllers.draftTemplates.routes.ViewAllTemplatesController.onPageLoad(ern, None).url

  def emcsTfeDraftMovementsUrl(ern: String): String = controllers.drafts.routes.ViewAllDraftMovementsController.onPageLoad(ern, GetDraftMovementsSearchOptions()).url

  def emcsTfeMessagesUrl(ern: String): String = controllers.messages.routes.ViewAllMessagesController.onPageLoad(ern, MessagesSearchOptions()).url

  def emcsTfeUndischargedMovementsUrl(ern: String): String =
    routes.ViewAllMovementsController.onPageLoad(ern, MovementListSearchOptions(undischargedMovements = Some(Undischarged))).url

  def getFeatureSwitchValue(feature: String): Boolean = configuration.get[Boolean](feature)

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

  def emcsTfeCreateMovementTaskListUrl(ern: String, draftId: String): String =
    servicesConfig.getString("urls.emcsTfeCreateMovement") + s"/trader/$ern/draft/$draftId/draft-movement"

  def emcsTfeChangeDraftDeferredMovementUrl(ern: String, draftId: String): String =
    servicesConfig.getString("urls.emcsTfeCreateMovement") + s"/trader/$ern/draft/$draftId/info/deferred-movement/change"

  def europaCheckLink: String =
    servicesConfig.getString("urls.europaCheckLink")

  def signUpBetaFormUrl: String = configuration.get[String]("urls.signupBetaForm")

  def businessTaxAccountUrl: String = configuration.get[String]("urls.businessTaxAccount")

  def traderKnownFactsBaseUrl: String =
    servicesConfig.baseUrl("emcs-tfe") + "/emcs-tfe/trader-known-facts"

  lazy val tradeTariffCommoditiesUrl: String = configuration.get[String]("urls.tradeTariffCommodities")

  def getUrlForCommodityCode(code: String): String = s"$tradeTariffCommoditiesUrl/${code}00"

  def messageStatisticsCacheTtl: Duration = Duration(configuration.get[String]("mongodb.messageStatistics.TTL"))

  def messageStatisticsReplaceIndexes(): Boolean = configuration.get[Boolean]("mongodb.messageStatistics.replaceIndexes")

  def messagesCacheTtl: Duration = Duration(configuration.get[String]("mongodb.messages.TTL"))

  def messagesReplaceIndexes(): Boolean = configuration.get[Boolean]("mongodb.messages.replaceIndexes")

  def prevalidateTraderUserAnswersCacheTtl: Duration = Duration(configuration.get[String]("mongodb.prevalidateTraderUserAnswers.TTL"))

  def prevalidateTraderUserAnswersReplaceIndexes: Boolean = configuration.get[Boolean]("mongodb.prevalidateTraderUserAnswers.replaceIndexes")

  def accountHomeBanner: Boolean = isEnabled(AccountHomeBanner)

  def templatesLinkVisible: Boolean = isEnabled(TemplatesLink)

  def maxTemplates: Int = configuration.get[String]("constants.maxTemplates").toInt
}
