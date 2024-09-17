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
import mocks.connectors._
import models.draftTemplates.TemplateList
import models.response._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class DraftTemplatesServiceSpec
  extends SpecBase
    with MockDraftTemplatesConnector
    with MockIsUniqueDraftTemplateNameConnector
    with MockGetDraftTemplateConnector
    with MockDeleteDraftTemplateConnector
    with DraftTemplatesFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new DraftTemplatesService(mockDraftTemplatesConnector, mockIsUniqueDraftTemplateNameConnector, mockGetDraftTemplatesConnector, mockDeleteDraftTemplateConnector)

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

  ".getTemplate" should {
    "return a template" when {
      "connector returns Right(_)" in {

        MockGetDraftTemplateConnector.getTemplate("GBRC123456789", "1").returns(Future.successful(Right(Some(fullTemplate))))

        val result = testService.getTemplate("GBRC123456789", "1").futureValue

        result mustBe Some(fullTemplate)
      }
    }

    "throw an exception" when {
      "connector returns Left(_)" in {

        MockGetDraftTemplateConnector.getTemplate("GBRC123456789", "1").returns(Future.successful(Left(JsonValidationError)))

        val result = intercept[DraftTemplateGetException](await(testService.getTemplate("GBRC123456789", "1")))

        result.message mustBe s"Failed to retrieve template for GBRC123456789: $JsonValidationError"
      }
    }
  }

  ".doesExist" should {
    "return a boolean" when {
      "connector returns Right(_)" in {

        MockIsUniquelDraftTemplateNameConnector.doesExist("GBRC123456789", "1").returns(Future.successful(Right(true)))

        val result = testService.doesExist("GBRC123456789", "1").futureValue

        result mustBe true
      }
    }

    "throw an exception" when {
      "connector returns Left(_)" in {

        MockIsUniquelDraftTemplateNameConnector.doesExist("GBRC123456789", "1").returns(Future.successful(Left(JsonValidationError)))

        val result = intercept[DraftTemplateCheckNameException](await(testService.doesExist("GBRC123456789", "1")))

        result.message mustBe s"Failed to check template name: 1 for ERN: GBRC123456789 - $JsonValidationError"
      }
    }

  }

  ".set" should {
    "return a template" when {
      "connector returns Right(_)" in {

        MockGetDraftTemplateConnector.set("GBRC123456789", "1", updateFullTemplate).returns(Future.successful(Right(updateFullTemplate)))

        val result = testService.set("GBRC123456789", "1", updateFullTemplate).futureValue

        result mustBe updateFullTemplate
      }
    }

    "throw an exception" when {
      "connector returns Left(_)" in {

        MockGetDraftTemplateConnector.set("GBRC123456789", "1", updateFullTemplate).returns(Future.successful(Left(JsonValidationError)))

        val result = intercept[DraftTemplateSetException](await(testService.set("GBRC123456789", "1", updateFullTemplate)))

        result.message mustBe s"Failed to update template ID: 1 for ERN: GBRC123456789 - $JsonValidationError"
      }
    }
  }

  ".delete" should {
    "return true" when {

      "the connector returns success" in {

        MockDeleteDraftTemplateConnector.delete(testErn, testTemplateId).returns(Future.successful(Right(true)))

        val result = await(testService.delete(testErn, testTemplateId))

        result mustBe true
      }
    }

    "throw an exception" when {

      "the connector returns an error" in {

        MockDeleteDraftTemplateConnector.delete(testErn, testTemplateId).returns(Future.successful(Left(NotFoundError)))

        val result = intercept[Exception] {
          await(testService.delete(testErn, testTemplateId))
        }

        result mustBe a[DeleteTemplateException]
      }
    }
  }
}
