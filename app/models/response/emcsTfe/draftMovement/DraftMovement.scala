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

package models.response.emcsTfe.draftMovement

import play.api.libs.json._

import java.time.Instant

case class DraftMovement(ern: String,
                         draftId: String,
                         data: JsObject,
                         submissionFailures: Seq[MovementSubmissionFailure],
                         lastUpdated: Instant,
                         hasBeenSubmitted: Boolean,
                         submittedDraftId: Option[String])

object DraftMovement {

  implicit val format: Format[DraftMovement] = Json.format
}
