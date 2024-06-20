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
import uk.gov.hmrc.emcstfe.models.cancellationOfMovement.CancellationReasonType._

class CancellationReasonTypeSpec extends SpecBase {


  "CancellationReasonType" should {

    "return the correct values" in {

      Other.toString mustBe "0"
      TypingError.toString mustBe "1"
      SaleInterrupted.toString mustBe "2"
      DuplicateMovement.toString mustBe "3"
      LateMovement.toString mustBe "4"

    }
  }
}
