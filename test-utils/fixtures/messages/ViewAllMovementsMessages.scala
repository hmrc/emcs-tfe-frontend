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

import fixtures.messages.BaseEnglish.titleHelper

object ViewAllMovementsMessages {

  sealed trait ViewMessages { _: i18n =>
    val heading: String = "Movements"
    val title: String = titleHelper(heading)
    def headingWithCount(count: Int): String = s"$count results found - Movements"
    def titleWithCount(count: Int): String = titleHelper(headingWithCount(count))

    def headingWithSearch(count: Int, search: String): String = s"$count results found - Search results for $search"
    def titleWithSearch(count: Int, search: String): String = titleHelper(headingWithSearch(count, search))

    def dateOfDispatch(string: String): String = s"Date of dispatch: $string"
    val sortByLabel = "Sort by"
    val sortByButton = "Sort movements"
    val sortArcAscending = "ARC (A-Z)"
    val sortArcDescending = "ARC (Z-A)"
    val sortNewest = "Dispatched (newest)"
    val sortOldest = "Dispatched (oldest)"
    val previous: String = "Previous"
    val next: String = "Next"

    val searchHeading = "Search for a movement"
    val searchText = "You can search by Administrative Reference Code (ARC), Local Reference Number (LRN), trader Excise Reference Number (ERN) or transporter name."
    val searchInputHiddenLabel = "Search for a movement"
    val searchSelectHiddenLabel = "Search by"
    val searchSelectARC = "ARC"
    val searchSelectLRN = "LRN"
    val searchSelectERN = "ERN"
    val searchSelectTransporter = "Transporter"
    val searchButton = "Search"

    val skipToResults = "Skip to results"
    def resultsFound(num: Int): String = s"$num results found"
    val noResultsFound = "No results found"

    val filtersHeading = "Filters"
    val filtersButton = "Apply filters"
    val filtersDirection = "Direction"
    val filtersDirectionOption1 = "Goods in"
    val filtersDirectionOption2 = "Goods out"
    val filtersUndischarged = "Undischarged"
    val filtersUndischargedOption1 = "Undischarged"
    val filtersStatus = "Status"
    val filtersStatusChoose = "Choose status"
    val filtersStatusActive = "Active"
    val filtersStatusCancelled = "Cancelled"
    val filtersStatusDeemedExported = "Deemed exported"
    val filtersStatusDelivered = "Delivered"
    val filtersStatusDiverted = "Diverted"
    val filtersStatusExporting = "Exporting"
    val filtersStatusManuallyClosed = "Manually closed"
    val filtersStatusPartiallyRefused = "Partially refused"
    val filtersStatusRefused = "Refused"
    val filtersStatusReplaced = "Replaced"
    val filtersStatusRejected = "Rejected"
    val filtersStatusStopped = "Stopped"
    val filtersEpc = "Excise product code"
    val filtersEpcChoose = "Choose item"
    val filtersCountry = "Country of origin"
    val filtersCountryChoose = "Choose country"
    val filtersDispatchedFrom = "Dispatched from"
    val filtersDispatchedTo = "Dispatched to"
    val filtersReceiptedFrom = "Receipted from"
    val filtersReceiptedTo = "Receipted to"
    val filtersDay = "Day"
    val filtersMonth = "Month"
    val filtersYear = "Year"

    val movementConsignee: String => String = ern => s"Consignee: $ern"
    val movementConsignor: String => String = ern => s"Consignor: $ern"
    val movementOtherTraderId: String => String = ern => s"Other trader ID: $ern"

  }

  object English extends ViewMessages with EN
}
