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

package mocks.services

import models.draftTemplates.{Template, TemplateList}
import org.scalamock.handlers.{CallHandler4, CallHandler5}
import org.scalamock.scalatest.MockFactory
import services.DraftTemplatesService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockDraftTemplatesService extends MockFactory {

  lazy val mockDraftTemplatesService: DraftTemplatesService = mock[DraftTemplatesService]

  object MockDraftTemplatesService {
    def list(ern: String, page: Int): CallHandler4[String, Int, HeaderCarrier, ExecutionContext, Future[TemplateList]] =
      (mockDraftTemplatesService.list(_: String, _: Int)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, page, *, *)

    def getTemplate(ern: String, templateId: String): CallHandler4[String, String, HeaderCarrier, ExecutionContext, Future[Option[Template]]] =
      (mockDraftTemplatesService.getTemplate(_: String, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, templateId, *, *)

    def doesExist(ern: String, templateName: String): CallHandler4[String, String, HeaderCarrier, ExecutionContext, Future[Boolean]] =
      (mockDraftTemplatesService.doesExist(_: String, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, templateName, *, *)

    def set(ern: String, templateId: String, template: Template): CallHandler5[String, String, Template, HeaderCarrier, ExecutionContext, Future[Template]] =
      (mockDraftTemplatesService.set(_: String, _: String, _: Template)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, templateId, template, *, *)

    def delete(ern: String, templateId: String): CallHandler4[String, String, HeaderCarrier, ExecutionContext, Future[Boolean]] =
      (mockDraftTemplatesService.delete(_: String, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, templateId, *, *)
  }

}