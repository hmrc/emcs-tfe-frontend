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
import models.draftMovements.GetDraftMovementsSearchOptions
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.Pagination
import utils.{DateUtils, PaginationUtil}

import javax.inject.Inject

class DraftMovementsPaginationHelper @Inject()() extends DateUtils {

  def constructPagination(pageCount: Int, ern: String, search: GetDraftMovementsSearchOptions): Option[Pagination] = {

    val paginationHelper = new PaginationUtil {
      override val link: Int => String = (index: Int) => routes.ViewAllDraftMovementsController.onPageLoad(ern, search.copy(index = index)).url
      override val currentPage: Int = search.index
      override val pages: Int = pageCount
    }

    paginationHelper.constructPagination()
  }
}
