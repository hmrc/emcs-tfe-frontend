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

package utils

import uk.gov.hmrc.govukfrontend.views.Aliases.{Pagination, PaginationLink}
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.PaginationItem

trait PaginationUtil {

  val link: Int => String
  val currentPage: Int
  val pages: Int

  def constructPagination(): Option[Pagination] =
    Option.when(pages > 1) {
      Pagination(
        items = Some(Seq(
          calculatePageLinks(1 until currentPage),
          Seq(pageItem(currentPage, link, isCurrent = Some(true))),
          calculatePageLinks(currentPage + 1 to pages)
        ).flatten),
        previous = Option.when(currentPage > 1)(PaginationLink(link(currentPage - 1))),
        next = Option.when(currentPage < pages)(PaginationLink(link(currentPage + 1)))
      )
    }

  private def pageItem(currentPage: Int, link: Int => String, isCurrent: Option[Boolean] = None): PaginationItem = PaginationItem(
    href = link(currentPage),
    number = Some(currentPage.toString),
    current = isCurrent
  )

  private def calculatePageLinks(indexes: Seq[Int]): Seq[PaginationItem] =
    if (indexes.size <= 3) indexes.map(pageItem(_, link)) else {
      Seq(pageItem(indexes.head, link), PaginationItem(href = "", ellipsis = Some(true)), pageItem(indexes.last, link))
    }

}
