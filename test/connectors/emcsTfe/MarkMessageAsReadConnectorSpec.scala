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

package connectors.emcsTfe

import base.SpecBase
import fixtures.MessagesFixtures
import mocks.config.MockAppConfig
import mocks.connectors.MockHttpClient
import models.response.JsonValidationError
import models.response.emcsTfe.messages.MarkMessageAsReadResponse
import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}

class MarkMessageAsReadConnectorSpec extends SpecBase with Status with MimeTypes with HeaderNames with MockAppConfig with MockHttpClient with MessagesFixtures {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val connector = new MarkMessageAsReadConnector(mockHttpClient, mockAppConfig)

    val baseUrl: String = "http://test-BaseUrl"

    MockedAppConfig.emcsTfeBaseUrl.returns(baseUrl)
  }

  "markMessageAsRead" must {
    "return a successful response" when {
      "downstream call is successful" in new Test {
        val testMessageId = 1234

        val expectedResult = MarkMessageAsReadResponse(1)

        MockHttpClient
          .putEmpty(url"$baseUrl/message/$testErn/$testMessageId")
          .returns(Future.successful(expectedResult))

        val actualResult = connector.markMessageAsRead(ern = testErn, testMessageId)

        await(actualResult) mustBe expectedResult
      }
    }

    "return an error response" when {
      "when downstream call fails" in new Test {
        val testMessageId = 1234

        val expectedResult = Left(JsonValidationError)

        MockHttpClient
          .putEmpty(url"$baseUrl/message/$testErn/$testMessageId")
          .returns(Future.successful(expectedResult))

        val actualResult = connector.markMessageAsRead(ern = testErn, testMessageId)

        await(actualResult) mustBe expectedResult
      }
    }
  }

}
