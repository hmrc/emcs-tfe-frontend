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

package views

import base.SpecBase
import fixtures.MovementListFixtures
import fixtures.messages.ViewMovementListMessages.English
import models.auth.UserRequest
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import play.twirl.api.Html
import viewmodels.MovementsListTableHelper
import views.html.ViewMovementListPage
import views.html.components.table

class ViewMovementListPageViewSpec extends SpecBase with MovementListFixtures {

  abstract class TestFixture(implicit messages: Messages) {

    implicit val fakeRequest = FakeRequest("GET", "/movements")

    val page: ViewMovementListPage = app.injector.instanceOf[ViewMovementListPage]
    val helper: MovementsListTableHelper = app.injector.instanceOf[MovementsListTableHelper]
    val table: table = app.injector.instanceOf[table]

    implicit lazy val messagesApi = app.injector.instanceOf[MessagesApi]

    val dataRequest = DataRequest(
      UserRequest(fakeRequest, testErn, testInternalId, testCredId, hasMultipleErns = false),
      testMinTraderKnownFacts
    )

    lazy val html: Html = page(testErn, getMovementListResponse)(dataRequest, messages)
    lazy val document: Document = Jsoup.parse(html.toString)
  }

  "The ViewMovementListPage view" when {

    Seq(English) foreach { viewMessages =>

      implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(viewMessages.lang))

      s"being rendered for ${viewMessages.lang.code}" must {

        s"have the correct title" in new TestFixture {
          document.title mustBe viewMessages.title
        }

        s"have the correct h1" in new TestFixture {
          document.select("h1").text() mustBe viewMessages.heading(getMovementListResponse.count)
        }

        "have a table rendered for the movements, rendered by the helper" in new TestFixture {
          document.select("table thead tr th:nth-of-type(1)").text() mustBe viewMessages.tableArc
          document.select("table thead tr th:nth-of-type(2)").text() mustBe viewMessages.tableConsignor
        }
      }
    }
  }
}
