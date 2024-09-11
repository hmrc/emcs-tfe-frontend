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
import models.response.{ErrorResponse, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DeleteDraftTemplateConnector @Inject()(val http: HttpClientV2, config: AppConfig) extends DeleteDraftTemplateHttpParser {

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def deleteTemplate(ern: String, templateId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Either[ErrorResponse, Boolean]] = {
    val url = s"$baseUrl/template/$ern/$templateId"

    delete(url)
      .recover {
        case _: Exception =>
          Left(UnexpectedDownstreamResponseError)
      }
  }
}