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
import models.common.DestinationType.{Export, TemporaryCertifiedConsignee, TemporaryRegisteredConsignee}
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
    override def h2(i: Int) = s"h2:nth-of-type($i)"
    override def h3(i: Int) = s"h3:nth-of-type($i)"

    def summaryListAtIndexRowKey(summaryListIndex: Int, rowIndex: Int) = s"dl:nth-of-type($summaryListIndex) div.govuk-summary-list__row:nth-of-type($rowIndex) > dt"

    def summaryListAtIndexRowValue(summaryListIndex: Int, rowIndex: Int) = s"dl:nth-of-type($summaryListIndex) div.govuk-summary-list__row:nth-of-type($rowIndex) > dd"
  }

  ".constructMovementDelivery" should {
    "output the correct HTML" when {
      "consignor, place of dispatch, consignee and place of destination are present" in {
        val result = helper.constructMovementDelivery(getMovementResponseModel)
        val summaryList = Jsoup.parse(result.toString())
        summaryList.select(Selectors.h3(1)).text() mustBe "Consignor"
        summaryList.select(Selectors.summaryListAtIndexRowKey(1, 1)).text() mustBe "Business name"
        summaryList.select(Selectors.summaryListAtIndexRowValue(1, 1)).text() mustBe "Current 801 Consignor"
        summaryList.select(Selectors.summaryListAtIndexRowKey(1, 2)).text() mustBe "Excise registration number (ERN)"
        summaryList.select(Selectors.summaryListAtIndexRowValue(1, 2)).text() mustBe "GBRC345GTR145"
        summaryList.select(Selectors.summaryListAtIndexRowKey(1, 3)).text() mustBe "Address"
        summaryList.select(Selectors.summaryListAtIndexRowValue(1, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        summaryList.select(Selectors.h3(2)).text() mustBe "Place of dispatch"
        summaryList.select(Selectors.summaryListAtIndexRowKey(2, 1)).text() mustBe "Business name"
        summaryList.select(Selectors.summaryListAtIndexRowValue(2, 1)).text() mustBe "Current 801 Consignor"
        summaryList.select(Selectors.summaryListAtIndexRowKey(2, 2)).text() mustBe "Excise ID (ERN)"
        summaryList.select(Selectors.summaryListAtIndexRowValue(2, 2)).text() mustBe "GBRC345GTR145"
        summaryList.select(Selectors.summaryListAtIndexRowKey(2, 3)).text() mustBe "Address"
        summaryList.select(Selectors.summaryListAtIndexRowValue(2, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        summaryList.select(Selectors.h3(3)).text() mustBe "Consignee"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 1)).text() mustBe "Business name"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 1)).text() mustBe "Current 801 Consignee"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 2)).text() mustBe "Excise registration number (ERN)"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 2)).text() mustBe "GB12345GTR144"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 3)).text() mustBe "Address"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        summaryList.select(Selectors.h3(4)).text() mustBe "Place of destination"
        summaryList.select(Selectors.summaryListAtIndexRowKey(4, 1)).text() mustBe "Business name"
        summaryList.select(Selectors.summaryListAtIndexRowValue(4, 1)).text() mustBe "Current 801 Consignee"
        summaryList.select(Selectors.summaryListAtIndexRowKey(4, 2)).text() mustBe "Excise registration number (ERN)"
        summaryList.select(Selectors.summaryListAtIndexRowValue(4, 2)).text() mustBe "GBRC345GTR145"
        summaryList.select(Selectors.summaryListAtIndexRowKey(4, 3)).text() mustBe "Address"
        summaryList.select(Selectors.summaryListAtIndexRowValue(4, 3)).text() mustBe "Main101 Zeebrugge ZZ78"
      }

      "consignor, consignee and place of destination are present" in {
        val result = helper.constructMovementDelivery(getMovementResponseModel.copy(placeOfDispatchTrader = None))
        val summaryList = Jsoup.parse(result.toString())
        summaryList.select(Selectors.h3(1)).text() mustBe "Consignor"
        summaryList.select(Selectors.summaryListAtIndexRowKey(1, 1)).text() mustBe "Business name"
        summaryList.select(Selectors.summaryListAtIndexRowValue(1, 1)).text() mustBe "Current 801 Consignor"
        summaryList.select(Selectors.summaryListAtIndexRowKey(1, 2)).text() mustBe "Excise registration number (ERN)"
        summaryList.select(Selectors.summaryListAtIndexRowValue(1, 2)).text() mustBe "GBRC345GTR145"
        summaryList.select(Selectors.summaryListAtIndexRowKey(1, 3)).text() mustBe "Address"
        summaryList.select(Selectors.summaryListAtIndexRowValue(1, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        summaryList.select(Selectors.h3(2)).text() mustBe "Consignee"
        summaryList.select(Selectors.summaryListAtIndexRowKey(2, 1)).text() mustBe "Business name"
        summaryList.select(Selectors.summaryListAtIndexRowValue(2, 1)).text() mustBe "Current 801 Consignee"
        summaryList.select(Selectors.summaryListAtIndexRowKey(2, 2)).text() mustBe "Excise registration number (ERN)"
        summaryList.select(Selectors.summaryListAtIndexRowValue(2, 2)).text() mustBe "GB12345GTR144"
        summaryList.select(Selectors.summaryListAtIndexRowKey(2, 3)).text() mustBe "Address"
        summaryList.select(Selectors.summaryListAtIndexRowValue(2, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        summaryList.select(Selectors.h3(3)).text() mustBe "Place of destination"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 1)).text() mustBe "Business name"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 1)).text() mustBe "Current 801 Consignee"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 2)).text() mustBe "Excise registration number (ERN)"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 2)).text() mustBe "GBRC345GTR145"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 3)).text() mustBe "Address"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 3)).text() mustBe "Main101 Zeebrugge ZZ78"
      }

      "consignor, place of dispatch and place of destination are present" in {
        val result = helper.constructMovementDelivery(getMovementResponseModel.copy(consigneeTrader = None))
        val summaryList = Jsoup.parse(result.toString())
        summaryList.select(Selectors.h3(1)).text() mustBe "Consignor"
        summaryList.select(Selectors.summaryListAtIndexRowKey(1, 1)).text() mustBe "Business name"
        summaryList.select(Selectors.summaryListAtIndexRowValue(1, 1)).text() mustBe "Current 801 Consignor"
        summaryList.select(Selectors.summaryListAtIndexRowKey(1, 2)).text() mustBe "Excise registration number (ERN)"
        summaryList.select(Selectors.summaryListAtIndexRowValue(1, 2)).text() mustBe "GBRC345GTR145"
        summaryList.select(Selectors.summaryListAtIndexRowKey(1, 3)).text() mustBe "Address"
        summaryList.select(Selectors.summaryListAtIndexRowValue(1, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        summaryList.select(Selectors.h3(2)).text() mustBe "Place of dispatch"
        summaryList.select(Selectors.summaryListAtIndexRowKey(2, 1)).text() mustBe "Business name"
        summaryList.select(Selectors.summaryListAtIndexRowValue(2, 1)).text() mustBe "Current 801 Consignor"
        summaryList.select(Selectors.summaryListAtIndexRowKey(2, 2)).text() mustBe "Excise ID (ERN)"
        summaryList.select(Selectors.summaryListAtIndexRowValue(2, 2)).text() mustBe "GBRC345GTR145"
        summaryList.select(Selectors.summaryListAtIndexRowKey(2, 3)).text() mustBe "Address"
        summaryList.select(Selectors.summaryListAtIndexRowValue(2, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        summaryList.select(Selectors.h3(3)).text() mustBe "Place of destination"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 1)).text() mustBe "Business name"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 1)).text() mustBe "Current 801 Consignee"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 2)).text() mustBe "Excise registration number (ERN)"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 2)).text() mustBe "GBRC345GTR145"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 3)).text() mustBe "Address"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 3)).text() mustBe "Main101 Zeebrugge ZZ78"
      }

      "consignor, place of dispatch and consignee are present" in {
        val result = helper.constructMovementDelivery(getMovementResponseModel.copy(deliveryPlaceTrader = None))
        val summaryList = Jsoup.parse(result.toString())
        summaryList.select(Selectors.h3(1)).text() mustBe "Consignor"
        summaryList.select(Selectors.summaryListAtIndexRowKey(1, 1)).text() mustBe "Business name"
        summaryList.select(Selectors.summaryListAtIndexRowValue(1, 1)).text() mustBe "Current 801 Consignor"
        summaryList.select(Selectors.summaryListAtIndexRowKey(1, 2)).text() mustBe "Excise registration number (ERN)"
        summaryList.select(Selectors.summaryListAtIndexRowValue(1, 2)).text() mustBe "GBRC345GTR145"
        summaryList.select(Selectors.summaryListAtIndexRowKey(1, 3)).text() mustBe "Address"
        summaryList.select(Selectors.summaryListAtIndexRowValue(1, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        summaryList.select(Selectors.h3(2)).text() mustBe "Place of dispatch"
        summaryList.select(Selectors.summaryListAtIndexRowKey(2, 1)).text() mustBe "Business name"
        summaryList.select(Selectors.summaryListAtIndexRowValue(2, 1)).text() mustBe "Current 801 Consignor"
        summaryList.select(Selectors.summaryListAtIndexRowKey(2, 2)).text() mustBe "Excise ID (ERN)"
        summaryList.select(Selectors.summaryListAtIndexRowValue(2, 2)).text() mustBe "GBRC345GTR145"
        summaryList.select(Selectors.summaryListAtIndexRowKey(2, 3)).text() mustBe "Address"
        summaryList.select(Selectors.summaryListAtIndexRowValue(2, 3)).text() mustBe "Main101 Zeebrugge ZZ78"

        summaryList.select(Selectors.h3(3)).text() mustBe "Consignee"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 1)).text() mustBe "Business name"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 1)).text() mustBe "Current 801 Consignee"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 2)).text() mustBe "Excise registration number (ERN)"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 2)).text() mustBe "GB12345GTR144"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 3)).text() mustBe "Address"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 3)).text() mustBe "Main101 Zeebrugge ZZ78"
      }

      "consignee is present and destination type is Export (VAT and EORI provided)" in {
        val result = helper.constructMovementDelivery(getMovementResponseModel.copy(consigneeTrader = getMovementResponseModel.consigneeTrader.map(_.copy(
          eoriNumber = Some("EORI123456")
        )), destinationType = Export))
        val summaryList = Jsoup.parse(result.toString())

        summaryList.select(Selectors.h3(3)).text() mustBe "Consignee"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 1)).text() mustBe "Business name"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 1)).text() mustBe "Current 801 Consignee"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 2)).text() mustBe "Identification number"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 2)).text() mustBe "GB12345GTR144"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 3)).text() mustBe "Address"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 3)).text() mustBe "Main101 Zeebrugge ZZ78"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 4)).text() mustBe "EORI number"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 4)).text() mustBe "EORI123456"
      }

      "consignee is present and destination type is Export (VAT provided and EORI not provided)" in {
        val result = helper.constructMovementDelivery(getMovementResponseModel.copy(consigneeTrader = getMovementResponseModel.consigneeTrader.map(_.copy(
          eoriNumber = None,
        )), destinationType = Export))
        val summaryList = Jsoup.parse(result.toString())

        summaryList.select(Selectors.h3(3)).text() mustBe "Consignee"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 1)).text() mustBe "Business name"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 1)).text() mustBe "Current 801 Consignee"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 2)).text() mustBe "Identification number"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 2)).text() mustBe "GB12345GTR144"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 3)).text() mustBe "Address"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 3)).text() mustBe "Main101 Zeebrugge ZZ78"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 4)).isEmpty mustBe true
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 4)).isEmpty mustBe true
      }

      "consignee is present and destination type is Temporary registered consignee" in {
        val result = helper.constructMovementDelivery(getMovementResponseModel.copy(destinationType = TemporaryRegisteredConsignee))
        val summaryList = Jsoup.parse(result.toString())

        summaryList.select(Selectors.h3(3)).text() mustBe "Consignee"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 1)).text() mustBe "Business name"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 1)).text() mustBe "Current 801 Consignee"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 2)).text() mustBe "Identification number for temporary registered consignee"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 2)).text() mustBe "GB12345GTR144"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 3)).text() mustBe "Address"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 3)).text() mustBe "Main101 Zeebrugge ZZ78"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 4)).isEmpty mustBe true
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 4)).isEmpty mustBe true
      }

      "consignee is present and destination type is Temporary certified consignee" in {
        val result = helper.constructMovementDelivery(getMovementResponseModel.copy(destinationType = TemporaryCertifiedConsignee))
        val summaryList = Jsoup.parse(result.toString())

        summaryList.select(Selectors.h3(3)).text() mustBe "Consignee"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 1)).text() mustBe "Business name"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 1)).text() mustBe "Current 801 Consignee"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 2)).text() mustBe "Identification number for temporary certified consignee"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 2)).text() mustBe "GB12345GTR144"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 3)).text() mustBe "Address"
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 3)).text() mustBe "Main101 Zeebrugge ZZ78"
        summaryList.select(Selectors.summaryListAtIndexRowKey(3, 4)).isEmpty mustBe true
        summaryList.select(Selectors.summaryListAtIndexRowValue(3, 4)).isEmpty mustBe true
      }
    }
  }

}
