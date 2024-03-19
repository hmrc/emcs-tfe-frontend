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

package fixtures.messages.prevalidateTrader

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}
import models.Index

object ExciseProductCodeMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>

    val heading: String = s"Add excise product codes (EPC)"
    val title: String = titleHelper(heading)

    val defaultSelectOption = "Choose excise product code"
    val beerSelectOption = "B000: Beer"

    def label(idx: Index) = s"Excise product code ${idx.displayIndex}"
    val paragraph = "Add an excise product code for the goods you want to check. You will have the chance to add more codes later."
  }

  object English extends ViewMessages with BaseEnglish

}
