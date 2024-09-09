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

import play.api.libs.functional.syntax._
import play.api.libs.json._


case class TemplateList(templates: Seq[Template], count: Int)

object TemplateList {
  implicit val reads: Reads[TemplateList] = (
    (
      (JsPath).read[Seq[Template]] or // TODO: remove when pagination is implemented
        (JsPath \ "templates").read[Seq[Template]]
      ) and
      Reads.pure(30) // TODO: fix when pagination is implemented
    )(TemplateList.apply _)

  implicit val writes: OWrites[TemplateList] = Json.writes[TemplateList]

  def empty: TemplateList = TemplateList(Seq.empty, 0)
}
