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

object MovementEventMessages {

  sealed trait EventMessages extends BaseEnglish { _ : i18n =>

    val messageIssued = "Message issued 4 December 2024 at 5:00 pm"
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

    //IE803 Movement Split Notification
    val ie803MovementSplitHeading: String = "Movement split"
    val ie803MovementSplitTitle: String = titleHelper(ie803MovementSplitHeading)
    def ie803MovementSplitP1(dateOfSplit: String) = s"A request to split this movement was submitted on $dateOfSplit."
    val ie803MovementSplitP2 = "The new ARCs for the split movement are:"
  }

  object English extends EventMessages with EN

}
