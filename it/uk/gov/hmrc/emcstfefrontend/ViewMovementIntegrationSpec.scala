/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.emcstfefrontend

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfefrontend.fixtures.ModeOfTransportListFixture
import uk.gov.hmrc.emcstfefrontend.stubs.DownstreamStub
import uk.gov.hmrc.emcstfefrontend.support.IntegrationBaseSpec

import scala.xml.Elem

class ViewMovementIntegrationSpec extends IntegrationBaseSpec with ModeOfTransportListFixture {


  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = "/consignment/my-ern/my-arc"
    def emcsTfeUri: String = s"/emcs-tfe/movement/my-ern/my-arc"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
    }
  }

  "Calling the mode of transport page" should {
    "return a success page" when {
      "all downstream calls are successful" in new Test {
        override def setupStubs(): StubMapping = {
          DownstreamStub.onSuccess(DownstreamStub.GET, emcsTfeUri, Status.OK, Json.parse(
            """{
              |  "localReferenceNumber": "MyLrn",
              |  "eadStatus": "MyEadStatus",
              |  "consignorName": "MyConsignor",
              |  "dateOfDispatch": "2010-03-04",
              |  "journeyTime": "MyJourneyTime",
              |  "numberOfItems": 0
              |}""".stripMargin))
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
        response.body should include("Administrative reference code")
        response.body should include("my-arc")
      }
    }
    "return an error page" when {
      "downstream call returns unexpected JSON" in new Test {
        val emcsTfeResponseBody: JsValue = Json.parse(
          s"""
             |{
             |   "field": "test message"
             |}
             |""".stripMargin
        )

        override def setupStubs(): StubMapping = {
          DownstreamStub.onSuccess(DownstreamStub.GET, emcsTfeUri, Status.OK, emcsTfeResponseBody)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.body should include("Sorry, we’re experiencing technical difficulties")
      }

      "downstream call returns something other than JSON" in new Test {
        val emcsTfeResponseBody: Elem = <message>test message</message>

        override def setupStubs(): StubMapping = {
          DownstreamStub.onSuccess(DownstreamStub.GET, emcsTfeUri, Status.OK, emcsTfeResponseBody)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.header("Content-Type") shouldBe Some("text/html; charset=UTF-8")
        response.body should include("Sorry, we’re experiencing technical difficulties")
      }
      "downstream call returns a non-200 HTTP response" in new Test {
        val emcsTfeResponseBody: JsValue = Json.parse(
          s"""
             |{
             |   "message": "test message"
             |}
             |""".stripMargin
        )

        override def setupStubs(): StubMapping = {
          DownstreamStub.onSuccess(DownstreamStub.GET, emcsTfeUri, Status.INTERNAL_SERVER_ERROR, emcsTfeResponseBody)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.INTERNAL_SERVER_ERROR
        response.body should include("Sorry, we’re experiencing technical difficulties")
      }
    }
  }
}
