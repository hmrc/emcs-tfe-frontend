package uk.gov.hmrc.emcstfefrontend

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfefrontend.stubs.DownstreamStub
import uk.gov.hmrc.emcstfefrontend.support.IntegrationBaseSpec
import uk.gov.hmrc.emcstfefrontend.support.ModeOfTransportListFixture.validModeOfTransportListJson

import scala.xml.Elem

class ModeOfTransportControllerIntegrationSpec extends IntegrationBaseSpec {


  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = "/mode-of-transport"
    def referenceDataUri: String = s"/emcs-tfe-reference-data/other-reference-data-list"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
    }
  }

  "Calling the mode of transport page" should {
    "return a success page" when {
      "all downstream calls are successful" in new Test {
        override def setupStubs(): StubMapping = {
          DownstreamStub.onSuccess(DownstreamStub.GET, referenceDataUri, Status.OK, validModeOfTransportListJson)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
        response.body should include("Other")
        response.body should include("Postal consignment")
      }
    }
    "return an error page" when {
      "downstream call returns unexpected JSON" in new Test {
        val referenceDataResponseBody: JsValue = Json.parse(
          s"""
             |{
             |   "field": "test message"
             |}
             |""".stripMargin
        )

        override def setupStubs(): StubMapping = {
          DownstreamStub.onSuccess(DownstreamStub.GET, referenceDataUri, Status.OK, referenceDataResponseBody)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.body should include("Something went wrong!")
      }

      "downstream call returns something other than JSON" in new Test {
        val referenceDataResponseBody: Elem = <message>test message</message>

        override def setupStubs(): StubMapping = {
          DownstreamStub.onSuccess(DownstreamStub.GET, referenceDataUri, Status.OK, referenceDataResponseBody)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.header("Content-Type") shouldBe Some("text/html; charset=UTF-8")
        response.body should include("Something went wrong!")
      }
      "downstream call returns a non-200 HTTP response" in new Test {
        val referenceDataResponseBody: JsValue = Json.parse(
          s"""
             |{
             |   "message": "test message"
             |}
             |""".stripMargin
        )

        override def setupStubs(): StubMapping = {
          DownstreamStub.onSuccess(DownstreamStub.GET, referenceDataUri, Status.INTERNAL_SERVER_ERROR, referenceDataResponseBody)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.body should include("Something went wrong!")
      }
    }
  }
}
