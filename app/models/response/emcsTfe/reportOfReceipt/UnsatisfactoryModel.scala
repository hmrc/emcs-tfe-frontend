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

package models.response.emcsTfe.reportOfReceipt

import models.common.WrongWithMovement
import play.api.libs.json.{Json, OFormat}

case class UnsatisfactoryModel(reason: WrongWithMovement,
                               additionalInformation: Option[String])

object UnsatisfactoryModel {
  implicit val format: OFormat[UnsatisfactoryModel] = Json.format

  implicit val ordering: Ordering[UnsatisfactoryModel] =
    (x: UnsatisfactoryModel, y: UnsatisfactoryModel) => Ordering.by(sortingOrder).compare(x.reason, y.reason)

  private val sortingOrder: Map[WrongWithMovement, Int] = Map(
    WrongWithMovement.Shortage -> 0,
    WrongWithMovement.Excess -> 1,
    WrongWithMovement.Damaged -> 2,
    WrongWithMovement.BrokenSeals -> 3,
    WrongWithMovement.Other -> 4
  )
}
