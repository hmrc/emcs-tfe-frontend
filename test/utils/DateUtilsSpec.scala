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

package utils

import base.SpecBase

import java.time.{LocalDate, LocalDateTime, LocalTime}

class DateUtilsSpec extends SpecBase {

  trait Test extends DateUtils

  "formatDateForUIOutput" must {
    "format the date in the correct format" in new Test {
      val unformattedDate: LocalDate = LocalDate.parse("2010-03-04")

      unformattedDate.formatDateForUIOutput() mustBe "4 March 2010"
    }
  }

  "formatTimeForUIOutput" should {
    "format the time in the correct format" in new Test {
      val time: LocalTime = LocalTime.of(20, 1, 3)
      time.formatTimeForUIOutput().toLowerCase mustBe "8:01 pm"
    }
  }

  ".parseDateTime" must {

    "return a parseable ChRIS EventDate" in new Test {
      val input = "2023-12-02T14:35:07"
      val expected = LocalDateTime.of(2023, 12, 2, 14, 35, 7)

      val response = parseDateTime(input)

      response mustBe expected
    }

    "return a parseable EIS EventDate" in new Test {
      val input = "2023-12-02T14:35:07.000Z"
      val expected = LocalDateTime.of(2023, 12, 2, 14, 35, 7, 0)

      val response = parseDateTime(input)

      response mustBe expected
    }

    "return today's date if unable to parse date/time" in new Test {
      val input = "2023-twelve-02T14:35:07"

      val response = parseDateTime(input)

      response mustBe LocalDate.now.atStartOfDay
    }
  }
}
