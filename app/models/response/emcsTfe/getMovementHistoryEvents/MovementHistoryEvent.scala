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
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsValue, Reads, __}

case class MovementHistoryEvent(
                                 eventType: EventTypes,
                                 eventDate: String,
                                 sequenceNumber: Int,
                                 messageRole: Int,
                                 upstreamArc: Option[String],
                                 isFirstEventTypeInHistory: Boolean) {

  val eventId: Int = eventDate.hashCode >>> 1
}

object MovementHistoryEvent {
  implicit val reads: Reads[MovementHistoryEvent] =
    (
      (__ \ "eventType").read[EventTypes] and
      (__ \ "eventDate").read[String] and
      (__ \ "sequenceNumber").read[Int] and
      (__ \ "messageRole").read[Int] and
      (__ \ "upstreamArc").readNullable[String] and
      Reads.pure(false)
    )(MovementHistoryEvent.apply _)

  val seqReads: Reads[Seq[MovementHistoryEvent]] = (json: JsValue) => {
    json.validate[Seq[MovementHistoryEvent]](Reads.seq[MovementHistoryEvent])
      .map { events =>
        val seenEventTypes = scala.collection.mutable.Set[EventTypes]()

        events
          .sortBy(_.eventDate)
          .map { event =>
            if (seenEventTypes.contains(event.eventType)) {
              event.copy(isFirstEventTypeInHistory = false)
            } else {
              seenEventTypes.add(event.eventType)
              event.copy(isFirstEventTypeInHistory = true)
            }
        }
    }
  }
}
