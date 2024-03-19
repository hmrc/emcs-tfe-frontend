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

object PrevalidateConsigneeTraderIdentificationMessages {

  sealed trait ViewMessages {
    val title: String = "What is the consignee’s trader ID?"
    val h1: String = "What is the consignee’s trader ID?"
    val p: String = "This could be an excise registration number (ERN) or a temporary authorisation reference for temporary consignees, sometimes known as a Temporary Consignment Authorisation (TCA) number."
    val label: String = "Enter consignee trader ID"
    val hint: String = "The ID contains 13 characters, starting with 2 letters of the member state of the consignee, such as GBWK123456789"
  }

  object English extends ViewMessages with BaseEnglish

}
