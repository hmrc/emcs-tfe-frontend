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

package viewmodels.helpers

import base.SpecBase
import fixtures.ExciseProductCodeFixtures
import fixtures.messages.prevalidateTrader.PrevalidateAddToListMessages
import models.UserAnswers
import models.requests.UserAnswersRequest
import pages.prevalidateTrader.PrevalidateEPCPage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.checkAnswers.prevalidate.PrevalidateExciseProductCodeSummary

class PrevalidateAddToListHelperSpec extends SpecBase with ExciseProductCodeFixtures {

  class Setup(userAnswers: UserAnswers = emptyUserAnswers) {
    implicit lazy val request: UserAnswersRequest[AnyContentAsEmpty.type] = userAnswersRequest(FakeRequest(), userAnswers)
  }

  "PrevalidateAddToListHelper" must {

    Seq(PrevalidateAddToListMessages.English).foreach { messagesForLang =>

      implicit lazy val msgs: Messages = messages(Seq(messagesForLang.lang))

      s"being rendered in language code: '${messagesForLang.lang.code}'" must {

        "return nothing" when {

          s"no answers specified for" in new Setup() {

            PrevalidateAddToListHelper.addedEpcs() mustBe SummaryList(Seq())
          }
        }

        "return required rows" when {

          s"EPCs have been added" in new Setup(emptyUserAnswers
            .set(PrevalidateEPCPage(testIndex1), beerExciseProductCode)
            .set(PrevalidateEPCPage(testIndex2), wineExciseProductCode)
          ) {

            PrevalidateAddToListHelper.addedEpcs() mustBe
              SummaryList(Seq(
                PrevalidateExciseProductCodeSummary.row(testIndex1, beerExciseProductCode),
                PrevalidateExciseProductCodeSummary.row(testIndex2, wineExciseProductCode)
              ))
          }
        }
      }
    }
  }
}
