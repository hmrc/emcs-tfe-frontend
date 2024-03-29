package connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.referenceData.GetExciseProductCodesConnector
import fixtures.{BaseFixtures, ExciseProductCodeFixtures}
import models.ExciseProductCode
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import play.api.libs.json.Json
import play.api.test.Helpers._
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class GetExciseProductCodesConnectorISpec extends IntegrationBaseSpec
  with ScalaFutures
  with BaseFixtures
  with IntegrationPatience
  with ExciseProductCodeFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  val url = "/emcs-tfe-reference-data/oracle/epc-codes"

  val exciseProductCodes: Seq[ExciseProductCode] = Seq(
    beerExciseProductCode,
    wineExciseProductCode
  )

  ".getExciseProductCodes" must {

    lazy val connector: GetExciseProductCodesConnector = app.injector.instanceOf[GetExciseProductCodesConnector]

    "must return Right(Seq[ExciseProductCode]) when the server responds OK" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.arr(beerExciseProductCode, wineExciseProductCode))))
      )

      connector.getExciseProductCodes().futureValue mustBe Right(exciseProductCodes)
    }

    "must fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getExciseProductCodes().futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getExciseProductCodes().futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
