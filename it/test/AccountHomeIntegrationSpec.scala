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

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import fixtures.BaseFixtures
import play.api.http.{HeaderNames, Status}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import stubs.{AuthStub, DownstreamStub}
import support.IntegrationBaseSpec

import scala.xml.Elem

class AccountHomeIntegrationSpec extends IntegrationBaseSpec with BaseFixtures {

  private trait Test {
    def setupStubs(): StubMapping

    def uri: String = s"/trader/$testErn/account"

    def getMessageStatisticsUri: String = s"/emcs-tfe/message-statistics/$testErn"
    def getTraderKnownFactsUri: String = s"/emcs-tfe-reference-data/oracle/trader-known-facts"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
    }
  }

  "Calling the account home page" when {

    "user is unauthorised" must {
      "redirect to the Unauthorised controller" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request().get())
        response.status mustBe Status.SEE_OTHER
        response.header(HeaderNames.LOCATION) mustBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
      }
    }

    "user is authorised" must {

      "return unauthorised" when {
        "ERN from the URL does not match the ERN of the logged in User" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised("wrongErn")
          }

          val response: WSResponse = await(request().get())
          response.status mustBe Status.SEE_OTHER
          response.header(HeaderNames.LOCATION) mustBe Some(controllers.errors.routes.UnauthorisedController.unauthorised().url)
        }
      }

      "return a success page" when {
        "all downstream calls are successful" in new Test {
          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, getTraderKnownFactsUri, Map("exciseRegistrationId" -> testErn), Status.OK, Json.toJson(testMinTraderKnownFacts))
            DownstreamStub.onSuccess(DownstreamStub.GET, getMessageStatisticsUri, Status.OK, Json.parse(
              s"""{
                 |  "dateTime" : "testDateTime",
                 |  "exciseRegistrationNumber" : "$testErn",
                 |  "countOfAllMessages" : 1,
                 |  "countOfNewMessages" : 1
                 |}""".stripMargin))
          }

          val response: WSResponse = await(request().get())
          response.status mustBe Status.OK
        }

        "downstream call to Message Statistics returns unexpected JSON" in new Test {
          val emcsTfeResponseBody: JsValue = Json.parse(
            s"""
               |{
               |   "field": "test message"
               |}
               |""".stripMargin
          )

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, getTraderKnownFactsUri, Map("exciseRegistrationId" -> testErn), Status.OK, Json.toJson(testMinTraderKnownFacts))
            DownstreamStub.onSuccess(DownstreamStub.GET, getMessageStatisticsUri, Status.OK, emcsTfeResponseBody)
          }

          val response: WSResponse = await(request().get())
          response.status mustBe Status.OK
        }

        "downstream call to Message Statistics returns something other than JSON" in new Test {
          val emcsTfeResponseBody: Elem = <message>test message</message>

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, getTraderKnownFactsUri, Map("exciseRegistrationId" -> testErn), Status.OK, Json.toJson(testMinTraderKnownFacts))
            DownstreamStub.onSuccess(DownstreamStub.GET, getMessageStatisticsUri, Status.OK, emcsTfeResponseBody)
          }

          val response: WSResponse = await(request().get())
          response.status mustBe Status.OK
        }
        "downstream call to Message Statistics returns a non-200 HTTP response" in new Test {
          val emcsTfeResponseBody: JsValue = Json.parse(
            s"""
               |{
               |   "message": "test message"
               |}
               |""".stripMargin
          )

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, getTraderKnownFactsUri, Map("exciseRegistrationId" -> testErn), Status.OK, Json.toJson(testMinTraderKnownFacts))
            DownstreamStub.onSuccess(DownstreamStub.GET, getMessageStatisticsUri, Status.INTERNAL_SERVER_ERROR, emcsTfeResponseBody)
          }

          val response: WSResponse = await(request().get())
          response.status mustBe Status.OK
        }
      }
    }
  }
}
