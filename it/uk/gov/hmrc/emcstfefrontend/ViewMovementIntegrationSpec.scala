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
import play.api.http.{HeaderNames, Status}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import uk.gov.hmrc.emcstfefrontend.fixtures.BaseFixtures
import uk.gov.hmrc.emcstfefrontend.stubs.{AuthStub, DownstreamStub}
import uk.gov.hmrc.emcstfefrontend.support.IntegrationBaseSpec

import scala.xml.Elem

class ViewMovementIntegrationSpec extends IntegrationBaseSpec with BaseFixtures {


  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = s"/consignment/$testErn/$testArc"
    def emcsTfeUri: String = s"/emcs-tfe/movement/$testErn/$testArc"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
    }
  }

  "Calling the mode of transport page" when {

    "user is unauthorised" must {
      "redirect to the Unauthorised controller" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.SEE_OTHER
        response.header(HeaderNames.LOCATION) shouldBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
      }
    }

    "user is authorised" should {

      "return unauthorised" when {
        "ERN from the URL does not match the ERN of the logged in User" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised("wrongErn")
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.SEE_OTHER
          response.header(HeaderNames.LOCATION) shouldBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
        }
      }

      "return a success page" when {
        "all downstream calls are successful" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, emcsTfeUri, Status.OK, Json.parse(
              """{
                |  "localReferenceNumber": "MyLrn",
                |  "eadStatus": "MyEadStatus",
                |   "consignorTrader" : {
                |     "traderExciseNumber" : "GB12345GTR144",
                |     "traderName" : "MyConsignor",
                |     "address": {
                |       "street" : "Main101",
                |       "postcode" : "ZZ78",
                |       "city" : "Zeebrugge"
                |     }
                |   },
                |  "dateOfDispatch": "2010-03-04",
                |  "journeyTime": "MyJourneyTime",
                |  "numberOfItems": 0
                |}""".stripMargin))
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.OK
          response.body should include("Administrative reference code")
          response.body should include(testArc)
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
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, emcsTfeUri, Status.OK, emcsTfeResponseBody)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.body should include("Sorry, we’re experiencing technical difficulties")
        }

        "downstream call returns something other than JSON" in new Test {
          val emcsTfeResponseBody: Elem = <message>test message</message>

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
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
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, emcsTfeUri, Status.INTERNAL_SERVER_ERROR, emcsTfeResponseBody)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe Status.INTERNAL_SERVER_ERROR
          response.body should include("Sorry, we’re experiencing technical difficulties")
        }
      }
    }
  }
}
