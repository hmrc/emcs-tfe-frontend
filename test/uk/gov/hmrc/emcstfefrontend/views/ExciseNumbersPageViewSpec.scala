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
import uk.gov.hmrc.emcstfefrontend.fixtures.messages.ExciseNumbersMessages
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.emcstfefrontend.views.html.ExciseNumbersPage
import uk.gov.hmrc.emcstfefrontend.views.html.components.link

class ExciseNumbersPageViewSpec extends UnitSpec with MovementListFixtures {

  implicit val fakeRequest = FakeRequest("GET", "/excise-numbers")

  lazy val page: ExciseNumbersPage = app.injector.instanceOf[ExciseNumbersPage]
  lazy val link: link = app.injector.instanceOf[link]

  abstract class TestFixture(implicit messages: Messages) {
    lazy val html: Html = page(Set("ern1", "ern2", "ern3"))
    lazy val document: Document = Jsoup.parse(html.toString)
  }

  "The ExciseNumbersPage view" when {

    Seq(ExciseNumbersMessages.English, ExciseNumbersMessages.Welsh) foreach { viewMessages =>

      implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(viewMessages.lang))

      s"being rendered for ${viewMessages.lang.code}" should {

        s"have the correct title" in new TestFixture {
          document.title shouldBe viewMessages.title
        }

        s"have the correct h1" in new TestFixture {
          document.select("h1").text() shouldBe viewMessages.heading
        }

        "have the correct p1" in new TestFixture {
          document.select("#multiple-emcs-numbers").text() shouldBe viewMessages.p1
        }

        "have the correct p2" in new TestFixture {
          document.select("#select-a-number").text() shouldBe viewMessages.p2
        }
      }
    }
  }
}
