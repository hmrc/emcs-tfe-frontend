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

package fixtures

import models.response.emcsTfe.messages.{GetMessagesResponse, Message}

import java.time.LocalDateTime

trait MessagesFixtures extends BaseFixtures {

  def createMessage(messageType: String, optRelatedMessageType: Option[String] = None): Message =
    Message(
      uniqueMessageIdentifier = 1234L,
      dateCreatedOnCore = LocalDateTime.of(2024, 1, 5, 0, 0, 0, 0),
      arc = Some("ARC1001"),
      messageType = messageType,
      relatedMessageType = optRelatedMessageType,
      sequenceNumber = Some(1),
      readIndicator = false,
      lrn = Some("LRN1001"),
      messageRole = 0,
      submittedByRequestingTrader = false
    )

  lazy val message1: Message = Message(
    uniqueMessageIdentifier = 1001L,
    dateCreatedOnCore = LocalDateTime.of(2024,1,5,0,0,0,0),
    arc = Some("ARC1001"),
    messageType = "IE819",
    relatedMessageType = None,
    sequenceNumber = Some(1),
    readIndicator = false,
    lrn = Some("LRN1001"),
    messageRole = 0,
    submittedByRequestingTrader = false
  )

  lazy val message2: Message = Message(
    uniqueMessageIdentifier = 1002L,
    dateCreatedOnCore = LocalDateTime.of(2024, 1, 6, 0, 0, 0, 0),
    arc = None,
    messageType = "IE704",
    relatedMessageType = Some("IE818"),
    sequenceNumber = Some(1),
    readIndicator = true,
    lrn = Some("LRN1001"),
    messageRole = 0,
    submittedByRequestingTrader = true
  )

  lazy val getMessageResponse: GetMessagesResponse = GetMessagesResponse(
    messages = Seq(message1, message2),
    totalNumberOfMessagesAvailable = 3
  )

  def constructMessageResponse(numberOfMessages: Int): GetMessagesResponse = {
    GetMessagesResponse(
      messages = Seq.fill(numberOfMessages)(message1),
      totalNumberOfMessagesAvailable = numberOfMessages
    )
  }

  case class TestMessage(
                          message: Message,
                          messageTitle: String,
                          messageSubTitle: Option[String],
                          reportOfReceiptLink: Boolean = false,
                          explainDelayLink: Boolean = false,
                          changeDestinationLink: Boolean = false
                        )

  val ie819ReceivedAlert = TestMessage(
    message = createMessage("IE819").copy(messageRole = 0, submittedByRequestingTrader = false),
    messageTitle = "Alert or rejection received",
    messageSubTitle = Some("Alert received")
  )

  val ie819ReceivedReject = TestMessage(
    message = createMessage("IE819").copy(messageRole = 1, submittedByRequestingTrader = false),
    messageTitle = "Alert or rejection received",
    messageSubTitle = Some("Rejection received")
  )

  val ie819SubmittedAlert = TestMessage(
    message = createMessage("IE819").copy(messageRole = 0, submittedByRequestingTrader = true),
    messageTitle = "Alert or rejection submitted successfully",
    messageSubTitle = Some("Alert successful submission")
  )

  val ie819SubmittedReject = TestMessage(
    message = createMessage("IE819").copy(messageRole = 1, submittedByRequestingTrader = true),
    messageTitle = "Alert or rejection submitted successfully",
    messageSubTitle = Some("Rejection successful submission")
  )

  val ie810ReceivedCancellation = TestMessage(
    message = createMessage("IE810").copy(messageRole = 0, submittedByRequestingTrader = false),
    messageTitle = "Cancellation of movement received",
    messageSubTitle = None
  )

  val ie810SubmittedCancellation = TestMessage(
    message = createMessage("IE810").copy(messageRole = 0, submittedByRequestingTrader = true),
    messageTitle = "Cancellation of movement submitted successfully",
    messageSubTitle = None
  )

  val ie813ReceivedChangeDestination = TestMessage(
    message = createMessage("IE813").copy(messageRole = 0, submittedByRequestingTrader = false),
    messageTitle = "Change of destination",
    messageSubTitle = None,
    reportOfReceiptLink = true,
    explainDelayLink = true
  )

  val ie813SubmittedChangeDestination = TestMessage(
    message = createMessage("IE813").copy(messageRole = 0, submittedByRequestingTrader = true),
    messageTitle = "Change of destination submitted successfully",
    messageSubTitle = None
  )

  val ie829ReceivedCustomsAcceptance = TestMessage(
    message = createMessage("IE829").copy(messageRole = 0, submittedByRequestingTrader = false),
    messageTitle = "Customs acceptance of movement for export",
    messageSubTitle = None,
    changeDestinationLink = true
  )

  val ie704ErrorCancellationIE810 = TestMessage(
    message = createMessage("IE704", optRelatedMessageType = Some("IE810")),
    messageTitle = "Error with cancellation",
    messageSubTitle = None
  )
}