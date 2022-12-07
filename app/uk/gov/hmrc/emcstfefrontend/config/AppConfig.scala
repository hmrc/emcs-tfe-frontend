/*
 * Copyright 2022 HM Revenue & Customs
 *
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
}

@Singleton
class AppConfigImpl @Inject()(val servicesConfig: ServicesConfig, val config: Configuration) extends AppConfig {

  private def referenceDataService: String = servicesConfig.baseUrl("reference-data")
  def referenceDataBaseUrl: String = s"$referenceDataService/emcs-tfe-reference-data"

  private def emcsTfeService: String = servicesConfig.baseUrl("emcs-tfe")
  def emcsTfeBaseUrl: String = s"$emcsTfeService/emcs-tfe"

  def welshLanguageSupportEnabled: Boolean = config.getOptional[Boolean]("features.welsh-language-support").getOrElse(false)

  def getReferenceDataStubFeatureSwitch(): Boolean = servicesConfig.getBoolean("feature-switch.enable-reference-data-stub-source")
}
