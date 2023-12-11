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

import models.response.emcsTfe.{GetMovementListItem, GetMovementListResponse}
import play.api.libs.json.{JsValue, Json}

import java.time.LocalDateTime

trait MovementListFixtures extends BaseFixtures {

  lazy val movement1: GetMovementListItem = GetMovementListItem(
    arc = "18GB00000000000232361",
    dateOfDispatch = LocalDateTime.parse("2009-01-26T14:11:00"),
    movementStatus = "Accepted",
    otherTraderID = "ABCD1234"
  )

  lazy val movement1Json: JsValue = Json.obj(
    "arc" -> "18GB00000000000232361",
    "dateOfDispatch" -> LocalDateTime.parse("2009-01-26T14:11:00"),
    "movementStatus" -> "Accepted",
    "otherTraderID" -> "ABCD1234"
  )

  lazy val movement2: GetMovementListItem = GetMovementListItem(
    arc = "GBTR000000EMCS1000040",
    dateOfDispatch = LocalDateTime.parse("2009-01-26T14:12:00"),
    movementStatus = "Accepted",
    otherTraderID = "ABCD1234"
  )

  lazy val movement2Json: JsValue = Json.obj(
    "arc" -> "GBTR000000EMCS1000040",
    "dateOfDispatch" -> "2009-01-26T14:12:00",
    "movementStatus" -> "Accepted",
    "otherTraderID" -> "ABCD1234"
  )

  lazy val getMovementListResponse: GetMovementListResponse = GetMovementListResponse(Seq(movement1, movement2), 2)

  lazy val getMovementListJson: JsValue = Json.obj(
    "movements" -> Json.arr(
      movement1Json,
      movement2Json
    ),
    "count" -> 2
  )

}