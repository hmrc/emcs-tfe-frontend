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

import base.SpecBase
import fixtures.DraftTemplatesFixtures
import mocks.connectors.{MockDraftTemplatesConnector, MockGetDraftTemplateConnector, MockIsUniqueDraftTemplateNameConnector}
import models.draftTemplates.TemplateList
import models.response.{DraftTemplatesListException, JsonValidationError}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class DraftTemplatesServiceSpec
  extends SpecBase
    with MockDraftTemplatesConnector
    with MockIsUniqueDraftTemplateNameConnector
    with MockGetDraftTemplateConnector
    with DraftTemplatesFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new DraftTemplatesService(mockDraftTemplatesConnector, mockIsUniqueDraftTemplateNameConnector, mockGetDraftTemplatesConnector)

  ".list" should {
    "return a Seq of templates" when {
      "connector returns Right(_)" in {

        MockDraftTemplatesConnector.list(testErn, 1).returns(Future.successful(Right(TemplateList(templateList, 30))))

        val result = testService.list(testErn, 1).futureValue

        result mustBe TemplateList(templateList, 30)
      }
    }

    "throw an exception" when {
      "connector returns Left(_)" in {

        MockDraftTemplatesConnector.list(testErn, 1).returns(Future.successful(Left(JsonValidationError)))

        val result = intercept[DraftTemplatesListException](await(testService.list(testErn, 1)))

        result.message mustBe s"Failed to retrieve list of templates for $testErn: $JsonValidationError"
      }
    }
  }
}
