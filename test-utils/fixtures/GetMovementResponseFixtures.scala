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
import models.common.{AddressModel, TraderModel, TransportDetailsModel}
import models.response.emcsTfe.EadEsadModel
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.emcstfefrontend.models.response.emcsTfe.GetMovementResponse

import java.time.LocalDate

trait GetMovementResponseFixtures { _: BaseFixtures =>

  val getMovementResponseModel: GetMovementResponse = GetMovementResponse(
    arc = testArc,
    sequenceNumber = testSequenceNumber,
    localReferenceNumber = testLrn,
    eadEsad = EadEsadModel(
      localReferenceNumber = testLrn,
      invoiceNumber = "INV123",
      invoiceDate = None,
      originTypeCode = TaxWarehouse,
      dateOfDispatch = "",
      timeOfDispatch = Some("00:00:00"),
      upstreamArc = None,
      importSadNumber = None
    ),
    eadStatus = "Accepted",
    consignorTrader = TraderModel(
      traderExciseNumber = "GB12345GTR144",
      traderName = "Current 801 Consignor",
      address = AddressModel(
        streetNumber = None,
        street = Some("Main101"),
        postcode = Some("ZZ78"),
        city = Some("Zeebrugge")
      )
    ),
    dateOfDispatch = LocalDate.parse("2008-11-20"),
    journeyTime = "20 days",
    numberOfItems = 2,
    transportDetails = Seq[TransportDetailsModel](
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
    )

  )

  val getMovementResponseInputJson: JsValue = Json.obj(
    "arc" -> testArc,
    "sequenceNumber" -> testSequenceNumber,
    "localReferenceNumber" -> testLrn,
    "eadEsad" -> Json.obj(
      "localReferenceNumber" -> "123",
      "invoiceNumber" -> "INV123",
      "originTypeCode" -> "1",
      "dateOfDispatch" -> "",
      "timeOfDispatch" -> "00:00:00"
    ),
    "eadStatus" -> "Accepted",
    "consignorTrader" -> Json.obj(
      "traderExciseNumber" -> "GB12345GTR144",
      "traderName" -> "Current 801 Consignor",
      "address" -> Json.obj(
        "street" -> "Main101",
        "postcode" -> "ZZ78",
        "city" -> "Zeebrugge"
      )
    ),
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
    )
  )
}
