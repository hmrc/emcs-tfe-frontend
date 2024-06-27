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
import models.response.emcsTfe.InterruptionReasonType._

class InterruptionReasonTypeSpec extends SpecBase {

  "InterruptionReasonType" should {

    "return the correct values" in {

      Other.toString mustBe "0"
      FraudSuspected.toString mustBe "1"
      GoodsDestroyed.toString mustBe "2"
      GoodsLostOrStolen.toString mustBe "3"
      InterruptionRequestAtControl.toString mustBe "4"

    }
  }
}
