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
import fixtures.GetSubmissionFailureMessageFixtures
import mocks.config.MockAppConfig
import mocks.connectors.MockHttpClient
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class GetSubmissionFailureMessageConnectorSpec extends SpecBase
  with Status
  with MimeTypes
  with HeaderNames
  with MockAppConfig
  with MockHttpClient
  with GetSubmissionFailureMessageFixtures {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val connector = new GetSubmissionFailureMessageConnector(mockHttpClient, mockAppConfig)

    val baseUrl: String = "http://test-BaseUrl"
    MockedAppConfig.emcsTfeBaseUrl.returns(baseUrl)
  }

  "getSubmissionFailureMessage" must {
    "return a successful response" when {
      "downstream call is successful" in new Test {

        val response: HttpResponse = HttpResponse(status = Status.OK,
          json = Json.toJson(GetSubmissionFailureMessageResponseFixtures.getSubmissionFailureMessageResponseModel), headers = Map.empty)

        MockHttpClient.get(s"$baseUrl/submission-failure-message/ern/1234").returns(Future.successful(response))

        await(connector.getSubmissionFailureMessage(exciseRegistrationNumber = "ern", uniqueMessageIdentifier = 1234)) mustBe response
      }
    }

    "return an error response" when {

      "when downstream call fails (due to a JSON validation error)" in new Test {

        MockHttpClient.get(s"$baseUrl/submission-failure-message/ern/1234").returns(Future.successful(Left(JsonValidationError)))

        await(connector.getSubmissionFailureMessage(exciseRegistrationNumber = "ern", uniqueMessageIdentifier = 1234)) mustBe Left(JsonValidationError)
      }

      "when downstream call fails (due to a non-200 status code being returned)" in new Test {

        MockHttpClient.get(s"$baseUrl/submission-failure-message/ern/1234").returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        await(connector.getSubmissionFailureMessage(exciseRegistrationNumber = "ern", uniqueMessageIdentifier = 1234)) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}
