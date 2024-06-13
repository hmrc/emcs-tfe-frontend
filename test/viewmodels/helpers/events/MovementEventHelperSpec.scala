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

package viewmodels.helpers.events

import base.SpecBase
import fixtures.GetMovementResponseFixtures
import models.common.GuarantorType.{Consignee, Consignor, GuarantorNotRequired, NoGuarantor, Owner, Transporter}
import models.common.TransportArrangement.OwnerOfGoods
import models.common.UnitOfMeasure.Kilograms
import models.common.WrongWithMovement.{BrokenSeals, Damaged, Excess, Other, Shortage}
import models.common._
import models.requests.DataRequest
import models.response.emcsTfe.reportOfReceipt.{IE818ItemModelWithCnCodeInformation, ReceiptedItemsModel, UnsatisfactoryModel}
import models.response.emcsTfe.{GetMovementResponse, HeaderEadEsadModel, TransportModeModel}
import models.response.referenceData.CnCodeInformation
import org.jsoup.Jsoup
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.Html
import viewmodels.helpers.SummaryListHelper.summaryListRowBuilder
import views.BaseSelectors
import views.ViewUtils.LocalDateExtensions

// scalastyle:off magic.number
class MovementEventHelperSpec extends SpecBase with GetMovementResponseFixtures {

  implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"))

  val helper: MovementEventHelper = app.injector.instanceOf[MovementEventHelper]

  implicit lazy val messages: Messages = messages(request)

  object Selectors extends BaseSelectors {
    override def h2(i: Int) = s"h2:nth-of-type($i)"

    override def h3(i: Int) = s"h3:nth-of-type($i)"

    override val link: Int => String = i => s"a:nth-of-type($i)"

    def summaryListRowKey(i: Int) = s"div.govuk-summary-list__row:nth-of-type($i) > dt"

    def summaryListRowValue(i: Int) = s"div.govuk-summary-list__row:nth-of-type($i) > dd"
  }


  "movementInformationCard" must {
    implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(
      eadEsad = eadEsadModel.copy(
        upstreamArc = Some("ARC1234567890")
      )
    )

    "output the correct rows" when {
      "when all data is present" in {

        val result = helper.movementInformationCard()
        val doc = Jsoup.parse(result.toString())

        doc.select(Selectors.h2(1)).text() mustBe "Movement information"
        doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Replaced ARC"
        doc.select(Selectors.summaryListRowValue(1)).text() mustBe "ARC1234567890"
        doc.select(Selectors.summaryListRowKey(2)).text() mustBe "LRN"
        doc.select(Selectors.summaryListRowValue(2)).text() mustBe testLrn
        doc.select(Selectors.summaryListRowKey(3)).text() mustBe "Origin type"
        doc.select(Selectors.summaryListRowValue(3)).text() mustBe "Tax warehouse"
        doc.select(Selectors.summaryListRowKey(4)).text() mustBe "Destination type"
        doc.select(Selectors.summaryListRowValue(4)).text() mustBe "Tax warehouse"
        doc.select(Selectors.summaryListRowKey(5)).text() mustBe "Date of dispatch"
        doc.select(Selectors.summaryListRowValue(5)).text() mustBe "20 November 2008"
        doc.select(Selectors.summaryListRowKey(6)).text() mustBe "Time of dispatch"
        doc.select(Selectors.summaryListRowValue(6)).text() mustBe "12:00 am"
        doc.select(Selectors.summaryListRowKey(7)).text() mustBe "Invoice reference"
        doc.select(Selectors.summaryListRowValue(7)).text() mustBe "INV123"
        doc.select(Selectors.summaryListRowKey(8)).text() mustBe "Invoice date"
        doc.select(Selectors.summaryListRowValue(8)).text() mustBe "1 December 2023"
      }
    }
  }

  "consignorInformationCard" must {
    implicit val _movement: GetMovementResponse = getMovementResponseModel

    "output the correct rows" when {
      "when all data is present" in {

        val result = helper.consignorInformationCard()
        val doc = Jsoup.parse(result.toString())

        doc.select(Selectors.h2(1)).text() mustBe "Consignor"
        doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Name"
        doc.select(Selectors.summaryListRowValue(1)).text() mustBe "Current 801 Consignor"
        doc.select(Selectors.summaryListRowKey(2)).text() mustBe "Excise Registration Number (ERN)"
        doc.select(Selectors.summaryListRowValue(2)).text() mustBe "GBRC345GTR145"
        doc.select(Selectors.summaryListRowKey(3)).text() mustBe "Address"
        doc.select(Selectors.summaryListRowValue(3)).text() mustBe "Main101 Zeebrugge ZZ78"
      }
    }
  }

  "placeOfDispatchInformationCard" must {
    "output the correct rows when all data is present" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel

      val result = helper.placeOfDispatchInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.select(Selectors.h2(1)).text() mustBe "Place of dispatch"

      doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Name"
      doc.select(Selectors.summaryListRowValue(1)).text() mustBe "Current 801 Consignor"
      doc.select(Selectors.summaryListRowKey(2)).text() mustBe "Warehouse excise ID"
      doc.select(Selectors.summaryListRowValue(2)).text() mustBe "GBRC345GTR145"
      doc.select(Selectors.summaryListRowKey(3)).text() mustBe "Address"
      doc.select(Selectors.summaryListRowValue(3)).text() mustBe "Main101 Zeebrugge ZZ78"
    }

    "output nothing when there is no place of dispatch" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(placeOfDispatchTrader = None)

