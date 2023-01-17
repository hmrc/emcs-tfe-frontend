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

package uk.gov.hmrc.emcstfefrontend.connectors.referenceData

import play.api.libs.json.Reads
import uk.gov.hmrc.emcstfefrontend.config.AppConfig
import uk.gov.hmrc.emcstfefrontend.models.response.referenceData.ModeOfTransportListModel
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetOtherReferenceDataListConnector @Inject()(val http: HttpClient,
                                                   config: AppConfig) extends ReferenceDataHttpParser[ModeOfTransportListModel] {

  lazy val baseUrl: String = config.referenceDataBaseUrl

  override implicit val reads: Reads[ModeOfTransportListModel] = ModeOfTransportListModel.format

  def getOtherReferenceDataList()(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[ModeOfTransportListModel] = {
    def getOtherReferenceDataListUrl(): String = s"$baseUrl/other-reference-data-list"

    get(getOtherReferenceDataListUrl())
  }

}
