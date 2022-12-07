/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.mocks.config

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfefrontend.config.AppConfig

trait MockAppConfig extends MockFactory {
  val mockAppConfig: AppConfig = mock[AppConfig]

  object MockedAppConfig {
    // MTD ID Lookup Config
    def referenceDataBaseUrl: CallHandler[String] = (mockAppConfig.referenceDataBaseUrl _: () => String).expects()
    def emcsTfeBaseUrl: CallHandler[String] = (mockAppConfig.emcsTfeBaseUrl _: () => String).expects()
    def getReferenceDataStubFeatureSwitch: CallHandler[Boolean] = (mockAppConfig.getReferenceDataStubFeatureSwitch _: () => Boolean).expects()
  }

}
