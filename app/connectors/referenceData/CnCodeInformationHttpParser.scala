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

package connectors.referenceData

import connectors.BaseConnectorUtils
import models.requests.CnCodeInformationRequest
import models.response.referenceData.CnCodeInformationResponse
import models.response.{ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.Status.OK
import play.api.libs.json.{Reads, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

trait CnCodeInformationHttpParser extends BaseConnectorUtils[CnCodeInformationResponse] {

  implicit val reads: Reads[CnCodeInformationResponse] = CnCodeInformationResponse.reads

  def http: HttpClient

  implicit object CnCodeInformationReads extends HttpReads[Either[ErrorResponse, CnCodeInformationResponse]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, CnCodeInformationResponse] = {
      response.status match {
        case OK =>
          response.validateJson match {
            case Some(valid) => Right(valid)
            case None =>
              logger.warn(s"[read] Bad JSON response from emcs-tfe-reference-data")
              Left(JsonValidationError)
          }
        case status =>
          logger.warn(s"[read] Unexpected status from emcs-tfe-reference-data: $status")
          Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  def post(url: String, body: CnCodeInformationRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext, writes: Writes[CnCodeInformationRequest]): Future[Either[ErrorResponse, CnCodeInformationResponse]] =
    http.POST[CnCodeInformationRequest, Either[ErrorResponse, CnCodeInformationResponse]](url, body)(writes, CnCodeInformationReads, hc, ec)
}
