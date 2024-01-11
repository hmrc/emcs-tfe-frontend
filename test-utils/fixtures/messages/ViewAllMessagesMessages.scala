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

object ViewAllMessagesMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String = "Messages"
    val heading: String = "Messages"

    val sortByLabel = "Sort by"
    val sortMessageTypeA = "Message (A - Z)"
    val sortMessageTypeD = "Message (Z - A)"
    val sortDateReceivedA = "Received (oldest)"
    val sortDateReceivedD = "Received (newest)"
    val sortArcA = "Arc (A - Z)"
    val sortArcD = "Arc (Z - A)"
    val sortReadIndicatorA = "Read indicator (A - Z)"
    val sortReadIndicatorD = "Read indicator (Z - A)"

    val tableMessageHeading = "Message"
    val tableMessageStatus = "Status"
    val tableMessageDate = "Date"
    val tableMessageAction = "Action"
  }

  object English extends ViewMessages with EN
}
