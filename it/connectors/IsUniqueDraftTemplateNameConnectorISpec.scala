package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.IsUniqueDraftTemplateNameConnector
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class IsUniqueDraftTemplateNameConnectorISpec extends IntegrationBaseSpec {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val connector: IsUniqueDraftTemplateNameConnector = app.injector.instanceOf[IsUniqueDraftTemplateNameConnector]

  ".doesExist()" must {

    val url = s"/emcs-tfe/template/name-already-exists?ern=GBRC123456789&templateName=name"

    "return a boolean when the server responds OK" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(OK).withBody("true"))
      )

      connector.doesExist("GBRC123456789", "name").futureValue mustBe Right(true)
    }

    "must fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.doesExist("GBRC123456789", "name").futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with a body that can't be parsed to the expected response model" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.obj(
            "x" -> "y"
          ))))
      )

      connector.doesExist("GBRC123456789", "name").futureValue mustBe Left(JsonValidationError)
    }

    "must fail when the connection fails" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.doesExist("GBRC123456789", "name").futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
