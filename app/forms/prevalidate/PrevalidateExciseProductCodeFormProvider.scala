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

import forms.mappings.Mappings
import models.ExciseProductCode
import play.api.data.Form

import javax.inject.Inject

class PrevalidateExciseProductCodeFormProvider @Inject() extends Mappings {

  def apply(exciseProductCodes: Seq[ExciseProductCode]): Form[String] =
    Form(
      "excise-product-code" -> text("prevalidateTrader.exciseProductCode.error.required")
        .verifying(valueInList(exciseProductCodes.map(_.code), "prevalidateTrader.exciseProductCode.error.required"))
    )
}
