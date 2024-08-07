/*
 * Copyright 2024 HM Revenue & Customs
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

package views.prevalidateTrader

import base.ViewSpecBase
import fixtures.messages.prevalidateTrader.PrevalidateTraderStartMessages
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.prevalidateTrader.PrevalidateTraderStartView
import views.{BaseSelectors, ViewBehaviours}

class PrevalidateTraderStartViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors {
    val continueButton = "#continue-button"
  }

  "PrevalidateTraderStartView" when {

    Seq(PrevalidateTraderStartMessages.English).foreach { messagesForLanguage =>

      s"being rendered in lang code of '${messagesForLanguage.lang.code}'" should {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

        val view = app.injector.instanceOf[PrevalidateTraderStartView]

        implicit val doc: Document = Jsoup.parse(view(testOnwardRoute).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.titleHelper(messagesForLanguage.title),
          Selectors.h1 -> messagesForLanguage.h1,
          Selectors.p(1) -> messagesForLanguage.p,
          Selectors.button -> messagesForLanguage.continue
        ))

        "render the nav links" in {
          val homeLink = doc.select("#navigation-home-link")
          homeLink.text mustBe "Home"
        }
      }
    }
  }
}
