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

package models.response.emcsTfe

import models.common.UnitOfMeasure
import play.api.libs.json.{Json, Reads}

case class MovementItem(itemUniqueReference: Int,
                        productCode: String,
                        cnCode: String,
                        quantity: BigDecimal,
                        commercialDescription: Option[String],
                        packaging: Seq[Packaging],
                        unitOfMeasure: Option[UnitOfMeasure])

object MovementItem {
  implicit val reads: Reads[MovementItem] = Json.reads
}
