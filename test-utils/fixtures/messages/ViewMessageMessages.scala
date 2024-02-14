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
    val changeDestinationLinkText = "Submit new change of destination"
    val helpline = "Contact the HMRC excise helpline (opens in new tab) if you need more help or advice."
    val helplineLink = "Contact the HMRC excise helpline (opens in new tab)"
    val thirdParty = "If you used commercial software for your submission, please correct these errors with the same software that you used for the submission."
  }

  sealed trait IE810SubmissionFailureMessages { _: i18n =>
    val cancelMovement = "If you still want to cancel this movement you can submit a new cancellation with the errors corrected."
    val changeDestination = "However you can only cancel a movement up to the date and time recorded on the electronic administrative document (eAD). If the date and time on the eAD has passed, you can choose to submit a change of destination."
    val cancelMovementLink = "cancel this movement"
    val changeDestinationLink = "change of destination"
  }

  sealed trait IE837SubmissionFailureMessages { _: i18n =>
    val submitNewExplainDelay = "You need to submit a new explanation of a delay."
    val submitNewExplainDelayLink = "submit a new explanation of a delay"
  }

  object English extends ViewMessages with IE810SubmissionFailureMessages with IE837SubmissionFailureMessages with EN
}
