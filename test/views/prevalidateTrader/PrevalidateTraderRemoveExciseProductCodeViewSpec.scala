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
import fixtures.ExciseProductCodeFixtures
import fixtures.messages.prevalidateTrader.PrevalidateTraderRemoveExciseProductCodeMessages
import forms.prevalidateTrader.PrevalidateRemoveExciseProductCodeFormProvider
import models.requests.UserAnswersRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.prevalidateTrader.PrevalidateTraderRemoveExciseProductCodeView
import views.{BaseSelectors, ViewBehaviours}

class PrevalidateTraderRemoveExciseProductCodeViewSpec extends ViewSpecBase with ViewBehaviours with ExciseProductCodeFixtures {

  object Selectors extends BaseSelectors

  lazy val formProvider: PrevalidateRemoveExciseProductCodeFormProvider = app.injector.instanceOf[PrevalidateRemoveExciseProductCodeFormProvider]

  lazy val view: PrevalidateTraderRemoveExciseProductCodeView = app.injector.instanceOf[PrevalidateTraderRemoveExciseProductCodeView]

  "PrevalidateTraderRemoveExciseProductCodeView" when {

    Seq(PrevalidateTraderRemoveExciseProductCodeMessages.English).foreach { messagesForLanguage =>

      s"being rendered in lang code of '${messagesForLanguage.lang.code}'" should {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: UserAnswersRequest[AnyContentAsEmpty.type] = userAnswersRequest(FakeRequest())

        implicit val doc: Document = Jsoup.parse(view(formProvider(testEpcWine), controllers.prevalidateTrader.routes.PrevalidateRemoveExciseProductCodeController.onPageLoad(testErn, testIndex1), testEpcWine).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.prevalidateTraderCaption,
          Selectors.title -> messagesForLanguage.titleHelper(messagesForLanguage.title(testEpcWine)),
          Selectors.legend -> messagesForLanguage.h1(testEpcWine),
          Selectors.radioButton(1) -> messagesForLanguage.yes,
          Selectors.radioButton(2) -> messagesForLanguage.no,
          Selectors.button -> messagesForLanguage.saveAndContinue
        ))

        "render the nav links" in {
          val homeLink = doc.select(".moj-primary-navigation").select("a").first()
          homeLink.text mustBe "Home"
        }
      }
    }
  }

}
