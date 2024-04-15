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

import models.response.emcsTfe.draftMovement.{DraftMovement, GetDraftMovementsResponse}
import play.api.libs.json.{JsObject, Json}

import java.time.{LocalDateTime, ZoneOffset}

trait DraftMovementsFixtures extends BaseFixtures with MovementSubmissionFailureFixtures {

  val draftMovementModelMax: DraftMovement = DraftMovement(
    ern = testErn,
    draftId = testDraftId,
    data = Json.obj(),
    submissionFailures = Seq(movementSubmissionFailureModelMax),
    lastUpdated = LocalDateTime.parse("2020-01-01T12:00:00").toInstant(ZoneOffset.UTC),
    hasBeenSubmitted = true,
    submittedDraftId = Some("someID")
  )

  val draftMovementJsonMax: JsObject = Json.obj(
    "ern" -> testErn,
    "draftId" -> testDraftId,
    "data" -> Json.obj(),
    "submissionFailures" -> Json.arr(movementSubmissionFailureJsonMax),
    "lastUpdated" -> LocalDateTime.parse("2020-01-01T12:00:00").toInstant(ZoneOffset.UTC),
    "hasBeenSubmitted" -> true,
    "submittedDraftId" -> "someID"
  )

  val draftMovementModelMin: DraftMovement = DraftMovement(
    ern = testErn,
    draftId = testDraftId,
    data = Json.obj(),
    submissionFailures = Seq(),
    lastUpdated = LocalDateTime.parse("2020-01-01T12:00:00").toInstant(ZoneOffset.UTC),
    hasBeenSubmitted = true,
    submittedDraftId = None
  )

  val draftMovementJsonMin: JsObject = Json.obj(
    "ern" -> testErn,
    "draftId" -> testDraftId,
    "data" -> Json.obj(),
    "submissionFailures" -> Json.arr(),
    "lastUpdated" -> LocalDateTime.parse("2020-01-01T12:00:00").toInstant(ZoneOffset.UTC),
    "hasBeenSubmitted" -> true
  )

  val getDraftMovementsResponseModelMax: GetDraftMovementsResponse = GetDraftMovementsResponse(
    count = 1,
    paginatedDrafts = Seq(draftMovementModelMin)
  )

  val getDraftMovementsResponseJsonMax: JsObject = Json.obj(
    "count" -> 1,
    "paginatedDrafts" -> Json.arr(draftMovementJsonMin)
  )

  val getDraftMovementsResponseModelMin: GetDraftMovementsResponse = GetDraftMovementsResponse(
    count = 0,
    paginatedDrafts = Seq()
  )

  val getDraftMovementsResponseJsonMin: JsObject = Json.obj(
    "count" -> 0,
    "paginatedDrafts" -> Json.arr()
  )
}