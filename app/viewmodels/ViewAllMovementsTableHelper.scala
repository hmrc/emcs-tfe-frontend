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

package viewmodels

import models.draftMovements.GetDraftMovementsSearchOptions
import models.response.emcsTfe.GetMovementListItem
import models.{MovementFilterDirectionOption, MovementListSearchOptions}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.html.components.GovukTag
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{Table, TableRow}
import utils.DateUtils
import viewmodels.govuk.TagFluency
import views.html.viewAllMovements.MovementTableRowContent

import javax.inject.Inject

class ViewAllMovementsTableHelper @Inject()(movementTableRowContent: MovementTableRowContent) extends DateUtils with TagFluency {

  private[viewmodels] def dataRows(ern: String, movements: Seq[GetMovementListItem], directionFilterOption: MovementFilterDirectionOption)
                                  (implicit messages: Messages): Seq[Seq[TableRow]] =
    movements.map { movement =>
      Seq(
        TableRow(
          content = HtmlContent(movementTableRowContent(ern, movement, directionFilterOption)),
          colspan = Some(70),
          classes = "word-wrap-break-word"
        ),
        TableRow(
          content = HtmlContent(new GovukTag().apply(movement.statusTag())),
          classes = "govuk-!-text-align-right",
          colspan = Some(30)
        )
      )
    }

  def constructTable(ern: String, movements: Seq[GetMovementListItem], directionFilterOption: MovementFilterDirectionOption)
                    (implicit messages: Messages): Table =
    Table(
      caption = Some(messages("viewAllMovements.table.caption")),
      captionClasses = "govuk-visually-hidden",
      rows = dataRows(ern, movements, directionFilterOption)(messages),
      classes = "table-fixed"
    )

  def generatePageTitle(totalMovements: Int, currentFilters: MovementListSearchOptions)
                       (implicit messages: Messages): String =
    generatePageTitle(
      totalMovements = totalMovements,
      searchValue = currentFilters.searchValue,
      sortByDisplayName = currentFilters.sortBy.displayName,
      hasFilterApplied = currentFilters.hasFilterApplied,
    )

  def generatePageTitle(totalMovements: Int, currentFilters: GetDraftMovementsSearchOptions)
                       (implicit messages: Messages): String =
    generatePageTitle(
      totalMovements = totalMovements,
      searchValue = currentFilters.searchValue,
      sortByDisplayName = currentFilters.sortBy.displayName,
      hasFilterApplied = currentFilters.hasFilterApplied,
    )

  private[viewmodels] def generatePageTitle(
                                             totalMovements: Int,
                                             searchValue: Option[String],
                                             sortByDisplayName: String,
                                             hasFilterApplied: Boolean,
                                           )(implicit messages: Messages): String = {
    val filteredMessage: String = if (hasFilterApplied) messages("viewAllMovements.filtered") else ""
    val sortByMessage: String = messages(sortByDisplayName)

    val searchTermMessage = searchValue match {
      case Some(searchTerm) => messages(s"viewAllMovements.searchTerm", searchTerm)
      case None => ""
    }

    totalMovements match {
      case 0 => messages(s"viewAllMovements.noResultsFound", "", filteredMessage, searchTermMessage)
      case 1 => messages(s"viewAllMovements.resultFound", "", filteredMessage, searchTermMessage, sortByMessage)
      case _ => messages(s"viewAllMovements.resultsFound", totalMovements, filteredMessage, searchTermMessage, sortByMessage)
    }
  }
}
