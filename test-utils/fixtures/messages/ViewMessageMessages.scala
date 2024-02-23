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
    val explainDelayLinkText = "Submit an explanation of a delay"
    val changeDestinationLinkText = "Submit a change of destination"
    val explainShortageExcessLinkText = "Submit explanation for a shortage or excess"
    val helplineLink = "Contact the HMRC excise helpline"
    val helplinePostLink = "if you need more help or advice."
    val helpline = s"$helplineLink (opens in new tab) $helplinePostLink"
    val thirdParty = "If you used commercial software for your submission, please correct these errors with the same software that you used for the submission."
  }

  sealed trait IE810SubmissionFailureMessages {
    val cancelMovementPreLink = "If you still want to"
    val cancelMovementLink = "cancel this movement"
    val cancelMovementPostLink = "you can submit a new cancellation with the errors corrected."
    val cancelMovement = s"$cancelMovementPreLink $cancelMovementLink $cancelMovementPostLink"
    val changeDestinationPreLink = "However you can only cancel a movement up to the date and time recorded on the electronic administrative document (eAD). If the date and time on the eAD has passed, you can choose to"
    val changeDestinationLink = "submit a change of destination"
    val changeDestination = s"$changeDestinationPreLink $changeDestinationLink."
  }

  sealed trait IE837SubmissionFailureMessages {
    val submitNewExplainDelayPreLink = "You need to"
    val submitNewExplainDelayLink = "submit a new explanation of a delay"
    val submitNewExplainDelay = s"$submitNewExplainDelayPreLink $submitNewExplainDelayLink."
  }

  sealed trait IE871SubmissionFailureMessages { _: i18n =>
    val submitNewExplanationOfShortageOrExcessPreLink = "You need to"
    val submitNewExplanationOfShortageOrExcessLink = "submit a new explanation of a shortage or excess"
    val submitNewExplanationOfShortageOrExcess = s"$submitNewExplanationOfShortageOrExcessPreLink $submitNewExplanationOfShortageOrExcessLink."
  }

  sealed trait IE818SubmissionFailureMessages { _: i18n =>
    val submitNewReportOfReceiptPreLink = "You need to"
    val submitNewReportOfReceiptLink = "submit a new report of receipt"
    val submitNewReportOfReceipt = s"$submitNewReportOfReceiptPreLink $submitNewReportOfReceiptLink."
  }

  sealed trait IE819SubmissionFailureMessages {
    val submitNewAlertRejectionPreLink = "To correct any errors you must"
    val submitNewAlertRejectionLink = "submit a new alert or rejection"
    val submitNewAlertRejectionPostLink = "of this movement."
    val submitNewAlertRejection = s"$submitNewAlertRejectionPreLink $submitNewAlertRejectionLink $submitNewAlertRejectionPostLink"
  }

  sealed trait IE825SubmissionFailureMessages {
    val splitMovementError = "Your split movement contained validation errors."
  }

  sealed trait IE813SubmissionFailureMessages {
    val submitNewChangeDestination = "You need to submit a new change of destination."
    val submitNewChangeDestinationLink = "submit a new change of destination"
  }

  sealed trait IE815SubmissionFailureMessages {
    val movementInformationHeading = "Movement information"

    val submitNewMovementSingularError = "The error cannot be fixed, so you need to create a new movement."
    val submitNewMovementMultipleErrors = "At least one of these errors cannot be fixed, so you need to create a new movement."
    val createNewMovementLink = "create a new movement"

    val updateMovementLink = "Update and resubmit the movement."
    val warningTextWhenFixable = "If you delete this message you will not be able to access the original draft movement details and must create a new movement."

    val arcText = "An ARC will only be created for a movement once it has been successfully submitted."

    val ie815thirdParty = "If you used commercial software for your submission, please correct these errors with the same software that you used for the submission, or"
    val ie815thirdPartyLink = "create a new movement"
  }

  object English extends ViewMessages
    with IE810SubmissionFailureMessages
    with IE837SubmissionFailureMessages
    with IE871SubmissionFailureMessages
    with IE818SubmissionFailureMessages
    with IE819SubmissionFailureMessages
    with IE813SubmissionFailureMessages
    with IE815SubmissionFailureMessages
    with IE825SubmissionFailureMessages
    with EN
}
