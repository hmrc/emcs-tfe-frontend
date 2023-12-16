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
import models.MovementSearchSelectOption._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.Messages

class MovementSearchSelectOptionSpec extends SpecBase with GuiceOneAppPerSuite {

  "MovementSearchSelectOption" should {

    "be constructed from all valid codes" in {

      MovementSearchSelectOption.apply("arc") mustBe ARC
      MovementSearchSelectOption.apply("lrn") mustBe LRN
      MovementSearchSelectOption.apply("otherTraderId") mustBe ERN
      MovementSearchSelectOption.apply("transporterTraderName") mustBe Transporter
    }

    "throws illegal argument error when the code can't be mapped to a search type" in {
      intercept[IllegalArgumentException](MovementSearchSelectOption.apply("OtherSearch")).getMessage mustBe
        s"Invalid argument of 'OtherSearch' received which can not be mapped to a MovementSearchSelectOption"
    }

    s"when being rendered in lang code of '${English.lang.code}'" must {

      implicit val msgs: Messages = messages(Seq(English.lang))

      "output the correct messages for ARC" in {

        msgs(ARC.displayName) mustBe English.searchKeyArc
      }

      "output the correct messages for LRN" in {

        msgs(LRN.displayName) mustBe English.searchKeyLrn
      }

      "output the correct messages for ERN" in {

        msgs(ERN.displayName) mustBe English.searchKeyErn
      }

      "output the correct messages for Transporter" in {

        msgs(Transporter.displayName) mustBe English.searchKeyTransporter
      }
    }
  }
}
