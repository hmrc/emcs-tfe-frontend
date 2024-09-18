import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.referenceData.GetTraderKnownFactsConnector
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{EitherValues, OptionValues}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier


class GetTraderKnownFactsIntegrationSpec
  extends IntegrationBaseSpec
    with ScalaFutures
    with IntegrationPatience
    with EitherValues
    with OptionValues {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  def stubUrl(ern: String) = s"/emcs-tfe-reference-data/oracle/trader-known-facts?exciseRegistrationId=$ern"

  def emcsTfeUrl(ern: String) = s"/emcs-tfe/trader-known-facts?exciseRegistrationId=$ern"

  ".getTraderKnownFacts" must {
    def app: Application =
      new GuiceApplicationBuilder()
        .configure("microservice.services.emcs-tfe.port" -> wireMockServer.port)
        .build()

    lazy val connector: GetTraderKnownFactsConnector = app.injector.instanceOf[GetTraderKnownFactsConnector]

    "return Right(Seq[TraderKnownFacts]) when the server responds OK" in {

      wireMockServer.stubFor(
        get(urlEqualTo(emcsTfeUrl(testErn)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.obj("traderName" -> "testTraderName"))))
      )

      connector.getTraderKnownFacts(testErn).futureValue mustBe Right(Some(testMinTraderKnownFacts))
    }

    "return Right(None) when the server responds NO_CONTENT" in {

      wireMockServer.stubFor(
        get(urlEqualTo(emcsTfeUrl(testErn)))
          .willReturn(aResponse().withStatus(NO_CONTENT))
      )

      connector.getTraderKnownFacts(testErn).futureValue mustBe Right(None)
    }

    "fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        get(urlEqualTo(emcsTfeUrl(testErn)))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getTraderKnownFacts(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "fail when the connection fails" in {

      wireMockServer.stubFor(
        get(urlEqualTo(emcsTfeUrl(testErn)))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getTraderKnownFacts(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
