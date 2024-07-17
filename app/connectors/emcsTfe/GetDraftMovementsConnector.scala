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
import models.draftMovements.GetDraftMovementsSearchOptions
import models.response.emcsTfe.draftMovement.GetDraftMovementsResponse
import models.response.{ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.libs.json.{JsResultException, Reads}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetDraftMovementsConnector @Inject()(val http: HttpClient,
                                           config: AppConfig) extends EmcsTfeHttpParser[GetDraftMovementsResponse] {

  override implicit val reads: Reads[GetDraftMovementsResponse] = GetDraftMovementsResponse.reads

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def getDraftMovements(ern: String, search: Option[GetDraftMovementsSearchOptions] = None)
                       (implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[ErrorResponse, GetDraftMovementsResponse]] = {

    def url: String = s"$baseUrl/user-answers/create-movement/drafts/search/$ern"

    get(url, search.map(_.queryParams).getOrElse(Seq.empty))
      .recover {
        case JsResultException(errors) =>
          logger.warn(s"[getDraftMovements][$ern] Bad JSON response from emcs-tfe: " + errors)
          Left(JsonValidationError)
        case error =>
          logger.warn(s"[getDraftMovements][$ern] Unexpected error from emcs-tfe: ${error.getClass} ${error.getMessage}")
          Left(UnexpectedDownstreamResponseError)
      }
  }
}
