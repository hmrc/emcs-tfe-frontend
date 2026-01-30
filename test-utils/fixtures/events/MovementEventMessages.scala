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

package fixtures.events

import fixtures.messages.{BaseEnglish, EN, i18n}
import models.common.SubmitterType
import models.common.SubmitterType.{Consignee, Consignor}
import utils.DateUtils

import java.time.LocalDateTime

object MovementEventMessages extends DateUtils {

  sealed trait EventMessages extends BaseEnglish {
    _: i18n =>

    def messageIssued(dateTime: LocalDateTime = LocalDateTime.of(2024, 12, 4, 17, 0, 0)) =
      s"Message issued ${dateTime.formatDateTimeForUIOutput()}"

    def arc(arc: String) = s"ARC: $arc"

    val printScreenContent: String = "Print this screen to make a record of this message."
    val printOrSaveEadContent: String = "Print or save a copy of the eAD for a printed or digital copy of the movement."

    //IE801 First Notification of Movement
    val ie801Heading: String = "Movement created"
    val ie801Title: String = titleHelper(ie801Heading)
    val ie801P1: String = "This is the first notification for the movement."

    //IE802 Change Destination Reminder
    val ie802ChangeDestinationHeading: String = "Reminder to provide change of destination"
    val ie802ChangeDestinationTitle: String = titleHelper(ie802ChangeDestinationHeading)
    val ie802ChangeDestinationP1 = "A change of destination for this movement must be submitted."

    //IE802 Report of Receipt Reminder
    val ie802ReportReceiptHeading: String = "Reminder to provide report of receipt"
    val ie802ReportReceiptTitle: String = titleHelper(ie802ReportReceiptHeading)
    val ie802ReportReceiptP1 = "A report of receipt for this movement must be submitted."

    //IE802 Movement Destination Reminder
    val ie802MovementDestinationHeading: String = "Reminder to provide destination"
    val ie802MovementDestinationTitle: String = titleHelper(ie802MovementDestinationHeading)
    val ie802MovementDestinationP1 = "A destination for this movement must be submitted."

    //IE803 Movement Diverted Notification
    val ie803MovementDivertedHeading: String = "Diverted movement"
    val ie803MovementDivertedTitle: String = titleHelper(ie803MovementDivertedHeading)
    val ie803MovementDivertedP1 = "This movement has been diverted."

    def ie803MovementDivertedP2(dateOfSplit: String) = s"A change of destination for the movement was submitted on $dateOfSplit. You are receiving this message because you are no longer the intended consignee for the diverted movement. You can find more details in the change of destination message."

    //IE803 Movement Split Notification
    val ie803MovementSplitHeading: String = "Movement split"
    val ie803MovementSplitTitle: String = titleHelper(ie803MovementSplitHeading)

    def ie803MovementSplitP1(dateOfSplit: String) = s"A request to split this movement was submitted on $dateOfSplit."

    val ie803MovementSplitP2 = "The new ARCs for the split movement are:"

    //IE810 Movement Cancelled Notification
    val ie807MovementInterceptedHeading: String = "Movement intercepted"
    val ie807MovementInterceptedTitle: String = titleHelper(ie807MovementInterceptedHeading)

    def ie807MovementInterceptedP1(exciseOffice: String) = s"This movement was intercepted by excise office $exciseOffice"

    val IE807MovementInterceptedKey1 = "Reason for interruption"
    val IE807MovementInterceptedKey2 = "More information"
    val IE807MovementInterceptedValue1 = "Other"
    val IE807MovementInterceptedValue2 = "some info"

    //IE810 Movement Cancelled Notification
    val ie810MovementCancelledHeading: String = "Movement cancelled"
    val ie810MovementCancelledTitle: String = titleHelper(ie810MovementCancelledHeading)
    val ie810MovementCancelledP1 = "This movement has been cancelled."
    val IE810MovementCancelledKey1 = "Reason for cancellation"
    val IE810MovementCancelledKey2 = "Explanation provided by consignor"
    val IE810MovementCancelledValue1 = "Other"
    val IE810MovementCancelledValue2 = "some info"

