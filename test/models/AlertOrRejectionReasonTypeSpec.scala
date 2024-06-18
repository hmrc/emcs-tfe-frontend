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

package models

import base.SpecBase
import models.response.emcsTfe.AlertOrRejectionReasonType._

class AlertOrRejectionReasonTypeSpec extends SpecBase {

  "AlertOrRejectionReasonType" must {

    "have the correct underlying enum values" in {
      Other.toString mustBe "0"
      EADNotConcernRecipient.toString mustBe "1"
      ProductDoesNotMatchOrder.toString mustBe "2"
      QuantityDoesNotMatchOrder.toString mustBe "3"
    }
  }
}
