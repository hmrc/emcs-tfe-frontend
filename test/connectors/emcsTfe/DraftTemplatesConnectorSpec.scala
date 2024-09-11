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
import models.draftTemplates.TemplateList
import models.response.JsonValidationError
import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}

class DraftTemplatesConnectorSpec extends SpecBase
  with Status with MimeTypes with HeaderNames with MockHttpClient with DraftTemplatesFixtures {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val connector = new DraftTemplatesConnector(mockHttpClient, appConfig)

  "list" should {

    "return a successful response" when {

      "downstream call is successful" in {

        val expectedResult = Right(TemplateList(templateList, templateList.length))

        MockHttpClient
          .get(url"${appConfig.emcsTfeBaseUrl}/templates/$testErn?page=1&pageSize=${TemplateList.DEFAULT_MAX_ROWS}")
          .returns(Future.successful(Right(TemplateList(templateList, templateList.length))))

        val actualResult = connector.list(
          ern = testErn,
          page = 1
        ).futureValue

        actualResult mustBe expectedResult
      }
    }

    "return an error response" when {

      "when downstream call fails" in {

        val expectedResult = Left(JsonValidationError)

        MockHttpClient
          .get(url"${appConfig.emcsTfeBaseUrl}/templates/$testErn?page=1&pageSize=${TemplateList.DEFAULT_MAX_ROWS}")
          .returns(Future.successful(Left(JsonValidationError)))

        val actualResult = connector.list(ern = testErn, page = 1).futureValue

        actualResult mustBe expectedResult
      }
    }
  }
}