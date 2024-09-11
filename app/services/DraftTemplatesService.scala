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

import connectors.emcsTfe.{DraftTemplatesConnector, GetFullDraftTemplateConnector, IsUniqueDraftTemplateNameConnector}
import models.draftTemplates.{FullTemplate, TemplateList}
import models.response.DraftTemplatesListException
import models.response.DraftTemplateGetException
import models.response.DraftTemplateCheckNameException
import models.response.DraftTemplateSetException
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DraftTemplatesService @Inject()(connector: DraftTemplatesConnector, isUniqueDraftTemplateNameConnector: IsUniqueDraftTemplateNameConnector, getFullDraftTemplateConnector: GetFullDraftTemplateConnector) extends Logging {
  def list(ern: String, page: Int)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TemplateList] = connector.list(ern, page).map {
    case Right(templateList) => templateList
    case Left(errorResponse) => throw DraftTemplatesListException(s"Failed to retrieve list of templates for $ern: $errorResponse")
  }

  def getFullTemplate(ern: String, templateId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[FullTemplate]] = getFullDraftTemplateConnector.getFullTemplate(ern, templateId).map {
    case Right(Some(template)) => Some(template)
    case Right(None) => None
    case Left(errorResponse) => throw DraftTemplateGetException(s"Failed to retrieve template for $ern: $errorResponse")
  }

  def doesExist(ern: String, templateName: String)(implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Boolean] ={
    isUniqueDraftTemplateNameConnector.doesExist(ern, templateName) map {
      case Right(true) => true
      case Right(false) => false
      case Left(errorResponse) => throw DraftTemplateCheckNameException(s"Failed to check template name: $templateName for ERN: $ern - $errorResponse")
    }
  }

  def set(ern: String, templateId: String, template: FullTemplate)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[FullTemplate] = getFullDraftTemplateConnector.set(ern, templateId, template).map{
    case Right(template) => template
    case Left(errorResponse) => throw DraftTemplateSetException(s"Failed to update template ID: $templateId for ERN: $ern - $errorResponse")
  }
}