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
