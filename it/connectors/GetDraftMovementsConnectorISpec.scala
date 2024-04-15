package connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.GetDraftMovementsConnector
import fixtures.{BaseFixtures, DraftMovementsFixtures, MessagesFixtures}
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.libs.json.Json
import play.api.test.Helpers._
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class GetDraftMovementsConnectorISpec extends IntegrationBaseSpec
  with ScalaFutures
  with BaseFixtures
  with IntegrationPatience
  with DraftMovementsFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  def url: String = s"/emcs-tfe/user-answers/create-movement/drafts/search/$testErn"

  ".getDraftMovements" must {

    lazy val connector: GetDraftMovementsConnector = app.injector.instanceOf[GetDraftMovementsConnector]

    "must return Right(Seq[GetDraftMovementsResponse]) when the server responds OK" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(getDraftMovementsResponseJsonMax)))
      )

      connector.getDraftMovements(testErn).futureValue mustBe Right(getDraftMovementsResponseModelMax)
    }

    "must fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getDraftMovements(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getDraftMovements(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
