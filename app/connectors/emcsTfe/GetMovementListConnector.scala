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

package connectors.emcsTfe

import config.AppConfig
import models.MovementListSearchOptions
import models.response.ErrorResponse
import models.response.emcsTfe.GetMovementListResponse
import play.api.libs.json.Reads
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetMovementListConnector @Inject()(val http: HttpClientV2,
                                         config: AppConfig) extends EmcsTfeHttpParser[GetMovementListResponse] {

  override implicit val reads: Reads[GetMovementListResponse] = GetMovementListResponse.reads

  lazy val baseUrl: String = config.emcsTfeBaseUrl
  def getMovementList(exciseRegistrationNumber: String, search: Option[MovementListSearchOptions] = None)
                     (implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[ErrorResponse, GetMovementListResponse]] =
    get(s"$baseUrl/movements/$exciseRegistrationNumber", search.map(_.queryParams).getOrElse(Seq.empty))

}
