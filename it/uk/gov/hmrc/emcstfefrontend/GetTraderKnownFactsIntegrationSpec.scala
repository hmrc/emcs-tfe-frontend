package uk.gov.hmrc.emcstfefrontend

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{EitherValues, OptionValues}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.emcstfefrontend.connectors.referenceData.GetTraderKnownFactsConnector
import uk.gov.hmrc.emcstfefrontend.models.common.TraderKnownFacts
import uk.gov.hmrc.emcstfefrontend.models.response.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfefrontend.support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier


import scala.concurrent.ExecutionContext.Implicits.global

class GetTraderKnownFactsIntegrationSpec
  extends IntegrationBaseSpec
  with ScalaFutures
  with IntegrationPatience
  with EitherValues
  with OptionValues {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  def url(ern: String) = s"/emcs-tfe-reference-data/oracle/trader-known-facts?exciseRegistrationId=$ern"

  val testErn = "testErn"
  val testMinTraderKnownFacts: TraderKnownFacts = TraderKnownFacts(
    traderName = "testTraderName",
    addressLine1 = None,
    addressLine2 = None,
    addressLine3 = None,
    addressLine4 = None,
    addressLine5 = None,
    postcode = None
  )

  ".getTraderKnownFacts" should {
    "when the feature switch is disabled" when {

      def app: Application =
        new GuiceApplicationBuilder()
          .configure("microservice.services.emcs-tfe-reference-data.port" -> wireMockServer.port)
          .configure("features.stub-get-trader-known-facts" -> "false")
          .build()

      lazy val connector: GetTraderKnownFactsConnector = app.injector.instanceOf[GetTraderKnownFactsConnector]

      "must return Right(Seq[TraderKnownFacts]) when the server responds OK" in {

        wireMockServer.stubFor(
          get(urlEqualTo(url(testErn)))
            .willReturn(
              aResponse()
                .withStatus(OK)
                .withBody(Json.stringify(Json.obj("traderName" -> "testTraderName"))))
        )

        connector.getTraderKnownFacts(testErn).futureValue shouldBe Right(Some(testMinTraderKnownFacts))
      }

      "must return Right(None) when the server responds NO_CONTENT" in {

        wireMockServer.stubFor(
          get(urlEqualTo(url(testErn)))
            .willReturn(aResponse().withStatus(NO_CONTENT))
        )

        connector.getTraderKnownFacts(testErn).futureValue shouldBe Right(None)
      }

      "must fail when the server responds with any other status" in {

        wireMockServer.stubFor(
          get(urlEqualTo(url(testErn)))
            .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
        )

        connector.getTraderKnownFacts(testErn).futureValue shouldBe Left(UnexpectedDownstreamResponseError)
      }

      "must fail when the connection fails" in {

        wireMockServer.stubFor(
          get(urlEqualTo(url(testErn)))
            .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
        )

        connector.getTraderKnownFacts(testErn).futureValue shouldBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  "when the feature switch is enabled" should {

    def app: Application =
      new GuiceApplicationBuilder()
        .configure("microservice.services.emcs-tfe-reference-data-stub.port" -> wireMockServer.port)
        .configure("features.stub-get-trader-known-facts" -> "true")
        .build()

    lazy val connector: GetTraderKnownFactsConnector = app.injector.instanceOf[GetTraderKnownFactsConnector]

    "must return true when the server responds OK" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url(testErn)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.obj("traderName" -> "testTraderName"))))
      )

      connector.getTraderKnownFacts(testErn).futureValue shouldBe Right(Some(testMinTraderKnownFacts))
    }

    "must return false when the server responds NO_CONTENT" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url(testErn)))
          .willReturn(aResponse().withStatus(NO_CONTENT))
      )

      connector.getTraderKnownFacts(testErn).futureValue shouldBe Right(None)
    }

    "must fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url(testErn)))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getTraderKnownFacts(testErn).futureValue shouldBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url(testErn)))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getTraderKnownFacts(testErn).futureValue shouldBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
