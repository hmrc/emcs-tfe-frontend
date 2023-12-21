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
import fixtures.messages.ViewAllMovementsMessages.English
import models.MovementSortingSelectOption.{ArcAscending, ArcDescending, Newest, Oldest}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.Messages

class MovementSortingSelectOptionSpec extends SpecBase with GuiceOneAppPerSuite {

  "MovementSortingSelectOption" should {

    "be constructed from all valid codes" in {

      MovementSortingSelectOption.apply("arcAsc") mustBe ArcAscending
      MovementSortingSelectOption.apply("arcDesc") mustBe ArcDescending
      MovementSortingSelectOption.apply("newest") mustBe Newest
      MovementSortingSelectOption.apply("oldest") mustBe Oldest
    }

    "throws illegal argument error when EPC can't be mapped to GoodsType" in {
      intercept[IllegalArgumentException](MovementSortingSelectOption.apply("OtherSort")).getMessage mustBe
        s"Invalid argument of 'OtherSort' received which can not be mapped to a MovementSortingSelectOption"
    }

    s"when being rendered in lang code of '${English.lang.code}'" must {

      implicit val msgs: Messages = messages(Seq(English.lang))

      "output the correct messages for ArcAscending" in {

        msgs(ArcAscending.displayName) mustBe English.sortArcAscending
      }

      "output the correct messages for ArcDescending" in {

        msgs(ArcDescending.displayName) mustBe English.sortArcDescending
      }

      "output the correct messages for Newest" in {

        msgs(Newest.displayName) mustBe English.sortNewest
      }

      "output the correct messages for Oldest" in {

        msgs(Oldest.displayName) mustBe English.sortOldest
      }
    }
  }
}
