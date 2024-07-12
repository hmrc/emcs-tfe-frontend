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
import models.response.emcsTfe.GlobalConclusionofReceiptReasonCodeType._

class GlobalConclusionofReceiptReasonCodeTypeSpec extends SpecBase {

  "GlobalConclusionofReceiptReasonCodeType" should {

    "return the correct values" in {

      ReceiptAcceptedAndSatisfactory.toString mustBe "1"
      ReceiptAcceptedAlthoughUnsatisfactory.toString mustBe "2"
      ReceiptRefused.toString mustBe "3"
      ReceiptPartiallyRefused.toString mustBe "4"
      ExitAcceptedAndSatisfactory.toString mustBe "21"
      ExitAcceptedAlthoughUnsatisfactory.toString mustBe "22"
      ExitRefused.toString mustBe "23"

    }
  }
}
