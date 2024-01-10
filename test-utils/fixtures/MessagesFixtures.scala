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

import models.response.emcsTfe.messages.{GetMessagesResponse, Message, MessagesData}

import java.time.LocalDateTime

trait MessagesFixtures extends BaseFixtures {

  lazy val message1: Message = Message(
    uniqueMessageIdentifier = 1001L,
    dateCreatedOnCore = LocalDateTime.of(2024,1,5,0,0,0,0),
    arc = Some("ARC1001"),
    messageType = "IE818",
    relatedMessageType = None,
    sequenceNumber = Some(1),
    readIndicator = false,
    lrn = Some("LRN1001"),
    messageRole = 0,
    submittedByRequestingTrader = true
  )

  lazy val message2: Message = Message(
    uniqueMessageIdentifier = 1002L,
    dateCreatedOnCore = LocalDateTime.of(2024, 1, 6, 0, 0, 0, 0),
    arc = None,
    messageType = "IE818",
    relatedMessageType = None,
    sequenceNumber = Some(1),
    readIndicator = true,
    lrn = Some("LRN1002"),
    messageRole = 0,
    submittedByRequestingTrader = false
  )

  lazy val getMessageResponse: GetMessagesResponse = GetMessagesResponse(
    dateTime = "",
    exciseRegistrationNumber = testErn,
    messagesData = MessagesData(
      messages = Seq(message1, message2), totalNumberOfMessagesAvailable = 2
    )
  )


}