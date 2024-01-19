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

import controllers.routes
import models.MovementListSearchOptions
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.{Pagination, PaginationItem, PaginationLink}
import utils.DateUtils

import javax.inject.Inject

class MovementPaginationHelper @Inject()() extends DateUtils {

  def constructPagination(pageCount: Int, ern: String, search: MovementListSearchOptions): Option[Pagination] = {

    if (pageCount == 1) None else {

      val previousLink: Option[PaginationLink] = if (search.index == 1) None else Some(PaginationLink(
        href = routes.ViewAllMovementsController.onPageLoad(ern, search.copy(index = search.index - 1)).url
      ))

      val firstItem: Option[PaginationItem] = if (search.index <= 1) None else Some(PaginationItem(
        href = routes.ViewAllMovementsController.onPageLoad(ern, search.copy(index = 1)).url,
        number = Some("1")
      ))

      val previousEllipses: Option[PaginationItem] = if (search.index <= 3) None else Some(PaginationItem(
        href = "",
        ellipsis = Some(true)
      ))

      val previousItem: Option[PaginationItem] = if (search.index <= 2) None else Some(PaginationItem(
        href = routes.ViewAllMovementsController.onPageLoad(ern, search.copy(index = search.index - 1)).url,
        number = Some((search.index - 1).toString)
      ))

      val currentItem: Option[PaginationItem] = Some(PaginationItem(
        href = routes.ViewAllMovementsController.onPageLoad(ern, search.copy(index = search.index)).url,
        number = Some(search.index.toString),
        current = Some(true)
      ))

      val nextItem: Option[PaginationItem] = if ((pageCount - search.index) <= 1) None else Some(PaginationItem(
        href = routes.ViewAllMovementsController.onPageLoad(ern, search.copy(index = search.index + 1)).url,
        number = Some((search.index + 1).toString)
      ))

      val nextEllipses: Option[PaginationItem] = if ((pageCount - search.index) <= 2) None else Some(PaginationItem(
        href = "",
        ellipsis = Some(true)
      ))

      val lastItem: Option[PaginationItem] = if (search.index >= pageCount) None else Some(PaginationItem(
        href = routes.ViewAllMovementsController.onPageLoad(ern, search.copy(index = pageCount)).url,
        number = Some(pageCount.toString)
      ))

      val nextLink = if (search.index == pageCount) None else Some(PaginationLink(
        href = routes.ViewAllMovementsController.onPageLoad(ern, search.copy(index = search.index + 1)).url)
      )

      val paginationItems = Seq(
        firstItem,
        previousEllipses,
        previousItem,
        currentItem,
        nextItem,
        nextEllipses,
        lastItem
      ).flatten

      Some(Pagination(
        items = Some(paginationItems),
        previous = previousLink,
        next = nextLink
      ))
    }
  }
}
