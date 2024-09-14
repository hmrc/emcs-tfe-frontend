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

package mocks.connectors


import connectors.emcsTfe.GetDraftTemplateConnector
import models.draftTemplates.Template
import models.response.ErrorResponse
import org.scalamock.handlers.{CallHandler4, CallHandler5}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockGetDraftTemplateConnector extends MockFactory {

  lazy val mockGetDraftTemplatesConnector: GetDraftTemplateConnector = mock[GetDraftTemplateConnector]

  object MockGetFullDraftTemplateConnector {
    def getFullTemplate(ern: String, templateId: String): CallHandler4[String, String, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, Option[Template]]]] =
      (mockGetDraftTemplatesConnector.getTemplate(_: String, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, templateId, *, *)

    def set(ern: String, templateId: String, template: Template): CallHandler5[String, String, Template, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, Template]]] =
      (mockGetDraftTemplatesConnector.set(_: String, _: String, _: Template)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, templateId, template, *, *)
  }

}