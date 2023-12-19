package connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.referenceData.GetItemPackagingTypesConnector
import fixtures.ItemFixtures
import models.response.UnexpectedDownstreamResponseError
import play.api.test.Helpers._
import support.IntegrationBaseSpec
import uk.gov.hmrc.http.HeaderCarrier

class GetItemPackagingTypesConnectorISpec extends IntegrationBaseSpec with ItemFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  def url = s"/emcs-tfe-reference-data/oracle/packaging-types"

  ".getItemPackagingTypes" must {

    lazy val connector: GetItemPackagingTypesConnector = app.injector.instanceOf[GetItemPackagingTypesConnector]

    s"return Right(Seq[ItemPackaging]) when the server responds OK" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(testItemPackagingTypesJson.toString()))
      )

      connector.getItemPackagingTypes.futureValue mustBe Right(testItemPackagingTypes)
    }

    "must fail when the server responds with any other status" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getItemPackagingTypes.futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      wireMockServer.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getItemPackagingTypes.futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
