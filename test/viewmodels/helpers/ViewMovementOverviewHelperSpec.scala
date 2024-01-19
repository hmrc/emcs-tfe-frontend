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

package viewmodels.helpers

import base.SpecBase
import fixtures.GetMovementResponseFixtures
import models.requests.DataRequest
import org.jsoup.Jsoup
import play.api.i18n.Messages
import play.api.test.FakeRequest
import views.BaseSelectors

class ViewMovementOverviewHelperSpec extends SpecBase with GetMovementResponseFixtures {

  implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"))

  val helper: ViewMovementOverviewHelper = app.injector.instanceOf[ViewMovementOverviewHelper]
  implicit lazy val messages: Messages = messages(request)

  object Selectors extends BaseSelectors {
    val cardTitle = ".govuk-summary-card__title"

    def cardRowKey(i: Int) = s"div.govuk-summary-list__row:nth-of-type($i) > dt"

    def cardRowValue(i: Int) = s"div.govuk-summary-list__row:nth-of-type($i) > dd"

    def cardAtIndexTitle(i: Int) = s"div.govuk-summary-card:nth-of-type($i) .govuk-summary-card__title"

    def cardAtIndexRowKey(cardIndex: Int, rowIndex: Int) = s"div.govuk-summary-card:nth-of-type($cardIndex) div.govuk-summary-list__row:nth-of-type($rowIndex) > dt"

    def cardAtIndexRowValue(cardIndex: Int, rowIndex: Int) = s"div.govuk-summary-card:nth-of-type($cardIndex) div.govuk-summary-list__row:nth-of-type($rowIndex) > dd"
  }

  "constructMovementOverview" should {
    "output the correct rows" in {
      val result = helper.constructMovementOverview(getMovementResponseModel)
      val card = Jsoup.parse(result.toString())
      card.select(Selectors.cardTitle).text() mustBe "Overview"
      card.select(Selectors.cardRowKey(1)).text() mustBe "Local Reference Number (LRN)"
      card.select(Selectors.cardRowValue(1)).text() mustBe testLrn
      card.select(Selectors.cardRowKey(2)).text() mustBe "Electronic administrative document (eAD) status"
      card.select(Selectors.cardRowValue(2)).text() mustBe "Accepted"
      card.select(Selectors.cardRowKey(3)).text() mustBe "Date of dispatch"
      card.select(Selectors.cardRowValue(3)).text() mustBe "20 November 2008"
      card.select(Selectors.cardRowKey(4)).text() mustBe "Expected date of arrival"
      card.select(Selectors.cardRowValue(4)).text() mustBe "10 December 2008"
      card.select(Selectors.cardRowKey(5)).text() mustBe "Consignor"
      card.select(Selectors.cardRowValue(5)).text() mustBe "GBRC345GTR145"
      card.select(Selectors.cardRowKey(6)).text() mustBe "Number of items"
      card.select(Selectors.cardRowValue(6)).text() mustBe "2"
      card.select(Selectors.cardRowKey(7)).text() mustBe "Transporting vehicle(s)"
      card.select(Selectors.cardRowValue(7)).text() mustBe "AB11 1T4 AB22 2T4"
    }
  }
}
