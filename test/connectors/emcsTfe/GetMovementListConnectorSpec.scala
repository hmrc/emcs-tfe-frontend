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
import fixtures.MovementListFixtures
import mocks.config.MockAppConfig
import mocks.connectors.MockHttpClient
import models.MovementListSearchOptions
import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}

class GetMovementListConnectorSpec extends SpecBase with Status with MimeTypes with HeaderNames with MockAppConfig with MockHttpClient with MovementListFixtures {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val connector = new GetMovementListConnector(mockHttpClient, mockAppConfig)

    val baseUrl: String = "http://test-BaseUrl"
    MockedAppConfig.emcsTfeBaseUrl.returns(baseUrl)
  }

  "getMovementList" must {

    "return a successful response" when {

      "no search options are given" in new Test {

        MockHttpClient
          .get(url"$baseUrl/movements/$testErn")
          .returns(Future.successful(getMovementListResponse))

        val expectedResult = getMovementListResponse

        val actualResult = connector.getMovementList(
          exciseRegistrationNumber = testErn,
          None
        )

        await(actualResult) mustBe expectedResult
      }

      "search options are given" in new Test {

        val searchOptions = MovementListSearchOptions()

        MockHttpClient
          .get(url"$baseUrl/movements/$testErn?${searchOptions.queryParams}")
          .returns(Future.successful(getMovementListResponse))

        val expectedResult = getMovementListResponse

        val actualResult = connector.getMovementList(
          exciseRegistrationNumber = testErn,
          Some(searchOptions)
        )

        await(actualResult) mustBe expectedResult
      }
    }
  }
}
