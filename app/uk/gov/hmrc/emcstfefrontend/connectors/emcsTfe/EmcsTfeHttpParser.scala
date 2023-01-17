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

package uk.gov.hmrc.emcstfefrontend.connectors.emcsTfe

import play.api.http.Status.OK
import uk.gov.hmrc.emcstfefrontend.connectors.BaseConnectorUtils
import uk.gov.hmrc.emcstfefrontend.models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

trait EmcsTfeHttpParser[A] extends BaseConnectorUtils[A] {

  def http: HttpClient

  implicit object EmcsTfeReads extends HttpReads[A] {
    override def read(method: String, url: String, response: HttpResponse): A = {
      response.status match {
        case OK => response.validateJson match {
          case Some(valid) => valid
          case None =>
            logger.warn(s"[EmcsTfeHttpParser][read] Bad JSON response from emcs-tfe")
            throw new Exception(s"[EmcsTfeHttpParser][read] ${JsonValidationError.message}")
        }
        case status =>
          logger.warn(s"[EmcsTfeHttpParser][read] Unexpected status from emcs-tfe: $status")
          throw new Exception(s"[EmcsTfeHttpParser][read] ${UnexpectedDownstreamResponseError.message}")
      }
    }
  }

  def get(url: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] = http.GET[A](url)(EmcsTfeReads, hc, ec)
}
