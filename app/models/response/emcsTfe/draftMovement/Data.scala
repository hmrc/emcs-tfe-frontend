/*
 * Copyright 2024 HM Revenue & Customs
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

import models.movementScenario.MovementScenario
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, __}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.LocalDate

case class Data(lrn: String,
                movementScenario: Option[MovementScenario],
                consigneeReference: Option[String],
                dispatchDate: Option[LocalDate])

object Data {
  implicit val reads: Reads[Data] = (
    (__ \ "info" \ "localReferenceNumber").read[String] and
      (__ \ "info" \ "destinationType").readNullable[MovementScenario] and
      (__ \ "consignee" \ "exciseRegistrationNumber").read[String]
        .orElse((__ \ "consignee" \ "consigneeExportVat").read[String])
        .orElse((__ \ "consignee" \ "consigneeExportEori").read[String])
        .orElse(Reads.pure("")).map(ref => if(ref == "") None else Some(ref)) and
      (__ \ "info" \ "dispatchDetails" \ "date").readNullable[LocalDate]
        .orElse((__ \ "info" \ "dispatchDetails" \ "date").readNullable[LocalDate](MongoJavatimeFormats.localDateReads))
    )(Data.apply _)
}