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
import models.messages.MessagesSearchOptions
import models.response.emcsTfe.messages.GetMessagesResponse
import models.response.{ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.libs.json.{JsResultException, Reads}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetMessagesConnector @Inject()(val http: HttpClient, config: AppConfig) extends EmcsTfeHttpParser[GetMessagesResponse] {

  override implicit val reads: Reads[GetMessagesResponse] = GetMessagesResponse.format

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def getMessages(exciseRegistrationNumber: String, search: Option[MessagesSearchOptions] = None)(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[ErrorResponse, GetMessagesResponse]] = {
    def url: String = s"$baseUrl/messages/$exciseRegistrationNumber"

    get(url, search.map(_.queryParams).getOrElse(Seq.empty))
      .recover {
        case JsResultException(errors) =>
          logger.warn(s"[getMessages] Bad JSON response from emcs-tfe: " + errors)
          Left(JsonValidationError)
        case error =>
          logger.warn(s"[getMessages] Unexpected error from emcs-tfe: ${error.getClass} ${error.getMessage}")
          Left(UnexpectedDownstreamResponseError)
      }
  }
}
