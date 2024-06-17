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

package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.PrevalidateTraderConnector
import fixtures.{BaseFixtures, ExciseProductCodeFixtures, PrevalidateTraderFixtures}
import models.requests.PrevalidateTraderRequest
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class PrevalidateTraderConnectorISpec extends IntegrationBaseSpec
  with ScalaFutures
  with BaseFixtures
  with IntegrationPatience
  with ExciseProductCodeFixtures
  with PrevalidateTraderFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val connector: PrevalidateTraderConnector = app.injector.instanceOf[PrevalidateTraderConnector]

  def url(): String = s"/emcs-tfe/pre-validate-trader/$testErn"

  val ernToCheck = "GBWK002281023"
  val entityGroupToCheck = testEntityGroup

  val requestModel: PrevalidateTraderRequest = PrevalidateTraderRequest(ernToCheck, entityGroupToCheck, Seq(testEpcWine, testEpcBeer))

  ".markMessageAsRead" must {

    "must return Right(PreValidateTraderApiResponse) when the server responds OK" in {
      wireMockServer.stubFor(
        post(urlEqualTo(url()))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(requestModel))))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(preValidateApiResponseAsJson)))
      )

      connector.prevalidateTrader(testErn, requestModel).futureValue mustBe Right(preValidateApiResponseModel)
    }

    "must fail when the server response with any other status" in {
      wireMockServer.stubFor(
        post(urlEqualTo(url()))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.prevalidateTrader(testErn, requestModel).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {
      wireMockServer.stubFor(
        post(urlEqualTo(url()))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.prevalidateTrader(testErn, requestModel).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

  }
}
