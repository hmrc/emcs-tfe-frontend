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
import models.requests.PrevalidateTraderRequest
import models.response.emcsTfe.prevalidateTrader.PreValidateTraderApiResponse
import models.response.{ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.libs.json.{JsResultException, Reads}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PrevalidateTraderConnector @Inject()(val http: HttpClient, config: AppConfig) extends EmcsTfeHttpParser[PreValidateTraderApiResponse] {

  override implicit val reads: Reads[PreValidateTraderApiResponse] = PreValidateTraderApiResponse.format

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def prevalidateTrader(ern: String, requestModel: PrevalidateTraderRequest)
                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, PreValidateTraderApiResponse]] = {
    def url: String = s"$baseUrl/pre-validate-trader/$ern"

    post(url, requestModel).recover {
      case JsResultException(errors) =>
        logger.warn(s"[prevalidateTrader] Bad JSON response from emcs-tfe: " + errors)
        Left(JsonValidationError)
      case error =>
        logger.warn(s"[prevalidateTrader] Unexpected error from emcs-tfe: ${error.getClass} ${error.getMessage}")
        Left(UnexpectedDownstreamResponseError)
      }
  }
}
