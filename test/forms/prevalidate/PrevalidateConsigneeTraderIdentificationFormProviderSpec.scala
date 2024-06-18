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

package forms.prevalidate

import forms.behaviours.FieldBehaviours
import forms.mappings.Mappings
import forms.{EXCISE_NUMBER_REGEX, XSS_REGEX}
import models.prevalidate.EntityGroup
import play.api.data.{Form, FormError}

class PrevalidateConsigneeTraderIdentificationFormProviderSpec extends FieldBehaviours with Mappings {
  val requiredKey = "prevalidateTrader.consigneeTraderIdentification.ern.error.required"
  val invalidRegexKey = "prevalidateTrader.consigneeTraderIdentification.ern.error.invalidRegex"
  val invalidCharactersKey = "prevalidateTrader.consigneeTraderIdentification.ern.error.invalidCharacters"

  val form  = new PrevalidateConsigneeTraderIdentificationFormProvider()()

  ".ern" - {

    val fieldName = "ern"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      dataItem = "GB00123456789"
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithERN(
      form,
      fieldName,
      formatError = FormError(fieldName, invalidRegexKey, Seq(EXCISE_NUMBER_REGEX))
    )

    behave like fieldWithoutXss(
      form,
      fieldName,
      formatError = FormError(fieldName, invalidCharactersKey, Seq(XSS_REGEX))
    )
  }

    ".entityGroup" - {
      val testForm = Form(
        "value" -> enumerable[EntityGroup]()
      )

      "must bind a valid option" in {
        val result = testForm.bind(Map("value" -> "UK Record"))
        result.get mustEqual EntityGroup.UKTrader
      }

      "must not bind an invalid option" in {
        val result = testForm.bind(Map("value" -> "Asia Record"))
        result.errors must contain(FormError("value", "error.invalid"))
      }

      "must not bind an empty map" in {
        val result = testForm.bind(Map.empty[String, String])
        result.errors must contain(FormError("value", "error.required"))
      }
    }

}
