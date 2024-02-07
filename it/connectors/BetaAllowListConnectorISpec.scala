package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import connectors.betaAllowList.BetaAllowListConnector
import fixtures.BaseFixtures
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.test.Helpers.AUTHORIZATION
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.{Authorization, HeaderCarrier}

class BetaAllowListConnectorISpec
  extends IntegrationBaseSpec
    with ScalaFutures
    with Matchers
    with IntegrationPatience
    with EitherValues
    with OptionValues
    with BaseFixtures {

  val authToken: String = "auth-value"
  implicit private lazy val hc: HeaderCarrier = HeaderCarrier(authorization = Some(Authorization(authToken)))

  private lazy val connector: BetaAllowListConnector = app.injector.instanceOf[BetaAllowListConnector]

  ".check" must {

    val url = s"/emcs-tfe/beta/eligibility/$testErn/navHub"

    "return true when the server responds OK" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo(authToken))
          .willReturn(aResponse().withStatus(OK))
      )

      connector.check(testErn).futureValue mustBe Right(true)
    }

    "must return false when the server responds NOT_FOUND" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo(authToken))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.check(testErn).futureValue mustBe Right(false)
    }

    "must fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo(authToken))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.check(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo(authToken))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.check(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}