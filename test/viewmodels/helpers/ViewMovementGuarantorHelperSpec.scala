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
import models.common.GuarantorType._
import models.common.{AddressModel, MovementGuaranteeModel, TraderModel}
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
    override def h2(i: Int) = s"h2:nth-of-type($i)"
    def summaryListRowKey(i: Int) = s"div.govuk-summary-list__row:nth-of-type($i) > dt"
    def summaryListRowValue(i: Int) = s"div.govuk-summary-list__row:nth-of-type($i) > dd"
  }

  ".constructMovementGuarantor" should {

    Seq(GuarantorNotRequired, NoGuarantor).foreach { guarantorType =>
      s"render no summary rows when the guarantor type is ${simpleName(guarantorType)}" in {
        val result = helper.constructMovementGuarantor(movement = getMovementResponseModel.copy(movementGuarantee = MovementGuaranteeModel(guarantorType, None)))
        val body = Jsoup.parse(result.toString())
        body.select("h2").text() mustBe "Guarantor details"
        body.select("p").text() mustBe "No guarantor required for this movement."
      }
    }

    Seq(
      Consignor -> "Consignor",
      Transporter -> "Transporter",
      Owner -> "Owner of goods",
      Consignee -> "Consignee"
    ).foreach { guarantorType =>
      s"render 1 summary card when the guarantor is a single guarantor type - ${simpleName(guarantorType._1)}" in {
        val result = helper.constructMovementGuarantor(getMovementResponseModel.copy(movementGuarantee = MovementGuaranteeModel(guarantorType._1, Some(Seq(
          TraderModel(
            traderExciseNumber = Some("GBRC345GTR145"),
            traderName = Some("Trader name here"),
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
        card.select(Selectors.summaryListRowKey(1)).text() mustBe "Type"
        card.select(Selectors.summaryListRowValue(1)).text() mustBe guarantorType._2
        card.select(Selectors.summaryListRowKey(2)).text() mustBe "Name"
        card.select(Selectors.summaryListRowValue(2)).text() mustBe "Trader name here"
        card.select(Selectors.summaryListRowKey(3)).text() mustBe "Excise ID"
        card.select(Selectors.summaryListRowValue(3)).text() mustBe "GBRC345GTR145"
        card.select(Selectors.summaryListRowKey(4)).text() mustBe "Address"
        card.select(Selectors.summaryListRowValue(4)).text() mustBe "Main101 Zeebrugge ZZ78"
        card.select(Selectors.summaryListRowKey(5)).text() mustBe "VAT registration number"
        card.select(Selectors.summaryListRowValue(5)).text() mustBe "GB123456789"
      }
    }

    Seq(
      ConsignorTransporterOwner -> "Joint guarantee of the consignor, of the transporter and of the owner of the Excise products",
      ConsignorTransporterOwnerConsignee -> "Joint guarantee of the consignor, of the transporter, of the owner of the Excise products and of the consignee",
      JointConsignorConsignee -> "Joint guarantee of the consignor and of the consignee",
      TransporterOwner -> "Joint guarantee of the transporter and of the owner of the Excise products",
      TransporterOwnerConsignee -> "Joint guarantee of the transporter, of the owner of the Excise products and of the consignee"
    ).foreach { guarantorType =>
      s"render 2 summary cards when the guarantor is a joint guarantor type - ${simpleName(guarantorType._1)}" in {
        val result = helper.constructMovementGuarantor(getMovementResponseModel.copy(movementGuarantee = MovementGuaranteeModel(guarantorType._1, Some(Seq(
          TraderModel(
            traderExciseNumber = Some("GBRC345GTR145"),
            traderName = Some("Trader name 1 here"),
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
            traderName = Some("Trader name 2 here"),
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

        val cards = Jsoup.parse(result.toString())
        val summaryCards = cards.getElementsByClass("govuk-summary-card")

        val firstSummaryCard = summaryCards.get(0)
        firstSummaryCard.select(Selectors.h2(1)).text() mustBe "Guarantor summary"
        val firstSummaryListRows = firstSummaryCard.getElementsByClass("govuk-summary-list__row")
        firstSummaryListRows.select(Selectors.summaryListRowKey(1)).text() mustBe "Type"
        firstSummaryListRows.select(Selectors.summaryListRowValue(1)).text() mustBe guarantorType._2

        val firstJointGuarantor = summaryCards.get(1)
        firstJointGuarantor.select(Selectors.h2(1)).text() mustBe "Joint guarantor 1"
        val firstGuarantorSummaryListRows = firstJointGuarantor.getElementsByClass("govuk-summary-list__row")
        firstGuarantorSummaryListRows.select(Selectors.summaryListRowKey(1)).text() mustBe "Name"
        firstGuarantorSummaryListRows.select(Selectors.summaryListRowValue(1)).text() mustBe "Trader name 1 here"
        firstGuarantorSummaryListRows.select(Selectors.summaryListRowKey(2)).text() mustBe "Excise ID"
        firstGuarantorSummaryListRows.select(Selectors.summaryListRowValue(2)).text() mustBe "GBRC345GTR145"
        firstGuarantorSummaryListRows.select(Selectors.summaryListRowKey(3)).text() mustBe "Address"
        firstGuarantorSummaryListRows.select(Selectors.summaryListRowValue(3)).text() mustBe "Main101 Zeebrugge ZZ78"
        firstGuarantorSummaryListRows.select(Selectors.summaryListRowKey(4)).text() mustBe "VAT registration number"
        firstGuarantorSummaryListRows.select(Selectors.summaryListRowValue(4)).text() mustBe "GB123456789"

        val secondJointGuarantor = summaryCards.get(2)
        secondJointGuarantor.select(Selectors.h2(1)).text() mustBe "Joint guarantor 2"
        val secondGuarantorSummaryListRows = secondJointGuarantor.getElementsByClass("govuk-summary-list__row")
        secondGuarantorSummaryListRows.select(Selectors.summaryListRowKey(1)).text() mustBe "Name"
        secondGuarantorSummaryListRows.select(Selectors.summaryListRowValue(1)).text() mustBe "Trader name 2 here"
        secondGuarantorSummaryListRows.select(Selectors.summaryListRowKey(2)).text() mustBe "Excise ID"
        secondGuarantorSummaryListRows.select(Selectors.summaryListRowValue(2)).text() mustBe "GBRC345GTR146"
        secondGuarantorSummaryListRows.select(Selectors.summaryListRowKey(3)).text() mustBe "Address"
        secondGuarantorSummaryListRows.select(Selectors.summaryListRowValue(3)).text() mustBe "Main102 Zeebrugge ZZ79"
        secondGuarantorSummaryListRows.select(Selectors.summaryListRowKey(4)).text() mustBe "VAT registration number"
        secondGuarantorSummaryListRows.select(Selectors.summaryListRowValue(4)).text() mustBe "GB123456790"
      }
    }

    Seq(
      ConsignorTransporter -> "Joint guarantee of the consignor and of the transporter",
      ConsignorTransporterConsignee -> "Joint guarantee of the consignor, of the transporter and of the consignee",
      ConsignorOwner -> "Joint guarantee of the consignor and of the owner of the Excise products",
      ConsignorOwnerConsignee -> "Joint guarantee of the consignor, of the owner of the Excise products and of the consignee",
      TransporterConsignee -> "Joint guarantee of the transporter and of the consignee",
      OwnerConsignee -> "Joint guarantee of the owner of the Excise products and of the consignee"
    ).foreach { guarantorType =>
      s"render 1 summary card when the guarantor is a joint guarantor type with only 1 guarantor - ${simpleName(guarantorType._1)}" in {
        val result = helper.constructMovementGuarantor(getMovementResponseModel.copy(movementGuarantee = MovementGuaranteeModel(guarantorType._1, Some(Seq(
          TraderModel(
            traderExciseNumber = Some("GBRC345GTR145"),
            traderName = Some("Trader name 1 here"),
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

        val cards = Jsoup.parse(result.toString())
        val summaryCards = cards.getElementsByClass("govuk-summary-card")

        val firstSummaryCard = summaryCards.get(0)
        firstSummaryCard.select(Selectors.h2(1)).text() mustBe "Guarantor summary"
        val firstSummaryListRows = firstSummaryCard.getElementsByClass("govuk-summary-list__row")
        firstSummaryListRows.select(Selectors.summaryListRowKey(1)).text() mustBe "Type"
        firstSummaryListRows.select(Selectors.summaryListRowValue(1)).text() mustBe guarantorType._2

        val firstJointGuarantor = summaryCards.get(1)
        firstJointGuarantor.select(Selectors.h2(1)).text() mustBe "Joint guarantor 1"
        val firstGuarantorSummaryListRows = firstJointGuarantor.getElementsByClass("govuk-summary-list__row")
        firstGuarantorSummaryListRows.select(Selectors.summaryListRowKey(1)).text() mustBe "Name"
        firstGuarantorSummaryListRows.select(Selectors.summaryListRowValue(1)).text() mustBe "Trader name 1 here"
        firstGuarantorSummaryListRows.select(Selectors.summaryListRowKey(2)).text() mustBe "Excise ID"
        firstGuarantorSummaryListRows.select(Selectors.summaryListRowValue(2)).text() mustBe "GBRC345GTR145"
        firstGuarantorSummaryListRows.select(Selectors.summaryListRowKey(3)).text() mustBe "Address"
        firstGuarantorSummaryListRows.select(Selectors.summaryListRowValue(3)).text() mustBe "Main101 Zeebrugge ZZ78"
        firstGuarantorSummaryListRows.select(Selectors.summaryListRowKey(4)).text() mustBe "VAT registration number"
        firstGuarantorSummaryListRows.select(Selectors.summaryListRowValue(4)).text() mustBe "GB123456789"
      }
    }
  }
}
