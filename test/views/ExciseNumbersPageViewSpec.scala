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

import base.ViewSpecBase
import fixtures.messages.ExciseNumbersMessages
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.Html
import views.html.ExciseNumbersPage
import views.html.components.link

class ExciseNumbersPageViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    val multipleErnsText = "#multiple-emcs-numbers"
    val selectANumberText = "#select-a-number"
  }

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/excise-numbers")

  lazy val page: ExciseNumbersPage = app.injector.instanceOf[ExciseNumbersPage]
  lazy val link: link = app.injector.instanceOf[link]

  "The ExciseNumbersPage view" when {

    Seq(ExciseNumbersMessages.English) foreach { viewMessages =>

      implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(viewMessages.lang))

      s"being rendered for ${viewMessages.lang.code}" must {

        lazy val html: Html = page(Set("ern1", "ern2", "ern3"))
        implicit lazy val document: Document = asDocument(html)

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> viewMessages.title,
          Selectors.h1 -> viewMessages.heading,
          Selectors.multipleErnsText -> viewMessages.p1,
          Selectors.selectANumberText -> viewMessages.p2
        ))

      }
    }
  }
}