    //IE818 Report of Receipt
    val ie818P1 = "A report of receipt has been submitted for this movement."
    val ie818P1Export = "A report of export has been received for this movement."
    val ie818P2 = "For refused or partially refused receipts, the consignor may now need to split, " +
      "cancel or complete a change of destination for the movement. " +
      "For receipts recording a shortage or excess, there may be duty or other liabilities."

    //IE819 Alert/Reject Notification (shared messages)
    val ie819ConsigneeInformation = "Information about consignee details"
    val ie819GoodsTypeInformation = "Information about goods types"
    val ie819GoodsQuantityInformation = "Information about goods quantities"
    val ie819OtherInformation = "Information about other reason(s)"

    //IE819 Alert Notification
    val ie819AlertHeading: String = "Alert submitted"
    val ie819AlertTitle: String = titleHelper(ie819AlertHeading)
    val ie819AlertP1 = "The consignee has alerted the consignor to an issue."
    val ie819AlertH2 = "Alert details"
    val ie819AlertDate = "Date of alert"
    val ie819AlertSummaryReason = "Reason for alert"
    val ie819AlertSummaryReasons = "Reasons for alert"

    //IE819 Rejection Notification
    val ie819RejectionHeading: String = "Rejection submitted"
    val ie819RejectionTitle: String = titleHelper(ie819RejectionHeading)
    val ie819RejectionP1 = "The consignee has rejected the movement."
    val ie819RejectionH2 = "Rejection details"
    val ie819RejectionDate = "Date of rejection"
    val ie819RejectionSummaryReason = "Reason for rejection"
    val ie819RejectionSummaryReasons = "Reasons for rejection"

    //IE829 Movement accepted by customs
    val ie829Heading: String = "Movement accepted by customs"
    val ie829Title: String = titleHelper(ie829Heading)
    val ie829Paragraph1: String = "This movement has been accepted for export by customs office number GB000383."

    //IE839 Movement rejected by customs
    val ie839MovementRejectedByCustomsHeading: String = "Movement rejected by customs"
    val ie839MovementRejectedByCustomsTitle: String = titleHelper(ie839MovementRejectedByCustomsHeading)

    def ie839MovementRejectedByCustomsP1WithCustomsOffice(customsOfficeNumber: String) = s"This movement has been rejected for export by customs office number $customsOfficeNumber."

    val ie839MovementRejectedByCustomsP1 = "This movement has been rejected for export by the customs office."
    val ie839MovementRejectedByCustomsRejectionDate = "Rejection date"
    val ie839MovementRejectedByCustomsRejectionReason = "Rejection reason code"
    val ie839MovementRejectedByCustomsRejectionReason1 = "1 - Import data not found"
    val ie839MovementRejectedByCustomsRejectionReason2 = "2 - The content of the e-AD does not match with import data"
    val ie839MovementRejectedByCustomsRejectionReason3 = "3 - Export data not found"
    val ie839MovementRejectedByCustomsRejectionReason4 = "4 - The content of the e-AD does not match with export data"
    val ie839MovementRejectedByCustomsRejectionReason5 = "5 - Goods are rejected at export procedure"
    val ie839MovementRejectedByCustomsLRN = "Local reference number (LRN)"
    val ie839MovementRejectedByCustomsDocumentRef = "Document reference number"
    val ie839MovementRejectedByCustomsDiagnosisHeading = "Diagnosis"

    def ie839MovementRejectedByCustomsDiagnosisNumberedHeading(number: Int) = s"Diagnosis $number"

