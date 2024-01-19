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

class ViewMovementDeliveryHelperSpec extends SpecBase with GetMovementResponseFixtures {

  implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"))

  val helper: ViewMovementDeliveryHelper = app.injector.instanceOf[ViewMovementDeliveryHelper]
  implicit lazy val messages: Messages = messages(request)

  object Selectors extends BaseSelectors {
    val cardTitle = ".govuk-summary-card__title"

    def cardRowKey(i: Int) = s"div.govuk-summary-list__row:nth-of-type($i) > dt"

    def cardRowValue(i: Int) = s"div.govuk-summary-list__row:nth-of-type($i) > dd"

    def cardAtIndexTitle(i: Int) = s"div.govuk-summary-card:nth-of-type($i) .govuk-summary-card__title"

    def cardAtIndexRowKey(cardIndex: Int, rowIndex: Int) = s"div.govuk-summary-card:nth-of-type($cardIndex) div.govuk-summary-list__row:nth-of-type($rowIndex) > dt"

    def cardAtIndexRowValue(cardIndex: Int, rowIndex: Int) = s"div.govuk-summary-card:nth-of-type($cardIndex) div.govuk-summary-list__row:nth-of-type($rowIndex) > dd"
  }

  ".constructMovementDelivery" should {
    "output the correct cards" when {

      "consignor, place of dispatch, consignee and place of destination are present" in {
        val result = helper.constructMovementDelivery(getMovementResponseModel)
        val card = Jsoup.parse(result.toString())
        card.select(Selectors.cardAtIndexTitle(1)).text() mustBe "Consignor"
        card.select(Selectors.cardAtIndexRowKey(1, 1)).text() mustBe "Business name"
        card.select(Selectors.cardAtIndexRowValue(1, 1)).text() mustBe "Current 801 Consignor"
        card.select(Selectors.cardAtIndexRowKey(1, 2)).text() mustBe "Excise registration number (ERN)"
        card.select(Selectors.cardAtIndexRowValue(1, 2)).text() mustBe "GBRC345GTR145"
        card.select(Selectors.cardAtIndexRowKey(1, 3)).text() mustBe "Address"
        card.select(Selectors.cardAtIndexRowValue(1, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        card.select(Selectors.cardAtIndexTitle(2)).text() mustBe "Place of dispatch"
        card.select(Selectors.cardAtIndexRowKey(2, 1)).text() mustBe "Business name"
        card.select(Selectors.cardAtIndexRowValue(2, 1)).text() mustBe "Current 801 Consignor"
        card.select(Selectors.cardAtIndexRowKey(2, 2)).text() mustBe "Excise ID (ERN)"
        card.select(Selectors.cardAtIndexRowValue(2, 2)).text() mustBe "GBRC345GTR145"
        card.select(Selectors.cardAtIndexRowKey(2, 3)).text() mustBe "Address"
        card.select(Selectors.cardAtIndexRowValue(2, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        card.select(Selectors.cardAtIndexTitle(3)).text() mustBe "Consignee"
        card.select(Selectors.cardAtIndexRowKey(3, 1)).text() mustBe "Business name"
        card.select(Selectors.cardAtIndexRowValue(3, 1)).text() mustBe "Current 801 Consignee"
        card.select(Selectors.cardAtIndexRowKey(3, 2)).text() mustBe "Excise registration number (ERN)"
        card.select(Selectors.cardAtIndexRowValue(3, 2)).text() mustBe "GB12345GTR144"
        card.select(Selectors.cardAtIndexRowKey(3, 3)).text() mustBe "Address"
        card.select(Selectors.cardAtIndexRowValue(3, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        card.select(Selectors.cardAtIndexTitle(4)).text() mustBe "Place of destination"
        card.select(Selectors.cardAtIndexRowKey(4, 1)).text() mustBe "Business name"
        card.select(Selectors.cardAtIndexRowValue(4, 1)).text() mustBe "Current 801 Consignee"
        card.select(Selectors.cardAtIndexRowKey(4, 2)).text() mustBe "Excise registration number (ERN)"
        card.select(Selectors.cardAtIndexRowValue(4, 2)).text() mustBe "GBRC345GTR145"
        card.select(Selectors.cardAtIndexRowKey(4, 3)).text() mustBe "Address"
        card.select(Selectors.cardAtIndexRowValue(4, 3)).text() mustBe "Main101 Zeebrugge ZZ78"
      }

      "consignor, consignee and place of destination are present" in {
        val result = helper.constructMovementDelivery(getMovementResponseModel.copy(placeOfDispatchTrader = None))
        val card = Jsoup.parse(result.toString())
        card.select(Selectors.cardAtIndexTitle(1)).text() mustBe "Consignor"
        card.select(Selectors.cardAtIndexRowKey(1, 1)).text() mustBe "Business name"
        card.select(Selectors.cardAtIndexRowValue(1, 1)).text() mustBe "Current 801 Consignor"
        card.select(Selectors.cardAtIndexRowKey(1, 2)).text() mustBe "Excise registration number (ERN)"
        card.select(Selectors.cardAtIndexRowValue(1, 2)).text() mustBe "GBRC345GTR145"
        card.select(Selectors.cardAtIndexRowKey(1, 3)).text() mustBe "Address"
        card.select(Selectors.cardAtIndexRowValue(1, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        card.select(Selectors.cardAtIndexTitle(2)).text() mustBe "Consignee"
        card.select(Selectors.cardAtIndexRowKey(2, 1)).text() mustBe "Business name"
        card.select(Selectors.cardAtIndexRowValue(2, 1)).text() mustBe "Current 801 Consignee"
        card.select(Selectors.cardAtIndexRowKey(2, 2)).text() mustBe "Excise registration number (ERN)"
        card.select(Selectors.cardAtIndexRowValue(2, 2)).text() mustBe "GB12345GTR144"
        card.select(Selectors.cardAtIndexRowKey(2, 3)).text() mustBe "Address"
        card.select(Selectors.cardAtIndexRowValue(2, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        card.select(Selectors.cardAtIndexTitle(3)).text() mustBe "Place of destination"
        card.select(Selectors.cardAtIndexRowKey(3, 1)).text() mustBe "Business name"
        card.select(Selectors.cardAtIndexRowValue(3, 1)).text() mustBe "Current 801 Consignee"
        card.select(Selectors.cardAtIndexRowKey(3, 2)).text() mustBe "Excise registration number (ERN)"
        card.select(Selectors.cardAtIndexRowValue(3, 2)).text() mustBe "GBRC345GTR145"
        card.select(Selectors.cardAtIndexRowKey(3, 3)).text() mustBe "Address"
        card.select(Selectors.cardAtIndexRowValue(3, 3)).text() mustBe "Main101 Zeebrugge ZZ78"
      }

      "consignor, place of dispatch and place of destination are present" in {
        val result = helper.constructMovementDelivery(getMovementResponseModel.copy(consigneeTrader = None))
        val card = Jsoup.parse(result.toString())
        card.select(Selectors.cardAtIndexTitle(1)).text() mustBe "Consignor"
        card.select(Selectors.cardAtIndexRowKey(1, 1)).text() mustBe "Business name"
        card.select(Selectors.cardAtIndexRowValue(1, 1)).text() mustBe "Current 801 Consignor"
        card.select(Selectors.cardAtIndexRowKey(1, 2)).text() mustBe "Excise registration number (ERN)"
        card.select(Selectors.cardAtIndexRowValue(1, 2)).text() mustBe "GBRC345GTR145"
        card.select(Selectors.cardAtIndexRowKey(1, 3)).text() mustBe "Address"
        card.select(Selectors.cardAtIndexRowValue(1, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        card.select(Selectors.cardAtIndexTitle(2)).text() mustBe "Place of dispatch"
        card.select(Selectors.cardAtIndexRowKey(2, 1)).text() mustBe "Business name"
        card.select(Selectors.cardAtIndexRowValue(2, 1)).text() mustBe "Current 801 Consignor"
        card.select(Selectors.cardAtIndexRowKey(2, 2)).text() mustBe "Excise ID (ERN)"
        card.select(Selectors.cardAtIndexRowValue(2, 2)).text() mustBe "GBRC345GTR145"
        card.select(Selectors.cardAtIndexRowKey(2, 3)).text() mustBe "Address"
        card.select(Selectors.cardAtIndexRowValue(2, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        card.select(Selectors.cardAtIndexTitle(3)).text() mustBe "Place of destination"
        card.select(Selectors.cardAtIndexRowKey(3, 1)).text() mustBe "Business name"
        card.select(Selectors.cardAtIndexRowValue(3, 1)).text() mustBe "Current 801 Consignee"
        card.select(Selectors.cardAtIndexRowKey(3, 2)).text() mustBe "Excise registration number (ERN)"
        card.select(Selectors.cardAtIndexRowValue(3, 2)).text() mustBe "GBRC345GTR145"
        card.select(Selectors.cardAtIndexRowKey(3, 3)).text() mustBe "Address"
        card.select(Selectors.cardAtIndexRowValue(3, 3)).text() mustBe "Main101 Zeebrugge ZZ78"
      }

      "consignor, place of dispatch and consignee are present" in {
        val result = helper.constructMovementDelivery(getMovementResponseModel.copy(deliveryPlaceTrader = None))
        val card = Jsoup.parse(result.toString())
        card.select(Selectors.cardAtIndexTitle(1)).text() mustBe "Consignor"
        card.select(Selectors.cardAtIndexRowKey(1, 1)).text() mustBe "Business name"
        card.select(Selectors.cardAtIndexRowValue(1, 1)).text() mustBe "Current 801 Consignor"
        card.select(Selectors.cardAtIndexRowKey(1, 2)).text() mustBe "Excise registration number (ERN)"
        card.select(Selectors.cardAtIndexRowValue(1, 2)).text() mustBe "GBRC345GTR145"
        card.select(Selectors.cardAtIndexRowKey(1, 3)).text() mustBe "Address"
        card.select(Selectors.cardAtIndexRowValue(1, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        card.select(Selectors.cardAtIndexTitle(2)).text() mustBe "Place of dispatch"
        card.select(Selectors.cardAtIndexRowKey(2, 1)).text() mustBe "Business name"
        card.select(Selectors.cardAtIndexRowValue(2, 1)).text() mustBe "Current 801 Consignor"
        card.select(Selectors.cardAtIndexRowKey(2, 2)).text() mustBe "Excise ID (ERN)"
        card.select(Selectors.cardAtIndexRowValue(2, 2)).text() mustBe "GBRC345GTR145"
        card.select(Selectors.cardAtIndexRowKey(2, 3)).text() mustBe "Address"
        card.select(Selectors.cardAtIndexRowValue(2, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        card.select(Selectors.cardAtIndexTitle(3)).text() mustBe "Consignee"
        card.select(Selectors.cardAtIndexRowKey(3, 1)).text() mustBe "Business name"
        card.select(Selectors.cardAtIndexRowValue(3, 1)).text() mustBe "Current 801 Consignee"
        card.select(Selectors.cardAtIndexRowKey(3, 2)).text() mustBe "Excise registration number (ERN)"
        card.select(Selectors.cardAtIndexRowValue(3, 2)).text() mustBe "GB12345GTR144"
        card.select(Selectors.cardAtIndexRowKey(3, 3)).text() mustBe "Address"
        card.select(Selectors.cardAtIndexRowValue(3, 3)).text() mustBe "Main101 Zeebrugge ZZ78"
      }
    }
  }

}
