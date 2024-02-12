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

import models.response.emcsTfe.messages.Message
import play.api.i18n.Messages

import javax.inject.Inject

class MessagesHelper @Inject()() {

  private def messageKey(aMessage: Message): String =
    if (aMessage.isAnErrorMessage) {
      s"messages.${aMessage.messageType}.${aMessage.submittedByRequestingTrader}.${aMessage.relatedMessageType.get}.${aMessage.messageRole}"
    } else {
      s"messages.${aMessage.messageType}.${aMessage.submittedByRequestingTrader}.${aMessage.messageRole}"
    }

  def messageDescriptionKey(aMessage: Message): String =
    s"${messageKey(aMessage)}.description"

  def messageTypeKey(aMessage: Message)(implicit messages: Messages): Option[String] =
    Some(s"${messageKey(aMessage)}.messageType").filter(messages.isDefinedAt)

  def additionalInformationKey(aMessage: Message)(implicit messages: Messages): Option[String] =
    Some(s"${messageKey(aMessage)}.paragraph").filter(messages.isDefinedAt)
}