    val ie839MovementRejectedByCustomsBodyRecordUniqueReference = "Body record unique reference"
    val ie839MovementRejectedByCustomsDiagnosisCode = "Diagnosis Code"
    val ie839MovementRejectedByCustomsDiagnosisCode1 = "1 - Unknown ARC"
    val ie839MovementRejectedByCustomsDiagnosisCode2 = "2 - Body Record Unique Reference does not exist in the e-AD OR No corresponding GOODS ITEM in the export declaration"
    val ie839MovementRejectedByCustomsDiagnosisCode3 = "3 - No corresponding GOODS ITEM in the export declaration"
    val ie839MovementRejectedByCustomsDiagnosisCode4 = "4 - Weight/mass do not match"
    val ie839MovementRejectedByCustomsDiagnosisCode5 = "5 - The destination type code of the e-AD is not export"
    val ie839MovementRejectedByCustomsDiagnosisCode6 = "6 - Commodity (CN) codes do not match"
    val ie839MovementRejectedByCustomsConsignee = "Consignee"

    //IE905 Manual Closure of Movement Notification
    val ie905ManualClosureResponseHeading: String = "Manual closure of a movement"
    val ie905ManualClosureResponseTitle: String = titleHelper(ie905ManualClosureResponseHeading)
    val ie905ManualClosureResponseP1 = "This movement has been manually closed."

    //IE837 Delay notification
    val ie837Heading: String = "Explanation for delay submitted"
    val ie837Title: String = titleHelper(ie837Heading)
    val ie837Paragraph1: String = "An explanation for delay has been submitted."
    val ie837SubmittedBy: String = "Submitted by"
    val ie837SubmittedByValue: SubmitterType => String = {
      case Consignor => "Consignor"
      case Consignee => "Consignee"
    }
    val ie837SubmitterId: SubmitterType => String = {
      case Consignor => "Consignor’s identification number"
      case Consignee => "Consignee’s identification number"
    }
    val ie837DelayType: String = "What’s been delayed"
    val ie837DelayReason: String = "Reason for delay"

    //IE813 Movement Destination Reminder
    val ie813MovementDestinationHeading: String = "Change of destination submitted"
    val ie813MovementDestinationTitle: String = titleHelper(ie813MovementDestinationHeading)
    val ie813MovementDestinationP1 = "A change of destination has been submitted for this movement. This message shows all information that may have been updated due to the change of destination."

    //IE871 Shortage Or Excess Notification
    val ie871Heading: String = "Explanation for a shortage or excess submitted"
    val ie871Title: String = titleHelper(ie871Heading)
    val ie871Paragraph1: String = "An explanation for a shortage or excess has been submitted."

    val ie871ShortageOrExcessH2 = "Shortage or excess details"
    val ie871GlobalDate = "Date shortage or excess observed"
    val ie871ConsigneeH2 = "Consignee"
    val ie871ConsignorH2 = "Consignor"
    val ie871ItemsH2 = "Items"
    val ie871ItemH3: Int => String = "Item " + _
    val ie871epc = "Excise Product Code (EPC)"
    val ie871reference = "Body record unique reference"
    val ie871amount = "Amount received"
    val ie871explanation = "Information about shortage or excess"

    //IE881 Manual Closure Response
    val ie881ManualClosureResponseHeading: String = "Manual closure response"
    val ie881ManualClosureResponseTitle: String = titleHelper(ie881ManualClosureResponseHeading)
    val ie881ManualClosureResponseP1 = "The information below shows details of the response to the manual closure of a movement."

