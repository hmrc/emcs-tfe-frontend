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
import fixtures.MovementListFixtures
import models.MovementListSearchOptions
import models.MovementSearchSelectOption.ARC
import models.MovementSortingSelectOption.ArcAscending
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination._
import viewmodels.PaginationHelper

// scalastyle:off magic.number
class PaginationHelperSpec extends SpecBase with MovementListFixtures {
  
  val onPageLoad: (String, MovementListSearchOptions) => Call = controllers.routes.ViewAllMovementsController.onPageLoad

  object Helper extends PaginationHelper()

  def baseOptions(index: Int): MovementListSearchOptions =
    MovementListSearchOptions(Some(ARC), Some("ARC123456"), ArcAscending, None, None, None, None, None, None, None, None, None, index, 10)

  def createPageItem(index: Int): PaginationItem = PaginationItem(
    href = onPageLoad(testErn, baseOptions(index)).url,
    number = Some(index.toString)
  )

  def createCurrentPageItem(index: Int): PaginationItem = PaginationItem(
    href = onPageLoad(testErn, baseOptions(index)).url,
    number = Some(index.toString),
    current = Some(true)
  )

  val ellipsis: PaginationItem = PaginationItem(
    href = "",
    ellipsis = Some(true)
  )

  def createPageLink(index: Int): PaginationLink = PaginationLink(
    href = onPageLoad(testErn, baseOptions(index)).url
  )

  ".constructPaginationForAllMovements" when {

    "1 page of movements is given" must {

      "return None" in {

        val expectedResult: Option[Pagination] = None

        val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
          pageCount = 1,
          ern = testErn,
          search = baseOptions(1)
        )

        actualResult shouldBe expectedResult
      }
    }

    "2 pages of movements are given" when {

      "the index is 1" must {

        "return a Pagination model without a previous link or ellipsis and only page items [1, 2]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createCurrentPageItem(1),
              createPageItem(2)
            )),
            previous = None,
            next = Some(createPageLink(2))
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 2,
            ern = testErn,
            search = baseOptions(1)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 2" must {

