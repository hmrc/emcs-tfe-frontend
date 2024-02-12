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

package viewmodels.helpers.messages

import base.SpecBase
import fixtures.MessagesFixtures
import fixtures.messages.ViewMessageMessages
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.i18n.Messages

class MessagesHelperSpec extends SpecBase with MessagesFixtures {

  implicit lazy val msgs: Messages = messages(Seq(ViewMessageMessages.English.lang))

  object Helper extends MessagesHelper()

  ".messageDescriptionKey" in {
    Helper.messageDescriptionKey(ie819ReceivedAlert.message) shouldBe "messages.IE819.false.0.description"
  }

  ".messageTypeKey" when {
    "when a message type is present" in {
      Helper.messageTypeKey(ie819ReceivedAlert.message) shouldBe Some("messages.IE819.false.0.messageType")
    }
    "when a message type is not present" in {
      Helper.messageTypeKey(ie829ReceivedCustomsAcceptance.message) shouldBe None
    }
  }

  ".additionalInformationKey" when {
    "when a message paragraph is present" in {
      Helper.additionalInformationKey(ie829ReceivedCustomsAcceptance.message) shouldBe Some("messages.IE829.false.0.paragraph")
    }
    "when a message paragraph is not present" in {
      Helper.additionalInformationKey(ie810ReceivedCancellation.message) shouldBe None
    }
  }

}
