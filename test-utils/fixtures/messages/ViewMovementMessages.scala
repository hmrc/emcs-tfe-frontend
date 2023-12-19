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

package fixtures.messages

object ViewMovementMessages {

  sealed trait ViewMessages extends BaseEnglish { _: i18n =>
    def title(arc: String, section: String): String = titleHelper(s"$arc - $section")
    val arcSubheading: String = "Administrative reference code"
    val overviewTabHeading = "Overview"
    val movementTabHeading = "Movement"
    val deliveryTabHeading = "Delivery"

    val overviewCardTitle = "Overview"
    val overviewCardLrn = "Local Reference Number (LRN)"
    val overviewCardEadStatus = "Electronic administrative document (eAD) status"
    val overviewCardDateOfDispatch = "Date of dispatch"
    val overviewCardExpectedDate = "Expected date of arrival"
    val overviewCardConsignor = "Consignor"
    val overviewCardNumberOfItems = "Number of items"
    val overviewCardTransporting = "Transporting vehicle(s)"

    val itemsH2 = "Item details"
    val itemsTableItemHeading = "Item"
    val itemsTableCommercialDescriptionHeading = "Commercial description"
    val itemsTableQuantityHeading = "Quantity"
    val itemsTablePackagingHeading = "Packaging"
    val itemsTableReceiptHeading = "Receipt"

    val itemsTableItemRow: Int => String = "Item " + _
    val itemsReceiptStatusNotReceipted = "Not submitted"
    val itemsReceiptStatusSatisfactory = "Satisfactory"
    val itemsReceiptStatusDamaged = "Damaged"
    val itemsReceiptStatusExcess = "Excess"
    val itemsReceiptStatusShortage = "Shortage"
    val itemsReceiptStatusBrokenSeals = "Broken seal(s)"
    val itemsReceiptStatusOther = "Other"

    val actionLinkSubmitReportOfReceipt = "Submit report of receipt"
    val actionLinkExplainDelay = "Explain a delay"
    val actionLinkExplainShortageOrExcess = "Explain a shortage or excess"
    val actionLinkCancelMovement = "Cancel this movement"
    val actionLinkChangeOfDestination = "Submit a change of destination"
    val actionLinkAlertOrRejection = "Submit alert or rejection"
    val actionLinkPrint = "Print or save a copy of the eAD"

    val movementSummaryCardTitle = "Summary"
    val movementSummaryCardLrn = "LRN"
    val movementSummaryCardEADStatus = "eAD status"
    val movementSummaryCardReceiptStatus = "Receipt status"
    val movementSummaryCardMovementType = "Movement type"
    val movementSummaryCardMovementDirection = "Movement direction"

    val movementTimeAndDateCardTitle = "Time and date"
    val movementTimeAndDateCardDateOfDispatch = "Date of dispatch"
    val movementTimeAndDateCardTimeOfDispatch = "Time of dispatch"
    val movementTimeAndDateCardPredictedDateOfArrival = "Predicted arrival"
    val movementTimeAndDateCardDateOfArrival = "Date of arrival"

    val movementInvoiceCardTitle = "Invoice"
    val movementInvoiceCardReference = "Invoice reference"
    val movementInvoiceCardDateOfIssue = "Invoice date of issue"

    val deliveryDetailsHeading = "Delivery details"
    val deliveryConsignorCardTitle = "Consignor"
    val deliveryPlaceOfDispatchCardTitle = "Place of dispatch"
    val deliveryConsigneeCardTitle = "Consignee"
    val deliveryPlaceOfDestinationCardTitle = "Place of destination"
    val deliveryCardBusinessName = "Business name"
    val deliveryCardERN = "Excise registration number (ERN)"
    val deliveryPlaceOfDispatchCardERN = "Excise ID (ERN)"
    val deliveryCardAddress = "Address"
  }

  object English extends ViewMessages with EN

}
