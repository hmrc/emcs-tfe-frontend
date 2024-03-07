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

package forms.behaviours

import forms.FormSpec
import play.api.data.{Form, FormError}

trait FieldBehaviours extends FormSpec {

  def fieldThatBindsValidData(form: Form[_],
                              fieldName: String,
                              dataItem: String): Unit = {

    "bind valid data" in {
      val result = form.bind(Map(fieldName -> dataItem)).apply(fieldName)
      result.value.value mustBe dataItem
      result.errors mustBe empty
    }
  }

  def mandatoryField(form: Form[_],
                     fieldName: String,
                     requiredError: FormError): Unit = {

    "not bind when key is not present at all" in {

      val result = form.bind(emptyForm).apply(fieldName)
      result.errors mustEqual Seq(requiredError)
    }

    "not bind blank values" in {

      val result = form.bind(Map(fieldName -> "")).apply(fieldName)
      result.errors mustEqual Seq(requiredError)
    }
  }

  def fieldWithFixedLength(form: Form[_],
                           fieldName: String,
                           lengthError: FormError,
                           requiredLength: Int): Unit = {

    "not bind when the value is less than the fixed length" in {
      val input = "A" * (requiredLength - 1)
      val result = form.bind(Map(fieldName -> input)).apply(fieldName)
      result.errors mustEqual Seq(lengthError)
    }

    "not bind when the value is more than the fixed length" in {
      val input = "A" * (requiredLength + 1)
      val result = form.bind(Map(fieldName -> input)).apply(fieldName)
      result.errors mustEqual Seq(lengthError)
    }

    "bind when the value is equal to the fixed length" in {
      val input = "A" * requiredLength
      val result = form.bind(Map(fieldName -> input)).apply(fieldName)
      result.errors mustBe empty
    }
  }

  def fieldWithERN(form: Form[_],
                   fieldName: String,
                   formatError: FormError): Unit = {

    "not bind when the ERN is missing a 2 character prefix" in {
      val result = form.bind(Map(fieldName -> "1100123456789")).apply(fieldName)
      result.errors mustEqual Seq(formatError)
    }

    "not bind when the ERN has a symbol" in {
      val result = form.bind(Map(fieldName -> "GB0-123456789")).apply(fieldName)
      result.errors mustEqual Seq(formatError)
    }

    "bind when the ERN is valid" in {
      val result = form.bind(Map(fieldName -> "GB00123456789")).apply(fieldName)
      result.errors mustBe empty
    }
  }

  def fieldWithEori(form: Form[_],
                   fieldName: String,
                   formatError: FormError): Unit = {

    "bind when the EORI number is valid" in {
      val result = form.bind(Map(fieldName -> "GB345678901234567")).apply(fieldName)
      result.errors mustBe empty
    }

    "not bind when the EORI number is missing a 2 character prefix" in {
      val result = form.bind(Map(fieldName -> "G2345678901234567")).apply(fieldName)
      result.errors mustEqual Seq(formatError)
    }

    "not bind when the EORI number has a symbol" in {
      val result = form.bind(Map(fieldName -> "GB-45678901234567")).apply(fieldName)
      result.errors mustEqual Seq(formatError)
    }

  }

}
