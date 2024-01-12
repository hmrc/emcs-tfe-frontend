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

package models.messages

import base.SpecBase
import fixtures.messages.ViewAllMessagesMessages.English
import models.messages.MessagesSortingSelectOption._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.Messages

class MessagesSortingSelectOptionSpec extends SpecBase with GuiceOneAppPerSuite {

  "MessagesSortingSelectOption" should {

    "be constructed from all valid codes" in {

      MessagesSortingSelectOption.apply("messageTypeA") mustBe MessageTypeA
      MessagesSortingSelectOption.apply("messageTypeD") mustBe MessageTypeD
      MessagesSortingSelectOption.apply("dateReceivedA") mustBe DateReceivedA
      MessagesSortingSelectOption.apply("dateReceivedD") mustBe DateReceivedD
      MessagesSortingSelectOption.apply("identifierA") mustBe IdentifierA
      MessagesSortingSelectOption.apply("identifierD") mustBe IdentifierD
      MessagesSortingSelectOption.apply("readIndicatorA") mustBe ReadIndicatorA
      MessagesSortingSelectOption.apply("readIndicatorD") mustBe ReadIndicatorD
    }

    "throws illegal argument error when sorting option cannot be mapped to MessagesSortingSelectOption" in {
      intercept[IllegalArgumentException](MessagesSortingSelectOption.apply("OtherSort")).getMessage mustBe
        s"Invalid argument of 'OtherSort' received which can not be mapped to a MessagesSortingSelectOption"
    }

    s"when being rendered in lang code of '${English.lang.code}'" must {

      implicit val msgs: Messages = messages(Seq(English.lang))

      "output the correct messages for MessageTypeA" in {

        msgs(MessageTypeA.displayName) mustBe English.sortMessageTypeA
      }

      "output the correct messages for MessageTypeD" in {

        msgs(MessageTypeD.displayName) mustBe English.sortMessageTypeD
      }

      "output the correct messages for DateReceivedA" in {

        msgs(DateReceivedA.displayName) mustBe English.sortDateReceivedA
      }

      "output the correct messages for DateReceivedD" in {

        msgs(DateReceivedD.displayName) mustBe English.sortDateReceivedD
      }

      "output the correct messages for IdentifierA" in {

        msgs(IdentifierA.displayName) mustBe English.sortIdentifierA
      }

      "output the correct messages for IdentifierD" in {

        msgs(IdentifierD.displayName) mustBe English.sortIdentifierD
      }

      "output the correct messages for ReadIndicatorA" in {

        msgs(ReadIndicatorA.displayName) mustBe English.sortReadIndicatorA
      }

      "output the correct messages for ReadIndicatorD" in {

        msgs(ReadIndicatorD.displayName) mustBe English.sortReadIndicatorD
      }
    }
  }
}