    val ie881ManualClosureResponseSequenceNumber = "Sequence number"
    val ie881ManualClosureResponseDateExciseProductsArrived = "Date excise products arrived"
    val ie881ManualClosureResponseConclusionOfReceipt = "Conclusion of receipt"
    val ie881ManualClosureResponseConclusionOfReceipt1 = "Receipt accepted and satisfactory"
    val ie881ManualClosureResponseConclusionOfReceipt2 = "Receipt accepted although unsatisfactory"
    val ie881ManualClosureResponseConclusionOfReceipt3 = "Receipt refused"
    val ie881ManualClosureResponseConclusionOfReceipt4 = "Receipt partially refused"
    val ie881ManualClosureResponseConclusionOfReceipt21 = "Exit accepted and satisfactory"
    val ie881ManualClosureResponseConclusionOfReceipt22 = "Exit accepted although unsatisfactory"
    val ie881ManualClosureResponseConclusionOfReceipt23 = "Exit refused"
    val ie881ManualClosureResponseMoreReceiptInformation = "More receipt information"
    val ie881ManualClosureResponseReasonCode = "Reason code"
    val ie881ManualClosureResponseReasonCode0 = "Other"
    val ie881ManualClosureResponseReasonCode1 = "Export closed but no IE518 available"
    val ie881ManualClosureResponseReasonCode2 = "Consignee no longer connected to EMCS"
    val ie881ManualClosureResponseReasonCode3 = "Exempted consignee"
    val ie881ManualClosureResponseReasonCode4 = "Exit confirmed but no IE829 submitted (IE818 out of sequence)"
    val ie881ManualClosureResponseReasonCode5 = "No movement but cancellation no longer possible"
    val ie881ManualClosureResponseReasonCode6 = "Multiple issuances of e-ADs/e-SADs for a single movement"
    val ie881ManualClosureResponseReasonCode7 = "e-AD/e-SAD does not cover actual movement"
    val ie881ManualClosureResponseReasonCode8 = "Erroneous report of receipt"
    val ie881ManualClosureResponseReasonCode9 = "Erroneous rejection of an e-AD/e-SAD"
    val ie881ManualClosureResponseReasonCodeDescription = "Reason code description"
    val ie881ManualClosureResponseMoreReasonInformation = "More reason information"
    val ie881ManualClosureResponseResponseStatus = "Response status"
    val ie881ManualClosureResponseResponseStatusFalse = "Rejected"
    val ie881ManualClosureResponseResponseStatusTrue = "Accepted"
    val ie881ManualClosureResponseRejectionReason = "Rejection reason"
    val ie881ManualClosureResponseRejectionReason0 = "Other"
    val ie881ManualClosureResponseRejectionReason1 = "Evidence provided does not justify manual closure"
    val ie881ManualClosureResponseRejectionReason2 = "Request Reason provided does not justify manual closure"
    val ie881ManualClosureResponseMoreRejectionInformation = "More rejection information"
    val ie881ManualClosureResponseDocumentHeading = "Supporting documents"
    val ie881ManualClosureResponseDocument = "Document {0}"
    val ie881ManualClosureResponseDocumentType = "Document type"
    val ie881ManualClosureResponseDocumentDescription = "Document description"
    val ie881ManualClosureResponseDocumentReference = "Document reference"
    val ie881ManualClosureResponseResponseHeading = "Response details"
    val ie881ManualClosureResponseItemLinkHidden = "for item {0}"
    val ie881ManualClosureResponseItemLink = "Item details"
    val ie881ManualClosureResponseItemH3 = "Item {0}"
    val ie881ManualClosureResponseItemEpc = "Excise Product Code (EPC)"
    val ie881ManualClosureResponseItemBodyRecordUniqueReference = "Body record unique reference"
    val ie881ManualClosureResponseItemShortageOrExcess = "Shortage or excess"
    val ie881ManualClosureResponseItemShortageOrExcessShortage = "Shortage"
    val ie881ManualClosureResponseItemShortageOrExcessExcess = "Excess"
    val ie881ManualClosureResponseItemShortageOrExcessQuantity = "Shortage or excess quantity"
    val ie881ManualClosureResponseItemRefusedQuantity = "Refused quantity"
    val ie881ManualClosureResponseItemMoreShortageOrExcessInformation = "More shortage or excess information"
    val ie881ManualClosureResponseNotProvided = "Not provided"

  }

  object English extends EventMessages with EN

}
