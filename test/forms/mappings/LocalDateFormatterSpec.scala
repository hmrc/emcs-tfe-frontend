/*
 * Copyright 2024 HM Revenue & Customs
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

package forms.mappings

import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.data.FormError

import java.time.LocalDate

class LocalDateFormatterSpec extends AnyFreeSpec with Matchers with OptionValues with Mappings {

  val allRequiredKey = "error.allRequired"
  val oneRequiredKey = "error.oneRequired"
  val twoRequiredKey = "error.twoRequired"
  val oneInvalidKey = "error.oneInvalid"
  val notARealDateKey = "error.notARealDate"
  val args = Seq.empty[String]

  val formatter = new LocalDateFormatter(
    allRequiredKey = allRequiredKey,
    oneRequiredKey = oneRequiredKey,
    twoRequiredKey = twoRequiredKey,
    oneInvalidKey = oneInvalidKey,
    notARealDateKey = notARealDateKey,
    args = args
  )

  "LocalDateFormatter" - {
    "bind a valid date" in {

      val data = Map(
        "date.day" -> "15",
        "date.month" -> "8",
        "date.year" -> "2023"
      )

      val result = formatter.bind("date", data)
      result shouldBe Right(LocalDate.of(2023, 8, 15))
    }

    "return an error for all missing fields" in {
      val data = Map(
        "date.day" -> "",
        "date.month" -> "",
        "date.year" -> ""
      )
      val result = formatter.bind("date", data)
      result shouldBe Left(Seq(FormError("date", allRequiredKey, args)))
    }

    "return an error for two missing fields" in {
      val data = Map(
        "date.day" -> "15",
        "date.month" -> "",
        "date.year" -> ""
      )
      val result = formatter.bind("date", data)
      result shouldBe Left(Seq(FormError("date", twoRequiredKey, Seq("month", "year") ++ args)))
    }

    "return an error for invalid day" in {
      val data = Map(
        "date.day" -> "32",
        "date.month" -> "8",
        "date.year" -> "2023"
      )
      val result = formatter.bind("date", data)
      result shouldBe Left(Seq(FormError("date", oneInvalidKey, Seq("day") ++ args)))
    }

    "return an error for invalid month" in {
      val data = Map(
        "date.day" -> "15",
        "date.month" -> "13",
        "date.year" -> "2023"
      )
      val result = formatter.bind("date", data)
      result shouldBe Left(Seq(FormError("date", oneInvalidKey, Seq("month") ++ args)))
    }

    "return an error for invalid year" in {
      val data = Map(
        "date.day" -> "15",
        "date.month" -> "8",
        "date.year" -> "0"
      )
      val result = formatter.bind("date", data)
      result shouldBe Left(Seq(FormError("date", oneInvalidKey, Seq("year") ++ args)))
    }

    "return an error for an invalid date" in {
      val data = Map(
        "date.day" -> "31",
        "date.month" -> "2",
        "date.year" -> "2023"
      )
      val result = formatter.bind("date", data)
      result shouldBe Left(Seq(FormError("date", notARealDateKey, args)))
    }

    "return an error for two invalid field" in {
      val data = Map(
        "date.day" -> "32",
        "date.month" -> "13",
        "date.year" -> "2025"
      )
      val result = formatter.bind("date", data)
      result shouldBe Left(Seq(FormError("date", notARealDateKey, args)))
    }

    "bind a valid date using a month name" in {
      val data = Map(
        "date.day" -> "15",
        "date.month" -> "August",
        "date.year" -> "2023"
      )
      val result = formatter.bind("date", data)
      result shouldBe Right(LocalDate.of(2023, 8, 15))
    }

    "bind a valid date using the first 3 characters of a month name" in {
      val data = Map(
        "date.day" -> "15",
        "date.month" -> "dec",
        "date.year" -> "2023"
      )
      val result = formatter.bind("date", data)
      result shouldBe Right(LocalDate.of(2023, 12, 15))
    }

    "return an error for invalid month name" in {
      val data = Map(
        "date.day" -> "15",
        "date.month" -> "Jin",
        "date.year" -> "2023"
      )
      val result = formatter.bind("date", data)
      result shouldBe Left(Seq(FormError("date", oneInvalidKey, Seq("month") ++ args)))
    }
  }
}
