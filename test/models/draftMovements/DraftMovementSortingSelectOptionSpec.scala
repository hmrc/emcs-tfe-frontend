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

package models.draftMovements

import base.SpecBase
import fixtures.messages.DraftMovementsMessages.English
import models.draftMovements.DraftMovementSortingSelectOption.{LrnAscending, LrnDescending, Newest, Oldest}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.Messages

class DraftMovementSortingSelectOptionSpec extends SpecBase with GuiceOneAppPerSuite {

  "DraftMovementSortingSelectOption" should {

    "be constructed from all valid codes" in {

      DraftMovementSortingSelectOption.apply("lrnAsc") mustBe LrnAscending
      DraftMovementSortingSelectOption.apply("lrnDesc") mustBe LrnDescending
      DraftMovementSortingSelectOption.apply("newest") mustBe Newest
      DraftMovementSortingSelectOption.apply("oldest") mustBe Oldest
    }

    "throws illegal argument error when the code can't be mapped to a search type" in {
      intercept[IllegalArgumentException](DraftMovementSortingSelectOption.apply("INVALID VALUE")).getMessage mustBe
        s"Invalid argument of 'INVALID VALUE' received which can not be mapped to a DraftMovementSortingSelectOption"
    }

    s"when being rendered in lang code of '${English.lang.code}'" must {

      implicit val msgs: Messages = messages(Seq(English.lang))

      "output the correct messages for ARC" in {

        msgs(LrnAscending.displayName) mustBe English.lrnAscending
      }

      "output the correct messages for LRN" in {

        msgs(LrnDescending.displayName) mustBe English.lrnDescending
      }

      "output the correct messages for ERN" in {

        msgs(Newest.displayName) mustBe English.newest
      }

      "output the correct messages for Transporter" in {

        msgs(Oldest.displayName) mustBe English.oldest
      }
    }
  }
}
