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

package uk.gov.hmrc.emcstfefrontend.viewmodels

import uk.gov.hmrc.emcstfefrontend.controllers.routes
import uk.gov.hmrc.emcstfefrontend.models.MovementListSearchOptions
import uk.gov.hmrc.emcstfefrontend.utils.DateUtils
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.{Pagination, PaginationItem, PaginationLink}

import javax.inject.Inject

class MovementPaginationHelper @Inject()() extends DateUtils {

  def constructPagination(pageCount: Int, ern: String, search: MovementListSearchOptions): Option[Pagination] = {

    if(pageCount == 1) None else {

      val paginationItems = (1 to pageCount).map { i =>
        PaginationItem(
          href = routes.ViewAllMovementsController.onPageLoad(ern, search.copy(index = i)).url,
          number = Some(i.toString)
        )
      }

      val previousLink = if (search.index == 1) None else {
        Some(PaginationLink(routes.ViewAllMovementsController.onPageLoad(ern, search.copy(index = search.index - 1)).url))
      }

      val nextLink = if (search.index == pageCount) None else {
        Some(PaginationLink(routes.ViewAllMovementsController.onPageLoad(ern, search.copy(index = search.index + 1)).url))
      }

      Some(Pagination(Some(paginationItems), previousLink, nextLink))
    }
  }
}
