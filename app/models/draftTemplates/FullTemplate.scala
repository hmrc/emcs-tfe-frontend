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

package models.draftTemplates

import models.UserAnswers.lastUpdatedKey
import play.api.libs.functional.syntax._
import play.api.libs.json._

import java.time.Instant

case class FullTemplate(
                       ern: String,
                     templateId: String,
                     templateName: String,
                       data: JsObject,
                       lastUpdated: String = Instant.now().toString
                   )

object FullTemplate {
  implicit val reads: Reads[FullTemplate] = (
    (JsPath \ "ern").read[String] and
    (JsPath \ "templateId").read[String] and
    (JsPath \ "templateName").read[String] and
    (JsPath \ "data" ).read[JsObject] and
    (__ \ lastUpdatedKey).read[String]

    )(FullTemplate.apply _)

  val writes: OWrites[FullTemplate] = (
    (JsPath \ "ern").write[String] and
    (JsPath \ "templateId").write[String] and
    (JsPath \ "templateName").write[String] and
    (JsPath \ "data").write[JsObject] and
    (__ \ lastUpdatedKey).write[String]
    )(unlift(FullTemplate.unapply))

  implicit val format: OFormat[FullTemplate] = OFormat(reads, writes)

}
