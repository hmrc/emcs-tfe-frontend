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

package uk.gov.hmrc.emcstfefrontend.fixtures

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.emcstfefrontend.models.response.emcsTfe.{GetMovementListItem, GetMovementListResponse}

import java.time.Instant

trait MovementListFixtures extends BaseFixtures {

  lazy val movement1 = GetMovementListItem(
    arc = "18GB00000000000232361",
    sequenceNumber = 1,
    consignorName = "Mr Consignor 801",
    dateOfDispatch = Instant.parse("2009-01-26T14:11:00.943Z"),
    movementStatus = "Accepted",
    destinationId = "ABC1234567832",
    consignorLanguageCode = "en"
  )

  lazy val movement1Json = Json.obj(
    "arc" -> "18GB00000000000232361",
    "sequenceNumber" -> 1,
    "consignorName" -> "Mr Consignor 801",
    "dateOfDispatch" -> Instant.parse("2009-01-26T14:11:00.943Z"),
    "movementStatus" -> "Accepted",
    "destinationId" -> "ABC1234567832",
    "consignorLanguageCode" -> "en"
  )

  lazy val movement2 = GetMovementListItem(
    arc = "GBTR000000EMCS1000040",
    sequenceNumber = 1,
    consignorName = "Mr Consignor 801",
    dateOfDispatch = Instant.parse("2009-01-26T14:12:00.943Z"),
    movementStatus = "Accepted",
    destinationId = "ABC1234567831",
    consignorLanguageCode = "en"
  )

  lazy val movement2Json = Json.obj(
    "arc" -> "GBTR000000EMCS1000040",
    "sequenceNumber" -> 1,
    "consignorName" -> "Mr Consignor 801",
    "dateOfDispatch" -> "2009-01-26T14:12:00.943Z",
    "movementStatus" -> "Accepted",
    "destinationId" -> "ABC1234567831",
    "consignorLanguageCode" -> "en"
  )

  lazy val getMovementListResponse: GetMovementListResponse = GetMovementListResponse(Seq(movement1, movement2))

  lazy val getMovementListJson: JsValue = Json.obj(
    "movements" -> Json.arr(
      movement1Json,
      movement2Json
    )
  )

}