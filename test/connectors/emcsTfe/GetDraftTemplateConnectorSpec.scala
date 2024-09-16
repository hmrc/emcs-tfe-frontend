/*
 * Copyright 2023 HM Revenue & Customs
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

import base.SpecBase
import fixtures.DraftTemplatesFixtures
import mocks.connectors.MockHttpClient
import models.response.JsonValidationError
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}

class GetDraftTemplateConnectorSpec extends SpecBase
  with Status with MimeTypes with HeaderNames with MockHttpClient with DraftTemplatesFixtures {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val connector = new GetDraftTemplateConnector(mockHttpClient, appConfig)

  "getTemplate" should {

    "return a successful response" when {

      "downstream call is successful" in {

        val expectedResult = Right(Some(fullTemplate))

        MockHttpClient
          .get(url"${appConfig.emcsTfeBaseUrl}/template/GBRC123456789/1")
          .returns(Future.successful(Right(fullTemplate)))

        val actualResult = connector.getTemplate(
          ern = "GBRC123456789",
          templateId = "1"
        ).futureValue

        actualResult mustBe expectedResult
      }
    }

    "return an error response" when {

      "when downstream call fails" in {

        val expectedResult = Left(JsonValidationError)

        MockHttpClient
          .get(url"${appConfig.emcsTfeBaseUrl}/template/GBRC123456789/1")
          .returns(Future.successful(Left(JsonValidationError)))

        val actualResult = connector.getTemplate(ern = "GBRC123456789", templateId = "1").futureValue

        actualResult mustBe expectedResult
      }
    }
  }

  "set" should {

    "return a successful response" when {

      "downstream call is successful" in {

        val expectedResult = Right(updateFullTemplate)

        MockHttpClient
          .put(url"${appConfig.emcsTfeBaseUrl}/template/GBRC123456789/1", Json.toJson(updateFullTemplate))
          .returns(Future.successful(Right(updateFullTemplate)))

        val actualResult = connector.set(
          ern = "GBRC123456789",
          templateId = "1",
          template = updateFullTemplate
        ).futureValue

        actualResult mustBe expectedResult
      }
    }

    "return an error response" when {

      "when downstream call fails" in {

        val expectedResult = Left(JsonValidationError)

        MockHttpClient
          .put(url"${appConfig.emcsTfeBaseUrl}/template/GBRC123456789/1", Json.toJson(updateFullTemplate))
          .returns(Future.successful(Left(JsonValidationError)))

        val actualResult = connector.set(ern = "GBRC123456789", templateId = "1", template = updateFullTemplate).futureValue

        actualResult mustBe expectedResult
      }
    }
  }
}