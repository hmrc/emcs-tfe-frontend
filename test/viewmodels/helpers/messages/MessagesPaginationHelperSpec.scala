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

package viewmodels.helpers.messages

import base.SpecBase
import fixtures.MessagesFixtures
import models.messages.MessagesSearchOptions
import models.messages.MessagesSortingSelectOption.DateReceivedD
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination._

class MessagesPaginationHelperSpec extends SpecBase with MessagesFixtures {

  object Helper extends MessagesPaginationHelper()

  def createPageItem(index: Int, isCurrentPage: Boolean = false): PaginationItem = PaginationItem(
    href = controllers.messages.routes.ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions(DateReceivedD, index, 10)).url,
    number = Some(index.toString),
    current = if (isCurrentPage) Some(true) else None
  )

  val ellipsis: PaginationItem = PaginationItem(
    href = "",
    ellipsis = Some(true)
  )

  def createPageLink(index: Int): PaginationLink = PaginationLink(
    href = controllers.messages.routes.ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions(DateReceivedD, index, 10)).url
  )

  ".constructPagination" when {

    "1 page of messages is given" must {

      "return None" in {

        val actualResult: Option[Pagination] = Helper.constructPagination(
          pageCount = 1,
          ern = testErn,
          search = MessagesSearchOptions(DateReceivedD, 1, 10)
        )

        actualResult shouldBe None
      }
    }

    "2 pages of messages are given" when {

      "the index is 1" must {

        "return a Pagination model without a previous link or ellipsis and only page items [1, 2]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1, true),
              createPageItem(2)
            )),
            previous = None,
            next = Some(createPageLink(2))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 2,
            ern = testErn,
            search = MessagesSearchOptions(DateReceivedD, 1, 10)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 2" must {

        "return a Pagination model without a next link or ellipsis and only page items [1, 2]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createPageItem(2, true)
            )),
            previous = Some(createPageLink(1)),
            next = None
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 2,
            ern = testErn,
            search = MessagesSearchOptions(DateReceivedD, 2, 10)
          )

          actualResult shouldBe expectedResult
        }
      }
    }

    "3 pages of messages are given" when {

      "the index is 1" must {

        "return a Pagination model without a previous link or ellipsis and only page items [1, 2, 3]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1, true),
              createPageItem(2),
              createPageItem(3)
            )),
            previous = None,
            next = Some(createPageLink(2))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 3,
            ern = testErn,
            search = MessagesSearchOptions(DateReceivedD, 1, 10)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 2" must {

        "return a Pagination model without ellipsis and page items [1, 2, 3]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createPageItem(2, true),
              createPageItem(3)
            )),
            previous = Some(createPageLink(1)),
            next = Some(createPageLink(3))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 3,
            ern = testErn,
            search = MessagesSearchOptions(DateReceivedD, 2, 10)
          )

          actualResult shouldBe expectedResult
        }

        "the index is 3" must {

          "return a Pagination model without a next link or ellipsis and only page items [1, 2, 3]" in {

            val expectedResult: Option[Pagination] = Some(Pagination(
              items = Some(Seq(
                createPageItem(1),
                createPageItem(2),
                createPageItem(3, true)
              )),
              previous = Some(createPageLink(2)),
              next = None
            ))

            val actualResult: Option[Pagination] = Helper.constructPagination(
              pageCount = 3,
              ern = testErn,
              search = MessagesSearchOptions(DateReceivedD, 3, 10)
            )

            actualResult shouldBe expectedResult
          }
        }
      }
    }

    "4 pages of messages are given" when {

      "the index is 1" must {

        "return a Pagination model without a previous link and only page items [1, 2, 3, 4]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1, true),
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
            search = MessagesSearchOptions(DateReceivedD, 1, 10)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 2" must {

        "return a Pagination model without ellipsis and page items [1, 2, 3, 4]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createPageItem(2, true),
              createPageItem(3),
              createPageItem(4)
            )),
            previous = Some(createPageLink(1)),
            next = Some(createPageLink(3))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 4,
            ern = testErn,
            search = MessagesSearchOptions(DateReceivedD, 2, 10)
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
              createPageItem(3, true),
              createPageItem(4)
            )),
            previous = Some(createPageLink(2)),
            next = Some(createPageLink(4))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 4,
            ern = testErn,
            search = MessagesSearchOptions(DateReceivedD, 3, 10)
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
              createPageItem(4, true)
            )),
            previous = Some(createPageLink(3)),
            next = None
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 4,
            ern = testErn,
            search = MessagesSearchOptions(DateReceivedD, 4, 10)
          )

          actualResult shouldBe expectedResult
        }
      }
    }

    "10 pages of messages are given" when {

      "the index is 1" must {

        "return a Pagination model without a previous link or ellipsis and only page items [1, 2, ellipsis, 10]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1, true),
              createPageItem(2),
              ellipsis,
              createPageItem(10)
            )),
            previous = None,
            next = Some(createPageLink(2))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 10,
            ern = testErn,
            search = MessagesSearchOptions(DateReceivedD, 1, 10)
          )

          actualResult shouldBe expectedResult
        }
      }

      "the index is 2" must {

        "return a Pagination model without ellipsis and page items [1, 2, 3, ellipsis, 10]" in {

          val expectedResult: Option[Pagination] = Some(Pagination(
            items = Some(Seq(
              createPageItem(1),
              createPageItem(2, true),
              createPageItem(3),
              ellipsis,
              createPageItem(10)
            )),
            previous = Some(createPageLink(1)),
            next = Some(createPageLink(3))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 10,
            ern = testErn,
            search = MessagesSearchOptions(DateReceivedD, 2, 10)
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
              createPageItem(3, true),
              createPageItem(4),
              ellipsis,
              createPageItem(10)
            )),
            previous = Some(createPageLink(2)),
            next = Some(createPageLink(4))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 10,
            ern = testErn,
            search = MessagesSearchOptions(DateReceivedD, 3, 10)
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
              createPageItem(4, true),
              createPageItem(5),
              ellipsis,
              createPageItem(10)
            )),
            previous = Some(createPageLink(3)),
            next = Some(createPageLink(5))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 10,
            ern = testErn,
            search = MessagesSearchOptions(DateReceivedD, 4, 10)
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
              createPageItem(5, true),
              createPageItem(6),
              ellipsis,
              createPageItem(10)
            )),
            previous = Some(createPageLink(4)),
            next = Some(createPageLink(6))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 10,
            ern = testErn,
            search = MessagesSearchOptions(DateReceivedD, 5, 10)
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
              createPageItem(6, true),
              createPageItem(7),
              ellipsis,
              createPageItem(10)
            )),
            previous = Some(createPageLink(5)),
            next = Some(createPageLink(7))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 10,
            ern = testErn,
            search = MessagesSearchOptions(DateReceivedD, 6, 10)
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
              createPageItem(7, true),
              createPageItem(8),
              createPageItem(9),
              createPageItem(10)
            )),
            previous = Some(createPageLink(6)),
            next = Some(createPageLink(8))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 10,
            ern = testErn,
            search = MessagesSearchOptions(DateReceivedD, 7, 10)
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
              createPageItem(8, true),
              createPageItem(9),
              createPageItem(10)
            )),
            previous = Some(createPageLink(7)),
            next = Some(createPageLink(9))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 10,
            ern = testErn,
            search = MessagesSearchOptions(DateReceivedD, 8, 10)
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
              createPageItem(9, true),
              createPageItem(10)
            )),
            previous = Some(createPageLink(8)),
            next = Some(createPageLink(10))
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 10,
            ern = testErn,
            search = MessagesSearchOptions(DateReceivedD, 9, 10)
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
              createPageItem(10, true)
            )),
            previous = Some(createPageLink(9)),
            next = None
          ))

          val actualResult: Option[Pagination] = Helper.constructPagination(
            pageCount = 10,
            ern = testErn,
            search = MessagesSearchOptions(DateReceivedD,  10, 10)
          )

          actualResult shouldBe expectedResult
        }
      }
    }
  }
}
