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

import models.movementScenario.MovementScenario
import views.ViewUtils.LocalDateExtensions

import java.time.LocalDate

object ViewAllDraftMovementsMessages {

  sealed trait ViewMessages { _: i18n =>
    val title: String = "Drafts"
    val heading: String = "Drafts"
    def dateOfDispatch(string: String): String = s"Date of dispatch: $string"
    val sortByLabel = "Sort by"
    val sortByButton = "Sort movements"
    val sortLrnAscending = "LRN (A-Z)"
    val sortLrnDescending = "LRN (Z-A)"
    val sortNewest = "Last updated (newest)"
    val sortOldest = "Last updated (oldest)"
    val previous: String = "Previous"
    val next: String = "Next"

    val searchHeading = "Search for a draft"
    val searchText = "You can search by Local Reference Number (LRN), consignee name, Excise Reference Number (ERN) or tax warehouse ERN."
    val searchInputHiddenLabel = "Search for a draft"
    val searchButton = "Search"

    val filtersHeading = "Filters"
    val filtersButton = "Apply filters"
    val filtersErrors = "Errors"
    val filtersErrorsOption1 = "Draft has errors"
    val filtersDestinationType = "Destination type"
    val filtersDestinationTypeOption1 = "Tax warehouse"
    val filtersDestinationTypeOption2 = "Registered consignee"
    val filtersDestinationTypeOption3 = "Temporary registered consignee"
    val filtersDestinationTypeOption4 = "Exempted organisation"
    val filtersDestinationTypeOption5 = "Direct delivery"
    val filtersDestinationTypeOption6 = "Unknown destination"
    val filtersDestinationTypeOption7 = "Export"
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
