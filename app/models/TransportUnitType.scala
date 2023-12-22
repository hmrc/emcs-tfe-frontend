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

package models

import models.common.{Enumerable, WithName}


sealed trait TransportUnitType {
  val messageKey: String
}

object TransportUnitType extends Enumerable.Implicits {

  case object Container extends WithName("1") with TransportUnitType {
    override val messageKey: String = "transportType.container"
  }

  case object Vehicle extends WithName("2") with TransportUnitType {
    override val messageKey: String = "transportType.vehicle"
  }

  case object Trailer extends WithName("3") with TransportUnitType {
    override val messageKey: String = "transportType.trailer"
  }

  case object Tractor extends WithName("4") with TransportUnitType {
    override val messageKey: String = "transportType.tractor"
  }

  case object FixedTransport extends WithName("5") with TransportUnitType {
    override val messageKey: String = "transportType.fixedTransport"
  }

  val values: Seq[TransportUnitType] = Seq(
    Container, FixedTransport, Tractor, Trailer, Vehicle
  )

  implicit val enumerable: Enumerable[TransportUnitType] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
