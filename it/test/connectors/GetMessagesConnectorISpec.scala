package connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.GetMessagesConnector
import fixtures.{BaseFixtures, MessagesFixtures}
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.libs.json.Json
import play.api.test.Helpers._
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class GetMessagesConnectorISpec extends IntegrationBaseSpec
  with ScalaFutures
  with BaseFixtures
  with IntegrationPatience
  with MessagesFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  val url = s"/emcs-tfe/messages/$testErn"

  ".getMessages" must {

    lazy val connector: GetMessagesConnector = app.injector.instanceOf[GetMessagesConnector]

    "must return Right(Seq[GetMessagesResponse]) when the server responds OK" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.toJson(getMessageResponse))))
      )

      connector.getMessages(testErn).futureValue mustBe Right(getMessageResponse)
    }

    "must fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getMessages(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getMessages(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
