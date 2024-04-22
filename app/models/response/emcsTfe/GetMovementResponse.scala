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

import models.MovementEadStatus
import models.common._
import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import models.response.emcsTfe.reportOfReceipt.ReportOfReceiptModel
import play.api.libs.json._
import utils.{DateUtils, ExpectedDateOfArrival}

import java.time.{LocalDate, LocalTime}


case class GetMovementResponse(
                                arc: String,
                                sequenceNumber: Int,
                                destinationType: DestinationType,
                                localReferenceNumber: String,
                                eadEsad: EadEsadModel,
                                eadStatus: MovementEadStatus,
                                deliveryPlaceTrader: Option[TraderModel],
                                placeOfDispatchTrader: Option[TraderModel],
                                firstTransporterTrader: Option[TransportTraderModel],
                                deliveryPlaceCustomsOfficeReferenceNumber: Option[String],
                                consignorTrader: TraderModel,
                                consigneeTrader: Option[TraderModel],
                                dateOfDispatch: LocalDate,
                                journeyTime: String,
                                documentCertificate: Option[Seq[DocumentCertificateModel]],
                                headerEadEsad: HeaderEadEsadModel,
                                transportMode: TransportModeModel,
                                numberOfItems: Int,
                                transportDetails: Seq[TransportDetailsModel],
                                reportOfReceipt: Option[ReportOfReceiptModel],
                                items: Seq[MovementItem],
                                movementGuarantee: MovementGuaranteeModel,
                                eventHistorySummary: Option[Seq[MovementHistoryEvent]]
                              ) extends DateUtils with ExpectedDateOfArrival {
  def formattedDateOfDispatch: String = dateOfDispatch.formatDateForUIOutput()

  def formattedDateOfArrival: Option[String] = reportOfReceipt.map(_.dateOfArrival.formatDateForUIOutput())

  def formattedExpectedDateOfArrival: String = {
    calculateExpectedDate(
      dateOfDispatch,
      eadEsad.timeOfDispatch.map {
        _.split(":")
      }.getOrElse(Array("0", "0", "0")) match {
        case Array(h, m, s) => LocalTime.of(h.toInt, m.toInt, s.toInt)
      },
      journeyTime
    ).toLocalDate.formatDateForUIOutput()
  }

  def isConsigneeOfMovement(ern: String): Boolean = consigneeTrader.exists(_.traderExciseNumber.getOrElse("") == ern)
}

object GetMovementResponse {

  implicit lazy val reads: Reads[GetMovementResponse] = for {
    arc <- (__ \ "arc").read[String]
    sequenceNumber <- (__ \ "sequenceNumber").read[Int]
    destinationType <- (__ \ "destinationType").read[DestinationType]
    consignorTrader <- (__ \ "consignorTrader").read[TraderModel]
    consigneeTrader <- (__ \ "consigneeTrader").readNullable[TraderModel]
    deliveryPlaceTrader <- (__ \ "deliveryPlaceTrader").readNullable[TraderModel]
    placeOfDispatchTrader <- (__ \ "placeOfDispatchTrader").readNullable[TraderModel]
    firstTransporterTrader <- (__ \ "firstTransporterTrader").readNullable[TransportTraderModel]
    deliveryPlaceCustomsOfficeReferenceNumber <- (__ \ "deliveryPlaceCustomsOfficeReferenceNumber").readNullable[String]
    localReferenceNumber <- (__ \ "localReferenceNumber").read[String]
    eadStatus <- (__ \ "eadStatus").read[MovementEadStatus]
    dateOfDispatch <- (__ \ "dateOfDispatch").read[LocalDate]
    journeyTime <- (__ \ "journeyTime").read[String]
    documentCertificate <- (__ \ "documentCertificate").readNullable[Seq[DocumentCertificateModel]]
    eadEsad <- (__ \ "eadEsad").read[EadEsadModel]
    headerEadEsad <- (__ \ "headerEadEsad").read[HeaderEadEsadModel]
    transportMode <- (__ \ "transportMode").read[TransportModeModel]
    movementGuarantee <- (__ \ "movementGuarantee").read[MovementGuaranteeModel]
    transportDetails <- (__ \ "transportDetails").read[Seq[TransportDetailsModel]]
    numberOfItems <- (__ \ "numberOfItems").read[Int]
    reportOfReceipt <- (__ \ "reportOfReceipt").readNullable[ReportOfReceiptModel]
    items <- (__ \ "items").read[Seq[MovementItem]]
    eventHistorySummary <- (__ \ "eventHistorySummary").readNullable[Seq[MovementHistoryEvent]]
  } yield {
    GetMovementResponse(
      arc = arc,
      sequenceNumber = sequenceNumber,
      destinationType = destinationType,
      consignorTrader = consignorTrader,
      consigneeTrader = consigneeTrader,
      deliveryPlaceTrader = deliveryPlaceTrader,
      placeOfDispatchTrader = placeOfDispatchTrader,
      firstTransporterTrader = firstTransporterTrader,
      deliveryPlaceCustomsOfficeReferenceNumber = deliveryPlaceCustomsOfficeReferenceNumber,
      localReferenceNumber = localReferenceNumber,
      eadStatus = eadStatus,
      dateOfDispatch = dateOfDispatch,
      journeyTime = journeyTime,
      documentCertificate = documentCertificate,
      eadEsad = eadEsad,
      headerEadEsad = headerEadEsad,
      transportMode = transportMode,
      movementGuarantee = movementGuarantee,
      transportDetails = transportDetails,
      numberOfItems = numberOfItems,
      reportOfReceipt = reportOfReceipt,
      items = items,
      eventHistorySummary = eventHistorySummary
    )
  }
}
