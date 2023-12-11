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

package views

import base.SpecBase
import models.auth.UserRequest
import models.common.{AddressModel, TraderModel}
import models.requests.DataRequest
import models.response.emcsTfe.GetMovementResponse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import play.api.test.Helpers.contentAsString
import play.twirl.api.Html
import views.html.ViewMovementPage

import java.time.LocalDate

class ViewMovementPageViewSpec extends SpecBase {
  val page: ViewMovementPage = app.injector.instanceOf[ViewMovementPage]
  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  "The ModeOfTransportPage view" must {

    val dataRequest = DataRequest(
      UserRequest(FakeRequest("GET", s"/consignment/$testErn/$testArc"), testErn, testInternalId, testCredId, hasMultipleErns = false)(messagesApi),
      testMinTraderKnownFacts
    )

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

    lazy val html: Html = page(testErn, testArc, testData)(dataRequest, implicitly)
    lazy val document: Document = Jsoup.parse(contentAsString(html))

    s"have the correct h1" in {
      document.select("h1").text() mustBe "ARC"
    }

    s"have the correct summary list contents" in {
      document.select(".govuk-summary-list__row:nth-child(1) .govuk-summary-list__key").text mustBe "Local Reference Number (LRN)"
      document.select(".govuk-summary-list__row:nth-child(1) .govuk-summary-list__value").text mustBe "MyLrn"
      document.select(".govuk-summary-list__row:nth-child(2) .govuk-summary-list__key").text mustBe "e-AD status"
      document.select(".govuk-summary-list__row:nth-child(2) .govuk-summary-list__value").text mustBe "MyEadStatus"
      document.select(".govuk-summary-list__row:nth-child(3) .govuk-summary-list__key").text mustBe "Consignor"
      document.select(".govuk-summary-list__row:nth-child(3) .govuk-summary-list__value").text mustBe "MyConsignor"
      document.select(".govuk-summary-list__row:nth-child(4) .govuk-summary-list__key").text mustBe "Date of dispatch"
      document.select(".govuk-summary-list__row:nth-child(4) .govuk-summary-list__value").text mustBe "04 March 2010"
      document.select(".govuk-summary-list__row:nth-child(5) .govuk-summary-list__key").text mustBe "Journey time"
      document.select(".govuk-summary-list__row:nth-child(5) .govuk-summary-list__value").text mustBe "MyJourneyTime"
      document.select(".govuk-summary-list__row:nth-child(6) .govuk-summary-list__key").text mustBe "Number of items"
      document.select(".govuk-summary-list__row:nth-child(6) .govuk-summary-list__value").text mustBe "0"
    }

    "have a link to the report a receipt flow for the Movement" in {
      val reportReceiptLink = document.select(".govuk-list > li:nth-child(1) > a:nth-child(1)")
      reportReceiptLink.text mustBe "Submit report of receipt"
      reportReceiptLink.attr("href") mustBe s"http://localhost:8313/emcs/report-receipt/trader/$testErn/movement/$testArc"
    }

    "have a link to the explain a delay flow for the Movement" in {
      val explainDelayLink = document.select(".govuk-list > li:nth-child(2) > a:nth-child(1)")
      explainDelayLink.text mustBe "Explain a delay"
      explainDelayLink.attr("href") mustBe s"http://localhost:8316/emcs/explain-delay/trader/$testErn/movement/$testArc"
    }

    "have a link to the explain shortage or excess for the Movement" in {
      val explainShortageExcessLink = document.select(".govuk-list > li:nth-child(3) > a:nth-child(1)")
      explainShortageExcessLink.text mustBe "Explain shortage or excess"
      explainShortageExcessLink.attr("href") mustBe s"http://localhost:8317/emcs/explain-shortage-or-excess/trader/$testErn/movement/$testArc"
    }

    "have a link to the Cancel Movement" in {
      val cancelMovementLink = document.select(".govuk-list > li:nth-child(4) > a:nth-child(1)")
      cancelMovementLink.text mustBe "Cancel movement"
      cancelMovementLink.attr("href") mustBe s"http://localhost:8318/emcs/cancel-movement/trader/$testErn/movement/$testArc"
    }

    "have a link to the Change Destination" in {
      val changeDestinationLink = document.select(".govuk-list > li:nth-child(5) > a:nth-child(1)")
      changeDestinationLink.text mustBe "Change destination"
      changeDestinationLink.attr("href") mustBe s"http://localhost:8319/emcs/change-destination/trader/$testErn/movement/$testArc"
    }

    "have a link to the Alert or rejection" in {
      val alertOrRejectionLink = document.select(".govuk-list > li:nth-child(6) > a:nth-child(1)")
      alertOrRejectionLink.text mustBe "Alert or rejection"
      alertOrRejectionLink.attr("href") mustBe s"http://localhost:8320/emcs/alert-or-rejection/trader/$testErn/movement/$testArc"
    }
  }
}
