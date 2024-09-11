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

import models.response.{ErrorResponse, UnexpectedDownstreamResponseError}
import play.api.http.Status.NO_CONTENT
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}

trait DeleteDraftTemplateHttpParser {
  def http: HttpClientV2

  implicit object DeleteTemplateReads extends HttpReads[Either[ErrorResponse, Boolean]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, Boolean] = {
      response.status match {
        case NO_CONTENT => Right(true)
        case _          => Left(UnexpectedDownstreamResponseError)
      }
    }
  }
  def delete(url: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Boolean]] =
    http.delete(url"$url").execute[Either[ErrorResponse, Boolean]]
}
