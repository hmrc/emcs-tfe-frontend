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

package fixtures

import models.common.AcceptMovement.Unsatisfactory
import models.common.GuarantorType.Consignee
import models.common.OriginType.TaxWarehouse
import models.common.WrongWithMovement.{BrokenSeals, Damaged, Excess, Other, Shortage}
import models.common._
import models.response.emcsTfe.reportOfReceipt.{ReceiptedItemsModel, ReportOfReceiptModel, UnsatisfactoryModel}
import models.response.emcsTfe.{EadEsadModel, GetMovementResponse, HeaderEadEsadModel, TransportModeModel}
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

trait GetMovementResponseFixtures extends ItemFixtures with GetMovementHistoryEventsResponseFixtures with ExciseProductCodeFixtures {
  _: BaseFixtures =>

  val eadEsadModel: EadEsadModel = EadEsadModel(
    localReferenceNumber = testLrn,
    invoiceNumber = "INV123",
    invoiceDate = Some("2023-12-01"),
    originTypeCode = TaxWarehouse,
    dateOfDispatch = "",
    timeOfDispatch = Some("00:00:00"),
    upstreamArc = None,
    importSadNumber = None
  )

  val reportOfReceiptResponse = ReportOfReceiptModel(
    arc = testArc,
    sequenceNumber = 2,
    dateAndTimeOfValidationOfReportOfReceiptExport = Some("2021-09-10T11:11:12"),
    consigneeTrader = Some(TraderModel(
      traderExciseNumber = "GBRC345GTR145",
      traderName = "Current 801 Consignee",
      address = AddressModel(
        streetNumber = None,
        street = Some("Main101"),
        postcode = Some("ZZ78"),
        city = Some("Zeebrugge")
      ),
      vatNumber = Some("GB123456789")
    )),
    deliveryPlaceTrader = Some(TraderModel(
      traderExciseNumber = "GBRC345GTR145",
      traderName = "Current 801 Consignee",
      address = AddressModel(
        streetNumber = None,
        street = Some("Main101"),
        postcode = Some("ZZ78"),
        city = Some("Zeebrugge")
      ),
      vatNumber = Some("GB123456789")
    )),
    destinationOffice = "XI004098",
    dateOfArrival = LocalDate.parse("2008-12-08"),
    acceptMovement = Unsatisfactory,
    otherInformation = Some("some great reason"),
    individualItems = Seq(ReceiptedItemsModel(
      eadBodyUniqueReference = 1,
      excessAmount = Some(21),
      shortageAmount = None,
      productCode = testEpcWine,
      refusedAmount = Some(1),
      unsatisfactoryReasons = Seq(
        UnsatisfactoryModel(Excess, Some("some info")),
        UnsatisfactoryModel(Shortage, None),
        UnsatisfactoryModel(Damaged, None),
        UnsatisfactoryModel(BrokenSeals, None),
        UnsatisfactoryModel(Other, None)
      )
    ))
  )

