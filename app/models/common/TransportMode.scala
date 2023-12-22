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

package models.common

import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait TransportMode {
  val messageKey: String
}

object TransportMode extends Enumerable.Implicits {

  case object AirTransport extends WithName("4") with TransportMode {
    override val messageKey: String = "transportMode.airTransport"
  }

  case object FixedTransportInstallations extends WithName("7") with TransportMode {
    override val messageKey: String = "transportMode.fixedTransportInstallations"
  }

  case object InlandWaterwayTransport extends WithName("8") with TransportMode {
    override val messageKey: String = "transportMode.inlandWaterwayTransport"
  }

  case object PostalConsignment extends WithName("5") with TransportMode {
    override val messageKey: String = "transportMode.postalConsignment"
  }

  case object RailTransport extends WithName("2") with TransportMode {
    override val messageKey: String = "transportMode.railTransport"
  }

  case object RoadTransport extends WithName("3") with TransportMode {
    override val messageKey: String = "transportMode.roadTransport"
  }

  case object SeaTransport extends WithName("1") with TransportMode {
    override val messageKey: String = "transportMode.seaTransport"
  }

  case object Other extends WithName("0") with TransportMode {
    override val messageKey: String = "transportMode.other"
  }

  val values: Seq[TransportMode] = Seq(
    AirTransport,
    FixedTransportInstallations,
    InlandWaterwayTransport,
    PostalConsignment,
    RailTransport,
    RoadTransport,
    SeaTransport,
    Other
  )

  implicit val enumerable: Enumerable[TransportMode] =
    Enumerable(values.map(v => v.toString -> v): _*)
}