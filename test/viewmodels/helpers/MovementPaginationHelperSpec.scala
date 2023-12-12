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

package viewmodels.helpers

import base.SpecBase
import controllers.routes.ViewAllMovementsController.onPageLoad
import fixtures.MovementListFixtures
import models.MovementListSearchOptions
import models.MovementSortingSelectOption.ArcAscending
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination._
import viewmodels.MovementPaginationHelper

class MovementPaginationHelperSpec extends SpecBase with MovementListFixtures {

  object Helper extends MovementPaginationHelper()

  def createPageItem(index: Int): PaginationItem = PaginationItem(
    href = onPageLoad(testErn, MovementListSearchOptions(ArcAscending, index, 10)).url,
    number = Some(index.toString)
  )

  def createPageLink(index: Int): PaginationLink = PaginationLink(
    href = onPageLoad(testErn, MovementListSearchOptions(ArcAscending, index, 10)).url
  )

  ".constructPagination" when {

    "only one page of movements is given" must {

      "return None" in {

        val expectedResult: Option[Pagination] = None

        val actualResult: Option[Pagination] = Helper.constructPagination(
          pageCount = 1,
          ern = testErn,
          search = MovementListSearchOptions(ArcAscending, 1, 10)
        )

        actualResult shouldBe expectedResult
      }
    }

    "two pages of movements are given" when {

      "the index is 1" must {

        "return a Pagination model without a previous link" in {

          val completePaginationObject: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createPageItem(2)
            )),
            previous = None,
            next = Some(createPageLink(2))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 2,
            ern = testErn,
            search = MovementListSearchOptions(ArcAscending, 1, 10)
          )

          actualResult shouldBe completePaginationObject
        }
      }

      "the index is 2" must {

        "return a Pagination model without a next link" in {

          val completePaginationObject: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createPageItem(2)
            )),
            previous = Some(createPageLink(1)),
            next = None
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 2,
            ern = testErn,
            search = MovementListSearchOptions(ArcAscending, 2, 10)
          )

          actualResult shouldBe completePaginationObject
        }
      }
    }

    "three pages of movements are given" when {

      "the index is 1" must {

        "return a Pagination model without a previous link" in {

          val completePaginationObject: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createPageItem(2),
              createPageItem(3)
            )),
            previous = None,
            next = Some(createPageLink(2))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 3,
            ern = testErn,
            search = MovementListSearchOptions(ArcAscending, 1, 10)
          )

          actualResult shouldBe completePaginationObject
        }
      }

      "the index is 2" must {

        "return a Pagination model both a previous and next link" in {

          val completePaginationObject: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createPageItem(2),
              createPageItem(3)
            )),
            previous = Some(createPageLink(1)),
            next = Some(createPageLink(3))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 3,
            ern = testErn,
            search = MovementListSearchOptions(ArcAscending, 2, 10)
          )

          actualResult shouldBe completePaginationObject
        }
      }

      "the index is 3" must {

        "return a Pagination model without a next link" in {

          val completePaginationObject: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createPageItem(2),
              createPageItem(3)
            )),
            previous = Some(createPageLink(2)),
            next = None
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 3,
            ern = testErn,
            search = MovementListSearchOptions(ArcAscending, 3, 10)
          )

          actualResult shouldBe completePaginationObject
        }
      }
    }

    "four pages of movements are given" when {

      "the index is 1" must {

        "return a Pagination model without a previous link" in {

          val completePaginationObject: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createPageItem(2),
              createPageItem(3),
              createPageItem(4)
            )),
            previous = None,
            next = Some(createPageLink(2))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 4,
            ern = testErn,
            search = MovementListSearchOptions(ArcAscending, 1, 10)
          )

          actualResult shouldBe completePaginationObject
        }
      }

      "the index is 2" must {

        "return a Pagination model both a previous and next link" in {

          val completePaginationObject: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createPageItem(2),
              createPageItem(3),
              createPageItem(4)
            )),
            previous = Some(createPageLink(1)),
            next = Some(createPageLink(3))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 4,
            ern = testErn,
            search = MovementListSearchOptions(ArcAscending, 2, 10)
          )

          actualResult shouldBe completePaginationObject
        }
      }

      "the index is 3" must {

        "return a Pagination model both a previous and next link" in {

          val completePaginationObject: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createPageItem(2),
              createPageItem(3),
              createPageItem(4)
            )),
            previous = Some(createPageLink(2)),
            next = Some(createPageLink(4))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 4,
            ern = testErn,
            search = MovementListSearchOptions(ArcAscending, 3, 10)
          )

          actualResult shouldBe completePaginationObject
        }
      }

      "the index is 4" must {

        "return a Pagination model without a next link" in {

          val completePaginationObject: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createPageItem(2),
              createPageItem(3),
              createPageItem(4)
            )),
            previous = Some(createPageLink(3)),
            next = None
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 4,
            ern = testErn,
            search = MovementListSearchOptions(ArcAscending, 4, 10)
          )

          actualResult shouldBe completePaginationObject
        }
      }
    }
  }
}
