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
import models.draftTemplates.Template
import models.response.{ErrorResponse, JsonValidationError, NoContentError, UnexpectedDownstreamResponseError}
import play.api.libs.json.{JsResultException, Reads}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DraftTemplatesConnector @Inject()(val http: HttpClientV2,
                                        config: AppConfig) extends EmcsTfeHttpParser[Seq[Template]] {

  override implicit val reads: Reads[Seq[Template]] = Template.readsSeq

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def list(ern: String, page: Int)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Seq[Template]]] = {
    def url: String = s"$baseUrl/templates/$ern"

    get(url, Seq("page" -> page.toString))
      .map {
        case Right(templates) => Right(templates)
        case Left(NoContentError) =>
          logger.debug(s"[list][$ern] No content from emcs-tfe")
          Right(Seq.empty)
        case Left(errorResponse) => Left(errorResponse)
      }
      .recover {
        case JsResultException(errors) =>
          logger.warn(s"[list][$ern] Bad JSON response from emcs-tfe: " + errors)
          Left(JsonValidationError)
        case error =>
          logger.warn(s"[markMovementAsDraft][$ern] Unexpected error: ${error.getClass} ${error.getMessage}")
          Left(UnexpectedDownstreamResponseError)
      }
  }
}
