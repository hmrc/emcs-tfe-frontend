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
import fixtures.MessagesFixtures
import mocks.config.MockAppConfig
import mocks.connectors.MockHttpClient
import models.messages.MessagesSearchOptions
import models.response.JsonValidationError
import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}

class GetMessagesConnectorSpec extends SpecBase with Status with MimeTypes with HeaderNames with MockAppConfig with MockHttpClient with MessagesFixtures {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val connector = new GetMessagesConnector(mockHttpClient, mockAppConfig)

    val baseUrl: String = "http://test-BaseUrl"

    MockedAppConfig.emcsTfeBaseUrl.returns(baseUrl)
  }

  "getMessages" must {

    "return a successful response" when {

      "downstream call is successful" in new Test {
        val searchOptions = MessagesSearchOptions()

        MockHttpClient
          .get(url"$baseUrl/messages/$testErn?${searchOptions.queryParams}")
          .returns(Future.successful(getMessageResponse))

        val expectedResult = getMessageResponse

        val actualResult = connector.getMessages(
          exciseRegistrationNumber = testErn,
          Some(searchOptions)
        )

        await(actualResult) mustBe expectedResult
      }
    }

    "return an error response" when {

      "when downstream call fails" in new Test {
        val searchOptions = MessagesSearchOptions()

        MockHttpClient
          .get(url"$baseUrl/messages/$testErn?${searchOptions.queryParams}")
          .returns(Future.successful(Left(JsonValidationError)))

        val expectedResult = Left(JsonValidationError)

        val actualResult = connector.getMessages(
          exciseRegistrationNumber = testErn,
          Some(searchOptions)
        )

        await(actualResult) mustBe expectedResult
      }
    }


  }
}
