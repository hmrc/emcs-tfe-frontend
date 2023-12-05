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

import play.api.libs.json.JsResultException
import uk.gov.hmrc.emcstfefrontend.config.AppConfig
import uk.gov.hmrc.emcstfefrontend.models.ExciseProductCode
import uk.gov.hmrc.emcstfefrontend.models.response.{ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.emcstfefrontend.utils.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


trait GetExciseProductCodesConnector {
  def baseUrl: String

  def getExciseProductCodes()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Seq[ExciseProductCode]]]
}

@Singleton
class GetExciseProductCodesConnectorImpl @Inject()(val http: HttpClient,
                                               config: AppConfig) extends GetExciseProductCodesHttpParser with GetExciseProductCodesConnector with Logging {

  def baseUrl: String = config.referenceDataBaseUrl

  def getExciseProductCodes()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Seq[ExciseProductCode]]] = {
    get(baseUrl + "/oracle/epc-codes")
      .recover {
        case JsResultException(errors) =>
          logger.warn(s"[getExciseProductCodes] Bad JSON response from emcs-tfe-reference-data: " + errors)
          Left(JsonValidationError)
        case error =>
          logger.warn(s"[getExciseProductCodes] Unexpected error from reference-data: ${error.getClass} ${error.getMessage}")
          Left(UnexpectedDownstreamResponseError)
      }
  }

}
