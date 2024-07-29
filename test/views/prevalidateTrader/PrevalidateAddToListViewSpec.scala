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
import fixtures.messages.prevalidateTrader.PrevalidateAddToListMessages
import fixtures.{ExciseProductCodeFixtures, ItemFixtures}
import forms.prevalidate.PrevalidateAddToListFormProvider
import mocks.services.MockGetCnCodeInformationService
import models.requests.UserAnswersRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.prevalidateTrader.PrevalidateEPCPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.helpers.PrevalidateAddToListHelper
import views.html.prevalidateTrader.PrevalidateAddToListView
import views.{BaseSelectors, ViewBehaviours}

import scala.concurrent.ExecutionContext

class PrevalidateAddToListViewSpec extends ViewSpecBase
  with ViewBehaviours
  with ItemFixtures
  with ExciseProductCodeFixtures
  with MockGetCnCodeInformationService {

  lazy val view = app.injector.instanceOf[PrevalidateAddToListView]
  lazy val form = app.injector.instanceOf[PrevalidateAddToListFormProvider].apply()

  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  object Selectors extends BaseSelectors {
    val legendQuestion = ".govuk-fieldset__legend.govuk-fieldset__legend--m"
    val removeItemLink: Int => String = index => s"#remove-epc-$index"
    val changeItemLink: Int => String = index => s"#change-epc-$index"
  }

  "PrevalidateAddToListView" when {

    Seq(PrevalidateAddToListMessages.English).foreach { messagesForLanguage =>

      s"being rendered in language code of '${messagesForLanguage.lang.code}'" when {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        s"being rendered for singular item" when {

          val userAnswers = emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), wineExciseProductCode)

          implicit val request: UserAnswersRequest[AnyContentAsEmpty.type] = userAnswersRequest(FakeRequest(), userAnswers)

          implicit val doc: Document = Jsoup.parse(view(
            formOpt = Some(form),
            onSubmitCall = testOnwardRoute,
            addedEpcs = PrevalidateAddToListHelper.addedEpcs()
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(count = 1),
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.prevalidateTraderCaption,
            Selectors.h1 -> messagesForLanguage.heading(count = 1),
            Selectors.summaryRowKey(1) -> wineExciseProductCode.code,
            Selectors.summaryRowValue(1) -> wineExciseProductCode.description,
            Selectors.changeItemLink(1) -> messagesForLanguage.changeEpc(wineExciseProductCode.code),
            Selectors.removeItemLink(1) -> messagesForLanguage.removeEpc(wineExciseProductCode.code),
            Selectors.legendQuestion -> messagesForLanguage.h2,
            Selectors.radioButton(1) -> messagesForLanguage.yes,
            Selectors.radioButton(2) -> messagesForLanguage.no,
            Selectors.button -> messagesForLanguage.saveAndContinue
          ))
        }

        s"when being rendered for multiple items" when {

          val userAnswers = emptyUserAnswers
            .set(PrevalidateEPCPage(testIndex1), wineExciseProductCode)
            .set(PrevalidateEPCPage(testIndex2), beerExciseProductCode)

          implicit val request: UserAnswersRequest[AnyContentAsEmpty.type] = userAnswersRequest(FakeRequest(), userAnswers)

          implicit val doc: Document = Jsoup.parse(view(
            formOpt = Some(form),
            onSubmitCall = testOnwardRoute,
            addedEpcs = PrevalidateAddToListHelper.addedEpcs()
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(count = 2),
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.prevalidateTraderCaption,
            Selectors.h1 -> messagesForLanguage.heading(count = 2),
            Selectors.summaryRowKey(1) -> wineExciseProductCode.code,
            Selectors.summaryRowValue(1) -> wineExciseProductCode.description,
            Selectors.changeItemLink(1) -> messagesForLanguage.changeEpc(wineExciseProductCode.code),
            Selectors.removeItemLink(1) -> messagesForLanguage.removeEpc(wineExciseProductCode.code),
            Selectors.summaryRowKey(2) -> beerExciseProductCode.code,
            Selectors.summaryRowValue(2) -> beerExciseProductCode.description,
            Selectors.changeItemLink(2) -> messagesForLanguage.changeEpc(beerExciseProductCode.code),
            Selectors.removeItemLink(2) -> messagesForLanguage.removeEpc(beerExciseProductCode.code),
            Selectors.legendQuestion -> messagesForLanguage.h2,
            Selectors.radioButton(1) -> messagesForLanguage.yes,
            Selectors.radioButton(2) -> messagesForLanguage.no,
            Selectors.button -> messagesForLanguage.saveAndContinue
          ))
        }

        s"when being rendered with no form" when {

          val userAnswers = emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), wineExciseProductCode)

          implicit val request: UserAnswersRequest[AnyContentAsEmpty.type] = userAnswersRequest(FakeRequest(), userAnswers)

          implicit val doc: Document = Jsoup.parse(view(
            formOpt = None,
            onSubmitCall = testOnwardRoute,
            addedEpcs = PrevalidateAddToListHelper.addedEpcs()
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(count = 1),
            Selectors.subHeadingCaptionSelector -> messagesForLanguage.prevalidateTraderCaption,
            Selectors.h1 -> messagesForLanguage.heading(count = 1),
            Selectors.button -> messagesForLanguage.saveAndContinue
          ))

          behave like pageWithElementsNotPresent(Seq(
            Selectors.legendQuestion,
            Selectors.radioButton(1),
            Selectors.radioButton(2)
          ))
        }

        "render the nav links" in {

          val userAnswers = emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), wineExciseProductCode)

          implicit val request: UserAnswersRequest[AnyContentAsEmpty.type] = userAnswersRequest(FakeRequest(), userAnswers)

          val document = Jsoup.parse(view(
            formOpt = Some(form),
            onSubmitCall = testOnwardRoute,
            addedEpcs = PrevalidateAddToListHelper.addedEpcs()
          ).toString())
          val homeLink = document.select("#navigation-home-link")
          homeLink.text mustBe "Home"
        }
      }
    }
  }
}

