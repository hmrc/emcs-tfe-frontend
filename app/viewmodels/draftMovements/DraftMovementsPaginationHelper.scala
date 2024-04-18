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

package viewmodels.draftMovements

import controllers.drafts.routes
import models.GetDraftMovementsSearchOptions
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.{Pagination, PaginationItem, PaginationLink}
import utils.DateUtils

import javax.inject.Inject

class DraftMovementsPaginationHelper @Inject()() extends DateUtils {

  def constructPagination(pageCount: Int, ern: String, search: GetDraftMovementsSearchOptions): Option[Pagination] = {

    def pageItem(index: Int, isCurrent: Option[Boolean] = None) = PaginationItem(
      href = routes.ViewAllDraftMovementsController.onPageLoad(ern, search.copy(index = index)).url,
      number = Some(index.toString),
      current = isCurrent
    )

    val ellipses = PaginationItem(href = "", ellipsis = Some(true))

    if (pageCount == 1) None else {

      def calculateEllipses(indexes: Seq[Int]): Seq[PaginationItem] =
        if (indexes.size <= 3) indexes.map(pageItem(_)) else {
          Seq(
            pageItem(indexes.head),
            ellipses,
            pageItem(indexes.last)
          )
        }

      val paginationItems = Seq(
        calculateEllipses(1 until search.index),
        Seq(pageItem(search.index, isCurrent = Some(true))),
        calculateEllipses(search.index + 1 to pageCount)
      ).flatten

      Some(Pagination(
        items = Some(paginationItems),
        previous = Option.when(search.index > 1)(PaginationLink(
          href = routes.ViewAllDraftMovementsController.onPageLoad(ern, search.copy(index = search.index - 1)).url
        )),
        next = Option.when(search.index < pageCount)(PaginationLink(
          href = routes.ViewAllDraftMovementsController.onPageLoad(ern, search.copy(index = search.index + 1)).url
        ))
      ))
    }
  }
}
