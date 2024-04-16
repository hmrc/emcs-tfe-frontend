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
import models.common.GuarantorType.{Consignee, Consignor, GuarantorNotRequired, NoGuarantor, Owner, Transporter}
import models.common.{AddressModel, GuarantorType, MovementGuaranteeModel, TraderModel}
import models.requests.DataRequest
import org.jsoup.Jsoup
import play.api.i18n.Messages
import play.api.test.FakeRequest
import views.BaseSelectors

class ViewMovementGuarantorHelperSpec extends SpecBase with GetMovementResponseFixtures {

  implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"))

  val helper: ViewMovementGuarantorHelper = app.injector.instanceOf[ViewMovementGuarantorHelper]
  implicit lazy val messages: Messages = messages(request)

  object Selectors extends BaseSelectors {
    val cardTitle = ".govuk-summary-card__title"

    def cardRowKey(i: Int) = s"div.govuk-summary-list__row:nth-of-type($i) > dt"

    def cardRowValue(i: Int) = s"div.govuk-summary-list__row:nth-of-type($i) > dd"

    def cardAtIndexTitle(i: Int) = s"div.govuk-summary-card:nth-of-type($i) .govuk-summary-card__title"

    def cardAtIndexRowKey(cardIndex: Int, rowIndex: Int) = s"div.govuk-summary-card:nth-of-type($cardIndex) div.govuk-summary-list__row:nth-of-type($rowIndex) > dt"

    def cardAtIndexRowValue(cardIndex: Int, rowIndex: Int) = s"div.govuk-summary-card:nth-of-type($cardIndex) div.govuk-summary-list__row:nth-of-type($rowIndex) > dd"
  }

  ".constructMovementGuarantor" should {

    Seq(GuarantorNotRequired, NoGuarantor).foreach { guarantorType =>
      s"render no summary rows when the guarantor type is $guarantorType" in {
        val result = helper.constructMovementGuarantor(getMovementResponseModel.copy(movementGuarantee = MovementGuaranteeModel(guarantorType, None)))
        val body = Jsoup.parse(result.toString())
        body.select("h2").text() mustBe "Guarantor details"
        body.select("p").text() mustBe "No guarantor required for this movement."
      }
    }

    Seq(
      Consignor -> "Consignor",
      Transporter -> "Transporter",
      Owner -> "Owner of goods",
      Consignee -> "Consignee",
    ).foreach { guarantorType =>
      s"render 1 summary card when the guarantor type is ${guarantorType._1}" in {
        val result = helper.constructMovementGuarantor(getMovementResponseModel.copy(movementGuarantee = MovementGuaranteeModel(guarantorType._1, Some(Seq(
          TraderModel(
            traderExciseNumber = Some("GBRC345GTR145"),
            traderName = Some("Current 801 Consignor"),
            address = Some(AddressModel(
              streetNumber = None,
              street = Some("Main101"),
              postcode = Some("ZZ78"),
              city = Some("Zeebrugge")
            )),
            vatNumber = Some("GB123456789"),
            eoriNumber = None
          )
        )))))
        val card = Jsoup.parse(result.toString())
        card.select(Selectors.cardAtIndexTitle(1)).text() mustBe "Summary"
        card.select(Selectors.cardAtIndexRowKey(1, 1)).text() mustBe "Type"
        card.select(Selectors.cardAtIndexRowValue(1, 1)).text() mustBe guarantorType._2
        card.select(Selectors.cardAtIndexRowKey(1, 2)).text() mustBe "Business name"
        card.select(Selectors.cardAtIndexRowValue(1, 2)).text() mustBe "Current 801 Consignor"
        card.select(Selectors.cardAtIndexRowKey(1, 3)).text() mustBe "Excise registration number (ERN)"
        card.select(Selectors.cardAtIndexRowValue(1, 3)).text() mustBe "GBRC345GTR145"
        card.select(Selectors.cardAtIndexRowKey(1, 4)).text() mustBe "Address"
        card.select(Selectors.cardAtIndexRowValue(1, 4)).text() mustBe "Main101 Zeebrugge ZZ78"
        card.select(Selectors.cardAtIndexRowKey(1, 5)).text() mustBe "VAT registration number"
        card.select(Selectors.cardAtIndexRowValue(1, 5)).text() mustBe "GB123456789"
      }
    }

    GuarantorType.values.diff(Seq(GuarantorNotRequired, NoGuarantor, Consignor, Transporter, Owner, Consignee)).foreach { guarantorType =>
      s"render 2 summary cards when the guarantor type is $guarantorType" in {
        //TODO: add this test when the ticket to handle multiple guarantors is being played
        val result = helper.constructMovementGuarantor(getMovementResponseModel.copy(movementGuarantee = MovementGuaranteeModel(guarantorType, Some(Seq(
          TraderModel(
            traderExciseNumber = Some("GBRC345GTR145"),
            traderName = Some("Current 801 Consignor 1"),
            address = Some(AddressModel(
              streetNumber = None,
              street = Some("Main101"),
              postcode = Some("ZZ78"),
              city = Some("Zeebrugge")
            )),
            vatNumber = Some("GB123456789"),
            eoriNumber = None
          ),
          TraderModel(
            traderExciseNumber = Some("GBRC345GTR146"),
            traderName = Some("Current 801 Consignor 2"),
            address = Some(AddressModel(
              streetNumber = None,
              street = Some("Main102"),
              postcode = Some("ZZ79"),
              city = Some("Zeebrugge")
            )),
            vatNumber = Some("GB123456790"),
            eoriNumber = None
          )
        )))))
      }
    }
  }

}
