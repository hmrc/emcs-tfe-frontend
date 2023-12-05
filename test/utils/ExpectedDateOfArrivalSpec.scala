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
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}

import java.time.{LocalDate, LocalDateTime, LocalTime}

class ExpectedDateOfArrivalSpec extends SpecBase {

  trait Test extends ExpectedDateOfArrival

  "calculateExpectedDate" when {

    "the time is midnight" should {
      val date: LocalDate = LocalDate.of(2023, 1, 1)
      val time: LocalTime = LocalTime.of(0, 0)

      "return the same date when the interval is expressed at the minimum of 1 hour" in new Test {
        calculateExpectedDate(date, time, "1 hours") shouldBe LocalDateTime.of(2023, 1, 1, 1, 0)
      }

      "return the same date when the interval is expressed at the maximum of 23 hours" in new Test {
        calculateExpectedDate(date, time, "23 hours") shouldBe LocalDateTime.of(2023, 1, 1, 23, 0)
      }

      "return the expected date when the interval is expressed at the minimum of 1 day" in new Test {
        calculateExpectedDate(date, time, "1 days") shouldBe LocalDateTime.of(2023, 1, 2, 0, 0)
      }

      "return the expected date when the interval is expressed at the maximum of 35 days" in new Test {
        calculateExpectedDate(date, time, "35 days") shouldBe LocalDateTime.of(2023, 2, 5, 0, 0)
      }
    }

    "the time is 1 hour to midnight" should {
      val date: LocalDate = LocalDate.of(2023, 1, 1)
      val time: LocalTime = LocalTime.of(23, 0)

      "return tomorrows date when the interval is expressed at the minimum of 1 hour" in new Test {
        calculateExpectedDate(date, time, "1 hours") shouldBe LocalDateTime.of(2023, 1, 2, 0, 0)
      }

      "return tomorrows date when the interval is expressed at the maximum of 23 hours" in new Test {
        calculateExpectedDate(date, time, "23 hours") shouldBe LocalDateTime.of(2023, 1, 2, 22, 0)
      }

      "return the expected date when the interval is expressed at the minimum of 1 day" in new Test {
        calculateExpectedDate(date, time, "1 days") shouldBe LocalDateTime.of(2023, 1, 2, 23, 0)
      }

      "return the expected date when the interval is expressed at the maximum of 35 days" in new Test {
        calculateExpectedDate(date, time, "35 days") shouldBe LocalDateTime.of(2023, 2, 5, 23, 0)
      }
    }

    "parsing an invalid journeyTime" should {

      "return the same date as the dispatch date" in new Test {

        val date: LocalDate = LocalDate.of(2023, 1, 1)
        val time: LocalTime = LocalTime.of(0, 0)

        calculateExpectedDate(date, time, "2 weeks") shouldBe LocalDateTime.of(2023, 1, 1, 0, 0)
      }
    }

  }

}
