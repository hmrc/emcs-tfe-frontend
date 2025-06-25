package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import connectors.referenceData.GetWineOperationsConnector
import fixtures.BaseFixtures
import models.requests.WineOperationsRequest
import models.response.referenceData.WineOperationsResponse
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import org.scalatest.concurrent.ScalaFutures
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.{JsArray, JsString, Json}
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class GetWineOperationsConnectorISpec extends IntegrationBaseSpec with ScalaFutures with BaseFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val connector: GetWineOperationsConnector = app.injector.instanceOf[GetWineOperationsConnector]

  ".getWineOperations" must {

    val url = "/emcs-tfe-reference-data/oracle/wine-operations"
    val request = WineOperationsRequest(Seq("4", "11", "9"))
    val requestJson = JsArray(Seq(JsString("4"), JsString("11"), JsString("9")))

    "return a response model when the server responds OK" in {

      wireMockServer.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestJson)))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.obj(
            "4" -> "The product has been sweetened",
            "11" -> "The product has been partially dealcoholised",
            "9" -> "The product has been made using oak chips"
          ))))
      )

      connector.getWineOperations(request).futureValue mustBe Right(WineOperationsResponse(data = Map(
        "4" -> "The product has been sweetened",
        "11" -> "The product has been partially dealcoholised",
        "9" -> "The product has been made using oak chips"
      )))
    }

    "fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestJson)))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getWineOperations(request).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "fail when the server responds with a body that can't be parsed to the expected response model" in {

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

      connector.getWineOperations(request).futureValue mustBe Left(JsonValidationError)
    }

    "fail when the connection fails" in {

      wireMockServer.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(request))))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getWineOperations(request).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
