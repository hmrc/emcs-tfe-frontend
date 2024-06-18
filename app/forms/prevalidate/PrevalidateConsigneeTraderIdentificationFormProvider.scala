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

import forms.mappings.Mappings
import forms.{ALPHANUMERIC_REGEX, EXCISE_NUMBER_REGEX, XSS_REGEX}
import models.prevalidate.{EntityGroup, PrevalidateTraderModel}
import play.api.data.Form
import play.api.data.Forms.mapping

import javax.inject.Inject

class PrevalidateConsigneeTraderIdentificationFormProvider @Inject() extends Mappings {

  def apply(): Form[PrevalidateTraderModel] =
    Form(
      mapping(
        "ern" -> text("prevalidateTrader.consigneeTraderIdentification.ern.error.required")
          .verifying(
            firstError(
              regexpUnlessEmpty(XSS_REGEX, "prevalidateTrader.consigneeTraderIdentification.ern.error.invalidCharacters"),
              regexpUnlessEmpty(ALPHANUMERIC_REGEX, "prevalidateTrader.consigneeTraderIdentification.ern.error.invalidCharacters"),
              regexpUnlessEmpty(EXCISE_NUMBER_REGEX, "prevalidateTrader.consigneeTraderIdentification.ern.error.invalidRegex")
            )
          ),
        "entityGroup" -> enumerable[EntityGroup]("prevalidateTrader.consigneeTraderIdentification.entityGroup.error.required")
      )(PrevalidateTraderModel.apply)(PrevalidateTraderModel.unapply)
    )
}
