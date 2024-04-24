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

package mocks.viewmodels

import models.draftMovements.GetDraftMovementsSearchOptions
import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.Pagination
import viewmodels.draftMovements.DraftMovementsPaginationHelper

trait MockDraftMovementsPaginationHelper extends MockFactory {

  lazy val mockDraftMovementsPaginationHelper: DraftMovementsPaginationHelper = mock[DraftMovementsPaginationHelper]

  object MockMovementPaginationHelper {

    def constructPagination(index: Int, pageCount: Int)
                           (returns: Option[Pagination] = None): CallHandler3[Int, String, GetDraftMovementsSearchOptions, Option[Pagination]] =
      (mockDraftMovementsPaginationHelper.constructPagination(_: Int, _: String, _: GetDraftMovementsSearchOptions))
        .expects(pageCount, *, GetDraftMovementsSearchOptions(index = index))
        .returns(returns)
  }
}
