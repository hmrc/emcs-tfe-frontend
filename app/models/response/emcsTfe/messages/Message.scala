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

package models.response.emcsTfe.messages

import play.api.libs.json.{Format, Json}

import java.time.LocalDateTime

case class Message(
                    uniqueMessageIdentifier: Long,
                    dateCreatedOnCore: LocalDateTime,
                    arc: Option[String],
                    messageType: String,
                    relatedMessageType: Option[String],
                    sequenceNumber: Option[Int],
                    readIndicator: Boolean,
                    lrn: Option[String],
                    messageRole: Int,
                    submittedByRequestingTrader: Boolean
                  ) {

  val isAnErrorMessage: Boolean = messageType == "IE704"

}

object Message {
  implicit val format: Format[Message] = Json.format[Message]
}
