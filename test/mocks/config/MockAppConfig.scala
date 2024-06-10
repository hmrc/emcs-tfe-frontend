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

package mocks.config

import config.AppConfig
import org.scalamock.handlers.{CallHandler, CallHandler0}
import org.scalamock.scalatest.MockFactory

trait MockAppConfig extends MockFactory {

  lazy val mockAppConfig: AppConfig = mock[AppConfig]

  object MockedAppConfig {
    def emcsTfeBaseUrl: CallHandler[String] = (() => mockAppConfig.emcsTfeBaseUrl).expects()
    def traderKnownFactsReferenceDataBaseUrl: CallHandler[String] = (() => mockAppConfig.traderKnownFactsReferenceDataBaseUrl).expects()
    def betaAllowListCheckingEnabled: CallHandler0[Boolean] = (() => mockAppConfig.betaAllowListCheckingEnabled).expects()
    def emcsLegacyMessageInboxUrl(ern: String): CallHandler0[String] = (() => mockAppConfig.emcsLegacyMessageInboxUrl(ern)).expects()

    def messageStatisticsNotificationEnabled: CallHandler0[Boolean] = (() => mockAppConfig.messageStatisticsNotificationEnabled).expects()
  }

}
