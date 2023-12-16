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

import models.common.OriginType.TaxWarehouse
import models.common.WrongWithMovement.{Excess, Shortage}
import models.common.{AddressModel, DestinationType, TraderModel, TransportDetailsModel}
import models.response.emcsTfe.reportOfReceipt.{ReceiptedItemsModel, ReportOfReceiptModel, UnsatisfactoryModel}
import models.response.emcsTfe.{EadEsadModel, GetMovementResponse}
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDate

trait GetMovementResponseFixtures {
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
      )
    ),
    deliveryPlaceTrader = Some(TraderModel(
      traderExciseNumber = "GBRC345GTR145",
      traderName = "Current 801 Consignee",
      address = AddressModel(
        streetNumber = None,
        street = Some("Main101"),
        postcode = Some("ZZ78"),
        city = Some("Zeebrugge")
      )
    )),
    placeOfDispatchTrader = Some(TraderModel(
      traderExciseNumber = "GBRC345GTR145",
      traderName = "Current 801 Consignor",
      address = AddressModel(
        streetNumber = None,
        street = Some("Main101"),
        postcode = Some("ZZ78"),
        city = Some("Zeebrugge")
      )
    )),
    consigneeTrader = Some(TraderModel(
      traderExciseNumber = "GB12345GTR144",
      traderName = "Current 801 Consignee",
      address = AddressModel(
        streetNumber = None,
        street = Some("Main101"),
        postcode = Some("ZZ78"),
        city = Some("Zeebrugge")
      )
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
    reportOfReceipt = Some(ReportOfReceiptModel(
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
        )
      )),
      deliveryPlaceTrader = Some(TraderModel(
        traderExciseNumber = "GBRC345GTR145",
        traderName = "Current 801 Consignee",
        address = AddressModel(
          streetNumber = None,
          street = Some("Main101"),
          postcode = Some("ZZ78"),
          city = Some("Zeebrugge")
        )
      )),
      destinationOffice = "XI004098",
      dateOfArrival = LocalDate.parse("2008-12-08"),
      acceptMovement = "satisfactory",
      otherInformation = Some("some great reason"),
      individualItems = Seq(ReceiptedItemsModel(
        eadBodyUniqueReference = 1,
        excessAmount = Some(21),
        shortageAmount = None,
        productCode = "W300",
        refusedAmount = Some(1),
        unsatisfactoryReasons = Seq(
          UnsatisfactoryModel(Excess, Some("some info")),
          UnsatisfactoryModel(Shortage, None),
        )
      ))
    ))
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
      )
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
    "placeOfDispatchTrader" -> Json.obj(
      "traderExciseNumber" -> "GBRC345GTR145",
      "traderName" -> "Current 801 Consignor",
      "address" -> Json.obj(
        "street" -> "Main101",
        "postcode" -> "ZZ78",
        "city" -> "Zeebrugge"
      )
    ),
    "deliveryPlaceTrader" -> Json.obj(
      "traderExciseNumber" -> "GB12345GTR144",
      "traderName" -> "Current 801 Consignee",
      "address" -> Json.obj(
        "street" -> "Main101",
        "postcode" -> "ZZ78",
        "city" -> "Zeebrugge"
      )
    ),
    "deliveryPlaceTrader" -> Json.obj(
      "traderExciseNumber" -> "GBRC345GTR145",
      "traderName" -> "Current 801 Consignee",
      "address" -> Json.obj(
        "street" -> "Main101",
        "postcode" -> "ZZ78",
        "city" -> "Zeebrugge"
      )
    ),
    "placeOfDispatchTrader" -> Json.obj(
      "traderExciseNumber" -> "GBRC345GTR145",
      "traderName" -> "Current 801 Consignor",
      "address" -> Json.obj(
        "street" -> "Main101",
        "postcode" -> "ZZ78",
        "city" -> "Zeebrugge"
      )
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
    "reportOfReceipt" -> Json.obj(
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
        )
      ),
      "deliveryPlaceTrader" -> Json.obj(
        "traderExciseNumber" -> "GBRC345GTR145",
        "traderName" -> "Current 801 Consignee",
        "address" -> Json.obj(
          "street" -> "Main101",
          "postcode" -> "ZZ78",
          "city" -> "Zeebrugge"
        )
      ),
      "destinationOffice" -> "XI004098",
      "dateOfArrival" -> "2008-12-08",
      "acceptMovement" -> "satisfactory",
      "individualItems" -> Json.arr(
        Json.obj(
          "eadBodyUniqueReference" -> 1,
          "productCode" -> "W300",
          "excessAmount" -> 21,
          "refusedAmount" -> 1,
          "unsatisfactoryReasons" -> Json.arr(
            Json.obj(
              "reason" -> "excess",
              "additionalInformation" -> "some info"
            ),
            Json.obj(
              "reason" -> "shortage"
            )
          )
        )
      ),
      "otherInformation" -> "some great reason"
    )
  )
}
