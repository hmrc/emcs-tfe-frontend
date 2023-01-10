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
