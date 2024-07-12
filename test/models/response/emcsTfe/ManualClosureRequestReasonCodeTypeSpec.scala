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

package models.response.emcsTfe

import base.SpecBase
import models.response.emcsTfe.ManualClosureRequestReasonCodeType._

class ManualClosureRequestReasonCodeTypeSpec extends SpecBase {

  "ManualClosureRequestReasonCodeType" should {

    "return the correct values" in {

      Other.toString mustBe "0"
      ExportClosedButNoIE518Available.toString mustBe "1"
      ConsigneeNoLongerConnectedToEMCS.toString mustBe "2"
      ExemptedConsignee.toString mustBe "3"
      ExitConfirmedButNoIE829Submitted.toString mustBe "4"
      NoMovementButCancellationNoLongerPossible.toString mustBe "5"
      MultipleIssuancesOfeADsOreSADsForASingleMovement.toString mustBe "6"
      eADOreSADDoesNotCoverActualMovement.toString mustBe "7"
      ErroneousReportOfReceipt.toString mustBe "8"
      ErroneousRejectionOfAneADOreSAD.toString mustBe "9"

    }
  }
}
