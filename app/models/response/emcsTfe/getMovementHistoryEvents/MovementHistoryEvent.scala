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

package models.response.emcsTfe.getMovementHistoryEvents

import models.EventTypes
import play.api.libs.json.{Format, JsValue, Json, Reads}

case class MovementHistoryEvent(eventType: EventTypes, eventDate: String, sequenceNumber: Int, messageRole: Int, upstreamArc: Option[String]) {
  val eventId: Int = eventDate.hashCode >>> 1
}

object MovementHistoryEvent {
  implicit val format: Format[MovementHistoryEvent] = Json.format[MovementHistoryEvent]

  val seqReads: Reads[Seq[MovementHistoryEvent]] = (json: JsValue) => {
    json.validate[Seq[MovementHistoryEvent]](Reads.seq[MovementHistoryEvent])
  }
}
