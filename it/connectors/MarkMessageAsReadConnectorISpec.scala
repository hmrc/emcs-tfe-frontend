package connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, put, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.MarkMessageAsReadConnector
import fixtures.{BaseFixtures, MessagesFixtures}
import models.response.UnexpectedDownstreamResponseError
import models.response.emcsTfe.messages.MarkMessageAsReadResponse
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class MarkMessageAsReadConnectorISpec extends IntegrationBaseSpec
  with ScalaFutures
  with BaseFixtures
  with IntegrationPatience
  with OptionValues
  with MessagesFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val connector: MarkMessageAsReadConnector = app.injector.instanceOf[MarkMessageAsReadConnector]

  def url(): String = s"/emcs-tfe/message/$testErn/$testUniqueMessageIdentifier"

  ".markMessageAsRead" must {

    val body = MarkMessageAsReadResponse(
      recordsAffected = 1
    )

    "must return Right(MarkMessageAsReadResponse) when the server responds OK" in {
      wireMockServer.stubFor(
        put(urlEqualTo(url()))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.toJson(body))))
      )

      connector.markMessageAsRead(testErn, testUniqueMessageIdentifier).futureValue mustBe Right(body)
    }

    "must fail when the server response with any other status" in {
      wireMockServer.stubFor(
        put(urlEqualTo(url()))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.markMessageAsRead(testErn, testUniqueMessageIdentifier).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {
      wireMockServer.stubFor(
        put(urlEqualTo(url()))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.markMessageAsRead(testErn, testUniqueMessageIdentifier).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

  }
}
