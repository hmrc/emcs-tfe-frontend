/*
 * Copyright 2025 HM Revenue & Customs
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

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.GetMovementHistoryEventsConnector
import fixtures.{BaseFixtures, GetMovementHistoryEventsResponseFixtures}
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.libs.json.Json
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class GetMovementHistoryEventsConnectorISpec extends IntegrationBaseSpec
  with ScalaFutures
  with BaseFixtures
  with IntegrationPatience
  with OptionValues
  with GetMovementHistoryEventsResponseFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val connector: GetMovementHistoryEventsConnector = app.injector.instanceOf[GetMovementHistoryEventsConnector]

  ".getMovementHistoryEvents" must {

    val body = Json.toJson(getMovementHistoryEventsResponseInputJson)

    def url(): String = s"/emcs-tfe/movement-history/$testErn/$testArc"

    "must return true when the server responds OK" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url()))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(body)))
      )

      connector.getMovementHistoryEvents(testErn, testArc).futureValue mustBe Right(getMovementHistoryEventsModel)
    }

    "must return false when the server responds NOT_FOUND" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url()))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.getMovementHistoryEvents(testErn, testArc).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url()))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getMovementHistoryEvents(testErn, testArc).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url()))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getMovementHistoryEvents(testErn, testArc).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }

}
