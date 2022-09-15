/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfefrontend.stubs.ReferenceDataStub
import uk.gov.hmrc.emcstfefrontend.support.IntegrationBaseSpec

import scala.xml.Elem

class HelloWorldIntegrationSpec extends IntegrationBaseSpec {

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = "/"
    def referenceDataUri: String = s"/emcs-tfe-reference-data/hello-world"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
    }
  }

  "Calling the hello world endpoint" should {
    "return a success page" when {
      "all downstream calls are successful" in new Test {
        val referenceDataResponseBody: JsValue = Json.parse(
          s"""
             |{
             |   "message": "test message"
             |}
             |""".stripMargin
        )
        override def setupStubs(): StubMapping = {
          ReferenceDataStub.onSuccess(ReferenceDataStub.GET, referenceDataUri, Status.OK, referenceDataResponseBody)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
        response.header("Content-Type") shouldBe Some("text/html; charset=UTF-8")
        response.body should include("test message")
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
          ReferenceDataStub.onSuccess(ReferenceDataStub.GET, referenceDataUri, Status.OK, referenceDataResponseBody)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.header("Content-Type") shouldBe Some("text/html; charset=UTF-8")
        response.body should include("JSON validation error")
      }
      "downstream call returns something other than JSON" in new Test {
        val referenceDataResponseBody: Elem = <message>test message</message>

        override def setupStubs(): StubMapping = {
          ReferenceDataStub.onSuccess(ReferenceDataStub.GET, referenceDataUri, Status.OK, referenceDataResponseBody)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.header("Content-Type") shouldBe Some("text/html; charset=UTF-8")
        response.body should include("JSON validation error")
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
          ReferenceDataStub.onSuccess(ReferenceDataStub.GET, referenceDataUri, Status.INTERNAL_SERVER_ERROR, referenceDataResponseBody)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.header("Content-Type") shouldBe Some("text/html; charset=UTF-8")
        response.body should include("Unexpected downstream response status")
      }
    }
  }
}
