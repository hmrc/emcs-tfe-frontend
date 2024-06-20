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

  sealed trait EventMessages extends BaseEnglish { _ : i18n =>

    def messageIssued(dateTime: LocalDateTime = LocalDateTime.of(2024, 12, 4, 17, 0, 0)) =
      s"Message issued ${dateTime.formatDateTimeForUIOutput()}"

    def arc(arc: String) = s"ARC: $arc"
    val printScreenContent: String = "Print this screen to make a record of this message."

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
  }

  object English extends EventMessages with EN

}
