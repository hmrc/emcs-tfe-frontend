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

import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.data.{Form, FormError}

import java.time.LocalDate

class OptionalDateMappingsSpec extends AnyFreeSpec with Matchers with OptionValues
  with Mappings {

  val form = Form(
    "value" -> optionalLocalDate(
      requiredKey = "error.required",
      twoRequiredKey = "error.required.two",
      invalidKey = "error.invalid"
    )
  )

  val invalidField: String = "beans"

  val missingField: Option[String] = None

  val date: LocalDate = LocalDate.now()

  "must bind valid data" in {
    val data = Map(
      "value.day" -> date.getDayOfMonth.toString,
      "value.month" -> date.getMonthValue.toString,
      "value.year" -> date.getYear.toString
    )

    val result = form.bind(data)

    result.value.value mustEqual Some(date)
  }

  "must bind valid data (where there's spaces either side)" in {

    val data = Map(
      "value.day" -> "    12  ",
      "value.month" -> "     4  ",
      "value.year" -> "   2022 "
    )

    val result = form.bind(data)

    result.value.value mustEqual Some(LocalDate.of(2022, 4, 12))
  }

  "must bind an empty date" in {

    val data: Map[String, String] = Map()

    val result = form.bind(data)

    result.value.value mustEqual None
  }

  "must fail to bind a date with a missing day" in {
    val initialData = Map(
      "value.month" -> date.getMonthValue.toString,
      "value.year" -> date.getYear.toString
    )

    val data = missingField.fold(initialData) {
      value =>
        initialData + ("value.day" -> value)
    }

    val result = form.bind(data)

    result.errors must contain only FormError("value", "error.required", List("day"))
  }

  "must fail to bind a date with an invalid day" in {
    val data = Map(
      "value.day" -> invalidField,
      "value.month" -> date.getMonthValue.toString,
      "value.year" -> date.getYear.toString
    )

    val result = form.bind(data)

    result.errors must contain(
      FormError("value", "error.invalid", List.empty)
    )
  }

  "must fail to bind a date with a missing month" in {
    val initialData = Map(
      "value.day" -> date.getDayOfMonth.toString,
      "value.year" -> date.getYear.toString
    )

    val data = missingField.fold(initialData) {
      value =>
        initialData + ("value.month" -> value)
    }

    val result = form.bind(data)

    result.errors must contain only FormError("value", "error.required", List("month"))
  }

  "must fail to bind a date with an invalid month" in {
    val data = Map(
      "value.day" -> date.getDayOfMonth.toString,
      "value.month" -> invalidField,
      "value.year" -> date.getYear.toString
    )

    val result = form.bind(data)

    result.errors must contain(
      FormError("value", "error.invalid", List.empty)
    )
  }

  "must fail to bind a date with a missing year" in {
    val initialData = Map(
      "value.day" -> date.getDayOfMonth.toString,
      "value.month" -> date.getMonthValue.toString
    )

    val data = missingField.fold(initialData) {
      value =>
        initialData + ("value.year" -> value)
    }

    val result = form.bind(data)

    result.errors must contain only FormError("value", "error.required", List("year"))
  }

  "must fail to bind a date with an invalid year" in {
    val data = Map(
      "value.day" -> date.getDayOfMonth.toString,
      "value.month" -> date.getMonthValue.toString,
      "value.year" -> invalidField
    )

    val result = form.bind(data)

    result.errors must contain(
      FormError("value", "error.invalid", List.empty)
    )
  }

  "must fail to bind a date with a missing day and month" in {
    val day = missingField.fold(Map.empty[String, String]) {
      value =>
        Map("value.day" -> value)
    }

    val month = missingField.fold(Map.empty[String, String]) {
      value =>
        Map("value.month" -> value)
    }

    val data: Map[String, String] = Map(
      "value.year" -> date.getYear.toString
    ) ++ day ++ month

    val result = form.bind(data)

    result.errors must contain only FormError("value", "error.required.two", List("day", "month"))
  }

  "must fail to bind a date with a missing day and year" in {
    val day = missingField.fold(Map.empty[String, String]) {
      value =>
        Map("value.day" -> value)
    }

    val year = missingField.fold(Map.empty[String, String]) {
      value =>
        Map("value.year" -> value)
    }

    val data: Map[String, String] = Map(
      "value.month" -> date.getMonthValue.toString
    ) ++ day ++ year

    val result = form.bind(data)

    result.errors must contain only FormError("value", "error.required.two", List("day", "year"))
  }

  "must fail to bind a date with a missing month and year" in {
    val month = missingField.fold(Map.empty[String, String]) {
      value =>
        Map("value.month" -> value)
    }

    val year = missingField.fold(Map.empty[String, String]) {
      value =>
        Map("value.year" -> value)
    }

    val data: Map[String, String] = Map(
      "value.day" -> date.getDayOfMonth.toString
    ) ++ month ++ year

    val result = form.bind(data)

    result.errors must contain only FormError("value", "error.required.two", List("month", "year"))
  }

  "must fail to bind an invalid day and month" in {
    val data = Map(
      "value.day" -> invalidField,
      "value.month" -> invalidField,
      "value.year" -> date.getYear.toString
    )

    val result = form.bind(data)

    result.errors must contain only FormError("value", "error.invalid", List.empty)
  }

  "must fail to bind an invalid day and year" in {
    val data = Map(
      "value.day" -> invalidField,
      "value.month" -> date.getMonthValue.toString,
      "value.year" -> invalidField
    )

    val result = form.bind(data)

    result.errors must contain only FormError("value", "error.invalid", List.empty)
  }

  "must fail to bind an invalid month and year" in {
    val data = Map(
      "value.day" -> date.getDayOfMonth.toString,
      "value.month" -> invalidField,
      "value.year" -> invalidField
    )

    val result = form.bind(data)

    result.errors must contain only FormError("value", "error.invalid", List.empty)
  }

  "must fail to bind an invalid day, month and year" in {
    val data = Map(
      "value.day" -> invalidField,
      "value.month" -> invalidField,
      "value.year" -> invalidField
    )

    val result = form.bind(data)

    result.errors must contain only FormError("value", "error.invalid", List.empty)
  }

  "must fail to bind an invalid date" in {
    val data = Map(
      "value.day" -> "30",
      "value.month" -> "2",
      "value.year" -> "2018"
    )

    val result = form.bind(data)

    result.errors must contain(
      FormError("value", "error.invalid", List.empty)
    )
  }

  "must unbind a date" in {
    val filledForm = form.fill(Some(date))

    filledForm("value.day").value.value mustEqual date.getDayOfMonth.toString
    filledForm("value.month").value.value mustEqual date.getMonthValue.toString
    filledForm("value.year").value.value mustEqual date.getYear.toString
  }

  "must unbind empty data" in {
    val filledForm = form.fill(None)

    filledForm mustBe form.copy(data = Map("value.day" -> "", "value.month" -> "", "value.year" -> ""), value = Some(None))
  }
}
