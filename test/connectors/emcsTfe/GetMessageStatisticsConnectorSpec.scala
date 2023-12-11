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
import mocks.config.MockAppConfig
import mocks.connectors.MockHttpClient
import models.response.emcsTfe.GetMessageStatisticsResponse
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfefrontend.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfefrontend.mocks.connectors.MockHttpClient
import uk.gov.hmrc.emcstfefrontend.models.MovementListSearchOptions
import uk.gov.hmrc.emcstfefrontend.models.common.{AddressModel, TraderModel}
import uk.gov.hmrc.emcstfefrontend.models.response.emcsTfe.GetMessageStatisticsResponse
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

class GetMessageStatisticsConnectorSpec extends SpecBase with Status with MimeTypes with HeaderNames with MockAppConfig with MockHttpClient {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val connector = new GetMessageStatisticsConnector(mockHttpClient, mockAppConfig)

    val baseUrl: String = "http://test-BaseUrl"
    MockedAppConfig.emcsTfeBaseUrl.returns(baseUrl)
  }

  "getMessageStatistics" must {
    "return a successful response" when {
      "downstream call is successful" in new Test {
        val model: GetMessageStatisticsResponse = GetMessageStatisticsResponse(
          dateTime = "testDateTime",
          exciseRegistrationNumber = "testExciseRegistrationNumber",
          countOfAllMessages = 1,
          countOfNewMessages = 1
        )

        val response: HttpResponse = HttpResponse(status = Status.OK, json = Json.toJson(model), headers = Map.empty)

        MockHttpClient.get(s"$baseUrl/message-statistics/ern").returns(Future.successful(response))

        await(connector.getMessageStatistics(exciseRegistrationNumber = "ern")) mustBe response
      }
    }
  }
}
