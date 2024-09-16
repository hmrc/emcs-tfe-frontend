package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.DraftTemplatesConnector
import fixtures.DraftTemplatesFixtures
import models.draftTemplates.TemplateList
import models.movementScenario.MovementScenario.UnknownDestination
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NO_CONTENT, OK}
import play.api.libs.json.Json
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class DraftTemplatesConnectorISpec extends IntegrationBaseSpec with DraftTemplatesFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val connector: DraftTemplatesConnector = app.injector.instanceOf[DraftTemplatesConnector]

  ".list()" must {

    val url = s"/emcs-tfe/templates/$testErn?page=1&pageSize=10"

    "return a non-empty Seq[Template] when the server responds OK" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(OK).withBody(Json.parse(
            s"""
              |{
              |  "templates": [
              |    {
              |      "ern": "$testErn",
              |      "templateId": "$testDraftId",
              |      "templateName": "template name",
              |      "data": {
              |        "info": {
              |          "destinationType": "unknownDestination"
              |        }
              |      },
              |      "lastUpdated": "2020-01-01T00:00:00Z"
              |    }
              |  ],
              |  "count": 1
              |}
              |""".stripMargin).toString()))
      )

      connector.list(testErn, 1).futureValue mustBe Right(
        TemplateList(Seq(createTemplate(testErn, testDraftId, "template name", UnknownDestination, None)), 1))
    }

    "return an empty Seq when the server responds NO_CONTENT" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(NO_CONTENT))
      )

      connector.list(testErn, 1).futureValue mustBe Right(TemplateList(Seq(), 0))
    }

    "must fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.list(testErn, 1).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with a body that can't be parsed to the expected response model" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.obj(
            "x" -> "y"
          ))))
      )

      connector.list(testErn, 1).futureValue mustBe Left(JsonValidationError)
    }

    "must fail when the connection fails" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.list(testErn, 1).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
