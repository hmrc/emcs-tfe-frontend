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

package fixtures.messages

object AlertRejectionReasonMessages {

  sealed trait Messages extends BaseEnglish { _ : i18n =>
    val consigneeDetailsWrong = "Some or all of the consignee details are wrong"
    val goodsTypeWrong = "Goods types do not match the order"
    val goodsQuantityWrong = "Goods quantities do not match the order"
    val other = "Other"
  }

  object English extends Messages with EN

}
