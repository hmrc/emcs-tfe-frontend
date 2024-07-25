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
import models.movementScenario.MovementScenario
import views.ViewUtils.LocalDateExtensions

import java.time.LocalDate

object ViewAllDraftMovementsMessages {

  sealed trait ViewMessages { _: i18n =>
    val heading: String = "Drafts"
    val title: String = titleHelper(heading)

    val headingWithNoResults: String = "No results found for beans - Drafts"
    val titleWithNoResults: String = titleHelper(headingWithNoResults)
    val headingWithOneResult: String = "1 result found for beans sorted by Last updated (newest) - Drafts"
    val titleWithOneResult: String = titleHelper(headingWithOneResult)
    def headingWithCount(count: Int): String = s"$count results found for beans sorted by Last updated (newest) - Drafts"
    def titleWithCount(count: Int): String = titleHelper(headingWithCount(count))

    def dateOfDispatch(string: String): String = s"Date of dispatch: $string"
    val sortByLabel = "Sort by"
    val sortByButton = "Sort movements"
    val sortLrnAscending = "LRN (A-Z)"
    val sortLrnDescending = "LRN (Z-A)"
    val sortNewest = "Last updated (newest)"
    val sortOldest = "Last updated (oldest)"
    val previous: String = "Previous"
    val next: String = "Next"

    val draftDeletedNotificationBannerTitle: String = "Success"
    def draftDeletedNotificationBannerContent(lrn: String): String = s"Draft movement deleted successfully: $lrn"

    val searchHeading = "Search for a draft"
    val searchText = "You can search by Local Reference Number (LRN), consignee name, Excise Reference Number (ERN) or tax warehouse ERN."
    val searchInputHiddenLabel = "Search for a draft"
    val searchButton = "Search"

    val createNewMovement = "Create a new movement"

    val skipToResults = "Skip to results"
    def resultsFound(num: Int): String = s"$num results found"
    val noResultsFound = "No results found"

    val filtersHeading = "Filters"
    val applyFiltersButton = "Apply filters"
    val clearFiltersLink = "Clear filters"
    val filtersErrors = "Errors"
    val filtersErrorsOption1 = "Draft has errors"
    val filtersDestinationType = "Destination type"
    val filtersDestinationTypeOption1 = "Tax warehouse"
    val filtersDestinationTypeOption2 = "Registered consignee"
    val filtersDestinationTypeOption3 = "Temporary registered consignee"
    val filtersDestinationTypeOption4 = "Direct delivery"
    val filtersDestinationTypeOption5 = "Exempted organisation"
    val filtersDestinationTypeOption6 = "Export"
    val filtersDestinationTypeOption7 = "Unknown destination"
    val filtersDestinationTypeOption8 = "Certified consignee"
    val filtersDestinationTypeOption9 = "Temporary certified consignee"
    val filtersExciseProduct = "Excise product code"
    val filtersExciseProductChoose = "Choose product code"
    val filtersDispatchedFrom = "Dispatched from"
    val filtersDispatchedTo = "Dispatched to"
    val filtersReceiptedFrom = "Receipted from"
    val filtersReceiptedTo = "Receipted to"
    val filtersDay = "Day"
    val filtersMonth = "Month"
    val filtersYear = "Year"

    val statusDraft = "Draft"
    val statusError = "Error"

    val destinationRowContent = (scenario: MovementScenario) => s"Destination: ${DestinationMessages.English.destinationType(scenario.destinationType)}"
    val consigneeRowContent: String => String = "Consignee: " + _
    val dispatchDateRowContent: LocalDate => String = s"Date of dispatch: " + _.formatDateForUIOutput()

  }

  object English extends ViewMessages with EN
}
