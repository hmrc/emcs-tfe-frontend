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

import models.MovementListSearchOptions
import org.scalamock.handlers.{CallHandler2, CallHandler3}
import org.scalamock.scalatest.MockFactory
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.Pagination
import viewmodels.PaginationHelper

trait MockPaginationHelper extends MockFactory with GuiceOneAppPerSuite {

  lazy val mockPaginationHelper: PaginationHelper = mock[PaginationHelper]
  lazy val actualPaginationHelper: PaginationHelper = app.injector.instanceOf[PaginationHelper]

  object MockPaginationHelper {

    def constructPaginationForAllMovements(index: Int, pageCount: Int)
                                          (returns: Option[Pagination] = None): CallHandler3[Int, String, MovementListSearchOptions, Option[Pagination]] =
      (mockPaginationHelper.constructPaginationForAllMovements(_: Int, _: String, _: MovementListSearchOptions))
        .expects(pageCount, *, MovementListSearchOptions(index = index))
        .returns(returns)

    def constructPaginationForDraftTemplates(index: Int, pageCount: Int)
                                          (returns: Option[Pagination] = None): CallHandler3[String, Int, Int, Option[Pagination]] =
      (mockPaginationHelper.constructPaginationForDraftTemplates(_: String, _: Int, _: Int))
        .expects(*, index, pageCount)
        .returns(returns)

    def constructPaginationForAllMovementsWithSearch(searchOptions: MovementListSearchOptions, pageCount: Int)
                                                    (returns: Option[Pagination] = None): CallHandler3[Int, String, MovementListSearchOptions, Option[Pagination]] =
      (mockPaginationHelper.constructPaginationForAllMovements(_: Int, _: String, _: MovementListSearchOptions))
        .expects(pageCount, *, searchOptions)
        .returns(returns)

    def calculatePageCount(numberOfItems: Int, maxNumberOfItemsPerPage: Int): CallHandler2[Int, Int, Int] =
      (mockPaginationHelper.calculatePageCount(_: Int, _: Int))
        .expects(numberOfItems, maxNumberOfItemsPerPage)
        .returns(actualPaginationHelper.calculatePageCount(numberOfItems, maxNumberOfItemsPerPage))
  }
}
