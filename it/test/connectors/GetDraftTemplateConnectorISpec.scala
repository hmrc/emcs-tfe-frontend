package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.GetDraftTemplateConnector
import models.draftTemplates.Template
import models.response.{JsonValidationError, NoContentError, UnexpectedDownstreamResponseError}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NO_CONTENT, OK}
import play.api.libs.json.Json
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

import java.time.Instant

class GetDraftTemplateConnectorISpec extends IntegrationBaseSpec {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val connector: GetDraftTemplateConnector = app.injector.instanceOf[GetDraftTemplateConnector]

  ".getTemplate()" must {

    val url = s"/emcs-tfe/template/GBRC123456789/1"

    "return a template when the server responds OK" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(OK).withBody(Json.parse(
            s"""
               |{
               |  "ern": "GBRC123456789",
               |  "templateId": "1",
               |  "templateName": "template name",
               |  "data": {
               |   "info": {
               |          "destinationType": "gbTaxWarehouse"
               |    }
               |   },
               |   "lastUpdated": "2024-09-12T20:58:10.997176Z"
               |}
               |""".stripMargin).toString()))
      )

      connector.getTemplate("GBRC123456789", "1").futureValue mustBe Right(Some(Template(
        ern = "GBRC123456789",
        templateId = "1",
        templateName = "template name",
        data = Json.obj(
          "info" -> Json.obj(
            "destinationType" -> "gbTaxWarehouse"
          )
        ),
        lastUpdated = Instant.parse("2024-09-12T20:58:10.997176Z")
      )))
    }

    "return a None when the server responds NO_CONTENT" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(NO_CONTENT))
      )

      connector.getTemplate("GBRC123456789", "1").futureValue mustBe Right(None)
    }

    "must fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getTemplate("GBRC123456789", "1").futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with a body that can't be parsed to the expected response model" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.obj(
            "x" -> "y"
          ))))
      )

      connector.getTemplate("GBRC123456789", "1").futureValue mustBe Left(JsonValidationError)
    }

    "must fail when the connection fails" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getTemplate("GBRC123456789", "1").futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }

  ".set()" must {

    val url = s"/emcs-tfe/template/GBRC123456789/1"

    val updatedTemplate = Template(
      ern = "GBRC123456789",
      templateId = "1",
      templateName = "template name2",
      data = Json.obj(
        "info" -> Json.obj(
          "destinationType" -> "gbTaxWarehouse"
        )
      ),
      lastUpdated = Instant.parse("2024-09-12T20:58:10.997176Z")
    )

    "return a template when the server responds OK" in {

      wireMockServer.stubFor(
        put(urlEqualTo(url))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.toJson(updatedTemplate))))
      )

      connector.set("GBRC123456789", "1", updatedTemplate).futureValue mustBe Right(Template(
        ern = "GBRC123456789",
        templateId = "1",
        templateName = "template name2",
        data = Json.obj(
          "info" -> Json.obj(
            "destinationType" -> "gbTaxWarehouse"
          )
        ),
        lastUpdated = Instant.parse("2024-09-12T20:58:10.997176Z")
      ))
    }

    "return a NoContentError when the server responds NO_CONTENT" in {

      wireMockServer.stubFor(
        put(urlEqualTo(url))
          .willReturn(aResponse().withStatus(NO_CONTENT))
      )

      connector.set("GBRC123456789", "1", updatedTemplate).futureValue mustBe Left(NoContentError)
    }

    "must fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        put(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.set("GBRC123456789", "1", updatedTemplate).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with a body that can't be parsed to the expected response model" in {

      wireMockServer.stubFor(
        put(urlEqualTo(url))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.obj(
            "x" -> "y"
          ))))
      )

      connector.set("GBRC123456789", "1", updatedTemplate).futureValue mustBe Left(JsonValidationError)
    }

    "must fail when the connection fails" in {

      wireMockServer.stubFor(
        put(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.set("GBRC123456789", "1", updatedTemplate).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
