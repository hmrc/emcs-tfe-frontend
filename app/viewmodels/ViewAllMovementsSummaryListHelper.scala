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
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.Pagination
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow, Value}
import utils.DateUtils
import viewmodels.govuk.TagFluency
import views.html.viewAllMovements.MovementSummaryListKeyContent

import javax.inject.Inject

class ViewAllMovementsSummaryListHelper @Inject()(keyContent: MovementSummaryListKeyContent) extends DateUtils with TagFluency {

  def constructSummaryListRows(ern: String, movements: Seq[GetMovementListItem], directionFilterOption: MovementFilterDirectionOption)
                    (implicit messages: Messages): Seq[SummaryListRow] =
    movements.map { movement =>
      SummaryListRow(
        Key(
          HtmlContent(keyContent(ern, movement, directionFilterOption)),
          classes = "govuk-!-width-two-thirds"
        ),
        Value(
          HtmlContent(new GovukTag().apply(movement.statusTag())),
          classes = "govuk-!-text-align-right"
        )
      )
    }

  def generatePageTitle(totalMovements: Int, currentFilters: MovementListSearchOptions, pagination: Option[Pagination])
                       (implicit messages: Messages): String =
    generatePageTitle(
      totalMovements = totalMovements,
      searchValue = currentFilters.searchValue,
      sortByDisplayName = currentFilters.sortBy.displayName,
      hasFilterApplied = currentFilters.hasFilterApplied,
      pagination = pagination
    )

  def generatePageTitle(totalMovements: Int, currentFilters: GetDraftMovementsSearchOptions, pagination: Option[Pagination])
                       (implicit messages: Messages): String =
    generatePageTitle(
      totalMovements = totalMovements,
      searchValue = currentFilters.searchValue,
      sortByDisplayName = currentFilters.sortBy.displayName,
      hasFilterApplied = currentFilters.hasFilterApplied,
      pagination = pagination
    )

  private[viewmodels] def generatePageTitle(
                                             totalMovements: Int,
                                             searchValue: Option[String],
                                             sortByDisplayName: String,
                                             hasFilterApplied: Boolean,
                                             pagination: Option[Pagination]
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
      case _ => {
        val pageTermMessage = pagination.flatMap {
          case Pagination(Some(items), _, _, _, _, _) if items.size > 1 =>
            items.find(_.current.contains(true)).flatMap(_.number).map { currentPageNumber =>
              s" ${messages("viewAllMovements.pageNumberTerm", currentPageNumber.toInt, items.size)}"
            }
          case _ => None
        }.getOrElse("")

        messages(s"viewAllMovements.resultsFound", totalMovements, filteredMessage, pageTermMessage, searchTermMessage, sortByMessage)
      }
    }
  }

}
