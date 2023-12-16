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

package models.response.emcsTfe

import models.common.{DestinationType, TraderModel, TransportDetailsModel}
import models.response.emcsTfe.reportOfReceipt.ReportOfReceiptModel
import play.api.libs.json.{Json, OFormat}
import utils.{DateUtils, ExpectedDateOfArrival}

import java.time.{LocalDate, LocalTime}


case class GetMovementResponse(
                                arc: String,
                                sequenceNumber: Int,
                                destinationType: DestinationType,
                                localReferenceNumber: String,
                                eadEsad: EadEsadModel,
                                eadStatus: String,
                                deliveryPlaceTrader: Option[TraderModel],
                                placeOfDispatchTrader: Option[TraderModel],
                                deliveryPlaceCustomsOfficeReferenceNumber: Option[String],
                                consignorTrader: TraderModel,
                                consigneeTrader: Option[TraderModel],
                                dateOfDispatch: LocalDate,
                                journeyTime: String,
                                numberOfItems: Int,
                                transportDetails: Seq[TransportDetailsModel],
                                reportOfReceipt: Option[ReportOfReceiptModel]
                              ) extends DateUtils with ExpectedDateOfArrival {
  def formattedDateOfDispatch: String = dateOfDispatch.formatDateForUIOutput()

  def formattedDateOfArrival: Option[String] = reportOfReceipt.map(_.dateOfArrival.formatDateForUIOutput())

  def formattedExpectedDateOfArrival: String = {
    calculateExpectedDate(
      dateOfDispatch,
      eadEsad.timeOfDispatch.map{ _.split(":")}.getOrElse(Array("0","0","0")) match {
        case Array(h, m, s) => LocalTime.of(h.toInt, m.toInt, s.toInt)
      },
      journeyTime
    ).toLocalDate.formatDateForUIOutput()
  }

}

object GetMovementResponse {
  implicit val format: OFormat[GetMovementResponse] = Json.format
}
