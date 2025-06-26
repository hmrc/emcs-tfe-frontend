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

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.DraftMovementConnector
import fixtures.GetSubmissionFailureMessageFixtures
import models.response.emcsTfe.draftMovement.DraftId
import models.response.emcsTfe.messages.submissionFailure.IE704FunctionalError
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class DraftMovementConnectorISpec extends IntegrationBaseSpec with GetSubmissionFailureMessageFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val connector: DraftMovementConnector = app.injector.instanceOf[DraftMovementConnector]

  ".markMovementAsDraft()" must {

    val url = s"/emcs-tfe/user-answers/create-movement/$testErn/$testDraftId/mark-as-draft"

    "return a response model when the server responds OK" in {

      wireMockServer.stubFor(
        put(urlEqualTo(url))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.obj(
            "draftId" -> testDraftId
          ))))
      )

      connector.markMovementAsDraft(testErn, testDraftId).futureValue mustBe Right(DraftId(testDraftId))
    }

    "must fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        put(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.markMovementAsDraft(testErn, testDraftId).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with a body that can't be parsed to the expected response model" in {

      wireMockServer.stubFor(
        put(urlEqualTo(url))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.obj(
            "x" -> "y"
          ))))
      )

      connector.markMovementAsDraft(testErn, testDraftId).futureValue mustBe Left(JsonValidationError)
    }

    "must fail when the connection fails" in {

      wireMockServer.stubFor(
        put(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.markMovementAsDraft(testErn, testDraftId).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }

  ".putErrorMessagesAndReturnDraftId()" must {

    val errors: Seq[IE704FunctionalError] = Seq(IE704FunctionalErrorFixtures.ie704FunctionalErrorModel)

    val url = s"/emcs-tfe/user-answers/create-movement/$testErn/$testLrn/error-messages"

    "return a response model when the server responds OK" in {

      wireMockServer.stubFor(
        put(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(errors))))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.obj(
            "draftId" -> testDraftId
          ))))
      )

      connector.putErrorMessagesAndReturnDraftId(testErn, testLrn, errors).futureValue mustBe Right(DraftId(testDraftId))
    }

    "must fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        put(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(errors))))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.putErrorMessagesAndReturnDraftId(testErn, testLrn, errors).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with a body that can't be parsed to the expected response model" in {

      wireMockServer.stubFor(
        put(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(errors))))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.obj(
            "x" -> "y"
          ))))
      )

      connector.putErrorMessagesAndReturnDraftId(testErn, testLrn, errors).futureValue mustBe Left(JsonValidationError)
    }

    "must fail when the connection fails" in {

      wireMockServer.stubFor(
        put(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(errors))))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.markMovementAsDraft(testErn, testDraftId).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
