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

package models.messages

import base.SpecBase
import models.messages.MessagesSortingSelectOption.{ArcA, DateReceivedD}

class MessagesSearchOptionsSpec extends SpecBase {

  "MessagesSearchOptions" must {

    "construct with default options" in {

      val expectedResult = MessagesSearchOptions(
        sortBy = DateReceivedD,
        index = 1,
        maxRows = 10
      )

      val actualResult = MessagesSearchOptions()

      actualResult mustBe expectedResult
    }

    "be able to be constructed by a QueryStringBinder" when {

      "no query params are supplied" in {

        val expectedResult = Some(Right(MessagesSearchOptions()))
        val actualResult = MessagesSearchOptions.queryStringBinder.bind("search", Map())

        actualResult mustBe expectedResult
      }

      "all query parameters are supplied" in {

        val expectedResult = Some(Right(MessagesSearchOptions(
          sortBy = ArcA,
          index = 5,
          maxRows = MessagesSearchOptions.DEFAULT_MAX_ROWS
        )))

        val actualResult = MessagesSearchOptions.queryStringBinder.bind("search", Map(
          "sortBy" -> Seq(ArcA.code),
          "index" -> Seq("5")
        ))

        actualResult mustBe expectedResult
      }

      "some query parameters are supplied" in {

        val expectedResult = Some(Right(MessagesSearchOptions(
          sortBy = DateReceivedD,
          index = 5,
          maxRows = MessagesSearchOptions.DEFAULT_MAX_ROWS
        )))

        val actualResult = MessagesSearchOptions.queryStringBinder.bind("search", Map(
          "index" -> Seq("5")
        ))

        actualResult mustBe expectedResult
      }

    }

    "unbind QueryString to URL format" in {

      val expectedResult = s"sortBy=${ArcA.code}&index=1"

      val actualResult = MessagesSearchOptions.queryStringBinder.unbind("search", MessagesSearchOptions(ArcA, 1, 10))

      actualResult mustBe expectedResult
    }

    "have the correct queryParams values" when {

      "using default values" in {

        val expectedResult: Seq[(String, String)] = Seq(
          "sortField" -> DateReceivedD.sortField,
          "sortOrder" -> DateReceivedD.sortOrder,
          "page" -> "1"
        )

        val actualResult = MessagesSearchOptions().queryParams

        actualResult mustBe expectedResult
      }

      "given values" in {

        val expectedResult: Seq[(String, String)] = Seq(
          "sortField" -> DateReceivedD.sortField,
          "sortOrder" -> DateReceivedD.sortOrder,
          "page" -> "25"
        )

        val actualResult = MessagesSearchOptions(
          sortBy = DateReceivedD,
          index = 25,
          maxRows = 10
        ).queryParams

        actualResult mustBe expectedResult
      }
    }

    "apply" should {

      "return a valid MessagesSearchOptions" in {

        val expectedResult = MessagesSearchOptions(
          sortBy = ArcA,
          index = 1,
          maxRows = 10
        )

        val actualResult = MessagesSearchOptions.apply(ArcA.code)

        actualResult mustBe expectedResult
      }
    }

    "unapply" should {

      "return the string values for MessagesSearchOptions" in {

        val expectedResult = Some("arcA")

        val actualResult = MessagesSearchOptions.unapply(
          MessagesSearchOptions(
            sortBy = ArcA,
            index = 1,
            maxRows = 10
          )
        )

        actualResult mustBe expectedResult
      }
    }
  }
}
