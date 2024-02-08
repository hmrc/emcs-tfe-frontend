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

package fixtures.messages

object ViewMessageMessages {

  sealed trait ViewMessages { _: i18n =>
    val labelMessageType = "Message type"
    val labelArc = "Administrative reference code"
    val labelLrn = "Local reference number"

    val messageTypeDescriptionForIE819 = "Alert received"

    val viewMovementLinkText = "View movement"
    val printMessageLinkText = "Print message"
    val deleteMessageLinkText = "Delete message"
    val reportOfReceiptLinkText = "Submit report of receipt"
    val explainDelayLinkText = "Create explanation for a delay and provide information"
  }

  object English extends ViewMessages with EN
}
