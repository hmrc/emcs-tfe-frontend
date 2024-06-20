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

import models.response.emcsTfe.DelayReasonType

object DelayReasonMessages {

  sealed trait Messages extends BaseEnglish { _ : i18n =>
    val other = "Other"
    val cancelledCommercialTransaction = "The commercial transaction has been cancelled"
    val pendingCommercialTransaction = "The commercial transaction is pending"
    val ongoingInvestigation = "Ongoing investigation by officials"
    val badWeather = "Bad weather conditions"
    val strikes = "Strike action"
    val accident = "An accident in transit"

    val rorType = "Submitting a report of receipt"
    val codType = "Providing a consignee or change of destination for the movement"

    val reason: DelayReasonType => String = {
      case DelayReasonType.Other => other
      case DelayReasonType.CancelledCommercialTransaction => cancelledCommercialTransaction
      case DelayReasonType.PendingCommercialTransaction => pendingCommercialTransaction
      case DelayReasonType.OngoingInvestigation => ongoingInvestigation
      case DelayReasonType.BadWeather => badWeather
      case DelayReasonType.Strikes => strikes
      case DelayReasonType.Accident => accident
    }

    val messageType: Int => String = {
      case 1 => rorType
      case 2 => codType
    }
  }

  object English extends Messages with EN

}
