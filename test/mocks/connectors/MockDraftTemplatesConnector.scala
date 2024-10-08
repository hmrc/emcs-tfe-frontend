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

import connectors.emcsTfe.DraftTemplatesConnector
import models.draftTemplates.TemplateList
import models.response.ErrorResponse
import org.scalamock.handlers.CallHandler4
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockDraftTemplatesConnector extends MockFactory {

  lazy val mockDraftTemplatesConnector: DraftTemplatesConnector = mock[DraftTemplatesConnector]

  object MockDraftTemplatesConnector {
    def list(ern: String, page: Int): CallHandler4[String, Int, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, TemplateList]]] =
      (mockDraftTemplatesConnector.list(_: String, _: Int)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, page, *, *)
  }

}