        "return a Pagination model without a next link or ellipsis and only page items [1, 2]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createCurrentPageItem(2)
            )),
            previous = Some(createPageLink(1)),
            next = None
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 2,
            ern = testErn,
            search = baseOptions(2)
          )

          actualResult shouldBe expectedResult
        }
      }
    }

    "3 pages of movements are given" when {

      "the index is 1" must {

        "return a Pagination model without a previous link or ellipsis and only page items [1, 2, 3]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createCurrentPageItem(1),
              createPageItem(2),
              createPageItem(3)
            )),
            previous = None,
            next = Some(createPageLink(2))
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 3,
            ern = testErn,
            search = baseOptions(1)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 2" must {

        "return a Pagination model without ellipsis and page items [1, 2, 3]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createCurrentPageItem(2),
              createPageItem(3)
            )),
            previous = Some(createPageLink(1)),
            next = Some(createPageLink(3))
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 3,
            ern = testErn,
            search = baseOptions(2)
          )

          actualResult shouldBe expectedResult
        }

        "the index is 3" must {

          "return a Pagination model without a next link or ellipsis and only page items [1, 2, 3]" in {

            val expectedResult: Option[Pagination] = Some(Pagination(
              items = Some(Seq(
                createPageItem(1),
                createPageItem(2),
                createCurrentPageItem(3)
              )),
              previous = Some(createPageLink(2)),
              next = None
            ))

            val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
              pageCount = 3,
              ern = testErn,
              search = baseOptions(3)
            )

            actualResult shouldBe expectedResult
          }
        }
      }
    }

    "4 pages of movements are given" when {

      "the index is 1" must {

        "return a Pagination model without a previous link and only page items [1, 2, 3, 4]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createCurrentPageItem(1),
              createPageItem(2),
              createPageItem(3),
              createPageItem(4)
            )),
            previous = None,
            next = Some(createPageLink(2))
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 4,
            ern = testErn,
            search = baseOptions(1)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 2" must {

        "return a Pagination model without ellipsis and page items [1, 2, 3, 4]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createCurrentPageItem(2),
              createPageItem(3),
              createPageItem(4)
            )),
            previous = Some(createPageLink(1)),
            next = Some(createPageLink(3))
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 4,
            ern = testErn,
            search = baseOptions(2)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 3" must {

        "return a Pagination model without ellipsis and page items 1, 2, 3 and 4" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createPageItem(2),
              createCurrentPageItem(3),
              createPageItem(4)
            )),
            previous = Some(createPageLink(2)),
            next = Some(createPageLink(4))
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 4,
            ern = testErn,
            search = baseOptions(3)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 4" must {

        "return a Pagination model without a next link and only page items 1, 2, 3 and 4" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createPageItem(2),
              createPageItem(3),
              createCurrentPageItem(4)
            )),
            previous = Some(createPageLink(3)),
            next = None
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 4,
            ern = testErn,
            search = baseOptions(4)
          )

          actualResult shouldBe expectedResult
        }
      }
    }

    "10 pages of movements are given" when {

      "the index is 1" must {

        "return a Pagination model without a previous link or ellipsis and only page items [1, 2, ellipsis, 10]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createCurrentPageItem(1),
              createPageItem(2),
              ellipsis,
              createPageItem(10)
            )),
            previous = None,
            next = Some(createPageLink(2))
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 10,
            ern = testErn,
            search = baseOptions(1)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 2" must {

        "return a Pagination model without ellipsis and page items [1, 2, 3, ellipsis, 10]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createCurrentPageItem(2),
              createPageItem(3),
              ellipsis,
              createPageItem(10)
            )),
            previous = Some(createPageLink(1)),
            next = Some(createPageLink(3))
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 10,
            ern = testErn,
            search = baseOptions(2)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 3" must {

        "return a Pagination model without ellipsis and page items [1, 2, 3, 4, ellipsis, 10]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createPageItem(2),
              createCurrentPageItem(3),
              createPageItem(4),
              ellipsis,
              createPageItem(10)
            )),
            previous = Some(createPageLink(2)),
            next = Some(createPageLink(4))
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 10,
            ern = testErn,
            search = baseOptions(3)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 4" must {

        "return a Pagination model without ellipsis and page items [1, 2, 3, 4, 5, ellipsis, 10]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createPageItem(2),
              createPageItem(3),
              createCurrentPageItem(4),
              createPageItem(5),
              ellipsis,
              createPageItem(10)
            )),
            previous = Some(createPageLink(3)),
            next = Some(createPageLink(5))
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 10,
            ern = testErn,
            search = baseOptions(4)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 5" must {

        "return a Pagination model without ellipsis and page items [1, ellipsis, 4, 5, 6, ellipsis, 10]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              ellipsis,
              createPageItem(4),
              createCurrentPageItem(5),
              createPageItem(6),
              ellipsis,
              createPageItem(10)
            )),
            previous = Some(createPageLink(4)),
            next = Some(createPageLink(6))
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 10,
            ern = testErn,
            search = baseOptions(5)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 6" must {

        "return a Pagination model without ellipsis and page items [1, ellipsis, 5, 6, 7, ellipsis, 10]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              ellipsis,
              createPageItem(5),
              createCurrentPageItem(6),
              createPageItem(7),
              ellipsis,
              createPageItem(10)
            )),
            previous = Some(createPageLink(5)),
            next = Some(createPageLink(7))
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 10,
            ern = testErn,
            search = baseOptions(6)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 7" must {

        "return a Pagination model without ellipsis and page items [1, ellipsis, 6, 7, 8, 9, 10]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              ellipsis,
              createPageItem(6),
              createCurrentPageItem(7),
              createPageItem(8),
              createPageItem(9),
              createPageItem(10)
            )),
            previous = Some(createPageLink(6)),
            next = Some(createPageLink(8))
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 10,
            ern = testErn,
            search = baseOptions(7)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 8" must {

        "return a Pagination model without ellipsis and page items [1, ellipsis, 7, 8, 9, 10]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              ellipsis,
              createPageItem(7),
              createCurrentPageItem(8),
              createPageItem(9),
              createPageItem(10)
            )),
            previous = Some(createPageLink(7)),
            next = Some(createPageLink(9))
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 10,
            ern = testErn,
            search = baseOptions(8)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 9" must {

        "return a Pagination model without ellipsis and page items [1, ellipsis, 8, 9, 10]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              ellipsis,
              createPageItem(8),
              createCurrentPageItem(9),
              createPageItem(10)
            )),
            previous = Some(createPageLink(8)),
            next = Some(createPageLink(10))
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 10,
            ern = testErn,
            search = baseOptions(9)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 10" must {

        "return a Pagination model without a next link and only page items 1, an ellipsis, 9 and 10" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              ellipsis,
              createPageItem(9),
              createCurrentPageItem(10)
            )),
            previous = Some(createPageLink(9)),
            next = None
          ))

          val actualResult: Option[Pagination] = Helper.constructPaginationForAllMovements(
            pageCount = 10,
            ern = testErn,
            search = baseOptions(10)
          )

          actualResult shouldBe expectedResult
        }
      }
    }
  }

  ".calculatePageCount" must {
    "return 1" when {
      "the number of items is 0" in {
        Helper.calculatePageCount(0, 10) mustBe 1
      }
      "the number of items is less than or equal to the max number of items per page" in {
        Helper.calculatePageCount(9, 10) mustBe 1
        Helper.calculatePageCount(10, 10) mustBe 1
        Helper.calculatePageCount(11, 10) must not be 1
      }
    }
    "return the number of pages" when {
      "the number of items is greater than the max number of items per page" in {
        Map(
          11 -> 2,
          20 -> 2,
          21 -> 3,
          30 -> 3,
          31 -> 4,
          40 -> 4,
          41 -> 5,
          50 -> 5,
          51 -> 6,
          60 -> 6
        ).foreach { case (numberOfItems, expectedPageCount) =>
          Helper.calculatePageCount(numberOfItems, 10) mustBe expectedPageCount
        }
      }
    }
  }
}
