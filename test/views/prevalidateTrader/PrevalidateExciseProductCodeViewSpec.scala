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

package views.prevalidateTrader

import base.ViewSpecBase
import fixtures.messages.prevalidateTrader.ExciseProductCodeMessages
import fixtures.{ExciseProductCodeFixtures, ItemFixtures}
import forms.prevalidate.PrevalidateExciseProductCodeFormProvider
import models.requests.UserAnswersRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.SelectItemHelper
import views.html.prevalidateTrader.PrevalidateExciseProductCodeView
import views.{BaseSelectors, ViewBehaviours}

class PrevalidateExciseProductCodeViewSpec extends ViewSpecBase with ViewBehaviours with ItemFixtures with ExciseProductCodeFixtures {

  object Selectors extends BaseSelectors {
    def selectOption(nthChild: Int) = s"#excise-product-code > option:nth-child($nthChild)"
  }

  "Excise Product Code view" must {

    Seq(ExciseProductCodeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" must {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: UserAnswersRequest[AnyContentAsEmpty.type] = userAnswersRequest(FakeRequest(), emptyUserAnswers)

        lazy val view = app.injector.instanceOf[PrevalidateExciseProductCodeView]
        val selectOptions = SelectItemHelper.constructSelectItems(
          selectOptions = Seq(beerExciseProductCode),
          defaultTextMessageKey = Some("prevalidateTrader.exciseProductCode.select.defaultValue"),
          withEpcDescription = true
        )
        val form = app.injector.instanceOf[PrevalidateExciseProductCodeFormProvider].apply(Seq(beerExciseProductCode))

        implicit val doc: Document = Jsoup.parse(view(form, testOnwardRoute, selectOptions, testIndex1).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.h2(1) -> messagesForLanguage.prevalidateTraderCaption,
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.p(1) -> messagesForLanguage.paragraph,
          Selectors.label("excise-product-code") -> messagesForLanguage.label(testIndex1),
          Selectors.selectOption(1) -> messagesForLanguage.defaultSelectOption,
          Selectors.selectOption(2) -> messagesForLanguage.beerSelectOption,
          Selectors.button -> messagesForLanguage.saveAndContinue
        ))
      }
    }
  }
}