      val result = helper.placeOfDispatchInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.body().text() mustBe ""
    }
  }

  "importInformationCard" must {
    "output the correct rows when all data is present" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(
        dispatchImportOfficeReferenceNumber = Some("GB123")
      )

      val result = helper.importInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.select(Selectors.h2(1)).text() mustBe "Import"

      doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Customs office code"
      doc.select(Selectors.summaryListRowValue(1)).text() mustBe "GB123"
    }

    "output nothing when there is no import office reference number" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(dispatchImportOfficeReferenceNumber = None)

      val result = helper.importInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.body().text() mustBe ""
    }
  }

  "consigneeInformationCard" must {
    implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(
      consigneeTrader = Some(TraderModel(
        traderExciseNumber = Some("GB12345GTR144"),
        traderName = Some("Current 801 Consignee"),
        address = Some(AddressModel(
          streetNumber = None,
          street = Some("Main101"),
          postcode = Some("ZZ78"),
          city = Some("Zeebrugge")
        )),
        vatNumber = Some("VAT1234567890"),
        eoriNumber = Some("EORI1234567890")
      ))
    )

    "output the correct rows when all data is present" in {
      val result = helper.consigneeInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.select(Selectors.h2(1)).text() mustBe "Consignee"

      doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Name"
      doc.select(Selectors.summaryListRowValue(1)).text() mustBe "Current 801 Consignee"
      doc.select(Selectors.summaryListRowKey(2)).text() mustBe "Excise Registration Number (ERN)"
      doc.select(Selectors.summaryListRowValue(2)).text() mustBe "GB12345GTR144"
      doc.select(Selectors.summaryListRowKey(3)).text() mustBe "Identification number"
      doc.select(Selectors.summaryListRowValue(3)).text() mustBe "VAT1234567890"
      doc.select(Selectors.summaryListRowKey(4)).text() mustBe "Address"
      doc.select(Selectors.summaryListRowValue(4)).text() mustBe "Main101 Zeebrugge ZZ78"
      doc.select(Selectors.summaryListRowKey(5)).text() mustBe "EORI number"
      doc.select(Selectors.summaryListRowValue(5)).text() mustBe "EORI1234567890"
    }

    "output the identifier number summary row when the destination type is a TemporaryRegisteredConsignee" in {
      val result = helper.consigneeInformationCard()(_movement.copy(destinationType = DestinationType.TemporaryRegisteredConsignee), messages)
      val doc = Jsoup.parse(result.toString())

      doc.select(Selectors.summaryListRowKey(2)).text() mustBe "Identification number for temporary registered consignee"
      doc.select(Selectors.summaryListRowValue(2)).text() mustBe "GB12345GTR144"
    }

    "output nothing when there is no consignee" in {
      val result = helper.consigneeInformationCard()(_movement.copy(consigneeTrader = None), messages)
      val doc = Jsoup.parse(result.toString())

      doc.body().text() mustBe ""
    }
  }

  "exemptedOrganisationInformationCard" must {
    "output the correct rows when all data is present" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(
        memberStateCode = Some("GB"),
        serialNumberOfCertificateOfExemption = Some("1234567890")
      )

      val result = helper.exemptedOrganisationInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.select(Selectors.h2(1)).text() mustBe "Exempted organisation"

      doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Member state code"
      doc.select(Selectors.summaryListRowValue(1)).text() mustBe "GB"
      doc.select(Selectors.summaryListRowKey(2)).text() mustBe "Exemption certificate serial number"
      doc.select(Selectors.summaryListRowValue(2)).text() mustBe "1234567890"
    }

    "output nothing when there are no exempted organisation details" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(
        memberStateCode = None,
        serialNumberOfCertificateOfExemption = None
      )

      val result = helper.exemptedOrganisationInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.body().text() mustBe ""
    }
  }

  "placeOfDestinationInformationCard" must {
    "output the correct rows when all data is present" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel

      val result = helper.placeOfDestinationInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.select(Selectors.h2(1)).text() mustBe "Place of destination"

      doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Name"
      doc.select(Selectors.summaryListRowValue(1)).text() mustBe "Current 801 Consignee"
      doc.select(Selectors.summaryListRowKey(2)).text() mustBe "Warehouse excise ID"
      doc.select(Selectors.summaryListRowValue(2)).text() mustBe "GBRC345GTR145"
      doc.select(Selectors.summaryListRowKey(3)).text() mustBe "VAT registration number"
      doc.select(Selectors.summaryListRowValue(3)).text() mustBe "GB123456789"
      doc.select(Selectors.summaryListRowKey(4)).text() mustBe "Address"
      doc.select(Selectors.summaryListRowValue(4)).text() mustBe "Main101 Zeebrugge ZZ78"
    }

    "output nothing when there are no exempted organisation details" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(deliveryPlaceTrader = None)

      val result = helper.placeOfDestinationInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.body().text() mustBe ""
    }
  }

  "exportInformationCard" must {
    "output the correct rows when all data is present" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel

      val result = helper.exportInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.select(Selectors.h2(1)).text() mustBe "Export"

      doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Customs office code"
      doc.select(Selectors.summaryListRowValue(1)).text() mustBe "FR000003"
    }

    "output nothing when there are no export delivery customs office" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(deliveryPlaceCustomsOfficeReferenceNumber = None)

      val result = helper.exportInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.body().text() mustBe ""
    }
  }

  "guarantorInformationCard" must {

    "output the correct rows" when {
      "the guarantor type is an Owner" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(
          movementGuarantee = getMovementResponseModel.movementGuarantee.copy(
            guarantorTypeCode = Owner
          )
        )

        val result = helper.guarantorInformationCard()
        val doc = Jsoup.parse(result.toString())

        doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Guarantor arranger"
        doc.select(Selectors.summaryListRowValue(1)).text() mustBe "Owner of goods"
        doc.select(Selectors.summaryListRowKey(2)).text() mustBe "Guarantor name"
        doc.select(Selectors.summaryListRowValue(2)).text() mustBe "Current 801 Guarantor"
        doc.select(Selectors.summaryListRowKey(3)).text() mustBe "Guarantor VAT registration number"
        doc.select(Selectors.summaryListRowValue(3)).text() mustBe "GB123456789"
        doc.select(Selectors.summaryListRowKey(4)).text() mustBe "Guarantor address"
        doc.select(Selectors.summaryListRowValue(4)).text() mustBe "Main101 Zeebrugge ZZ78"
      }

      "the guarantor type is a Transporter" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(
          movementGuarantee = getMovementResponseModel.movementGuarantee.copy(
            guarantorTypeCode = Transporter
          )
        )

        val result = helper.guarantorInformationCard()
        val doc = Jsoup.parse(result.toString())

        doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Guarantor arranger"
        doc.select(Selectors.summaryListRowValue(1)).text() mustBe "Transporter"
        doc.select(Selectors.summaryListRowKey(2)).text() mustBe "Guarantor name"
        doc.select(Selectors.summaryListRowValue(2)).text() mustBe "Current 801 Guarantor"
        doc.select(Selectors.summaryListRowKey(3)).text() mustBe "Guarantor VAT registration number"
        doc.select(Selectors.summaryListRowValue(3)).text() mustBe "GB123456789"
        doc.select(Selectors.summaryListRowKey(4)).text() mustBe "Guarantor address"
        doc.select(Selectors.summaryListRowValue(4)).text() mustBe "Main101 Zeebrugge ZZ78"
      }

      "the guarantor type is a Consignor" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(
          movementGuarantee = getMovementResponseModel.movementGuarantee.copy(
            guarantorTypeCode = Consignor
          )
        )

        val result = helper.guarantorInformationCard()
        val doc = Jsoup.parse(result.toString())

        doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Guarantor arranger"
        doc.select(Selectors.summaryListRowValue(1)).text() mustBe "Consignor"
        doc.select(Selectors.summaryListRowKey(2)).text() mustBe "Excise Registration Number (ERN)"
        doc.select(Selectors.summaryListRowValue(2)).text() mustBe "GBRC345GTR145"
      }

      "the guarantor type is a Consignee" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(
          movementGuarantee = getMovementResponseModel.movementGuarantee.copy(
            guarantorTypeCode = Consignee
          )
        )

        val result = helper.guarantorInformationCard()
        val doc = Jsoup.parse(result.toString())

        doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Guarantor arranger"
        doc.select(Selectors.summaryListRowValue(1)).text() mustBe "Consignee"
        doc.select(Selectors.summaryListRowKey(2)).text() mustBe "Excise Registration Number (ERN)"
        doc.select(Selectors.summaryListRowValue(2)).text() mustBe "GB12345GTR144"
      }

      "the guarantor type is a Consignee with a destination of Export" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(
          movementGuarantee = getMovementResponseModel.movementGuarantee.copy(
            guarantorTypeCode = Consignee
          ),
          destinationType = DestinationType.Export
        )

        val result = helper.guarantorInformationCard()
        val doc = Jsoup.parse(result.toString())

        doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Guarantor arranger"
        doc.select(Selectors.summaryListRowValue(1)).text() mustBe "Consignee"
        doc.select(Selectors.summaryListRowKey(2)).text() mustBe "Identification number"
        doc.select(Selectors.summaryListRowValue(2)).text() mustBe "GB12345GTR144"
      }

      "the guarantor type is a Consignee with a destination of TemporaryRegisteredConsignee" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(
          movementGuarantee = getMovementResponseModel.movementGuarantee.copy(
            guarantorTypeCode = Consignee
          ),
          destinationType = DestinationType.TemporaryRegisteredConsignee
        )

        val result = helper.guarantorInformationCard()
        val doc = Jsoup.parse(result.toString())

        doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Guarantor arranger"
        doc.select(Selectors.summaryListRowValue(1)).text() mustBe "Consignee"
        doc.select(Selectors.summaryListRowKey(2)).text() mustBe "Identification number for temporary registered consignee"
        doc.select(Selectors.summaryListRowValue(2)).text() mustBe "GB12345GTR144"
      }

      "the guarantor type is a GuarantorNotRequired" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(
          movementGuarantee = getMovementResponseModel.movementGuarantee.copy(
            guarantorTypeCode = GuarantorNotRequired
          )
        )

        val result = helper.guarantorInformationCard()
        val doc = Jsoup.parse(result.toString())

        doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Guarantor arranger"
        doc.select(Selectors.summaryListRowValue(1)).text() mustBe "No guarantor required"
      }
    }

    "the guarantor type is a NoGuarantor" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(
        movementGuarantee = getMovementResponseModel.movementGuarantee.copy(
          guarantorTypeCode = NoGuarantor
        )
      )

      val result = helper.guarantorInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Guarantor arranger"
      doc.select(Selectors.summaryListRowValue(1)).text() mustBe "No guarantor required"
    }

  }

  "journeyInformationCard" must {
    "output the correct rows when all data is present" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(
        transportMode = TransportModeModel(
          transportModeCode = TransportMode.AirTransport,
          complementaryInformation = Some("transport complementary information"),
        )
      )

      val result = helper.journeyInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.select(Selectors.h2(1)).text() mustBe "Journey type"

      doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Journey time"
      doc.select(Selectors.summaryListRowValue(1)).text() mustBe "20 days"
      doc.select(Selectors.summaryListRowKey(2)).text() mustBe "Mode of transport"
      doc.select(Selectors.summaryListRowValue(2)).text() mustBe "Air transport"
      doc.select(Selectors.summaryListRowKey(3)).text() mustBe "Mode of transport information"
      doc.select(Selectors.summaryListRowValue(3)).text() mustBe "transport complementary information"
    }

  }

  "transportArrangerInformationCard" must {
    "output the correct rows when all data is present" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(
        headerEadEsad = HeaderEadEsadModel(
          sequenceNumber = 1,
          dateAndTimeOfUpdateValidation = "2023-12-01T12:00:00Z",
          destinationType = DestinationType.TemporaryRegisteredConsignee,
          journeyTime = "20 days",
          transportArrangement = OwnerOfGoods
        ),
        transportArrangerTrader = Some(TraderModel(
          traderExciseNumber = None,
          traderName = Some("Mr transport arranger"),
          address = None,
          vatNumber = Some("VATarranger"),
          eoriNumber = None
        ))
      )

      val result = helper.transportArrangerInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.select(Selectors.h2(1)).text() mustBe "Transport arranger"

      doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Transport arranger"
      doc.select(Selectors.summaryListRowValue(1)).text() mustBe "Owner of goods"
      doc.select(Selectors.summaryListRowKey(2)).text() mustBe "Transport arranger name"
      doc.select(Selectors.summaryListRowValue(2)).text() mustBe "Mr transport arranger"
      doc.select(Selectors.summaryListRowKey(3)).text() mustBe "VAT registration number"
      doc.select(Selectors.summaryListRowValue(3)).text() mustBe "VATarranger"
    }

    "output nothing when there is no transport arranger" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(transportArrangerTrader = None)

      val result = helper.transportArrangerInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.body().text() mustBe ""
    }
  }

  "firstTransporterInformationCard" must {
    "output the correct rows when all data is present" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel

      val result = helper.firstTransporterInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.select(Selectors.h2(1)).text() mustBe "First transporter"

      doc.select(Selectors.summaryListRowKey(1)).text() mustBe "Transporter name"
      doc.select(Selectors.summaryListRowValue(1)).text() mustBe "testFirstTransporterTraderName"
      doc.select(Selectors.summaryListRowKey(2)).text() mustBe "Transport arranger VAT registration number"
      doc.select(Selectors.summaryListRowValue(2)).text() mustBe "testVatNumber"
      doc.select(Selectors.summaryListRowKey(3)).text() mustBe "Transporter address"
      doc.select(Selectors.summaryListRowValue(3)).text() mustBe "Main101 Zeebrugge ZZ78"
    }

    "output nothing when there are is no first transporter" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(firstTransporterTrader = None)

      val result = helper.firstTransporterInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.body().text() mustBe ""
    }
  }

  "sadInformationCard" must {
    "output the correct rows when all data is present" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(
        eadEsad = eadEsadModel.copy(
          importSadNumber = Some(Seq("123", "456"))
        ),
      )

      val result = helper.sadInformationCard()
      val doc = Jsoup.parse(result.toString())

      val summaryCards = doc.getElementsByClass("govuk-summary-card")

      val firstSadDoc = summaryCards.get(0)
      val firstSadDocSummaryListRows = firstSadDoc.getElementsByClass("govuk-summary-list__row")
      firstSadDocSummaryListRows.get(0).getElementsByTag("dt").text() mustBe "SAD number"
      firstSadDocSummaryListRows.get(0).getElementsByTag("dd").text() mustBe "123"

      val lastSadDoc = summaryCards.get(1)
      val lastSadDocSummaryListRows = lastSadDoc.getElementsByClass("govuk-summary-list__row")
      lastSadDocSummaryListRows.get(0).getElementsByTag("dt").text() mustBe "SAD number"
      lastSadDocSummaryListRows.get(0).getElementsByTag("dd").text() mustBe "456"
    }

    "output nothing when there are no single administrative documents" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(eadEsad = eadEsadModel.copy(importSadNumber = None))

      val result = helper.sadInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.body().text() mustBe ""
    }
  }

  "documentsInformationCard" must {
    "output the correct rows when all data is present" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel

      val result = helper.documentsInformationCard()
      val doc = Jsoup.parse(result.toString())

      val summaryCards = doc.getElementsByClass("govuk-summary-card")

      val firstCert = summaryCards.get(0)
      val firstCertDocSummaryListRows = firstCert.getElementsByClass("govuk-summary-list__row")
      firstCertDocSummaryListRows.get(0).getElementsByTag("dt").text() mustBe "Document type"
      firstCertDocSummaryListRows.get(0).getElementsByTag("dd").text() mustBe "1"
      firstCertDocSummaryListRows.get(1).getElementsByTag("dt").text() mustBe "Document reference"
      firstCertDocSummaryListRows.get(1).getElementsByTag("dd").text() mustBe "Document reference"

      val lastCert = summaryCards.get(1)
      val lastCertDocSummaryListRows = lastCert.getElementsByClass("govuk-summary-list__row")
      lastCertDocSummaryListRows.get(0).getElementsByTag("dt").text() mustBe "Document type"
      lastCertDocSummaryListRows.get(0).getElementsByTag("dd").text() mustBe "2"
      lastCertDocSummaryListRows.get(1).getElementsByTag("dt").text() mustBe "Document reference"
      lastCertDocSummaryListRows.get(1).getElementsByTag("dd").text() mustBe "Document reference 2"
    }

    "output nothing when there are no document certificate" in {
      implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(documentCertificate = None)

      val result = helper.documentsInformationCard()
      val doc = Jsoup.parse(result.toString())

      doc.body().text() mustBe ""
    }
  }

  "rorDetailsCard" when {
    "export" must {
      "output the correct rows when all data is present" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(destinationType = DestinationType.Export)

        val result = helper.rorDetailsCard(ie818Event)
        val doc = Jsoup.parse(result.toString())

        doc.select(Selectors.h2(1)).text() mustBe "Report of export details"

        val summaryList = doc.getElementsByClass("govuk-summary-list").get(0)

        val summaryListRows = summaryList.getElementsByClass("govuk-summary-list__row")
        summaryListRows.get(0).getElementsByTag("dt").text() mustBe "Date of arrival"
        summaryListRows.get(0).getElementsByTag("dd").text() mustBe reportOfReceiptResponse.dateOfArrival.formatDateForUIOutput()
        summaryListRows.get(1).getElementsByTag("dt").text() mustBe "Export status"
        summaryListRows.get(1).getElementsByTag("dd").text() mustBe "Accepted although unsatisfactory"
        summaryListRows.get(2).getElementsByTag("dt").text() mustBe "More information"
        summaryListRows.get(2).getElementsByTag("dd").text() mustBe reportOfReceiptResponse.otherInformation.get
      }
    }
    "not export" must {
      "output the correct rows when all data is present" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel

        val result = helper.rorDetailsCard(ie818Event)
        val doc = Jsoup.parse(result.toString())

        doc.select(Selectors.h2(1)).text() mustBe "Report of receipt details"

        val summaryList = doc.getElementsByClass("govuk-summary-list").get(0)

        val summaryListRows = summaryList.getElementsByClass("govuk-summary-list__row")
        summaryListRows.get(0).getElementsByTag("dt").text() mustBe "Date of arrival"
        summaryListRows.get(0).getElementsByTag("dd").text() mustBe reportOfReceiptResponse.dateOfArrival.formatDateForUIOutput()
        summaryListRows.get(1).getElementsByTag("dt").text() mustBe "Receipt status"
        summaryListRows.get(1).getElementsByTag("dd").text() mustBe "Accepted although unsatisfactory"
        summaryListRows.get(2).getElementsByTag("dt").text() mustBe "More information"
        summaryListRows.get(2).getElementsByTag("dd").text() mustBe reportOfReceiptResponse.otherInformation.get
      }
    }
    "no ror in movement history" must {
      "return empty HTML" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(reportOfReceipt = None)

        val result = helper.rorDetailsCard(ie818Event)
        result mustBe Html("")
      }
    }
  }

  "rorConsigneeCard" when {
    "consignee is present" must {
      "render the consignee section header" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel

        val result = helper.rorConsigneeCard(ie818Event)
        val doc = Jsoup.parse(result.toString())

        doc.select(Selectors.h2(1)).text() mustBe "Consignee"
      }
      "render the Name row" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel

        val result = helper.rorConsigneeCard(ie818Event)
        val doc = Jsoup.parse(result.toString())

        val summaryList = doc.getElementsByClass("govuk-summary-list").get(0)

        val summaryListRows = summaryList.getElementsByClass("govuk-summary-list__row")
        summaryListRows.get(0).getElementsByTag("dt").text() mustBe "Name"
        summaryListRows.get(0).getElementsByTag("dd").text() mustBe reportOfReceiptResponse.consigneeTrader.get.traderName.get
      }
      "render the Excise Registration Number (ERN) row when destination type = tax warehouse, direct delivery or registered consignee" in {
        Seq(DestinationType.TaxWarehouse, DestinationType.DirectDelivery, DestinationType.RegisteredConsignee).foreach {
          destinationType =>
            implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(destinationType = destinationType)

            val result = helper.rorConsigneeCard(ie818Event)
            val doc = Jsoup.parse(result.toString())

            val summaryList = doc.getElementsByClass("govuk-summary-list").get(0)

            val summaryListRows = summaryList.getElementsByClass("govuk-summary-list__row")
            summaryListRows.get(1).getElementsByTag("dt").text() mustBe "Excise Registration Number (ERN)"
            summaryListRows.get(1).getElementsByTag("dd").text() mustBe reportOfReceiptResponse.consigneeTrader.get.traderExciseNumber.get
        }
      }
      "not render the Excise Registration Number (ERN) row when destination type != tax warehouse, direct delivery or registered consignee" in {
        DestinationType.values
          .filterNot(Seq(DestinationType.TaxWarehouse, DestinationType.DirectDelivery, DestinationType.RegisteredConsignee).contains)
          .foreach {
            destinationType =>
              implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(destinationType = destinationType)

              val result = helper.rorConsigneeCard(ie818Event)
              val doc = Jsoup.parse(result.toString())

              val summaryList = doc.getElementsByClass("govuk-summary-list").get(0)

              val summaryListRows = summaryList.getElementsByClass("govuk-summary-list__row")
              summaryListRows.get(1).getElementsByTag("dt").text() must not be "Excise Registration Number (ERN)"
          }
      }
      "render the Identification number for temporary registered consignee row when destination type = temporary registered consignee" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(destinationType = DestinationType.TemporaryRegisteredConsignee)

        val result = helper.rorConsigneeCard(ie818Event)
        val doc = Jsoup.parse(result.toString())

        val summaryList = doc.getElementsByClass("govuk-summary-list").get(0)

        val summaryListRows = summaryList.getElementsByClass("govuk-summary-list__row")
        summaryListRows.get(1).getElementsByTag("dt").text() mustBe "Identification number for temporary registered consignee"
        summaryListRows.get(1).getElementsByTag("dd").text() mustBe reportOfReceiptResponse.consigneeTrader.get.traderExciseNumber.get
      }
      "not render the Identification number for temporary registered consignee row when destination type != temporary registered consignee" in {
        DestinationType.values
          .filterNot(_ == DestinationType.TemporaryRegisteredConsignee)
          .foreach {
            destinationType =>
              implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(destinationType = destinationType)

              val result = helper.rorConsigneeCard(ie818Event)
              val doc = Jsoup.parse(result.toString())

              val summaryList = doc.getElementsByClass("govuk-summary-list").get(0)

              val summaryListRows = summaryList.getElementsByClass("govuk-summary-list__row")
              summaryListRows.get(1).getElementsByTag("dt").text() must not be "Identification number for temporary registered consignee"
          }
      }
      "render the Identification number row if consignee vatNumber is present" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel

        val result = helper.rorConsigneeCard(ie818Event)
        val doc = Jsoup.parse(result.toString())

        val summaryList = doc.getElementsByClass("govuk-summary-list").get(0)

        val summaryListRows = summaryList.getElementsByClass("govuk-summary-list__row")
        summaryListRows.get(2).getElementsByTag("dt").text() mustBe "Identification number"
        summaryListRows.get(2).getElementsByTag("dd").text() mustBe "GB123456789"

      }
      "render the Address row" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel

        val result = helper.rorConsigneeCard(ie818Event)
        val doc = Jsoup.parse(result.toString())

        val summaryList = doc.getElementsByClass("govuk-summary-list").get(0)

        val summaryListRows = summaryList.getElementsByClass("govuk-summary-list__row")
        summaryListRows.get(3).getElementsByTag("dt").text() mustBe "Address"
        summaryListRows.get(3).getElementsByTag("dd").text() mustBe "Main101 Zeebrugge ZZ78"
      }
      "render the EORI number row if consignee eoriNumber is present" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(reportOfReceipt = Some(reportOfReceiptResponse.copy(
          consigneeTrader = Some(TraderModel(
            traderExciseNumber = Some("GBRC345GTR145"),
            traderName = Some("Current 801 Consignee"),
            address = Some(AddressModel(
              streetNumber = None,
              street = Some("Main101"),
              postcode = Some("ZZ78"),
              city = Some("Zeebrugge")
            )),
            vatNumber = Some("GB123456789"),
            eoriNumber = Some("GB123456789")
          ))
        )))

        val result = helper.rorConsigneeCard(ie818Event)
        val doc = Jsoup.parse(result.toString())

        val summaryList = doc.getElementsByClass("govuk-summary-list").get(0)

        val summaryListRows = summaryList.getElementsByClass("govuk-summary-list__row")
        summaryListRows.get(4).getElementsByTag("dt").text() mustBe "EORI number"
        summaryListRows.get(4).getElementsByTag("dd").text() mustBe "GB123456789"
      }
    }
    "no consignee in movement history" must {
      "return empty HTML" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(reportOfReceipt = Some(reportOfReceiptResponse.copy(consigneeTrader = None)))

        val result = helper.rorConsigneeCard(ie818Event)
        result mustBe Html("")
      }
    }
    "no ror in movement history" must {
      "return empty HTML" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(reportOfReceipt = None)

        val result = helper.rorConsigneeCard(ie818Event)
        result mustBe Html("")
      }
    }
  }

  "rorDestinationCard" when {
    "deliveryPlaceTrader is present" must {
      "render the place of destination section header" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel

        val result = helper.rorDestinationCard(ie818Event)
        val doc = Jsoup.parse(result.toString())

        doc.select(Selectors.h2(1)).text() mustBe "Place of destination"
      }
      "render the Name row" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel

        val result = helper.rorDestinationCard(ie818Event)
        val doc = Jsoup.parse(result.toString())

        val summaryList = doc.getElementsByClass("govuk-summary-list").get(0)

        val summaryListRows = summaryList.getElementsByClass("govuk-summary-list__row")
        summaryListRows.get(0).getElementsByTag("dt").text() mustBe "Name"
        summaryListRows.get(0).getElementsByTag("dd").text() mustBe reportOfReceiptResponse.deliveryPlaceTrader.get.traderName.get
      }
      "render the Excise Registration Number (ERN) row when destination type = tax warehouse" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(destinationType = DestinationType.TaxWarehouse)

        val result = helper.rorDestinationCard(ie818Event)
        val doc = Jsoup.parse(result.toString())

        val summaryList = doc.getElementsByClass("govuk-summary-list").get(0)

        val summaryListRows = summaryList.getElementsByClass("govuk-summary-list__row")
        summaryListRows.get(1).getElementsByTag("dt").text() mustBe "Warehouse excise ID"
        summaryListRows.get(1).getElementsByTag("dd").text() mustBe reportOfReceiptResponse.deliveryPlaceTrader.get.traderExciseNumber.get
      }
      "not render the Excise Registration Number (ERN) row when destination type != tax warehouse" in {
        DestinationType.values
          .filterNot(_ == DestinationType.TaxWarehouse)
          .filterNot(_ == DestinationType.Export)
          .foreach {
            destinationType =>
              implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(destinationType = destinationType)

              val result = helper.rorDestinationCard(ie818Event)
              val doc = Jsoup.parse(result.toString())

              val summaryList = doc.getElementsByClass("govuk-summary-list").get(0)

              val summaryListRows = summaryList.getElementsByClass("govuk-summary-list__row")
              summaryListRows.get(1).getElementsByTag("dt").text() must not be "Warehouse excise ID"
          }
      }
      "render the Identification number row if deliveryPlace vatNumber is present" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel

        val result = helper.rorDestinationCard(ie818Event)
        val doc = Jsoup.parse(result.toString())

        val summaryList = doc.getElementsByClass("govuk-summary-list").get(0)

        val summaryListRows = summaryList.getElementsByClass("govuk-summary-list__row")
        summaryListRows.get(2).getElementsByTag("dt").text() mustBe "VAT registration number"
        summaryListRows.get(2).getElementsByTag("dd").text() mustBe "GB123456789"

      }
      "render the Address row" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel

        val result = helper.rorDestinationCard(ie818Event)
        val doc = Jsoup.parse(result.toString())

        val summaryList = doc.getElementsByClass("govuk-summary-list").get(0)

        val summaryListRows = summaryList.getElementsByClass("govuk-summary-list__row")
        summaryListRows.get(3).getElementsByTag("dt").text() mustBe "Address"
        summaryListRows.get(3).getElementsByTag("dd").text() mustBe "Main101 Zeebrugge ZZ78"
      }
    }
    "destinationType == Export" must {
      "return empty HTML" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(destinationType = DestinationType.Export)

        val result = helper.rorDestinationCard(ie818Event)
        result mustBe Html("")
      }
    }
    "no deliveryPlaceTrader in movement history" must {
      "return empty HTML" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(reportOfReceipt = Some(reportOfReceiptResponse.copy(deliveryPlaceTrader = None)))

        val result = helper.rorDestinationCard(ie818Event)
        result mustBe Html("")
      }
    }
    "no ror in movement history" must {
      "return empty HTML" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(reportOfReceipt = None)

        val result = helper.rorDestinationCard(ie818Event)
        result mustBe Html("")
      }
    }
  }

  "rorExportCard" when {
    "destinationType == Export" must {
      "render the export section header" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(destinationType = DestinationType.Export)

        val result = helper.rorExportCard(ie818Event)
        val doc = Jsoup.parse(result.toString())

        doc.select(Selectors.h2(1)).text() mustBe "Export"
      }
      "render the Name row" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(destinationType = DestinationType.Export)

        val result = helper.rorExportCard(ie818Event)
        val doc = Jsoup.parse(result.toString())

        val summaryList = doc.getElementsByClass("govuk-summary-list").get(0)

        val summaryListRows = summaryList.getElementsByClass("govuk-summary-list__row")
        summaryListRows.get(0).getElementsByTag("dt").text() mustBe "Customs office code"
        summaryListRows.get(0).getElementsByTag("dd").text() mustBe reportOfReceiptResponse.destinationOffice
      }
    }
    "destinationType != Export" must {
      "return empty HTML" in {
        DestinationType.values.filterNot(_ == DestinationType.Export).foreach {
          destinationType =>
            implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(destinationType = destinationType)

            val result = helper.rorExportCard(ie818Event)
            result mustBe Html("")
        }
      }
    }
    "no ror in movement history" must {
      "return empty HTML" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(reportOfReceipt = None)

        val result = helper.rorExportCard(ie818Event)
        result mustBe Html("")
      }
    }
  }

  "rorItemsCard" when {
    AcceptMovement.values.filterNot(_ == AcceptMovement.Satisfactory).foreach {
      acceptMovement =>
        s"acceptMovement == $acceptMovement" must {
          "render a card with the correct header and the correct whatWasWrongRow" in {
            implicit val _movement: GetMovementResponse =
              getMovementResponseModel.copy(reportOfReceipt = Some(reportOfReceiptResponse.copy(acceptMovement = acceptMovement)))

            val items: Seq[IE818ItemModelWithCnCodeInformation] =
              reportOfReceiptResponse.individualItems.map(IE818ItemModelWithCnCodeInformation(_, CnCodeInformation(
                cnCode = "",
                cnCodeDescription = "test cn code description",
                exciseProductCode = "",
                exciseProductCodeDescription = "",
                unitOfMeasure = Kilograms
              )))
            val result = helper.rorItemsCard(ie818Event, items)
            val doc = Jsoup.parse(result.toString())

            doc.select(Selectors.h2(1)).text() mustBe "Items"
            items.zipWithIndex.foreach {
              case (item, index) =>

                // test card title
                doc.select(Selectors.h3(index + 1)).text() mustBe s"Item ${index + 1}"

                // test action link
                val actionLink = doc.select(Selectors.link(index + 1))
                actionLink.text() mustBe s"Item details for item ${index + 1} (${item.information.cnCodeDescription})"
                actionLink.attr("href") mustBe controllers.routes.ItemDetailsController.onPageLoad(testErn, testArc, item.rorItem.eadBodyUniqueReference).url

                // test what was wrong row
                val summaryList = doc.getElementsByClass("govuk-summary-list").get(index)
                val summaryListRows = summaryList.getElementsByClass("govuk-summary-list__row")
                summaryListRows.get(0).getElementsByTag("dt").text() mustBe "What was wrong"
                summaryListRows.get(0).getElementsByTag("dd").text() mustBe
                  item.rorItem.unsatisfactoryReasons
                    .map(reason => messages.messages(s"movementHistoryEvent.IE818.rorItems.whatWasWrong.${reason.reason}"))
                    .mkString(" ")
            }
          }
        }
    }
    "acceptMovement == satisfactory" must {
      "return empty HTML" in {
        implicit val _movement: GetMovementResponse =
          getMovementResponseModel.copy(reportOfReceipt = Some(reportOfReceiptResponse.copy(acceptMovement = AcceptMovement.Satisfactory)))

        val result = helper.rorItemsCard(ie818Event, Seq())
        result mustBe Html("")
      }
    }
    "no ror in movement history" must {
      "return empty HTML" in {
        implicit val _movement: GetMovementResponse = getMovementResponseModel.copy(reportOfReceipt = None)

        val result = helper.rorItemsCard(ie818Event, Seq())
        result mustBe Html("")
      }
    }
  }

  "generateShortageOrExcessRows" must {
    "return Seq with both rows" when {
      "both quantity and additional info are defined" in {
        val quantity = 10
        val additionalInfo = "additional info"

        val item = reportOfReceiptResponse.individualItems.map(IE818ItemModelWithCnCodeInformation(_, CnCodeInformation(
          cnCode = "",
          cnCodeDescription = "test cn code description",
          exciseProductCode = "",
          exciseProductCodeDescription = "",
          unitOfMeasure = Kilograms
        ))).head

        val result = helper.generateShortageOrExcessRows(true, Some(quantity), Some(additionalInfo))(ie818Event, item, messages)

        result mustBe Seq(
          Some(summaryListRowBuilder("Amount of shortage", "10 kg")),
          Some(summaryListRowBuilder("Information about shortage", "additional info"))
        )
      }
    }
    "return Seq with only 1 row" when {
      "only quantity is defined" in {
        val quantity = 10

        val item = reportOfReceiptResponse.individualItems.map(IE818ItemModelWithCnCodeInformation(_, CnCodeInformation(
          cnCode = "",
          cnCodeDescription = "test cn code description",
          exciseProductCode = "",
          exciseProductCodeDescription = "",
          unitOfMeasure = Kilograms
        ))).head

        val result = helper.generateShortageOrExcessRows(true, Some(quantity), None)(ie818Event, item, messages)

        result mustBe Seq(
          Some(summaryListRowBuilder("Amount of shortage", "10 kg")),
          None
        )
      }
      "only additional info is defined" in {
        val additionalInfo = "additional info"

        val item = reportOfReceiptResponse.individualItems.map(IE818ItemModelWithCnCodeInformation(_, CnCodeInformation(
          cnCode = "",
          cnCodeDescription = "test cn code description",
          exciseProductCode = "",
          exciseProductCodeDescription = "",
          unitOfMeasure = Kilograms
        ))).head

        val result = helper.generateShortageOrExcessRows(true, None, Some(additionalInfo))(ie818Event, item, messages)

        result mustBe Seq(
          None,
          Some(summaryListRowBuilder("Information about shortage", "additional info"))
        )
      }
    }
    "return an empty Seq" when {
      "both quantity and additionalInformation are None" in {
        val item = reportOfReceiptResponse.individualItems.map(IE818ItemModelWithCnCodeInformation(_, CnCodeInformation(
          cnCode = "",
          cnCodeDescription = "test cn code description",
          exciseProductCode = "",
          exciseProductCodeDescription = "",
          unitOfMeasure = Kilograms
        ))).head

        val result = helper.generateShortageOrExcessRows(true, None, None)(ie818Event, item, messages)

        result mustBe Seq(None, None)
      }
    }
    "handle both shortage and excess" in {
      Seq(
        (true, "shortage"),
        (false, "excess")
      ).foreach {
        case (isShortage, shortageOrExcess) =>
          val quantity = 10
          val additionalInfo = "additional info"

          val item = reportOfReceiptResponse.individualItems.map(IE818ItemModelWithCnCodeInformation(_, CnCodeInformation(
            cnCode = "",
            cnCodeDescription = "test cn code description",
            exciseProductCode = "",
            exciseProductCodeDescription = "",
            unitOfMeasure = Kilograms
          ))).head

          val result = helper.generateShortageOrExcessRows(isShortage, Some(quantity), Some(additionalInfo))(ie818Event, item, messages)

          result mustBe Seq(
            Some(summaryListRowBuilder(s"Amount of $shortageOrExcess", "10 kg")),
            Some(summaryListRowBuilder(s"Information about $shortageOrExcess", "additional info"))
          )
      }
    }
  }

  "rorItemRows" must {
    "render the correct row" when {
      "Shortage" in {
        val item = IE818ItemModelWithCnCodeInformation(
          ReceiptedItemsModel(
            eadBodyUniqueReference = 1,
            excessAmount = None,
            shortageAmount = Some(21),
            productCode = testEpcWine,
            refusedAmount = Some(1),
            unsatisfactoryReasons = Seq(
              UnsatisfactoryModel(Shortage, Some("some info")),
            )
          ), CnCodeInformation(
            cnCode = "",
            cnCodeDescription = "test cn code description",
            exciseProductCode = "",
            exciseProductCodeDescription = "",
            unitOfMeasure = Kilograms
          )
        )

        val result = helper.rorItemRows(ie818Event, item)(messages)

        result mustBe Seq(
          Some(summaryListRowBuilder("Amount of shortage", "21 kg")),
          Some(summaryListRowBuilder("Information about shortage", "some info"))
        )
      }
      "Excess" in {
        val item = IE818ItemModelWithCnCodeInformation(
          ReceiptedItemsModel(
            eadBodyUniqueReference = 1,
            excessAmount = Some(21),
            shortageAmount = None,
            productCode = testEpcWine,
            refusedAmount = Some(1),
            unsatisfactoryReasons = Seq(
              UnsatisfactoryModel(Excess, Some("some info")),
            )
          ), CnCodeInformation(
            cnCode = "",
            cnCodeDescription = "test cn code description",
            exciseProductCode = "",
            exciseProductCodeDescription = "",
            unitOfMeasure = Kilograms
          )
        )

        val result = helper.rorItemRows(ie818Event, item)(messages)

        result mustBe Seq(
          Some(summaryListRowBuilder("Amount of excess", "21 kg")),
          Some(summaryListRowBuilder("Information about excess", "some info"))
        )
      }
      "Damaged" in {
        val item = IE818ItemModelWithCnCodeInformation(
          ReceiptedItemsModel(
            eadBodyUniqueReference = 1,
            excessAmount = None,
            shortageAmount = None,
            productCode = testEpcWine,
            refusedAmount = Some(1),
            unsatisfactoryReasons = Seq(
              UnsatisfactoryModel(Damaged, Some("some info")),
            )
          ), CnCodeInformation(
            cnCode = "",
            cnCodeDescription = "test cn code description",
            exciseProductCode = "",
            exciseProductCodeDescription = "",
            unitOfMeasure = Kilograms
          )
        )

        val result = helper.rorItemRows(ie818Event, item)(messages)

        result mustBe Seq(
          Some(summaryListRowBuilder("Information about damaged item", "some info"))
        )
      }
      "BrokenSeals" in {
        val item = IE818ItemModelWithCnCodeInformation(
          ReceiptedItemsModel(
            eadBodyUniqueReference = 1,
            excessAmount = None,
            shortageAmount = None,
            productCode = testEpcWine,
            refusedAmount = Some(1),
            unsatisfactoryReasons = Seq(
              UnsatisfactoryModel(BrokenSeals, Some("some info")),
            )
          ), CnCodeInformation(
            cnCode = "",
            cnCodeDescription = "test cn code description",
            exciseProductCode = "",
            exciseProductCodeDescription = "",
            unitOfMeasure = Kilograms
          )
        )

        val result = helper.rorItemRows(ie818Event, item)(messages)

        result mustBe Seq(
          Some(summaryListRowBuilder("Information about broken seal(s)", "some info"))
        )
      }
      "Other" in {
        val item = IE818ItemModelWithCnCodeInformation(
          ReceiptedItemsModel(
            eadBodyUniqueReference = 1,
            excessAmount = None,
            shortageAmount = None,
            productCode = testEpcWine,
            refusedAmount = Some(1),
            unsatisfactoryReasons = Seq(
              UnsatisfactoryModel(Other, Some("some info")),
            )
          ), CnCodeInformation(
            cnCode = "",
            cnCodeDescription = "test cn code description",
            exciseProductCode = "",
            exciseProductCodeDescription = "",
            unitOfMeasure = Kilograms
          )
        )

        val result = helper.rorItemRows(ie818Event, item)(messages)

        result mustBe Seq(
          Some(summaryListRowBuilder("Other information", "some info"))
        )
      }
    }
    "order the rows correctly" in {
      val item = IE818ItemModelWithCnCodeInformation(
        ReceiptedItemsModel(
          eadBodyUniqueReference = 1,
          excessAmount = Some(22),
          shortageAmount = Some(21),
          productCode = testEpcWine,
          refusedAmount = Some(1),
          unsatisfactoryReasons = Seq(
            UnsatisfactoryModel(Excess, Some("some info excess")),
            UnsatisfactoryModel(Other, Some("some info other")),
            UnsatisfactoryModel(Shortage, Some("some info shortage")),
            UnsatisfactoryModel(BrokenSeals, Some("some info broken seals")),
            UnsatisfactoryModel(Damaged, Some("some info damaged item")),
          )
        ), CnCodeInformation(
          cnCode = "",
          cnCodeDescription = "test cn code description",
          exciseProductCode = "",
          exciseProductCodeDescription = "",
          unitOfMeasure = Kilograms
        )
      )

      val result = helper.rorItemRows(ie818Event, item)(messages)

      result mustBe Seq(
        Some(summaryListRowBuilder("Amount of shortage", "21 kg")),
        Some(summaryListRowBuilder("Information about shortage", "some info shortage")),
        Some(summaryListRowBuilder("Amount of excess", "22 kg")),
        Some(summaryListRowBuilder("Information about excess", "some info excess")),
        Some(summaryListRowBuilder("Information about damaged item", "some info damaged item")),
        Some(summaryListRowBuilder("Information about broken seal(s)", "some info broken seals")),
        Some(summaryListRowBuilder("Other information", "some info other"))
      )
    }
  }
}