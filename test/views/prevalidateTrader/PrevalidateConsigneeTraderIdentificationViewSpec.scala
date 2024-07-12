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
import fixtures.messages.prevalidateTrader.PrevalidateConsigneeTraderIdentificationMessages
import forms.prevalidate.PrevalidateConsigneeTraderIdentificationFormProvider
import models.requests.UserAnswersRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.prevalidateTrader.PrevalidateConsigneeTraderIdentificationView
import views.{BaseSelectors, ViewBehaviours}

class PrevalidateConsigneeTraderIdentificationViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  val formProvider = app.injector.instanceOf[PrevalidateConsigneeTraderIdentificationFormProvider]

  "PrevalidateConsigneeTraderIdentificationView" when {

    Seq(PrevalidateConsigneeTraderIdentificationMessages.English).foreach { messagesForLanguage =>

      s"being rendered in lang code of '${messagesForLanguage.lang.code}'" should {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: UserAnswersRequest[AnyContentAsEmpty.type] = userAnswersRequest(FakeRequest())

        val view = app.injector.instanceOf[PrevalidateConsigneeTraderIdentificationView]

        implicit val doc: Document = Jsoup.parse(view(formProvider(), testOnly.controllers.routes.UnderConstructionController.onPageLoad()).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.subHeadingCaptionSelector -> messagesForLanguage.prevalidateTraderCaption,
          Selectors.title -> messagesForLanguage.titleHelper(messagesForLanguage.title),
          Selectors.h1 -> messagesForLanguage.h1,
          Selectors.p(1) -> messagesForLanguage.p,
          Selectors.button -> messagesForLanguage.saveAndContinue,
          Selectors.label("ern") -> messagesForLanguage.label,
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.fieldSetLegend -> messagesForLanguage.entityGroupLabel,
          Selectors.radioButton(1) -> messagesForLanguage.radio1,
          Selectors.radioButton(2) -> messagesForLanguage.radio2,
          Selectors.radioButton(3) -> messagesForLanguage.radio3,
          Selectors.radioButton(4) -> messagesForLanguage.radio4
        ))

        "render the nav links" in {
          val homeLink = doc.select(".moj-primary-navigation").select("a").first()
          homeLink.text mustBe "Home"
        }
      }
    }
  }
}
