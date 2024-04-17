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

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

case class DraftMovement(ern: String,
                         draftId: String,
                         data: JsObject,
                         submissionFailures: Seq[MovementSubmissionFailure],
                         lastUpdated: Instant,
                         hasBeenSubmitted: Boolean,
                         submittedDraftId: Option[String])

object DraftMovement {

  val writes: Writes[DraftMovement] = Json.writes

  val reads: Reads[DraftMovement] = (
    (__ \ "ern").read[String] and
      (__ \ "draftId").read[String] and
      (__ \ "data").read[JsObject] and
      (__ \ "submissionFailures").read[Seq[MovementSubmissionFailure]] and
      (__ \ "lastUpdated").read[Instant](MongoJavatimeFormats.instantReads) and
      (__ \ "hasBeenSubmitted").read[Boolean] and
      (__ \ "submittedDraftId").readNullable[String]
    )(DraftMovement.apply _)

  implicit val format: Format[DraftMovement] = Format(reads, writes)

}
