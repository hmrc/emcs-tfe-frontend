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
import connectors.referenceData.GetCnCodeInformationConnector
import models.common.UnitOfMeasure.Kilograms
import models.requests.{CnCodeInformationItem, CnCodeInformationRequest}
import models.response.referenceData.{CnCodeInformation, CnCodeInformationResponse}
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.{JsArray, Json}
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class GetCnCodeInformationConnectorISpec extends IntegrationBaseSpec {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val connector: GetCnCodeInformationConnector = app.injector.instanceOf[GetCnCodeInformationConnector]

  ".getCnCodeInformation()" must {

    val url = "/emcs-tfe-reference-data/oracle/cn-code-information"
    val request = CnCodeInformationRequest(Seq(CnCodeInformationItem("T400", "24029000")))
    val requestJson = Json.obj("items" -> Json.arr(
      Json.obj(
        "productCode" -> "T400",
        "cnCode" -> "24029000"
      )
    ))

    "return a response model when the server responds OK" in {

      wireMockServer.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestJson)))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.obj(
            "24029000" -> Json.obj(
              "cnCode" -> "T400",
              "cnCodeDescription" -> "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
              "exciseProductCode" -> "24029000",
              "exciseProductCodeDescription" -> "Fine-cut tobacco for the rolling of cigarettes",
              "unitOfMeasureCode" -> 1
            )
          ))))
      )

      connector.getCnCodeInformation(request).futureValue mustBe Right(CnCodeInformationResponse(data = Map(
        "24029000" -> CnCodeInformation(
          cnCode = "T400",
          cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
          exciseProductCode = "24029000",
          exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
          unitOfMeasure = Kilograms
        )
      )))
    }

    "must fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestJson)))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getCnCodeInformation(request).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with a body that can't be parsed to the expected response model" in {

      wireMockServer.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestJson)))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.obj(
            "24029000" -> JsArray(Seq(Json.obj(
              "cnCodeDescription" -> "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
              "unitOfMeasureCode" -> 1
            )))
          ))))
      )

      connector.getCnCodeInformation(request).futureValue mustBe Left(JsonValidationError)
    }

    "must fail when the connection fails" in {

      wireMockServer.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(request))))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getCnCodeInformation(request).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