  val getMovementResponseModel: GetMovementResponse = GetMovementResponse(
    arc = testArc,
    sequenceNumber = testSequenceNumber,
    destinationType = DestinationType.TaxWarehouse,
    localReferenceNumber = testLrn,
    eadEsad = eadEsadModel,
    eadStatus = "Accepted",
    consignorTrader = TraderModel(
      traderExciseNumber = "GBRC345GTR145",
      traderName = "Current 801 Consignor",
      address = AddressModel(
        streetNumber = None,
        street = Some("Main101"),
        postcode = Some("ZZ78"),
        city = Some("Zeebrugge")
      ),
      vatNumber = Some("GB123456789")
    ),
    deliveryPlaceTrader = Some(TraderModel(
      traderExciseNumber = "GBRC345GTR145",
      traderName = "Current 801 Consignee",
      address = AddressModel(
        streetNumber = None,
        street = Some("Main101"),
        postcode = Some("ZZ78"),
        city = Some("Zeebrugge")
      ),
      vatNumber = Some("GB123456789")
    )),
    placeOfDispatchTrader = Some(TraderModel(
      traderExciseNumber = "GBRC345GTR145",
      traderName = "Current 801 Consignor",
      address = AddressModel(
        streetNumber = None,
        street = Some("Main101"),
        postcode = Some("ZZ78"),
        city = Some("Zeebrugge")
      ),
      vatNumber = Some("GB123456789")
    )),
    consigneeTrader = Some(TraderModel(
      traderExciseNumber = "GB12345GTR144",
      traderName = "Current 801 Consignee",
      address = AddressModel(
        streetNumber = None,
        street = Some("Main101"),
        postcode = Some("ZZ78"),
        city = Some("Zeebrugge")
      ),
      vatNumber = Some("GB123456789")
    )),
    deliveryPlaceCustomsOfficeReferenceNumber = Some("FR000003"),
    dateOfDispatch = LocalDate.parse("2008-11-20"),
    journeyTime = "20 days",
    numberOfItems = 2,
    transportDetails = Seq(
      TransportDetailsModel(
        transportUnitCode = "1",
        identityOfTransportUnits = Some("AB11 1T4"),
        commercialSealIdentification = None,
        complementaryInformation = None,
        sealInformation = None
      ),
      TransportDetailsModel(
        transportUnitCode = "2",
        identityOfTransportUnits = Some("AB22 2T4"),
        commercialSealIdentification = None,
        complementaryInformation = None,
        sealInformation = None
      )
    ),
    reportOfReceipt = Some(reportOfReceiptResponse),
    items = Seq(item1, item2),
    eventHistorySummary = Some(getMovementHistoryEventsModel),
    firstTransporterTrader = Some(TransportTraderModel(
      traderName = Some("testFirstTransporterTraderName"),
      address = Some(AddressModel(
        streetNumber = None,
        street = Some("Main101"),
        postcode = Some("ZZ78"),
        city = Some("Zeebrugge")
      )),
      vatNumber = Some("testVatNumber"),
      eoriNumber = Some("testEoriNumber"),
    )),
    headerEadEsad = HeaderEadEsadModel(
      sequenceNumber = 1,
      dateAndTimeOfUpdateValidation = "testDateTime",
      destinationType = DestinationType.TaxWarehouse,
      journeyTime = "testJourneyTime",
      transportArrangement = TransportArrangement.Consignor
    ),
    transportMode = TransportModeModel(
      transportModeCode = TransportMode.AirTransport,
      complementaryInformation = None
    ),
    movementGuarantee = MovementGuaranteeModel(
      guarantorTypeCode = Consignee,
      guarantorTrader = Some(Seq(TraderModel(
        traderExciseNumber = "GB12345GTR144",
        traderName = "Current 801 Guarantor",
        address = AddressModel(
          streetNumber = None,
          street = Some("Main101"),
          postcode = Some("ZZ78"),
          city = Some("Zeebrugge")
        ),
        vatNumber = Some("GB123456789")
      )))
    ),
    documentCertificate = Some(
      Seq(DocumentCertificateModel(
        Some("1"),
        Some("Document reference"),
        Some("Document description"),
        Some("Reference of document")
      ), DocumentCertificateModel(
        Some("2"),
        Some("Document reference 2"),
        Some("Document description 2"),
        Some("Reference of document 2")
      ))
    )
  )

  val reportOfReceiptJson = Json.obj(
    "arc" -> testArc,
    "sequenceNumber" -> 2,
    "dateAndTimeOfValidationOfReportOfReceiptExport" -> "2021-09-10T11:11:12",
    "consigneeTrader" -> Json.obj(
      "traderExciseNumber" -> "GBRC345GTR145",
      "traderName" -> "Current 801 Consignee",
      "address" -> Json.obj(
        "street" -> "Main101",
        "postcode" -> "ZZ78",
        "city" -> "Zeebrugge"
      ),
      "vatNumber" -> "GB123456789"
    ),
    "deliveryPlaceTrader" -> Json.obj(
      "traderExciseNumber" -> "GBRC345GTR145",
      "traderName" -> "Current 801 Consignee",
      "address" -> Json.obj(
        "street" -> "Main101",
        "postcode" -> "ZZ78",
        "city" -> "Zeebrugge"
      ),
      "vatNumber" -> "GB123456789"
    ),
    "destinationOffice" -> "XI004098",
    "dateOfArrival" -> "2008-12-08",
    "acceptMovement" -> Unsatisfactory.toString,
    "individualItems" -> Json.arr(
      Json.obj(
        "eadBodyUniqueReference" -> 1,
        "productCode" -> testEpcWine,
        "excessAmount" -> 21,
        "refusedAmount" -> 1,
        "unsatisfactoryReasons" -> Json.arr(
          Json.obj(
            "reason" -> "excess",
            "additionalInformation" -> "some info"
          ),
          Json.obj(
            "reason" -> "shortage"
          ),
          Json.obj(
            "reason" -> "damaged"
          ),
          Json.obj(
            "reason" -> "brokenSeals"
          ),
          Json.obj(
            "reason" -> "other"
          )
        )
      )
    ),
    "otherInformation" -> "some great reason"
  )

