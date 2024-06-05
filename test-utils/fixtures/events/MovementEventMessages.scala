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

    //IE802 Change Destination
    val ie802ChangeDestinationHeading: String = "Reminder to provide change of destination"
    val ie802ChangeDestinationTitle: String = titleHelper(ie802ChangeDestinationHeading)
    val ie802ChangeDestinationP1 = "A change of destination for this movement must be submitted."
  }

  object English extends EventMessages with EN

}
