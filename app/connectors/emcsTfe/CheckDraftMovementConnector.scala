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
import models.response.emcsTfe.draftMovement.{DraftExists, DraftId}
import models.response.emcsTfe.messages.submissionFailure.IE704FunctionalError
import models.response.{ErrorResponse, JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.libs.json.{JsResultException, Reads}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CheckDraftMovementConnector @Inject()(val http: HttpClient,
                                            config: AppConfig) extends EmcsTfeHttpParser[DraftExists] {

  override implicit val reads: Reads[DraftExists] = DraftExists.reads

  def checkDraftMovementExists(ern: String, id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, DraftExists]] =
    get(s"${config.emcsTfeBaseUrl}/user-answers/create-movement/draft/$ern/$id")
}
