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
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.Pagination
import utils.{DateUtils, PaginationUtil}

import javax.inject.Inject

class PaginationHelper @Inject()() extends DateUtils {

  def constructPaginationForAllMovements(pageCount: Int, ern: String, search: MovementListSearchOptions): Option[Pagination] = {

    val paginationHelper = new PaginationUtil {
      override val link: Int => String = (index: Int) => routes.ViewAllMovementsController.onPageLoad(ern, search.copy(index = index)).url
      override val currentPage: Int = search.index
      override val pages: Int = pageCount
    }

    paginationHelper.constructPagination()
  }

  def calculatePageCount(numberOfItems: Int, maxNumberOfItemsPerPage: Int): Int = numberOfItems match {
    case 0 => 1
    case count if count % maxNumberOfItemsPerPage != 0 => (numberOfItems / maxNumberOfItemsPerPage) + 1
    case _ => numberOfItems / maxNumberOfItemsPerPage
  }

  def constructPaginationForDraftTemplates(ern: String, page: Int, pageCount: Int): Option[Pagination] = {
    val paginationHelper = new PaginationUtil {
      override val link: Int => String =
        (index: Int) => controllers.draftTemplates.routes.ViewAllTemplatesController.onPageLoad(ern, Some(index)).url

      override val currentPage: Int = page
      override val pages: Int = pageCount
    }

    paginationHelper.constructPagination()
  }
}
