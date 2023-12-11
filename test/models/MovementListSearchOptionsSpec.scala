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

package uk.gov.hmrc.emcstfefrontend.models

import uk.gov.hmrc.emcstfefrontend.base.SpecBase
import uk.gov.hmrc.emcstfefrontend.models.MovementSortingSelectOption.Arc

class MovementListSearchOptionsSpec extends SpecBase {

  "GetMovementListSearchOptions" must {

    "construct with default options" in {

      val expectedResult = MovementListSearchOptions(
        sortOrder = Arc.code,
        index = 1,
        maxCount = 10
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
          sortOrder = "testSortOrder",
          index = 5,
          maxCount = MovementListSearchOptions.DEFAULT_MAX_ROWS
        )))

        val actualResult = MovementListSearchOptions.queryStringBinder.bind("search", Map(
          "sortOrder" -> Seq("testSortOrder"),
          "index" -> Seq("5")
        ))

        actualResult mustBe expectedResult
      }

      "some query parameters are supplied" in {

        val actualResult = MovementListSearchOptions.queryStringBinder.bind("search", Map(
          "index" -> Seq("5")
        ))

        val expectedResult = Some(Right(MovementListSearchOptions(
          sortOrder = Arc.code,
          index = 5,
          maxCount = MovementListSearchOptions.DEFAULT_MAX_ROWS
        )))

        actualResult mustBe expectedResult
      }

      "No query parameters are supplied" in {

        val actualResult = MovementListSearchOptions.queryStringBinder.bind("search", Map())

        val expectedResult = Some(Right(MovementListSearchOptions(
          sortOrder = Arc.code,
          index = 1,
          maxCount = MovementListSearchOptions.DEFAULT_MAX_ROWS
        )))

        actualResult mustBe expectedResult
      }
    }

    "unbind QueryString to URL format" in {

      val expectedResult = "sortOrder=D&index=1"

      val actualResult = MovementListSearchOptions.queryStringBinder.unbind("search", MovementListSearchOptions())

      actualResult mustBe expectedResult
    }

    "have the correct queryParams values" when {

      "using default values" in {

        val expectedResult: Seq[(String, String)] = Seq(
          "search.sortOrder" -> "D",
          "search.startingPosition" -> "1",
          "search.maxCount" -> "10"
        )

        val actualResult = MovementListSearchOptions().queryParams

        actualResult mustBe expectedResult
      }

      "given values" in {

        val expectedResult: Seq[(String, String)] = Seq(
          "search.sortOrder" -> "testOrder",
          "search.startingPosition" -> "26",
          "search.maxCount" -> "5"
        )

        val actualResult = MovementListSearchOptions(
          sortOrder = "testOrder",
          index = 6,
          maxCount = 5
        ).queryParams

        actualResult mustBe expectedResult
      }
    }

    "startingPosition must be correctly calculated from the index and maxRows" when {

      "maxRows is 10" when {

        "index is 1" in {

          val expectedResult = 1

          val actualResult = MovementListSearchOptions(
            index = 1,
            maxCount = 10
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 2" in {

          val expectedResult = 11

          val actualResult = MovementListSearchOptions(
            index = 2,
            maxCount = 10
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 50" in {

          val expectedResult = 491

          val actualResult = MovementListSearchOptions(
            index = 50,
            maxCount = 10
          ).startingPosition

          actualResult mustBe expectedResult
        }
      }

      "maxRows is 5" when {

        "index is 1" in {

          val expectedResult = 1

          val actualResult = MovementListSearchOptions(
            index = 1,
            maxCount = 5
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 2" in {

          val expectedResult = 6

          val actualResult = MovementListSearchOptions(
            index = 2,
            maxCount = 5
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 50" in {

          val expectedResult = 246

          val actualResult = MovementListSearchOptions(
            index = 50,
            maxCount = 5
          ).startingPosition

          actualResult mustBe expectedResult
        }
      }

      "maxRows is 30" when {

        "index is 1" in {

          val expectedResult = 1

          val actualResult = MovementListSearchOptions(
            index = 1,
            maxCount = 5
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 2" in {

          val expectedResult = 31

          val actualResult = MovementListSearchOptions(
            index = 2,
            maxCount = 30
          ).startingPosition

          actualResult mustBe expectedResult
        }

        "index is 50" in {

          val expectedResult = 1471

          val actualResult = MovementListSearchOptions(
            index = 50,
            maxCount = 30
          ).startingPosition

          actualResult mustBe expectedResult
        }
      }
    }
  }
}
