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

object PrevalidateTraderRemoveExciseProductCodeMessages {

  sealed trait ViewMessages {
    def title(epc: String): String = s"Are you sure you want to remove Excise Product Code $epc?"
    def h1(epc: String): String = s"Are you sure you want to remove Excise Product Code $epc?"
  }

  object English extends ViewMessages with BaseEnglish

}
