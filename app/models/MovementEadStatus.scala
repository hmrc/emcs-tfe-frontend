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

package models

import models.common.{Enumerable, WithName}

sealed trait MovementEadStatus {

  val schemaValues: Seq[String] = Seq(toString)
}

object MovementEadStatus extends Enumerable.Implicits {

  case object Accepted extends WithName("Accepted") with MovementEadStatus

  case object Cancelled extends WithName("Cancelled") with MovementEadStatus

  case object DeemedExported extends WithName("DeemedExported") with MovementEadStatus

  case object Delivered extends WithName("Delivered") with MovementEadStatus

  case object Diverted extends WithName("Diverted") with MovementEadStatus

  case object Exporting extends WithName("Exporting") with MovementEadStatus

  case object ManuallyClosed extends WithName("ManuallyClosed") with MovementEadStatus {

    override val schemaValues: Seq[String] = Seq("ManuallyClosed", "e-AD/e-SAD Manually Closed", "e-AD Manually Closed", "e-SAD Manually Closed")
  }

  case object NoneStatus extends WithName("None") with MovementEadStatus

  case object PartiallyRefused extends WithName("PartiallyRefused") with MovementEadStatus

  case object Refused extends WithName("Refused") with MovementEadStatus

  case object Replaced extends WithName("Replaced") with MovementEadStatus

  case object Rejected extends WithName("Rejected") with MovementEadStatus

  case object Stopped extends WithName("Stopped") with MovementEadStatus

  val values: Seq[MovementEadStatus] = Seq(
    Accepted, Cancelled, DeemedExported, Delivered,
    Diverted, Exporting, ManuallyClosed, NoneStatus,
    PartiallyRefused, Refused, Replaced, Rejected,
    Stopped
  )

  val cancelMovementValidStatuses: Seq[MovementEadStatus] = Seq(Accepted, Exporting, Rejected)
  val changeDestinationValidStatuses: Seq[MovementEadStatus] = Seq(Accepted, Exporting, PartiallyRefused, Refused, DeemedExported, Rejected)
  val alertOrRejectValidStatuses: Seq[MovementEadStatus] = Seq(Accepted)
  val reportOfReceiptValidStatuses: Seq[MovementEadStatus] = Seq(Accepted)
  val shortageOrExcessValidStatuses: Seq[MovementEadStatus] = Seq(Delivered, Diverted, ManuallyClosed, Refused, PartiallyRefused, Exporting, Stopped, DeemedExported)
  val shortageOrExcessExportValidStatuses: Seq[MovementEadStatus] = Seq(Delivered, Diverted, ManuallyClosed, Refused, PartiallyRefused, Exporting, Stopped, Accepted, Rejected)

  implicit val enumerable: Enumerable[MovementEadStatus] =
    Enumerable(values.flatMap(v => v.schemaValues.map(_ -> v)): _*)

  def destinationType(code: String): MovementEadStatus = values.find(_.toString == code) match {
    case Some(value) => value
    case None => throw new IllegalArgumentException(s"MovementEadStatus code of '$code' could not be mapped to a valid MovementEadStatus Type")
  }
}
