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

import models.response.emcsTfe.getMovementHistoryEvents.{GetMovementHistoryEventsResponse, MovementHistoryEvent}
import play.api.libs.json.{JsValue, Json}

trait GetMovementHistoryEventsResponseFixtures { _: BaseFixtures =>

  val getMovementHistoryEventsResponseModel: GetMovementHistoryEventsResponse = GetMovementHistoryEventsResponse(
    dateTime = "2023-12-12T17:00:00",
    exciseRegistrationNumber = testErn,
    movementHistory = Seq[MovementHistoryEvent](
      MovementHistoryEvent(
        eventType = "IE813",
        eventDate = "2023-12-01T15:00:00",
        sequenceNumber = 1,
        messageRole = 1,
        upstreamArc = Some(testArc)
      ),
      MovementHistoryEvent(
        eventType = "IE813",
        eventDate = "2023-12-02T13:00:00",
        sequenceNumber = 2,
        messageRole = 1,
        upstreamArc = Some(testArc)
      ),
      MovementHistoryEvent(
        eventType = "IE818",
        eventDate = "2023-12-04T08:00:00",
        sequenceNumber = 3,
        messageRole = 1,
        upstreamArc = Some(testArc)
      )
    )
  )

  val getMovementHistoryEventsResponseInputJson: JsValue = Json.obj(
    "dateTime" -> "2023-12-12T17:00:00",
    "exciseRegistrationNumber" -> testErn,
    "movementHistory" -> Json.arr(
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
        "eventDate" -> "2023-12-04T08:00:00",
        "sequenceNumber" -> 3,
        "messageRole" -> 1,
        "upstreamArc" -> testArc
      )
    )
  )

}
