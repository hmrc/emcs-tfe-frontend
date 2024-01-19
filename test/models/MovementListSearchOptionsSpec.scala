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

package models

import base.SpecBase
import models.MovementSearchSelectOption.{ARC, ERN, Transporter}
import models.MovementSortingSelectOption.{ArcAscending, ArcDescending, Oldest}

class MovementListSearchOptionsSpec extends SpecBase {

  "GetMovementListSearchOptions" must {

    "construct with default options" in {

      val expectedResult = MovementListSearchOptions(
        sortBy = ArcAscending,
        index = 1,
        maxRows = 10
      )

      val actualResult = MovementListSearchOptions()

      actualResult mustBe expectedResult
    }

    "be able to be constructed by a QueryStringBinder" when {

      "no query params are supplied" in {

        val expectedResult = Some(Right(MovementListSearchOptions()))
        val actualResult = MovementListSearchOptions.queryStringBinder.bind("search", Map())

        actualResult mustBe expectedResult
      }

      "all query parameters are supplied" in {

        val expectedResult = Some(Right(MovementListSearchOptions(
          sortBy = ArcDescending,
          index = 5,
          maxRows = MovementListSearchOptions.DEFAULT_MAX_ROWS
        )))

        val actualResult = MovementListSearchOptions.queryStringBinder.bind("search", Map(
          "sortBy" -> Seq(ArcDescending.code),
          "index" -> Seq("5")
        ))

        actualResult mustBe expectedResult
      }

      "some query parameters are supplied" in {

        val expectedResult = Some(Right(MovementListSearchOptions(
          sortBy = ArcAscending,
          index = 5,
          maxRows = MovementListSearchOptions.DEFAULT_MAX_ROWS
        )))

        val actualResult = MovementListSearchOptions.queryStringBinder.bind("search", Map(
          "index" -> Seq("5")
        ))

        actualResult mustBe expectedResult
      }

      "No query parameters are supplied" in {

        val expectedResult = Some(Right(MovementListSearchOptions(
          sortBy = ArcAscending,
          index = 1,
          maxRows = MovementListSearchOptions.DEFAULT_MAX_ROWS
        )))

        val actualResult = MovementListSearchOptions.queryStringBinder.bind("search", Map())

        actualResult mustBe expectedResult
      }
    }

    "unbind QueryString to URL format" in {

      val expectedResult = s"searchKey=arc&searchValue=ARC123&sortBy=${ArcAscending.code}&index=1"

      val actualResult = MovementListSearchOptions.queryStringBinder.unbind("search", MovementListSearchOptions(Some(ARC), Some("ARC123")))

      actualResult mustBe expectedResult
    }

    "have the correct queryParams values" when {

      "using default values" in {

        val expectedResult: Seq[(String, String)] = Seq(
          "search.sortOrder" -> ArcAscending.sortOrder,
          "search.sortField" -> ArcAscending.sortField,
          "search.startPosition" -> "0",
          "search.maxRows" -> "10"
        )

        val actualResult = MovementListSearchOptions().queryParams

        actualResult mustBe expectedResult
      }

      "given values" in {

        val expectedResult: Seq[(String, String)] = Seq(
          "search.transporterTraderName" -> "robots in disguise",
          "search.sortOrder" -> Oldest.sortOrder,
          "search.sortField" -> Oldest.sortField,
          "search.startPosition" -> "25",
          "search.maxRows" -> "5"
        )

        val actualResult = MovementListSearchOptions(
          searchKey = Some(Transporter),
          searchValue = Some("robots in disguise"),
          sortBy = Oldest,
          index = 6,
          maxRows = 5
        ).queryParams

        actualResult mustBe expectedResult
      }
    }

    "startingPosition must be correctly calculated from the index and maxRows" when {

      "maxRows is 10" when {

        "index is 1" in {

          val expectedResult = 0

          val actualResult = MovementListSearchOptions(
            index = 1,
            maxRows = 10
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 2" in {

          val expectedResult = 10

          val actualResult = MovementListSearchOptions(
            index = 2,
            maxRows = 10
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 50" in {

          val expectedResult = 490

          val actualResult = MovementListSearchOptions(
            index = 50,
            maxRows = 10
          ).startingPosition

          actualResult mustBe expectedResult
        }
      }

      "maxRows is 5" when {

        "index is 1" in {

          val expectedResult = 0

          val actualResult = MovementListSearchOptions(
            index = 1,
            maxRows = 5
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 2" in {

          val expectedResult = 5

          val actualResult = MovementListSearchOptions(
            index = 2,
            maxRows = 5
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 50" in {

          val expectedResult = 245

          val actualResult = MovementListSearchOptions(
            index = 50,
            maxRows = 5
          ).startingPosition

          actualResult mustBe expectedResult
        }
      }

      "maxRows is 30" when {

        "index is 1" in {

          val expectedResult = 0

          val actualResult = MovementListSearchOptions(
            index = 1,
            maxRows = 5
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 2" in {

          val expectedResult = 30

          val actualResult = MovementListSearchOptions(
            index = 2,
            maxRows = 30
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 50" in {

          val expectedResult = 1470

          val actualResult = MovementListSearchOptions(
            index = 50,
            maxRows = 30
          ).startingPosition

          actualResult mustBe expectedResult
        }
      }
    }

    "apply" should {

      "return a valid MovementListSearchOption" in {

        val expectedResult = MovementListSearchOptions(
          searchKey = Some(ARC),
          searchValue = Some("ARC123"),
          sortBy = Oldest,
          traderRole = Some(MovementFilterDirectionOption.GoodsIn),
          undischargedMovements = Some(MovementFilterUndischargedOption.Undischarged),
          index = 1,
          maxRows = 10
        )

        val actualResult = MovementListSearchOptions.apply(
          searchKey = Some(ARC.code),
          searchValue = Some("ARC123"),
          sortBy = Oldest.code,
          traderRoleOptions = Set(MovementFilterDirectionOption.GoodsIn),
          undischargedMovementsOptions = Set(MovementFilterUndischargedOption.Undischarged)
        )

        actualResult mustBe expectedResult
      }
    }

    "unapply" should {

      "return the string values for MovementListSearchOptions" in {

        val expectedResult = Some((Some("otherTraderId"), Some("ERN123456"), Oldest.code, Set(MovementFilterDirectionOption.GoodsOut), Set(MovementFilterUndischargedOption.Undischarged)))

        val actualResult = MovementListSearchOptions.unapply(
          MovementListSearchOptions(
            searchKey = Some(ERN),
            searchValue = Some("ERN123456"),
            sortBy = Oldest,
            traderRole = Some(MovementFilterDirectionOption.GoodsOut),
            undischargedMovements = Some(MovementFilterUndischargedOption.Undischarged),
            index = 1,
            maxRows = 10
          )
        )

        actualResult mustBe expectedResult
      }
    }
  }
}
