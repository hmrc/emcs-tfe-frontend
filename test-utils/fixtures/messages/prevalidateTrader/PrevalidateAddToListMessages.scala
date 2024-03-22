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

object PrevalidateAddToListMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>

    def heading(count: Int): String = count match {
      case 1 => "You have added 1 Excise Product Code"
      case _ => s"You have added $count Excise Product Codes"
    }

    def title(count: Int): String = titleHelper(heading(count))

    def removeHidden(idx: Index): String = s"excise product code ${idx.displayIndex}"

    def changeHidden(idx: Index): String = s"excise product code ${idx.displayIndex}"

    def removeEpc(idx: Index): String = s"$remove ${removeHidden(idx)}"
    def changeEpc(idx: Index): String = s"$change ${changeHidden(idx)}"

    val h2 = "Do you need to add another Excise Product Code (EPC)?"

    val errorRequired = "Select yes if you want to add another Excise Product Code (EPC)"
  }

  object English extends ViewMessages with BaseEnglish
}
