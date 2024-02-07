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

package connectors.betaAllowList

import models.response.{ErrorResponse, UnexpectedDownstreamResponseError}
import play.api.http.Status.{NOT_FOUND, OK}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.Logging

trait BetaAllowListHttpParser extends Logging {

  implicit object BetaAllowListReads extends HttpReads[Either[ErrorResponse, Boolean]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, Boolean] = {
      response.status match {
        case OK => Right(true)
        case NOT_FOUND => Right(false)
        case status =>
          logger.warn(s"[read] Unexpected status from emcs-tfe: $status")
          Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}