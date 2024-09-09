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

package services

import connectors.emcsTfe.DraftTemplatesConnector
import models.draftTemplates.TemplateList
import models.response.DraftTemplatesListException
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DraftTemplatesService @Inject()(connector: DraftTemplatesConnector) extends Logging {
  def list(ern: String, page: Int)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TemplateList] = connector.list(ern, page).map {
    case Right(templateList) => templateList
    case Left(errorResponse) => throw DraftTemplatesListException(s"Failed to retrieve list of templates for $ern: $errorResponse")
  }
}
