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
import models.response.emcsTfe.draftMovement.DraftId
import models.response.emcsTfe.messages.submissionFailure.IE704FunctionalError
import models.response.{ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.libs.json.{JsResultException, Reads}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DraftMovementConnector @Inject()(val http: HttpClient,
                                       config: AppConfig) extends EmcsTfeHttpParser[DraftId] {

  override implicit val reads: Reads[DraftId] = DraftId.reads

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def markMovementAsDraft(ern: String, draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, DraftId]] = {
    def url: String = s"$baseUrl/user-answers/create-movement/$ern/$draftId/mark-as-draft"

    get(url)
      .recover {
        case JsResultException(errors) =>
          logger.warn(s"[markMovementAsDraft] Bad JSON response from emcs-tfe: " + errors)
          Left(JsonValidationError)
        case error =>
          logger.warn(s"[markMovementAsDraft] Unexpected error: ${error.getClass} ${error.getMessage}")
          Left(UnexpectedDownstreamResponseError)
      }
  }

  def putErrorMessagesAndReturnDraftId(ern: String, lrn: String, errors: Seq[IE704FunctionalError])
                                      (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, DraftId]] = {
    def url: String = s"$baseUrl/user-answers/create-movement/$ern/$lrn/error-messages"

    put(url, errors)
      .recover {
        case JsResultException(errors) =>
          logger.warn(s"[putErrorMessagesAndReturnDraftId] Bad JSON response from emcs-tfe: " + errors)
          Left(JsonValidationError)
        case error =>
          logger.warn(s"[putErrorMessagesAndReturnDraftId] Unexpected error: ${error.getClass} ${error.getMessage}")
          Left(UnexpectedDownstreamResponseError)
      }
  }
}
