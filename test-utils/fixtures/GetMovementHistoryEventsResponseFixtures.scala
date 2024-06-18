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

import models.EventTypes._
import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import play.api.libs.json.{JsValue, Json}

trait GetMovementHistoryEventsResponseFixtures { _: BaseFixtures =>

  lazy val getMovementHistoryEventsModel: Seq[MovementHistoryEvent] = Seq(
    MovementHistoryEvent(
      eventType = IE813,
      eventDate = "2023-12-01T15:00:00",
      sequenceNumber = 1,
      messageRole = 1,
      upstreamArc = Some(testArc),
      isFirstEventTypeInHistory = true
    ),
    MovementHistoryEvent(
      eventType = IE813,
      eventDate = "2023-12-02T13:00:00",
      sequenceNumber = 2,
      messageRole = 1,
      upstreamArc = Some(testArc),
      isFirstEventTypeInHistory = false
    ),
    ie818Event
  )

  val getMovementHistoryEventsResponseInputJson: JsValue = Json.arr(
    Json.obj(
      "eventType" -> "IE813",
      "eventDate" -> "2023-12-01T15:00:00",
      "sequenceNumber" -> 1,
      "messageRole" -> 1,
      "upstreamArc" -> testArc
    ),
    Json.obj(
      "eventType" -> "IE813",
      "eventDate" -> "2023-12-02T13:00:00",
      "sequenceNumber" -> 2,
      "messageRole" -> 1,
      "upstreamArc" -> testArc
    ),
    Json.obj(
      "eventType" -> "IE818",
      "eventDate" -> "2024-12-04T17:00:00",
      "sequenceNumber" -> 3,
      "messageRole" -> 1,
      "upstreamArc" -> testArc
    )
  )

  val ie801Event = MovementHistoryEvent(
    eventType = IE801,
    eventDate = "2024-12-04T17:00:00", // hash code then bit shifted right = 853932155
    sequenceNumber = 1,
    messageRole = 0,
    upstreamArc = None,
    isFirstEventTypeInHistory = true
  )

  val ie802ChangeDestinationEvent = MovementHistoryEvent(
    eventType = IE802,
    eventDate = "2024-12-04T17:00:00", // hash code then bit shifted right = 853932155
    sequenceNumber = 1,
    messageRole = 1,
    upstreamArc = None,
    isFirstEventTypeInHistory = false
  )

  val ie802EventReportOfReceipt = MovementHistoryEvent(
    eventType = IE802,
    eventDate = "2024-12-04T17:00:00", // hash code then bit shifted right = 853932155
    sequenceNumber = 1,
    messageRole = 2,
    upstreamArc = None,
    isFirstEventTypeInHistory = true
  )

  val ie802MovementDestinationEvent = MovementHistoryEvent(
    eventType = IE802,
    eventDate = "2024-12-04T17:00:00", // hash code then bit shifted right = 853932155
    sequenceNumber = 1,
    messageRole = 3,
    upstreamArc = None,
    isFirstEventTypeInHistory = false
  )

  val ie803MovementDiversionEvent = MovementHistoryEvent(
    eventType = IE803,
    eventDate = "2024-12-04T17:00:00", // hash code then bit shifted right = 853932155
    sequenceNumber = 1,
    messageRole = 1,
    upstreamArc = None,
    isFirstEventTypeInHistory = false
  )

  val ie803MovementSplitEvent = MovementHistoryEvent(
    eventType = IE803,
    eventDate = "2024-12-04T17:00:00", // hash code then bit shifted right = 853932155
    sequenceNumber = 1,
    messageRole = 2,
    upstreamArc = None,
    isFirstEventTypeInHistory = false
  )

  val ie818Event = MovementHistoryEvent(
    eventType = IE818,
    eventDate = "2024-12-04T17:00:00", // hash code then bit shifted right = 853932155
    sequenceNumber = 3,
    messageRole = 1,
    upstreamArc = Some(testArc),
    isFirstEventTypeInHistory = true
  )

  val ie819AlertEventMultipleReasons = MovementHistoryEvent(
    eventType = IE819,
    eventDate = "2023-12-18T09:00:00",
    sequenceNumber = 1,
    messageRole = 0,
    upstreamArc = None,
    isFirstEventTypeInHistory = false
  )

  val ie819AlertEvent = MovementHistoryEvent(
    eventType = IE819,
    eventDate = "2023-12-18T10:00:00",
    sequenceNumber = 1,
    messageRole = 0,
    upstreamArc = None,
    isFirstEventTypeInHistory = false
  )

  val ie819RejectionEvent = MovementHistoryEvent(
    eventType = IE819,
    eventDate = "2023-12-19T09:00:00",
    sequenceNumber = 1,
    messageRole = 0,
    upstreamArc = None,
    isFirstEventTypeInHistory = false
  )
}
