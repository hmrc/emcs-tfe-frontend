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

package views.auth.errors

import base.ViewSpecBase
import config.AppConfig
import fixtures.messages.NotOnBetaListMessages
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.test.FakeRequest
import views.html.auth.errors.NotOnBetaListView
import views.{BaseSelectors, ViewBehaviours}

class NotOnBetaListViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  "NotOnBetaListView" when {

    Seq(NotOnBetaListMessages.English).foreach { messagesForLanguage =>

      s"being rendered in lang code of '${messagesForLanguage.lang.code}'" should {

        implicit val request = FakeRequest()
        implicit val msgs: Messages = messages(request)
        implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

        val view = app.injector.instanceOf[NotOnBetaListView]

        implicit val doc: Document = Jsoup.parse(view().toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.p(1) -> messagesForLanguage.p1,
          Selectors.h2(1) -> messagesForLanguage.alreadySignedUpH2,
          Selectors.p(2) -> messagesForLanguage.alreadySignedUpP1,
          Selectors.p(3) -> messagesForLanguage.alreadySignedUpP2,
          Selectors.h2(2) -> messagesForLanguage.notSignedUpH2,
          Selectors.p(4) -> messagesForLanguage.notSignedUpP1,
          Selectors.p(5) -> messagesForLanguage.notSignedUpP2
        ))

        "have the correct link to the form to sign up for the User Research" in {

          doc.select(Selectors.p(4)).select("a").attr("href") mustBe
            "https://forms.office.com/e/RehKkae1vH"
        }
      }
    }
  }
}