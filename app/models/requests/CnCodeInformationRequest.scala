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

package models.requests

import models.response.emcsTfe.MovementItem
import play.api.libs.json.{Json, OWrites}

case class CnCodeInformationRequest(items: Seq[CnCodeInformationItem])

case class CnCodeInformationItem(productCode: String, cnCode: String)

object CnCodeInformationRequest {
  implicit val writes: OWrites[CnCodeInformationRequest] = Json.writes
}

object CnCodeInformationItem {

  def apply(items: Seq[MovementItem]): Seq[CnCodeInformationItem] = items.map { item =>
    CnCodeInformationItem(item.productCode, item.cnCode)
  }

  implicit val writes: OWrites[CnCodeInformationItem] = Json.writes
}
