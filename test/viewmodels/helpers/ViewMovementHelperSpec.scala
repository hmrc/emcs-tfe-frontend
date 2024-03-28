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
import fixtures.GetMovementResponseFixtures
import models.common.DestinationType._
import models.common.RoleType.GBWK
import models.common.{AddressModel, TraderModel}
import models.movementScenario.MovementScenario.EuTaxWarehouse
import models.requests.DataRequest
import models.response.InvalidUserTypeException
import org.jsoup.Jsoup
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, SummaryListRow, Text, Value}
import views.BaseSelectors

class ViewMovementHelperSpec extends SpecBase with GetMovementResponseFixtures {

  implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"))

  val helper: ViewMovementHelper = app.injector.instanceOf[ViewMovementHelper]
  implicit lazy val messages: Messages = messages(request)

  object Selectors extends BaseSelectors {
    val cardTitle = ".govuk-summary-card__title"

    def cardRowKey(i: Int) = s"div.govuk-summary-list__row:nth-of-type($i) > dt"

    def cardRowValue(i: Int) = s"div.govuk-summary-list__row:nth-of-type($i) > dd"

    def cardAtIndexTitle(i: Int) = s"div.govuk-summary-card:nth-of-type($i) .govuk-summary-card__title"

    def cardAtIndexRowKey(cardIndex: Int, rowIndex: Int) = s"div.govuk-summary-card:nth-of-type($cardIndex) div.govuk-summary-list__row:nth-of-type($rowIndex) > dt"

    def cardAtIndexRowValue(cardIndex: Int, rowIndex: Int) = s"div.govuk-summary-card:nth-of-type($cardIndex) div.govuk-summary-list__row:nth-of-type($rowIndex) > dd"
  }


