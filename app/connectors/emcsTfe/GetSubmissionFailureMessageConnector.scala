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
import models.response.emcsTfe.messages.submissionFailure.GetSubmissionFailureMessageResponse
import models.response.{ErrorResponse, UnexpectedDownstreamResponseError}
import play.api.libs.json.Reads
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetSubmissionFailureMessageConnector @Inject()(val http: HttpClient, config: AppConfig) extends EmcsTfeHttpParser[GetSubmissionFailureMessageResponse] {

  override implicit val reads: Reads[GetSubmissionFailureMessageResponse] = GetSubmissionFailureMessageResponse.format

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def getSubmissionFailureMessage(exciseRegistrationNumber: String, uniqueMessageIdentifier: Long)
                                 (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, GetSubmissionFailureMessageResponse]] = {
    def url: String = s"$baseUrl/submission-failure-message/$exciseRegistrationNumber/$uniqueMessageIdentifier"

    get(url)
      .recover {
        case error =>
          logger.warn(s"[getMessages] Unexpected error from emcs-tfe: ${error.getClass} ${error.getMessage}")
          Left(UnexpectedDownstreamResponseError)
      }
  }
}
