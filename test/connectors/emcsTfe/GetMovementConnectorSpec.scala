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

import mocks.config.MockAppConfig
import mocks.connectors.MockHttpClient
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.Json
import models.response.emcsTfe.GetMovementResponse
import support.UnitSpec
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class GetMovementConnectorSpec extends UnitSpec with Status with MimeTypes with HeaderNames with MockAppConfig with MockHttpClient {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val connector = new GetMovementConnector(mockHttpClient, mockAppConfig)

    val baseUrl: String = "http://test-BaseUrl"
    MockedAppConfig.emcsTfeBaseUrl.returns(baseUrl)
  }

  "getMovement" should {
    "return a successful response" when {
      "downstream call is successful" in new Test {
        val model: GetMovementResponse = GetMovementResponse(
          localReferenceNumber = "EN", eadStatus = "Accepted", consignorName = "Current 801 Consignor", dateOfDispatch = LocalDate.parse("2008-11-20"), journeyTime = "20 days", numberOfItems = 2
        )
        val response: HttpResponse = HttpResponse(status = Status.OK, json = Json.toJson(model), headers = Map.empty)

        MockHttpClient.get(s"$baseUrl/movement/ern/arc").returns(Future.successful(response))

        await(connector.getMovement(exciseRegistrationNumber = "ern", arc = "arc")) shouldBe response
      }
    }
  }
}