  "constructMovementView" should {
    "output the correct cards" when {

      "the date of arrival is set" in {
        val result = helper.constructMovementView(getMovementResponseModel)
        val card = Jsoup.parse(result.toString())
        card.select(Selectors.cardAtIndexTitle(1)).text() mustBe "Summary"
        card.select(Selectors.cardAtIndexRowKey(1, 1)).text() mustBe "LRN"
        card.select(Selectors.cardAtIndexRowValue(1, 1)).text() mustBe testLrn
        card.select(Selectors.cardAtIndexRowKey(1, 2)).text() mustBe "eAD status"
        card.select(Selectors.cardAtIndexRowValue(1, 2)).text() mustBe "Accepted An eAD has been created and the movement may be in transit."
        card.select(Selectors.cardAtIndexRowKey(1, 3)).text() mustBe "Receipt status"
        card.select(Selectors.cardAtIndexRowValue(1, 3)).text() mustBe "Accepted and unsatisfactory"
        card.select(Selectors.cardAtIndexRowKey(1, 4)).text() mustBe "Movement type"
        card.select(Selectors.cardAtIndexRowValue(1, 4)).text() mustBe "Great Britain tax warehouse to Great Britain tax warehouse"
        card.select(Selectors.cardAtIndexRowKey(1, 5)).text() mustBe "Movement direction"
        card.select(Selectors.cardAtIndexRowValue(1, 5)).text() mustBe "Outbound"

        card.select(Selectors.cardAtIndexTitle(2)).text() mustBe "Time and date"
        card.select(Selectors.cardAtIndexRowKey(2, 1)).text() mustBe "Date of dispatch"
        card.select(Selectors.cardAtIndexRowValue(2, 1)).text() mustBe "20 November 2008"
        card.select(Selectors.cardAtIndexRowKey(2, 2)).text() mustBe "Time of dispatch"
        card.select(Selectors.cardAtIndexRowValue(2, 2)).text().toLowerCase mustBe "12:00 am"
        card.select(Selectors.cardAtIndexRowKey(2, 3)).text() mustBe "Date of arrival"
        card.select(Selectors.cardAtIndexRowValue(2, 3)).text() mustBe "8 December 2008"

        card.select(Selectors.cardAtIndexTitle(3)).text() mustBe "Invoice"
        card.select(Selectors.cardAtIndexRowKey(3, 1)).text() mustBe "Invoice reference"
        card.select(Selectors.cardAtIndexRowValue(3, 1)).text() mustBe "INV123"
        card.select(Selectors.cardAtIndexRowKey(3, 2)).text() mustBe "Invoice date of issue"
        card.select(Selectors.cardAtIndexRowValue(3, 2)).text() mustBe "1 December 2023"
      }

      "the date of arrival is NOT set (calculating the predicted date of arrival) - not showing the receipt status" in {
        val result = helper.constructMovementView(getMovementResponseModel.copy(reportOfReceipt = None))
        val card = Jsoup.parse(result.toString())
        card.select(Selectors.cardAtIndexTitle(1)).text() mustBe "Summary"
        card.select(Selectors.cardAtIndexRowKey(1, 1)).text() mustBe "LRN"
        card.select(Selectors.cardAtIndexRowValue(1, 1)).text() mustBe testLrn
        card.select(Selectors.cardAtIndexRowKey(1, 2)).text() mustBe "eAD status"
        card.select(Selectors.cardAtIndexRowValue(1, 2)).text() mustBe "Accepted An eAD has been created and the movement may be in transit."
        card.select(Selectors.cardAtIndexRowKey(1, 3)).text() mustBe "Movement type"
        card.select(Selectors.cardAtIndexRowValue(1, 3)).text() mustBe "Great Britain tax warehouse to Great Britain tax warehouse"
        card.select(Selectors.cardAtIndexRowKey(1, 4)).text() mustBe "Movement direction"
        card.select(Selectors.cardAtIndexRowValue(1, 4)).text() mustBe "Outbound"

        card.select(Selectors.cardAtIndexTitle(2)).text() mustBe "Time and date"
        card.select(Selectors.cardAtIndexRowKey(2, 1)).text() mustBe "Date of dispatch"
        card.select(Selectors.cardAtIndexRowValue(2, 1)).text() mustBe "20 November 2008"
        card.select(Selectors.cardAtIndexRowKey(2, 2)).text() mustBe "Time of dispatch"
        card.select(Selectors.cardAtIndexRowValue(2, 2)).text().toLowerCase mustBe "12:00 am"
        card.select(Selectors.cardAtIndexRowKey(2, 3)).text() mustBe "Predicted arrival"
        card.select(Selectors.cardAtIndexRowValue(2, 3)).text() mustBe "10 December 2008"

        card.select(Selectors.cardAtIndexTitle(3)).text() mustBe "Invoice"
        card.select(Selectors.cardAtIndexRowKey(3, 1)).text() mustBe "Invoice reference"
        card.select(Selectors.cardAtIndexRowValue(3, 1)).text() mustBe "INV123"
        card.select(Selectors.cardAtIndexRowKey(3, 2)).text() mustBe "Invoice date of issue"
        card.select(Selectors.cardAtIndexRowValue(3, 2)).text() mustBe "1 December 2023"
      }

      Seq("Accepted" -> "An eAD has been created and the movement may be in transit.",
        "Cancelled" -> "The consignor has cancelled the movement before the date of dispatch on the eAD.",
        "Delivered" -> "The consignee has accepted the goods and the movement has been closed.",
        "Diverted" -> "The consignor has successfully submitted a change of destination before the goods have been received or in response to the consignee’s complete refusal of the goods.",
        "ManuallyClosed" -> "The member state of the consignor has manually closed the movement due to a technical problem preventing a report of receipt or because the consignee has gone out of business.",
        "Refused" -> "The consignee has refused all goods in the movement and the consignor must create a change of destination.",
        "None" -> "",
        "PartiallyRefused" -> "The consignee has refused some goods in the movement and the consignor must create a change of destination.",
        "Exporting" -> "The movement has been accepted for export by customs.",
        "DeemedExported" -> "The movement has been approved for export by customs, but this status does not confirm that the goods have been exported (the report of receipt will provide confirmation).",
        "Replaced" -> "The movement of energy products has been split into two or more parts (up to a maximum of 9 parts).",
        "Stopped" -> "Officials have seized the movement because of an incident or irregularity.",
        "Rejected" -> "The consignee has rejected the movement and the consignor must now submit a change of destination if the goods are in transit or cancel the movement if the goods have not departed."
      ).foreach { statusToExplanation =>

        s"when the eAD status is ${statusToExplanation._1} - show the correct status and explanation" in {
          val result = helper.constructMovementView(getMovementResponseModel.copy(eadStatus = statusToExplanation._1))
          val card = Jsoup.parse(result.toString())
          if (statusToExplanation._1 == "None") {
            card.select(Selectors.cardAtIndexRowKey(1, 2)).text() mustBe "Receipt status"
          } else {
            card.select(Selectors.cardAtIndexRowKey(1, 2)).text() mustBe "eAD status"
            card.select(Selectors.cardAtIndexRowValue(1, 2)).text() mustBe s"${statusToExplanation._1} ${statusToExplanation._2}"
          }
        }

      }
    }
  }

