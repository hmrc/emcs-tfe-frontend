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
import models.messages.MessageStatisticsCache
import play.api.http.Status
import play.api.i18n.{Lang, Messages}
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import repositories.MessageStatisticsRepositoryImpl
import stubs.{AuthStub, DownstreamStub}
import support.IntegrationBaseSpec
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.servicenavigation.ServiceNavigationItem
import uk.gov.hmrc.mongo.test.PlayMongoRepositorySupport

class NavBarPartialIntegrationSpec extends IntegrationBaseSpec
  with PlayMongoRepositorySupport[MessageStatisticsCache] {

  override lazy val repository = app.injector.instanceOf[MessageStatisticsRepositoryImpl]

  override def beforeEach(): Unit = {
    dropCollection()
    ensureIndexes()
  }

  private trait Test {

    implicit val msgs: Messages = messages(Seq(Lang("en")))

    def setupStubs(): StubMapping

    def getMessageStatisticsUri: String = s"/emcs-tfe/message-statistics/$testErn"

    def request(): WSRequest = {
      setupStubs()
      buildAPIRequest(s"/emcs/partials/navigation-items/trader/$testErn")
    }
  }

  s"Calling /emcs/partials/navigation-items/trader/$testErn" when {

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

          val items: Seq[ServiceNavigationItem] = Json.parse(response.body).as[Seq[ServiceNavigationItem]]
          val messagesItem: Option[ServiceNavigationItem] = getMessagesItem(items)

          messagesItem.fold("")(item => item.content.asHtml.toString) must include regex """Messages\s*<span[^>]*>\s*1\s*</span>""".r
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


          val items: Seq[ServiceNavigationItem] = Json.parse(response.body).as[Seq[ServiceNavigationItem]]
          val messagesItem: Option[ServiceNavigationItem] = getMessagesItem(items)

          messagesItem.fold("")(item => item.content.asHtml.toString) mustBe "Messages"
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

  private def getMessagesItem(items: Seq[ServiceNavigationItem]): Option[ServiceNavigationItem] = items.find {
      case ServiceNavigationItem(HtmlContent(content), _, _, _, _, _) =>
        content.toString().contains("Messages")
      case _ =>
        false
  }

}

