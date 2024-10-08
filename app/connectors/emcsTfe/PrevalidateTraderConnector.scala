/*
 * Copyright 2024 HM Revenue & Customs
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
import models.requests.PrevalidateTraderRequest
import models.response.emcsTfe.prevalidateTrader.PreValidateTraderResponse
import models.response.{ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.libs.json.{JsResultException, Reads}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PrevalidateTraderConnector @Inject()(val http: HttpClientV2, config: AppConfig) extends EmcsTfeHttpParser[PreValidateTraderResponse] {

  override implicit val reads: Reads[PreValidateTraderResponse] = PreValidateTraderResponse.format

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def prevalidateTrader(ern: String, requestModel: PrevalidateTraderRequest)
                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, PreValidateTraderResponse]] = {
    def url: String = s"$baseUrl/pre-validate-trader/$ern"

    post(url, requestModel).recover {
      case JsResultException(errors) =>
        logger.warn(s"[prevalidateTrader][$ern] Bad JSON response from emcs-tfe: " + errors)
        Left(JsonValidationError)
      case error =>
        logger.warn(s"[prevalidateTrader][$ern] Unexpected error from emcs-tfe: ${error.getClass} ${error.getMessage}")
        Left(UnexpectedDownstreamResponseError)
      }
  }
}