  "getMovementTypeForMovementView" should {
    "show GB tax warehouse to GB tax warehouse" when {
      "the users ERN starts with GBWK and the movement scenario is GB Tax Warehouse " +
        "(destination type is TaxWarehouse and Delivery Place ERN starts with GB)" in {
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          destinationType = TaxWarehouse,
          deliveryPlaceTrader = Some(TraderModel(
            traderExciseNumber = Some("GBWK123456789"),
            traderName = Some("Current 801 Consignee"),
            address = Some(AddressModel(
              streetNumber = None,
              street = Some("Main101"),
              postcode = Some("ZZ78"),
              city = Some("Zeebrugge")
            )),
            vatNumber = Some("GB123456789"),
            eoriNumber = None
          ))
        ))

        result mustBe "Great Britain tax warehouse to Great Britain tax warehouse"
      }

      "the users ERN starts with GBWK and the movement scenario is GB Tax Warehouse " +
        "(destination type is TaxWarehouse and Delivery Place ERN starts with XI)" in {
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          destinationType = TaxWarehouse,
          deliveryPlaceTrader = Some(TraderModel(
            traderExciseNumber = Some("XI00345GTR145"),
            traderName = Some("Current 801 Consignee"),
            address = Some(AddressModel(
              streetNumber = None,
              street = Some("Main101"),
              postcode = Some("ZZ78"),
              city = Some("Zeebrugge")
            )),
            vatNumber = Some("GB123456789"),
            eoriNumber = None
          ))
        ))

        result mustBe "Great Britain tax warehouse to Great Britain tax warehouse"
      }
    }

    "show GB tax warehouse to Y for XIWK ERN (dispatch place is GB)" when {
      Seq(
        TaxWarehouse -> "Great Britain tax warehouse to Tax warehouse in Great Britain",
        DirectDelivery -> "Great Britain tax warehouse to Direct delivery",
        RegisteredConsignee -> "Great Britain tax warehouse to Registered consignee",
        TemporaryRegisteredConsignee -> "Great Britain tax warehouse to Temporary registered consignee",
        ExemptedOrganisation -> "Great Britain tax warehouse to Exempted organisation",
        UnknownDestination -> "Great Britain tax warehouse to Unknown destination"
      ).foreach { destinationTypeToMessage =>

        s"when the destination type is ${destinationTypeToMessage._1}" in {
          val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
            destinationType = destinationTypeToMessage._1,
            placeOfDispatchTrader = Some(TraderModel(
              traderExciseNumber = Some("GBRC345GTR145"),
              traderName = Some("Current 801 Consignee"),
              address = Some(AddressModel(
                streetNumber = None,
                street = Some("Main101"),
                postcode = Some("ZZ78"),
                city = Some("Zeebrugge")
              )),
              vatNumber = Some("GB123456789"),
              eoriNumber = None
            ))
          ))(dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789"), implicitly)

          result mustBe destinationTypeToMessage._2
        }

      }
    }

    "show XI tax warehouse to Y for XIWK ERN (dispatch place is XI)" when {
      Seq(
        TaxWarehouse -> "Northern Ireland tax warehouse to Tax warehouse in Great Britain",
        DirectDelivery -> "Northern Ireland tax warehouse to Direct delivery",
        RegisteredConsignee -> "Northern Ireland tax warehouse to Registered consignee",
        TemporaryRegisteredConsignee -> "Northern Ireland tax warehouse to Temporary registered consignee",
        ExemptedOrganisation -> "Northern Ireland tax warehouse to Exempted organisation",
        UnknownDestination -> "Northern Ireland tax warehouse to Unknown destination"
      ).foreach { destinationTypeToMessage =>

        s"when the destination type is ${destinationTypeToMessage._1}" in {
          val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
            destinationType = destinationTypeToMessage._1,
            placeOfDispatchTrader = Some(TraderModel(
              traderExciseNumber = Some("XI00345GTR145"),
              traderName = Some("Current 801 Consignee"),
              address = Some(AddressModel(
                streetNumber = None,
                street = Some("Main101"),
                postcode = Some("ZZ78"),
                city = Some("Zeebrugge")
              )),
              vatNumber = Some("GB123456789"),
              eoriNumber = None
            ))
          ))(dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789"), implicitly)

          result mustBe destinationTypeToMessage._2
        }

      }
    }

    "show Import for GB/XI" when {

      "the users ERN starts with GBRC" in {
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          destinationType = RegisteredConsignee
        ))(dataRequest(FakeRequest("GET", "/"), ern = "GBRC123456789"), implicitly)
        result mustBe "Import for Registered consignee"
      }

      "the users ERN starts with XIRC" in {
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          destinationType = RegisteredConsignee
        ))(dataRequest(FakeRequest("GET", "/"), ern = "XIRC123456789"), implicitly)
        result mustBe "Import for Registered consignee"
      }

    }

    "show the destination type only" when {

      "the users ERN starts with GBWK and the movement type is an Export (Customs declaration in UK)" in {
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          deliveryPlaceCustomsOfficeReferenceNumber = Some("GBWK123456789"),
          destinationType = Export
        ))(dataRequest(FakeRequest("GET", "/"), ern = "GBWK123456789"), implicitly)
        result mustBe "Export with customs declaration lodged in the United Kingdom"
      }

      "the users ERN starts with GBWK and the movement type is an Export (Customs declaration in EU)" in {
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          deliveryPlaceCustomsOfficeReferenceNumber = Some("FR123456789"),
          destinationType = Export
        ))(dataRequest(FakeRequest("GET", "/"), ern = "GBWK123456789"), implicitly)
        result mustBe "Export with customs declaration lodged in the European Union"
      }

      "the users ERN starts with XIWK and the movement type is an Export (Customs declaration in UK)" in {
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          deliveryPlaceCustomsOfficeReferenceNumber = Some("GBWK123456789"),
          destinationType = Export
        ))(dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789"), implicitly)
        result mustBe "Export with customs declaration lodged in the United Kingdom"
      }

      "the users ERN starts with XIWK and the movement type is an Export (Customs declaration in EU)" in {
        val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
          deliveryPlaceCustomsOfficeReferenceNumber = Some("FR123456789"),
          destinationType = Export
        ))(dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789"), implicitly)
        result mustBe "Export with customs declaration lodged in the European Union"
      }

    }

    "throw an exception when the user type / destination type can't be matched" in {
      lazy val result = helper.getMovementTypeForMovementView(getMovementResponseModel.copy(
        deliveryPlaceTrader = None,
        destinationType = TaxWarehouse
      ))(dataRequest(FakeRequest("GET", "/"), ern = "GBWK123456789"), implicitly)
      intercept[InvalidUserTypeException](result).message mustBe s"[ViewMovementHelper][constructMovementView][getMovementTypeForMovementView] invalid UserType and movement scenario combination for MOV journey: $GBWK | $EuTaxWarehouse"
    }
  }

  "getDateOfArrivalRow" should {

    "return the 'Predicted arrival'" when {

      "the date of arrival is not present in the GetMovementResponse" in {
        val result = helper.getDateOfArrivalRow(getMovementResponseModel.copy(reportOfReceipt = None))
        result mustBe SummaryListRow(
          key = Key(Text(value = "Predicted arrival")),
          value = Value(Text(value = "10 December 2008")),
          classes = "govuk-summary-list__row"
        )
      }

    }

    "return the 'Date of arrival'" when {

      "the date of arrival is present in the GetMovementResponse" in {
        val result = helper.getDateOfArrivalRow(getMovementResponseModel)
        result mustBe SummaryListRow(
          key = Key(Text(value = "Date of arrival")),
          value = Value(Text(value = "8 December 2008")),
          classes = "govuk-summary-list__row"
        )
      }

    }
  }
}
