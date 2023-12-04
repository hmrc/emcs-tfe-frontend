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

package uk.gov.hmrc.emcstfefrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.matchers.should.Matchers.convertToStringShouldWrapper
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout}
import play.twirl.api.Html
import uk.gov.hmrc.emcstfefrontend.base.SpecBase
import uk.gov.hmrc.emcstfefrontend.models.common.{AddressModel, TraderModel}
import uk.gov.hmrc.emcstfefrontend.models.response.emcsTfe.GetMovementResponse
import uk.gov.hmrc.emcstfefrontend.views.html.ViewMovementPage

import java.time.LocalDate

class ViewMovementPageViewSpec extends SpecBase {
  val page: ViewMovementPage = app.injector.instanceOf[ViewMovementPage]
  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  "The ModeOfTransportPage view" should {

    val testData = GetMovementResponse(
      "MyLrn",
      "MyEadStatus",
      consignorTrader = TraderModel(
        traderExciseNumber = "GB12345GTR144",
        traderName = "MyConsignor",
        address = AddressModel(
          streetNumber = None,
          street = Some("Main101"),
          postcode = Some("ZZ78"),
          city = Some("Zeebrugge")
        )
      ),
      LocalDate.parse("2010-03-04"),
      "MyJourneyTime",
      0
    )

    val ern = "ern12345"
    val arc = "12345"
    lazy val html: Html = page(ern, arc, testData)(FakeRequest(), implicitly)
    lazy val document: Document = Jsoup.parse(contentAsString(html))

    s"have the correct h1" in {
      document.select("h1").text() shouldBe "12345"
    }

    s"have the correct summary list contents" in {
      document.select(".govuk-summary-list__row:nth-child(1) .govuk-summary-list__key").text shouldBe "Local Reference Number (LRN)"
      document.select(".govuk-summary-list__row:nth-child(1) .govuk-summary-list__value").text shouldBe "MyLrn"
      document.select(".govuk-summary-list__row:nth-child(2) .govuk-summary-list__key").text shouldBe "e-AD status"
      document.select(".govuk-summary-list__row:nth-child(2) .govuk-summary-list__value").text shouldBe "MyEadStatus"
      document.select(".govuk-summary-list__row:nth-child(3) .govuk-summary-list__key").text shouldBe "Consignor"
      document.select(".govuk-summary-list__row:nth-child(3) .govuk-summary-list__value").text shouldBe "MyConsignor"
      document.select(".govuk-summary-list__row:nth-child(4) .govuk-summary-list__key").text shouldBe "Date of dispatch"
      document.select(".govuk-summary-list__row:nth-child(4) .govuk-summary-list__value").text shouldBe "04 March 2010"
      document.select(".govuk-summary-list__row:nth-child(5) .govuk-summary-list__key").text shouldBe "Journey time"
      document.select(".govuk-summary-list__row:nth-child(5) .govuk-summary-list__value").text shouldBe "MyJourneyTime"
      document.select(".govuk-summary-list__row:nth-child(6) .govuk-summary-list__key").text shouldBe "Number of items"
      document.select(".govuk-summary-list__row:nth-child(6) .govuk-summary-list__value").text shouldBe "0"
    }

    "have a link to the report a receipt flow for the Movement" in {
      val reportReceiptLink = document.select(".govuk-list > li:nth-child(1) > a:nth-child(1)")
      reportReceiptLink.text shouldBe "Submit report of receipt"
      reportReceiptLink.attr("href") shouldBe s"http://localhost:8313/emcs/report-receipt/trader/$ern/movement/$arc"
    }

    "have a link to the explain a delay flow for the Movement" in {
      val explainDelayLink = document.select(".govuk-list > li:nth-child(2) > a:nth-child(1)")
      explainDelayLink.text shouldBe "Explain a delay"
      explainDelayLink.attr("href") shouldBe s"http://localhost:8316/emcs/explain-delay/trader/$ern/movement/$arc"
    }

    "have a link to the explain shortage or excess for the Movement" in {
      val explainShortageExcessLink = document.select(".govuk-list > li:nth-child(3) > a:nth-child(1)")
      explainShortageExcessLink.text shouldBe "Explain shortage or excess"
      explainShortageExcessLink.attr("href") shouldBe s"http://localhost:8317/emcs/explain-shortage-or-excess/trader/$ern/movement/$arc"
    }

    "have a link to the Cancel Movement" in {
      val cancelMovementLink = document.select(".govuk-list > li:nth-child(4) > a:nth-child(1)")
      cancelMovementLink.text shouldBe "Cancel movement"
      cancelMovementLink.attr("href") shouldBe s"http://localhost:8318/emcs/cancel-movement/trader/$ern/movement/$arc"
    }

    "have a link to the Change Destination" in {
      val changeDestinationLink = document.select(".govuk-list > li:nth-child(5) > a:nth-child(1)")
      changeDestinationLink.text shouldBe "Change destination"
      changeDestinationLink.attr("href") shouldBe s"http://localhost:8319/emcs/change-destination/trader/$ern/movement/$arc"
    }

    "have a link to the Alert or rejection" in {
      val alertOrRejectionLink = document.select(".govuk-list > li:nth-child(6) > a:nth-child(1)")
      alertOrRejectionLink.text shouldBe "Alert or rejection"
      alertOrRejectionLink.attr("href") shouldBe s"http://localhost:8320/emcs/alert-or-rejection/trader/$ern/movement/$arc"
    }
  }
}
