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

  def welshLanguageSupportEnabled: Boolean
}

@Singleton
class AppConfigImpl @Inject()(val servicesConfig: ServicesConfig, val config: Configuration) extends AppConfig {

  private lazy val referenceDataService: String = servicesConfig.baseUrl("reference-data")
  lazy val referenceDataBaseUrl: String = s"$referenceDataService/emcs-tfe-reference-data"

  lazy val welshLanguageSupportEnabled: Boolean = config.getOptional[Boolean]("features.welsh-language-support").getOrElse(false)

}
