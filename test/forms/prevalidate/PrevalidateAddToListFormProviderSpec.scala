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

package forms.prevalidate

import fixtures.messages.prevalidateTrader.PrevalidateAddToListMessages
import forms.behaviours.BooleanFieldBehaviours
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.FormError
import play.api.i18n.{Lang, Messages, MessagesApi}

class PrevalidateAddToListFormProviderSpec extends BooleanFieldBehaviours with GuiceOneAppPerSuite {

  val form = new PrevalidateAddToListFormProvider()()

  val fieldName = "value"
  val requiredKey = "prevalidateTrader.addToList.error.required"

  ".value" - {

    behave like booleanField(
      form,
      fieldName,
      invalidError = FormError(fieldName, "error.boolean")
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  "Error messages" - {

    def messages(candidates: Seq[Lang]): Messages = app.injector.instanceOf[MessagesApi].preferred(candidates)

    Seq(PrevalidateAddToListMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in the language of '${messagesForLanguage.lang.code}'" - {

        val msgs = messages(Seq(messagesForLanguage.lang))

        "have the correct wording for the required key message" in {

          msgs(requiredKey) mustBe messagesForLanguage.errorRequired
        }
      }
    }
  }
}
