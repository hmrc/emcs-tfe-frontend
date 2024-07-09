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

package viewmodels.checkAnswers.prevalidate

import base.SpecBase
import controllers.prevalidateTrader.routes
import fixtures.ExciseProductCodeFixtures
import fixtures.messages.prevalidateTrader.PrevalidateAddToListMessages
import models.CheckMode
import models.requests.UserAnswersRequest
import org.scalatest.matchers.must.Matchers
import pages.prevalidateTrader.PrevalidateEPCPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Actions, SummaryListRow}
import viewmodels.govuk.summarylist._

class PrevalidateExciseProductCodeSummarySpec extends SpecBase with Matchers with ExciseProductCodeFixtures {

  "PrevalidateExciseProductCodeSummary" when {

    Seq(PrevalidateAddToListMessages.English).foreach { messagesForLanguage =>

      s"being rendered in lang code of '${messagesForLanguage.lang.code}'" when {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "there's an answer" must {

          "must output the expected row" in {

            implicit lazy val request: UserAnswersRequest[AnyContentAsEmpty.type] =
              userAnswersRequest(FakeRequest(), emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), wineExciseProductCode))

            PrevalidateExciseProductCodeSummary.row(testIndex1, wineExciseProductCode) mustBe
              SummaryListRow(
                key = KeyViewModel(Text(wineExciseProductCode.code)).withCssClass("govuk-!-width-10"),
                value = ValueViewModel(Text(wineExciseProductCode.description)).withCssClass("govuk-!-width-one-quarter"),
                actions = Some(Actions(items = Seq(
                  ActionItemViewModel(
                    href = routes.PrevalidateExciseProductCodeController.onPageLoad(request.ern, testIndex1, CheckMode).url,
                    content = Text(messagesForLanguage.change),
                    id = "change-epc-1"
                  ).withVisuallyHiddenText(messagesForLanguage.changeHidden(wineExciseProductCode.code)),
                  ActionItemViewModel(
                    href = routes.PrevalidateRemoveExciseProductCodeController.onPageLoad(request.ern, testIndex1).url,
                    content = Text(messagesForLanguage.remove),
                    id = "remove-epc-1"
                  ).withVisuallyHiddenText(messagesForLanguage.removeHidden(wineExciseProductCode.code))
                )))
              )
          }
        }
      }
    }
  }
}
