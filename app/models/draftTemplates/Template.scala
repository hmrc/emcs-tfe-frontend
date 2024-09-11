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

import models.movementScenario.MovementScenario
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Template(
                     templateId: String,
                     templateName: String,
                     destinationType: MovementScenario,
                     consigneeErn: Option[String]
                   )

object Template {
  implicit val reads: Reads[Template] = (
    (JsPath \ "templateId").read[String] and
      (JsPath \ "templateName").read[String] and
      (JsPath \ "data" \ "info" \ "destinationType").read[MovementScenario] and
      (
        (JsPath \ "data" \ "consignee" \ "exciseRegistrationNumber").read[String].map(Some(_)) or
          (JsPath \ "data" \ "consignee" \ "consigneeExportVat").readNullable[String]
        )
    )(Template.apply _)

  implicit val writes: OWrites[Template] = Json.writes[Template]
}
