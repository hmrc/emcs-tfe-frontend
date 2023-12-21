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

package forms.mappings

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.data.validation.{Invalid, Valid}

import java.time.LocalDate

class ConstraintsSpec extends AnyFreeSpec with Matchers with Constraints {


  "firstError" - {

    "must return Valid when all constraints pass" in {
      val result = firstError(maxLength(10, "error.length"), regexp("""^\w+$""", "error.regexp"))("foo")
      result mustEqual Valid
    }

    "must return Invalid when the first constraint fails" in {
      val result = firstError(maxLength(10, "error.length"), regexp("""^\w+$""", "error.regexp"))("a" * 11)
      result mustEqual Invalid("error.length", 10)
    }

    "must return Invalid when the second constraint fails" in {
      val result = firstError(maxLength(10, "error.length"), regexp("""^\w+$""", "error.regexp"))("")
      result mustEqual Invalid("error.regexp", """^\w+$""")
    }

    "must return Invalid for the first error when both constraints fail" in {
      val result = firstError(maxLength(-1, "error.length"), regexp("""^\w+$""", "error.regexp"))("")
      result mustEqual Invalid("error.length", -1)
    }
  }

  "minimumValue" - {

    "must return Valid for a number greater than the threshold" in {
      val result = minimumValue(1, "error.min").apply(2)
      result mustEqual Valid
    }

    "must return Valid for a number equal to the threshold" in {
      val result = minimumValue(1, "error.min").apply(1)
      result mustEqual Valid
    }

    "must return Invalid for a number below the threshold" in {
      val result = minimumValue(1, "error.min").apply(0)
      result mustEqual Invalid("error.min", 1)
    }
  }

  "maximumValue" - {

    "must return Valid for a number less than the threshold" in {
      val result = maximumValue(1, "error.max").apply(0)
      result mustEqual Valid
    }

    "must return Valid for a number equal to the threshold" in {
      val result = maximumValue(1, "error.max").apply(1)
      result mustEqual Valid
    }

    "must return Invalid for a number above the threshold" in {
      val result = maximumValue(1, "error.max").apply(2)
      result mustEqual Invalid("error.max", 1)
    }
  }

  "regexp" - {

    "must return Valid for an input that matches the expression" in {
      val result = regexp("""^\w+$""", "error.invalid")("foo")
      result mustEqual Valid
    }

    "must return Invalid for an input that does not match the expression" in {
      val result = regexp("""^\d+$""", "error.invalid")("foo")
      result mustEqual Invalid("error.invalid", """^\d+$""")
    }
  }

  "regexpUnlessEmpty" - {

    "must return Valid for an input that matches the expression" in {
      val result = regexpUnlessEmpty("""^\w+$""", "error.invalid")("foo")
      result mustEqual Valid
    }

    "must return Valid for an empty input" in {
      val result = regexpUnlessEmpty("""^\w+$""", "error.invalid")("")
      result mustEqual Valid
    }

    "must return Invalid for an input that does not match the expression" in {
      val result = regexpUnlessEmpty("""^\d+$""", "error.invalid")("foo")
      result mustEqual Invalid("error.invalid", """^\d+$""")
    }
  }

  "maxLength" - {

    "must return Valid for a string shorter than the allowed length" in {
      val result = maxLength(10, "error.length")("a" * 9)
      result mustEqual Valid
    }

    "must return Valid for an empty string" in {
      val result = maxLength(10, "error.length")("")
      result mustEqual Valid
    }

    "must return Valid for a string equal to the allowed length" in {
      val result = maxLength(10, "error.length")("a" * 10)
      result mustEqual Valid
    }

    "must return Invalid for a string longer than the allowed length" in {
      val result = maxLength(10, "error.length")("a" * 11)
      result mustEqual Invalid("error.length", 10)
    }
  }

  "maxDate" - {

    "must return Valid for a date before or equal to the maximum" in {
      val max = LocalDate.of(3000, 1, 1)
      val date = LocalDate.of(2000, 1, 1)

      val result = maxDate(max, "error.future")(date)
      result mustEqual Valid
    }
  }

  "must return Invalid for a date after the maximum" in {
    val max = LocalDate.of(3000, 1, 1)
    val date = max.plusDays(1)

    val result = maxDate(max, "error.future", "foo")(date)
    result mustEqual Invalid("error.future", "foo")
  }

  "minDate" - {

    "must return Valid for a date after or equal to the minimum" in {
      val min = LocalDate.of(2000, 1, 1)
      val date = min.plusDays(1)

      val result = minDate(min, "error.past", "foo")(date)
      result mustEqual Valid
    }

    "must return Invalid for a date before the minimum" in {
      val min = LocalDate.of(2000, 1, 1)
      val date = min.minusDays(1)

      val result = minDate(min, "error.past", "foo")(date)
      result mustEqual Invalid("error.past", "foo")
    }
  }

  "isDecimal" - {

    "must return Valid for a valid number" in {

      val result = isDecimal("error")("10.56")
      result mustEqual Valid
    }

    "must return Invalid for a non-numeric" in {

      val result = isDecimal("error")("banana")
      result mustEqual Invalid("error")
    }
  }

  "decimalRange" - {

    "must return Valid for a valid number (min)" in {

      val result = decimalRange(1.2, 835.5, "error")(1.2)
      result mustEqual Valid
    }

    "must return Valid for a valid number (max)" in {

      val result = decimalRange(1.2, 835.5, "error")(835.5)
      result mustEqual Valid
    }

    "must return Invalid for a number outside of the range (less than)" in {

      val result = decimalRange(1.2, 835.5, "error")(1.199999999)
      result mustEqual Invalid("error", 1.2, 835.5)
    }

    "must return Invalid for a number outside of the range (more than)" in {

      val result = decimalRange(1.2, 835.5, "error")(835.50000000001)
      result mustEqual Invalid("error", 1.2, 835.5)
    }
  }

  "maxDecimalPlaces" - {

    "must return Valid for a valid number of dp (max)" in {

      val result = maxDecimalPlaces(5, "error")(1.12345)
      result mustEqual Valid
    }

    "must return Valid for a valid number of dp (min)" in {

      val result = maxDecimalPlaces(5, "error")(1)
      result mustEqual Valid
    }

    "must return Invalid for a number that exceeds the dp" in {

      val result = maxDecimalPlaces(5, "error")(1.123456)
      result mustEqual Invalid("error", 5)
    }
  }

  "valueInList" - {

    "must return Valid when the given value is in the list" in {

      val result = valueInList(Seq("apple", "banana", "carrot"), "error.notInList")("apple")
      result mustEqual Valid
    }

    "must return Invalid when the given value is not in the list" in {

      val result = valueInList(Seq("apple", "banana", "carrot"), "error.notInList")("grape")
      result mustEqual Invalid("error.notInList")
    }

  }

  "exclusiveItemInSet" - {

    "must return Valid for an exclusive item selected and only that one is present" in {
      val result = exclusiveItemInSet("errorKey", "0")(Set("0"))
      result mustEqual Valid
    }

    "must return Valid for one non-exclusive item selected" in {
      val result = exclusiveItemInSet("errorKey", "0")(Set("1"))
      result mustEqual Valid
    }

    "must return Valid for more than one non-exclusive item selected" in {
      val result = exclusiveItemInSet("errorKey", "0")(Set("1", "2"))
      result mustEqual Valid
    }

    "must return Invalid when both a non-exclusive and exclusive item has been selected" in {
      val result = exclusiveItemInSet("errorKey", "0")(Set("0", "1"))
      result mustEqual Invalid("errorKey")
    }

  }
}
