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

package models.response.emcsTfe.draftMovement

import base.SpecBase
import fixtures.DraftMovementsFixtures
import play.api.libs.json.Json

class DraftMovementSpec extends SpecBase with DraftMovementsFixtures {

  "DraftMovement" when {

    "given max values" should {

      "write to Json" in {

        Json.toJson(draftMovementModelMax) mustBe draftMovementJsonMax
      }

      "read from Json" in {

        draftMovementJsonMax.as[DraftMovement] mustBe draftMovementModelMax
      }
    }

    "given min values" should {

      "write to Json" in {

        Json.toJson(draftMovementModelMin) mustBe draftMovementJsonMin
      }

      "read from Json" in {

        draftMovementJsonMin.as[DraftMovement] mustBe draftMovementModelMin
      }
    }
  }
}
