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

import models.MovementEadStatus.Accepted
import models.common.AcceptMovement.Unsatisfactory
import models.common.GuarantorType.Consignee
import models.common.OriginType.TaxWarehouse
import models.common.WrongWithMovement.{BrokenSeals, Damaged, Excess, Other, Shortage}
import models.common._
import models.response.emcsTfe.AlertOrRejectionType.{Alert, Rejection}
import models.response.emcsTfe.NotificationOfDivertedMovementType.ChangeOfDestination
import models.response.emcsTfe.customsRejection.CustomsRejectionDiagnosisCodeType.UnknownArc
import models.response.emcsTfe.customsRejection.CustomsRejectionReasonCodeType.ImportDataMismatch
import models.response.emcsTfe.customsRejection.{CustomsRejectionDiagnosis, NotificationOfCustomsRejectionModel}
import models.response.emcsTfe.reportOfReceipt.{ReceiptedItemsModel, ReportOfReceiptModel, UnsatisfactoryModel}
import models.response.emcsTfe._
import play.api.libs.json.{JsValue, Json}

import java.time.{LocalDate, LocalDateTime}

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
    )),
    deliveryPlaceTrader = Some(TraderModel(
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

  lazy val getMovementResponseModel: GetMovementResponse = GetMovementResponse(
    arc = testArc,
    sequenceNumber = testSequenceNumber,
    destinationType = DestinationType.TaxWarehouse,
    memberStateCode = None,
    serialNumberOfCertificateOfExemption = None,
    localReferenceNumber = testLrn,
    eadEsad = eadEsadModel,
    eadStatus = Accepted,
    consignorTrader = TraderModel(
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
    ),
    deliveryPlaceTrader = Some(TraderModel(
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
    )),
    placeOfDispatchTrader = Some(TraderModel(
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
    )),
    transportArrangerTrader = None,
    consigneeTrader = Some(TraderModel(
      traderExciseNumber = Some("GB12345GTR144"),
      traderName = Some("Current 801 Consignee"),
      address = Some(AddressModel(
        streetNumber = None,
        street = Some("Main101"),
        postcode = Some("ZZ78"),
        city = Some("Zeebrugge")
      )),
      vatNumber = None,
      eoriNumber = None
    )),
    dispatchImportOfficeReferenceNumber = None,
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
      eoriNumber = Some("testEoriNumber")
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
        traderExciseNumber = Some("GB12345GTR144"),
        traderName = Some("Current 801 Guarantor"),
        address = Some(AddressModel(
          streetNumber = None,
          street = Some("Main101"),
          postcode = Some("ZZ78"),
          city = Some("Zeebrugge")
        )),
        vatNumber = Some("GB123456789"),
        eoriNumber = None
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
    ),
    notificationOfDivertedMovement = Some(NotificationOfDivertedMovementModel(
      notificationType = ChangeOfDestination,
      notificationDateAndTime = LocalDateTime.of(2024, 6, 5, 0, 0, 1),
      downstreamArcs = Seq(testArc, testArc.dropRight(1) + "1")
    )),
    notificationOfAlertOrRejection = Some(Seq(
      NotificationOfAlertOrRejectionModel(
        notificationType = Alert,
        notificationDateAndTime = LocalDateTime.of(2023, 12, 18, 9, 0, 0),
        alertRejectReason = Seq(
          AlertOrRejectionReasonModel(
            reason = AlertOrRejectionReasonType.ProductDoesNotMatchOrder,
            additionalInformation = Some("Info")
          ),
          AlertOrRejectionReasonModel(
            reason = AlertOrRejectionReasonType.Other,
            additionalInformation = Some("Info")
          ),
          AlertOrRejectionReasonModel(
            reason = AlertOrRejectionReasonType.EADNotConcernRecipient,
            additionalInformation = Some("Info")
          ),
          AlertOrRejectionReasonModel(
            reason = AlertOrRejectionReasonType.QuantityDoesNotMatchOrder,
            additionalInformation = Some("Info")
          )
        )
      ),
      NotificationOfAlertOrRejectionModel(
        notificationType = Alert,
        notificationDateAndTime = LocalDateTime.of(2023, 12, 18, 10, 0, 0),
        alertRejectReason = Seq(
          AlertOrRejectionReasonModel(
            reason = AlertOrRejectionReasonType.EADNotConcernRecipient,
            additionalInformation = None
          )
        )
      ),
      NotificationOfAlertOrRejectionModel(
        notificationType = Rejection,
        notificationDateAndTime = LocalDateTime.of(2023, 12, 19, 9, 0, 0),
        alertRejectReason = Seq(
          AlertOrRejectionReasonModel(
            reason = AlertOrRejectionReasonType.QuantityDoesNotMatchOrder,
            additionalInformation = None
          )
        )
      )
    )),
    notificationOfAcceptedExport = Some(notificationOfAcceptedExport),
    cancelMovement = Some(CancelMovementModel(CancellationReasonType.Other, Some("some info"))),
    interruptedMovement = Some(InterruptionReasonModel(InterruptionReasonType.Other, "FR1234", Some("some info"))),
    notificationOfDelay = Some(Seq(
      NotificationOfDelayModel(
        submitterIdentification = "GBWK001234569",
        submitterType = SubmitterType.Consignor,
        explanationCode = DelayReasonType.Accident,
        complementaryInformation = Some("Lorry crashed off cliff"),
        dateTime = LocalDateTime.parse("2024-06-18T08:11:33")
      ),
      NotificationOfDelayModel(
        submitterIdentification = "GBWK001234569",
        submitterType = SubmitterType.Consignor,
        explanationCode = DelayReasonType.Strikes,
        complementaryInformation = None,
        dateTime = LocalDateTime.parse("2024-06-18T08:18:56")
      )
    )),
    notificationOfCustomsRejection = Some(
      NotificationOfCustomsRejectionModel(
        customsOfficeReferenceNumber = Some("AT002000"),
        rejectionDateAndTime = LocalDateTime.of(2024, 1, 15, 19, 14, 20),
        rejectionReasonCode = ImportDataMismatch,
        localReferenceNumber = Some("1111"),
        documentReferenceNumber = Some("7885"),
        diagnoses = Seq(CustomsRejectionDiagnosis(
          bodyRecordUniqueReference = "125",
          diagnosisCode = UnknownArc
        )),
        consignee = Some(
          TraderModel(
            traderExciseNumber = Some("XIWK000000206"),
            traderName = Some("SEED TRADER NI"),
            address = Some(
              AddressModel(
                streetNumber = Some("1"),
                street = Some("Catherdral"),
                postcode = Some("BT3 7BF"),
                city = Some("Salford")
              )),
            vatNumber = None,
            eoriNumber = None
          )
        )
      )
    ),
    notificationOfShortageOrExcess = Some(
      NotificationOfShortageOrExcessModel(
        submitterType = SubmitterType.Consignee,
        globalDateOfAnalysis = None,
        globalExplanation = None,
        individualItemReasons = Some(Seq(
          BodyAnalysisModel(
            exciseProductCode = "B000",
            bodyRecordUniqueReference = 1,
            explanation = "4 more than I expected",
            actualQuantity = Some(5)
          )
        ))
      )
    )
  )

  lazy val notificationOfAcceptedExport: NotificationOfAcceptedExportModel =
    NotificationOfAcceptedExportModel(
      customsOfficeNumber = "GB000383",
      dateOfAcceptance = LocalDate.of(2024, 2, 5),
      referenceNumberOfSenderCustomsOffice = "GB000101",
      identificationOfSenderCustomsOfficer = "John Doe",
      documentReferenceNumber = "645564546",
      consigneeTrader = TraderModel(
        traderExciseNumber = Some("BE345345345"),
        traderName = Some("PEAR Supermarket"),
        address = Some(
          AddressModel(
            streetNumber = None,
            street = Some("Angels Business Park"),
            postcode = Some("BD1 3NN"),
            city = Some("Bradford")
          )),
        vatNumber = None,
        eoriNumber = Some("GB00000578901")
      )
    )

  val notificationOfCustomsRejectionModel: NotificationOfCustomsRejectionModel = NotificationOfCustomsRejectionModel(
    customsOfficeReferenceNumber = Some("AT002000"),
    rejectionDateAndTime = LocalDateTime.of(2024, 1, 15, 19, 14, 20),
    rejectionReasonCode = ImportDataMismatch,
    localReferenceNumber = Some("1111"),
    documentReferenceNumber = Some("7885"),
    diagnoses = Seq(CustomsRejectionDiagnosis(
      bodyRecordUniqueReference = "125",
      diagnosisCode = UnknownArc
    )),
    consignee = Some(
      TraderModel(
        traderExciseNumber = Some("XIWK000000206"),
        traderName = Some("SEED TRADER NI"),
        address = Some(
          AddressModel(
            streetNumber = Some("1"),
            street = Some("Catherdral"),
            postcode = Some("BT3 7BF"),
            city = Some("Salford")
          )),
        vatNumber = None,
        eoriNumber = None
      )
    )
  )

  val reportOfReceiptJson: JsValue = Json.obj(
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
      )
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
    "notificationOfDivertedMovement" -> Json.obj(
      "notificationType" -> "1",
      "notificationDateAndTime" -> "2024-06-05T00:00:01",
      "downstreamArcs" -> Json.arr(
        testArc, s"${testArc.dropRight(1)}1"
      )
    ),
    "notificationOfAcceptedExport" -> Json.obj(
      "customsOfficeNumber" -> "GB000383",
      "dateOfAcceptance" -> "2024-02-05",
      "referenceNumberOfSenderCustomsOffice" -> "GB000101",
      "identificationOfSenderCustomsOfficer" -> "John Doe",
      "documentReferenceNumber" -> "645564546",
      "consigneeTrader" -> Json.obj(
        "traderExciseNumber" -> "BE345345345",
        "traderName" -> "PEAR Supermarket",
        "address" -> Json.obj(
          "street" -> "Angels Business Park",
          "postcode" -> "BD1 3NN",
          "city" -> "Bradford"
        ),
        "eoriNumber" -> "GB00000578901"
      )
    ),
    "cancelMovement" -> Json.obj(
      "reason" -> "0",
      "complementaryInformation" -> "some info"
    ),
    "interruptedMovement" -> Json.obj(
      "reasonCode" -> "0",
      "referenceNumberOfExciseOffice" -> "FR1234",
      "complementaryInformation" -> "some info"
    ),
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
      "eoriNumber" -> "testEoriNumber"
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
      ),
      Json.obj(
        "documentType" -> "2",
        "documentReference" -> "Document reference 2",
        "documentDescription" -> "Document description 2",
        "referenceOfDocument" -> "Reference of document 2"
      )
    ),
    "notificationOfAlertOrRejection" -> Json.arr(
      Json.obj(
        "notificationType" -> "0",
        "notificationDateAndTime" -> "2023-12-18T09:00:00",
        "alertRejectReason" -> Json.arr(
          Json.obj(
            "reason" -> "2",
            "additionalInformation" -> "Info"
          ),
          Json.obj(
            "reason" -> "0",
            "additionalInformation" -> "Info"
          ),
          Json.obj(
            "reason" -> "1",
            "additionalInformation" -> "Info"
          ),
          Json.obj(
            "reason" -> "3",
            "additionalInformation" -> "Info"
          )
        )
      ),
      Json.obj(
        "notificationType" -> "0",
        "notificationDateAndTime" -> "2023-12-18T10:00:00",
        "alertRejectReason" -> Json.arr(
          Json.obj(
            "reason" -> "1"
          )
        )
      ),
      Json.obj(
        "notificationType" -> "1",
        "notificationDateAndTime" -> "2023-12-19T09:00:00",
        "alertRejectReason" -> Json.arr(
          Json.obj(
            "reason" -> "3"
          )
        )
      )
    ),
    "notificationOfDelay" -> Json.arr(
      Json.obj(
        "submitterIdentification" -> "GBWK001234569",
        "submitterType" -> "1",
        "explanationCode" -> "6",
        "complementaryInformation" -> "Lorry crashed off cliff",
        "dateTime" -> "2024-06-18T08:11:33"
      ),
      Json.obj(
        "submitterIdentification" -> "GBWK001234569",
        "submitterType" -> "1",
        "explanationCode" -> "5",
        "dateTime" -> "2024-06-18T08:18:56"
      )
    ),
    "notificationOfCustomsRejection" -> Json.obj(
      "customsOfficeReferenceNumber" -> "AT002000",
      "rejectionDateAndTime" -> "2024-01-15T19:14:20",
      "rejectionReasonCode" -> "2",
      "localReferenceNumber" -> "1111",
      "documentReferenceNumber" -> "7885",
      "diagnoses" -> Json.arr(
        Json.obj(
          "bodyRecordUniqueReference" -> "125",
          "diagnosisCode" -> "1"
        )
      ),
      "consignee" -> Json.obj(
        "traderExciseNumber" -> "XIWK000000206",
        "traderName" -> "SEED TRADER NI",
        "address" -> Json.obj(
          "streetNumber" -> "1",
          "street" -> "Catherdral",
          "postcode" -> "BT3 7BF",
          "city" -> "Salford"
        )
      )
    ),
    "notificationOfShortageOrExcess" -> Json.obj(
      "submitterType" -> "2",
      "individualItemReasons" -> Json.arr(
        Json.obj(
          "exciseProductCode" -> "B000",
          "bodyRecordUniqueReference" -> 1,
          "explanation" -> "4 more than I expected",
          "actualQuantity" -> 5
        )
      )
    )
  )
}
