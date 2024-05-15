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
    override def h2(i: Int) = s"h2:nth-of-type($i)"
    def summaryListRowKey(i: Int) = s"div.govuk-summary-list__row:nth-of-type($i) > dt"
    def summaryListRowValue(i: Int) = s"div.govuk-summary-list__row:nth-of-type($i) > dd"
  }

  "constructMovementOverview" should {
    "output the correct rows" in {
      val result = helper.constructMovementOverview(getMovementResponseModel)
      val doc = Jsoup.parse(result.toString())
      doc.select(Selectors.h2(1)).text() mustBe "Overview"
      doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Local Reference Number (LRN)"
      doc.select(Selectors.summaryListRowValue(1)).text() mustBe testLrn
      doc.select(Selectors.summaryListRowKey(2)).text() mustBe "Electronic administrative document (eAD) status"
      doc.select(Selectors.summaryListRowValue(2)).text() mustBe "Accepted"
      doc.select(Selectors.summaryListRowKey(3)).text() mustBe "Date of dispatch"
      doc.select(Selectors.summaryListRowValue(3)).text() mustBe "20 November 2008"
      doc.select(Selectors.summaryListRowKey(4)).text() mustBe "Expected date of arrival"
      doc.select(Selectors.summaryListRowValue(4)).text() mustBe "10 December 2008"
      doc.select(Selectors.summaryListRowKey(5)).text() mustBe "Consignor"
      doc.select(Selectors.summaryListRowValue(5)).text() mustBe "GBRC345GTR145"
      doc.select(Selectors.summaryListRowKey(6)).text() mustBe "Number of items"
      doc.select(Selectors.summaryListRowValue(6)).text() mustBe "2"
      doc.select(Selectors.summaryListRowKey(7)).text() mustBe "Transporting vehicle(s)"
      doc.select(Selectors.summaryListRowValue(7)).text() mustBe "AB11 1T4 AB22 2T4"
    }
  }
}
