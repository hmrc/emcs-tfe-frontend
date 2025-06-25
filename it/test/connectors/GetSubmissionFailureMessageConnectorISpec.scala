package connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.GetSubmissionFailureMessageConnector
import fixtures.{BaseFixtures, GetSubmissionFailureMessageFixtures}
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.libs.json.Json
import play.api.test.Helpers._
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class GetSubmissionFailureMessageConnectorISpec extends IntegrationBaseSpec
  with ScalaFutures
  with BaseFixtures
  with IntegrationPatience
  with GetSubmissionFailureMessageFixtures {

  import GetSubmissionFailureMessageResponseFixtures._

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  val url = s"/emcs-tfe/submission-failure-message/$testErn/$testUniqueMessageIdentifier"

  ".getSubmissionFailureMessage" must {

    lazy val connector: GetSubmissionFailureMessageConnector = app.injector.instanceOf[GetSubmissionFailureMessageConnector]

    "must return Right(Seq[GetMessagesResponse]) when the server responds OK" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(getSubmissionFailureMessageResponseJson)))
      )

      connector.getSubmissionFailureMessage(testErn, testUniqueMessageIdentifier).futureValue mustBe Right(getSubmissionFailureMessageResponseModel)
    }

    "must fail when the server responds with incorrect JSON" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse()
            .withStatus(OK)
            .withBody(Json.stringify(Json.obj())))
      )

      connector.getSubmissionFailureMessage(testErn, testUniqueMessageIdentifier).futureValue mustBe Left(JsonValidationError)
    }

    "must fail when the server responds with any other status (ISE returned and handled by the parser)" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getSubmissionFailureMessage(testErn, testUniqueMessageIdentifier).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails (an exception is thrown and handled by the connector)" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getSubmissionFailureMessage(testErn, testUniqueMessageIdentifier).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
