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

package uk.gov.hmrc.emcstfefrontend.services

import javax.inject.{Inject, Singleton}
import play.api.Logger
import uk.gov.hmrc.emcstfefrontend.config.AppConfig
import uk.gov.hmrc.emcstfefrontend.connectors.ReferenceDataConnector
import uk.gov.hmrc.emcstfefrontend.models.response.{ModeOfTransportErrorResponse, ModeOfTransportListModel, ModeOfTransportListResponseModel, ModeOfTransportModel}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ModeOfTransportService @Inject()(referenceDataConnector: ReferenceDataConnector, config: AppConfig) {
  lazy val logger: Logger = Logger(this.getClass)

  def getOtherDataReferenceList(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[ModeOfTransportListResponseModel] = {
    if (config.getReferenceDataStubFeatureSwitch()) {
      referenceDataConnector.getOtherReferenceDataList().map {
        case success: ModeOfTransportListModel =>
          success
        case error: ModeOfTransportErrorResponse =>
          logger.error(s"[ModeOfTransportService][getOtherDataReferenceList] - Retrieved Other Data Reference List:\n\n$error")
          error
      }
    } else {
      Future.successful(ModeOfTransportListModel(List(ModeOfTransportModel("TRANSPORTMODE", "999", "hard coded response" ))))
    }
  }
}
