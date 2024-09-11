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

import connectors.emcsTfe._
import models.draftTemplates._
import models.response._
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DraftTemplatesService @Inject()(
                                       templateListConnector: DraftTemplatesConnector,
                                       templateExistsConnector: IsUniqueDraftTemplateNameConnector,
                                       templateConnector: GetDraftTemplateConnector,
                                       templateDeleteConnector: DeleteDraftTemplateConnector) extends Logging {

  def list(ern: String, page: Int)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[TemplateList] =
    templateListConnector.list(ern, page).map {
      case Right(templateList) => templateList
      case Left(errorResponse) => throw DraftTemplatesListException(s"Failed to retrieve list of templates for $ern: $errorResponse")
    }

  def getTemplate(ern: String, templateId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[Template]] =
    templateConnector.getTemplate(ern, templateId).map {
      case Right(Some(template)) => Some(template)
      case Right(None) => None
      case Left(errorResponse) => throw DraftTemplateGetException(s"Failed to retrieve template for $ern: $errorResponse")
    }

  def doesExist(ern: String, templateName: String)(implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {
    templateExistsConnector.doesExist(ern, templateName) map {
      case Right(true) => true
      case Right(false) => false
      case Left(errorResponse) => throw DraftTemplateCheckNameException(s"Failed to check template name: $templateName for ERN: $ern - $errorResponse")
    }
  }

  def set(ern: String, templateId: String, template: Template)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Template] =
    templateConnector.set(ern, templateId, template).map{
      case Right(template) => template
      case Left(errorResponse) => throw DraftTemplateSetException(s"Failed to update template ID: $templateId for ERN: $ern - $errorResponse")
    }

  def delete(ern: String, templateId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {
    templateDeleteConnector.deleteTemplate(ern, templateId) map {
      case Right(value)        => value
      case Left(errorResponse) => throw DeleteTemplateException(s"Failed to delete template with ID: $templateId for ERN: $ern - $errorResponse")
    }
  }

}
