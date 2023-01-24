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

package uk.gov.hmrc.emcstfefrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.emcstfefrontend.fixtures.MovementListFixtures
import uk.gov.hmrc.emcstfefrontend.fixtures.messages.ViewMovementListMessages.{English, Welsh}
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.emcstfefrontend.viewmodels.MovementsListTableHelper
import uk.gov.hmrc.emcstfefrontend.views.html.ViewMovementListPage
import uk.gov.hmrc.emcstfefrontend.views.html.components.table

class ViewMovementListPageViewSpec extends UnitSpec with MovementListFixtures {

  abstract class TestFixture(implicit messages: Messages) {

    implicit val fakeRequest = FakeRequest("GET", "/movements")

    val page: ViewMovementListPage = app.injector.instanceOf[ViewMovementListPage]
    val helper: MovementsListTableHelper = app.injector.instanceOf[MovementsListTableHelper]
    val table: table = app.injector.instanceOf[table]

    lazy val html: Html = page(ern, getMovementListResponse)
    lazy val document: Document = Jsoup.parse(html.toString)
  }

  "The ViewMovementListPage view" when {

    Seq(English, Welsh) foreach { viewMessages =>

      implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(viewMessages.lang))

      s"being rendered for ${viewMessages.lang.code}" should {

        s"have the correct title" in new TestFixture {
          document.title shouldBe viewMessages.title
        }

        s"have the correct h1" in new TestFixture {
          document.select("h1").text() shouldBe viewMessages.heading(getMovementListResponse.count)
        }

        "have a table rendered for the movements, rendered by the helper" in new TestFixture {
          document.select("table thead tr th:nth-of-type(1)").text() shouldBe viewMessages.tableArc
          document.select("table thead tr th:nth-of-type(2)").text() shouldBe viewMessages.tableDateOfDisptach
          document.select("table thead tr th:nth-of-type(3)").text() shouldBe viewMessages.tableStatus
        }
      }
    }
  }
}
