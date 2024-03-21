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

package forms

import forms.behaviours.FieldBehaviours
import play.api.data.FormError

class PrevalidateConsigneeTraderIdentificationFormProviderSpec extends FieldBehaviours {
  val requiredKey = "prevalidateTrader.consigneeTraderIdentification.error.required"
  val invalidRegexKey = "prevalidateTrader.consigneeTraderIdentification.error.invalidRegex"
  val invalidCharactersKey = "prevalidateTrader.consigneeTraderIdentification.error.invalidCharacters"

  val form  = new PrevalidateConsigneeTraderIdentificationFormProvider()()

  ".value" - {

    val fieldName = "value"

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

}
