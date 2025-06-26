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
import models.NavigationBannerInfo
import models.messages.MessageStatisticsCache
import play.api.http.Status
import play.api.i18n.{Lang, Messages}
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import play.twirl.api.Html
import repositories.MessageStatisticsRepositoryImpl
import stubs.{AuthStub, DownstreamStub}
import support.IntegrationBaseSpec
import uk.gov.hmrc.mongo.test.PlayMongoRepositorySupport
import views.html.components.navigation_bar

class NavBarPartialIntegrationSpec extends IntegrationBaseSpec
  with PlayMongoRepositorySupport[MessageStatisticsCache] {

  override lazy val repository = app.injector.instanceOf[MessageStatisticsRepositoryImpl]

  override def beforeEach(): Unit = {
    dropCollection()
    ensureIndexes()
  }

  private trait Test {

    val navBar: navigation_bar = app.injector.instanceOf[navigation_bar]
    implicit val msgs: Messages = messages(Seq(Lang("en")))

    def setupStubs(): StubMapping

    def getMessageStatisticsUri: String = s"/emcs-tfe/message-statistics/$testErn"

    def request(): WSRequest = {
      setupStubs()
      buildAPIRequest(s"/emcs/partials/navigation/trader/$testErn")
    }
  }

  s"Calling /emcs/partials/navigation/trader/$testErn" when {

    "request is Authorised" should {

      "when message statistics returns a success" should {

        "return the NavigationBar HTML including the notification count" in new Test {

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, getMessageStatisticsUri, Status.OK, Json.parse(
              s"""{
                 |  "dateTime" : "2024-01-01T12:45:12.123",
                 |  "exciseRegistrationNumber" : "$testErn",
                 |  "countOfAllMessages" : 1,
                 |  "countOfNewMessages" : 1
                 |}""".stripMargin))
          }

          val response: WSResponse = await(request().get())

          response.status mustBe Status.OK
          Html(response.body) mustBe navBar(NavigationBannerInfo(testErn, Some(1), None))
        }
      }

      "when message statistics fails" should {

        "return the NavigationBar HTML without the notification count" in new Test {

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            DownstreamStub.onSuccess(DownstreamStub.GET, getMessageStatisticsUri, Status.INTERNAL_SERVER_ERROR, Json.obj())
          }

          val response: WSResponse = await(request().get())

          response.status mustBe Status.OK
          Html(response.body) mustBe navBar(NavigationBannerInfo(testErn, None, None))
        }
      }
    }

    "request is Unauthorised" should {

      "return NO_CONTENT (204)" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.unauthorised()
        }

        val response: WSResponse = await(request().get())

        response.status mustBe Status.NO_CONTENT
      }
    }
  }
}
