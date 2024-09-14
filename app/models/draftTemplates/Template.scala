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
import play.api.libs.json._

import java.time.Instant

case class Template(
                     ern: String,
                     templateId: String,
                     templateName: String,
                     data: JsObject,
                     lastUpdated: Instant
                   ) {

  def destinationType: MovementScenario =
    (data \ "info" \ "destinationType")
      .as[MovementScenario]

  def consignee: Option[String] =
    (data \ "consignee" \ "exciseRegistrationNumber")
      .asOpt[String]
      .orElse((data \ "consignee" \ "consigneeExportVat")
        .asOpt[String])
}

object Template {
  implicit val format: Format[Template] = Json.format
}

