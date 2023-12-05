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

import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import base.SpecBase
import mocks.config.MockAppConfig
import mocks.connectors.MockHttpClient
import models.common.{AddressModel, TraderModel}
import models.response.emcsTfe.GetMovementResponse
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class GetMovementConnectorSpec extends SpecBase with Status with MimeTypes with HeaderNames with MockAppConfig with MockHttpClient {

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
          localReferenceNumber = "EN",
          eadStatus = "Accepted",
          consignorTrader = TraderModel(
            traderExciseNumber = "GB12345GTR144",
            traderName = "Current 801 Consignor",
            address = AddressModel(
              streetNumber = None,
              street = Some("Main101"),
              postcode = Some("ZZ78"),
              city = Some("Zeebrugge")
            )
          ),dateOfDispatch = LocalDate.parse("2008-11-20"),
          journeyTime = "20 days",
          numberOfItems = 2
        )

        val response: HttpResponse = HttpResponse(status = Status.OK, json = Json.toJson(model), headers = Map.empty)

        MockHttpClient.get(s"$baseUrl/movement/ern/arc?forceFetchNew=true").returns(Future.successful(response))

        await(connector.getMovement(exciseRegistrationNumber = "ern", arc = "arc")) shouldBe response
      }
    }
  }
}
