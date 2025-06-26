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

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, delete, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.DeleteDraftTemplateConnector
import models.response.UnexpectedDownstreamResponseError
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NO_CONTENT}
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class DeleteDraftTemplateConnectorISpec extends IntegrationBaseSpec {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val connector: DeleteDraftTemplateConnector = app.injector.instanceOf[DeleteDraftTemplateConnector]

  ".delete" must {

    val url = s"/emcs-tfe/template/$testErn/$testTemplateId"

    "return true when the server responds NO_CONTENT" in {

      wireMockServer.stubFor(
        delete(urlEqualTo(url))
          .willReturn(aResponse().withStatus(NO_CONTENT))
      )

      connector.deleteTemplate(testErn, testTemplateId).futureValue mustBe Right(true)
    }

    "must fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        delete(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.deleteTemplate(testErn, testTemplateId).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      wireMockServer.stubFor(
        delete(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.deleteTemplate(testErn, testTemplateId).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

  }
}
