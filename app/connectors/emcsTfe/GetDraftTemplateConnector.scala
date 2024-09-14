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
import play.api.libs.json.{Format, JsResultException}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetDraftTemplateConnector @Inject()(val http: HttpClientV2,
                                              config: AppConfig) extends EmcsTfeHttpParser[Template] {

  override implicit val reads: Format[Template] = Template.format

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def getTemplate(ern: String, templateId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Option[Template]]] = {
    def url: String = s"$baseUrl/template/$ern/$templateId"

    get(url)
      .map {
        case Right(template)      => Right(Some(template))
        case Left(NoContentError) => Right(None)
        case Left(errorResponse)  => Left(errorResponse)
      }
      .recover {
        case JsResultException(errors) =>
          logger.warn(s"[getTemplate][$ern] Bad JSON response from emcs-tfe: " + errors)
          Left(JsonValidationError)
        case error =>
          logger.warn(s"[getTemplate][$ern] Unexpected error: ${error.getClass} ${error.getMessage}")
          Left(UnexpectedDownstreamResponseError)
      }
  }

  def set(ern: String, templateId: String, template: Template)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Template]] = {
    def url: String = s"$baseUrl/template/$ern/$templateId"

    put(url, template)
      .recover {
        case JsResultException(errors) =>
          logger.warn(s"[set][$ern] Bad JSON response from emcs-tfe: " + errors)
          Left(JsonValidationError)
        case error =>
          logger.warn(s"[set][$ern] Unexpected error: ${error.getClass} ${error.getMessage}")
          Left(UnexpectedDownstreamResponseError)
      }
  }
}
