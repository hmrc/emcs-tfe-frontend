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
import connectors.emcsTfe.CreateDraftMovementConnector
import models.response.emcsTfe.draftTemplate.DraftMovementCreatedResponse
import models.response.{CreateDraftMovementException, UnexpectedDownstreamResponseError}
import play.api.http.Status.{CREATED, OK}
import play.api.libs.json.Json
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class CreateDraftMovementConnectorISpec extends IntegrationBaseSpec {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val connector: CreateDraftMovementConnector = app.injector.instanceOf[CreateDraftMovementConnector]

  "createDraftMovement"  must {

    val url = s"/emcs-tfe/template/$testErn/$testTemplateId/create-draft-from-template"

    "return true when the server responds CREATED" in {
      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse()
            .withStatus(CREATED)
            .withBody(Json.stringify(Json.obj("createdDraftId" -> testDraftId)))
          )

      )

      connector.createDraftMovement(testErn, testTemplateId).futureValue mustBe
        Right(DraftMovementCreatedResponse(createdDraftId = testDraftId))
    }

    "must fail when the server responds with any other status" in {
      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(OK))
      )

      connector.createDraftMovement(testErn, testTemplateId).futureValue mustBe
        Left(CreateDraftMovementException(s"Unexpected status from emcs-tfe: $OK"))
    }

    "must fail when the connection fails" in {
      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.createDraftMovement(testErn, testTemplateId).futureValue mustBe
        Left(UnexpectedDownstreamResponseError)
    }
  }

}
