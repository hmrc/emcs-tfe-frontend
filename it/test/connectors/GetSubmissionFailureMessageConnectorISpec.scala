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
import connectors.emcsTfe.GetSubmissionFailureMessageConnector
import fixtures.{BaseFixtures, GetSubmissionFailureMessageFixtures}
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.libs.json.Json
import play.api.test.Helpers._
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class GetSubmissionFailureMessageConnectorISpec extends IntegrationBaseSpec
  with ScalaFutures
  with BaseFixtures
  with IntegrationPatience
  with GetSubmissionFailureMessageFixtures {

  import GetSubmissionFailureMessageResponseFixtures._

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  val url = s"/emcs-tfe/submission-failure-message/$testErn/$testUniqueMessageIdentifier"

  ".getSubmissionFailureMessage" must {

    lazy val connector: GetSubmissionFailureMessageConnector = app.injector.instanceOf[GetSubmissionFailureMessageConnector]

    "must return Right(Seq[GetMessagesResponse]) when the server responds OK" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(getSubmissionFailureMessageResponseJson)))
      )

      connector.getSubmissionFailureMessage(testErn, testUniqueMessageIdentifier).futureValue mustBe Right(getSubmissionFailureMessageResponseModel)
    }

    "must fail when the server responds with incorrect JSON" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse()
            .withStatus(OK)
            .withBody(Json.stringify(Json.obj())))
      )

      connector.getSubmissionFailureMessage(testErn, testUniqueMessageIdentifier).futureValue mustBe Left(JsonValidationError)
    }

    "must fail when the server responds with any other status (ISE returned and handled by the parser)" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getSubmissionFailureMessage(testErn, testUniqueMessageIdentifier).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails (an exception is thrown and handled by the connector)" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getSubmissionFailureMessage(testErn, testUniqueMessageIdentifier).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
