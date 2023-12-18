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

import models.common.AcceptMovement.{PartiallyRefused, Satisfactory}
import models.response.emcsTfe.reportOfReceipt.ReportOfReceiptModel

import java.time.LocalDate

trait ReportOfReceiptFixtures extends TraderModelFixtures with ReceiptedItemsModelFixtures {
  val arrivalDate: LocalDate = LocalDate.now()
  val destinationOfficeId: String = "GB000434"

  val maxReportOfReceiptModel: ReportOfReceiptModel = ReportOfReceiptModel(
    arc = testArc,
    dateAndTimeOfValidationOfReportOfReceiptExport = None,
    sequenceNumber = 1,
    consigneeTrader = Some(maxTraderModel),
    deliveryPlaceTrader = Some(maxTraderModel),
    destinationOffice = destinationOfficeId,
    dateOfArrival = arrivalDate,
    acceptMovement = PartiallyRefused,
    individualItems = Seq(
      excessReceiptedItemsModel,
      excessReceiptedItemsModel.copy(eadBodyUniqueReference = 2)
    ),
    otherInformation = Some("other")
  )

  val minSubmitReportOfReceiptModel: ReportOfReceiptModel = ReportOfReceiptModel(
    arc = testArc,
    sequenceNumber = 1,
    consigneeTrader = None,
    deliveryPlaceTrader = None,
    destinationOffice = destinationOfficeId,
    dateOfArrival = arrivalDate,
    acceptMovement = Satisfactory,
    individualItems = Seq(),
    otherInformation = None,
    dateAndTimeOfValidationOfReportOfReceiptExport = None
  )
}