  val getMovementResponseInputJson: JsValue = Json.obj(
    "arc" -> testArc,
    "sequenceNumber" -> testSequenceNumber,
    "destinationType" -> "1",
    "localReferenceNumber" -> testLrn,
    "eadEsad" -> Json.obj(
      "localReferenceNumber" -> "123",
      "invoiceNumber" -> "INV123",
      "originTypeCode" -> "1",
      "dateOfDispatch" -> "",
      "timeOfDispatch" -> "00:00:00",
      "invoiceDate" -> "2023-12-01"
    ),
    "eadStatus" -> "Accepted",
    "consignorTrader" -> Json.obj(
      "traderExciseNumber" -> "GBRC345GTR145",
      "traderName" -> "Current 801 Consignor",
      "address" -> Json.obj(
        "street" -> "Main101",
        "postcode" -> "ZZ78",
        "city" -> "Zeebrugge"
      ),
      "vatNumber" -> "GB123456789"
    ),
    "consigneeTrader" -> Json.obj(
      "traderExciseNumber" -> "GB12345GTR144",
      "traderName" -> "Current 801 Consignee",
      "address" -> Json.obj(
        "street" -> "Main101",
        "postcode" -> "ZZ78",
        "city" -> "Zeebrugge"
      ),
      "vatNumber" -> "GB123456789"
    ),
    "movementGuarantee" -> Json.obj(
      "guarantorTypeCode" -> "4",
      "guarantorTrader" -> Json.arr(Json.obj(
        "traderExciseNumber" -> "GB12345GTR144",
        "traderName" -> "Current 801 Guarantor",
        "address" -> Json.obj(
          "street" -> "Main101",
          "postcode" -> "ZZ78",
          "city" -> "Zeebrugge"
        ),
        "vatNumber" -> "GB123456789"
      ))
    ),
    "placeOfDispatchTrader" -> Json.obj(
      "traderExciseNumber" -> "GBRC345GTR145",
      "traderName" -> "Current 801 Consignor",
      "address" -> Json.obj(
        "street" -> "Main101",
        "postcode" -> "ZZ78",
        "city" -> "Zeebrugge"
      ),
      "vatNumber" -> "GB123456789"
    ),
    "deliveryPlaceTrader" -> Json.obj(
      "traderExciseNumber" -> "GBRC345GTR145",
      "traderName" -> "Current 801 Consignee",
      "address" -> Json.obj(
        "street" -> "Main101",
        "postcode" -> "ZZ78",
        "city" -> "Zeebrugge"
      ),
      "vatNumber" -> "GB123456789"
    ),
    "placeOfDispatchTrader" -> Json.obj(
      "traderExciseNumber" -> "GBRC345GTR145",
      "traderName" -> "Current 801 Consignor",
      "address" -> Json.obj(
        "street" -> "Main101",
        "postcode" -> "ZZ78",
        "city" -> "Zeebrugge"
      ),
      "vatNumber" -> "GB123456789"
    ),
    "deliveryPlaceCustomsOfficeReferenceNumber" -> "FR000003",
    "dateOfDispatch" -> "2008-11-20",
    "journeyTime" -> "20 days",
    "numberOfItems" -> 2,
    "transportDetails" -> Json.arr(
      Json.obj(
        "transportUnitCode" -> "1",
        "identityOfTransportUnits" -> "AB11 1T4"
      ),
      Json.obj(
        "transportUnitCode" -> "2",
        "identityOfTransportUnits" -> "AB22 2T4"
      )
    ),
    "reportOfReceipt" -> reportOfReceiptJson,
    "items" -> Json.arr(
      item1Json,
      item2Json
    ),
    "eventHistorySummary" -> getMovementHistoryEventsResponseInputJson,
    "firstTransporterTrader" -> Json.obj(
      "traderName" -> "testFirstTransporterTraderName",
      "address" -> Json.obj(
        "street" -> "Main101",
        "postcode" -> "ZZ78",
        "city" -> "Zeebrugge"
      ),
      "vatNumber" -> "testVatNumber",
      "eoriNumber" -> "testEoriNumber",
    ),
    "headerEadEsad" -> Json.obj(
      "sequenceNumber" -> 1,
      "dateAndTimeOfUpdateValidation" -> "testDateTime",
      "destinationType" -> "1",
      "journeyTime" -> "testJourneyTime",
      "transportArrangement" -> "1"
    ),
    "transportMode" -> Json.obj(
      "transportModeCode" -> "4"
    ),
    "documentCertificate" -> Json.arr(
      Json.obj(
        "documentType" -> "1",
        "documentReference" -> "Document reference",
        "documentDescription" -> "Document description",
        "referenceOfDocument" -> "Reference of document"
    ), Json.obj(
        "documentType" -> "2",
        "documentReference" -> "Document reference 2",
        "documentDescription" -> "Document description 2",
        "referenceOfDocument" -> "Reference of document 2"
      ))

  )
}
