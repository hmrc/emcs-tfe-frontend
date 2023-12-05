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

import play.api.http.Status.OK
import play.api.libs.json.Reads
import uk.gov.hmrc.emcstfefrontend.connectors.BaseConnectorUtils
import uk.gov.hmrc.emcstfefrontend.models.MemberState
import uk.gov.hmrc.emcstfefrontend.models.response.{ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

trait GetMemberStatesHttpParser extends BaseConnectorUtils[Seq[MemberState]] {

  implicit val reads: Reads[Seq[MemberState]] = Reads.seq(MemberState.format)
  def http: HttpClient

  class GetMemberStatesReads() extends HttpReads[Either[ErrorResponse, Seq[MemberState]]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, Seq[MemberState]] = {
      response.status match {
        case OK =>
          response.validateJson match {
            case Some(valid: Seq[MemberState]) if valid.isDefinedAt(0) => Right(valid)
            case _ =>
              logger.warn(s"[read] Bad JSON response from emcs-tfe-reference-data")
              Left(JsonValidationError)
          }
        case status =>
          logger.warn(s"[read] Unexpected status from emcs-tfe-reference-data: $status")
          Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  def get(url: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Seq[MemberState]]] =
    http.GET[Either[ErrorResponse, Seq[MemberState]]](url = url)(new GetMemberStatesReads(), hc, ec)

}
