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
import models.ExciseProductCode

object PrevalidateTraderResultsMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>

    val headingNoErn = "We cannot find consignee’s trader ID"
    val titleNoErn = titleHelper(headingNoErn)
    val noErnLink = "Try again with another trader ID"

    val heading = "Results for this trader"
    val title = titleHelper(heading)

    val p1: String => String = id => s"Trader ID: $id"
    val h2Approved = "Trader can receive:"
    val h2NotApproved = "Trader cannot receive:"
    val bullet: ExciseProductCode => String = code => s"${code.code}: ${code.description}"
    val linkAddCode = "Check more codes for the same trader"
    val linkDifferentTrader = "Check codes for a different trader"

    val linkReturnToAccount = "Return to account home"
    val linkFeedback = "What did you think of this service?"
    val feedback = s"$linkFeedback (takes 30 seconds)"
  }

  object English extends ViewMessages with BaseEnglish
}
