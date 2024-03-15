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

package fixtures.messages.prevalidateTrader

import fixtures.messages.BaseEnglish

object PrevalidateTraderStartMessages {

  sealed trait ViewMessages {
    val title: String = "Check if a trader can receive excise goods"
    val h1: String = "Check if a trader can receive excise goods"
    val p: String = "You can use this tool to check if the consignee is able to receive your goods. You will need their trader ID, such as an excise registration number (ERN) or temporary authorisation number."
  }

  object English extends ViewMessages with BaseEnglish

}
