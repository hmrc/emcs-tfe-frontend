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

import models.response.emcsTfe.draftMovement.MovementSubmissionFailure
import play.api.libs.json.{JsObject, Json}

trait MovementSubmissionFailureFixtures extends BaseFixtures {

  val movementSubmissionFailureModelMax: MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = "errorType",
    errorReason = "errorReason",
    errorLocation = Some("errorLocation"),
    originalAttributeValue = Some("originalAttributeValue"),
    hasBeenFixed = true
  )

  val movementSubmissionFailureJsonMax: JsObject = Json.obj(
    "errorType" -> "errorType",
    "errorReason" -> "errorReason",
    "errorLocation" -> "errorLocation",
    "originalAttributeValue" -> "originalAttributeValue",
    "hasBeenFixed" -> true
  )

  val movementSubmissionFailureModelMin: MovementSubmissionFailure = MovementSubmissionFailure(
    errorType = "errorType",
    errorReason = "errorReason",
    errorLocation = None,
    originalAttributeValue = None,
    hasBeenFixed = true
  )

  val movementSubmissionFailureJsonMin: JsObject = Json.obj(
    "errorType" -> "errorType",
    "errorReason" -> "errorReason",
    "hasBeenFixed" -> true
  )

}
