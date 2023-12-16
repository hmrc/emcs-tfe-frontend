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

object ViewAllMovementsMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String = "Movements"
    val heading: String = "Movements"
    def consignor(string: String): String = s"Consignor: $string"
    def dateOfDispatch(string: String): String = s"Date of dispatch: $string"
    val sortByLabel = "Sort by"
    val sortByButton = "Sort movements"
    val sortArcAscending = "ARC (A-Z)"
    val sortArcDescending = "ARC (Z-A)"
    val sortNewest = "Dispatched (newest)"
    val sortOldest = "Dispatched (oldest)"
    val previous: String = "Previous"
    val next: String = "Next"


    val searchKeyArc = "ARC"
    val searchKeyLrn = "LRN"
    val searchKeyErn = "ERN"
    val searchKeyTransporter = "Transporter"

    val searchHeading = "Search for a movement"
    val searchText = "You can search by Administrative Reference Code (ARC), Local Reference Number (LRN), trader Excise Reference Number (ERN) or transporter name."
    val searchInputHiddenLabel = "Search for a movement"
    val searchSelectHiddenLabel = "Search by"
    val searchSelectARC = "ARC"
    val searchSelectLRN = "LRN"
    val searchSelectERN = "ERN"
    val searchSelectTransporter = "Transporter"

  }

  object English extends ViewMessages with EN
}
