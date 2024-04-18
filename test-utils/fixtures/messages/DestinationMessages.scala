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

package fixtures.messages

import models.common.DestinationType
import models.movementScenario.MovementScenario

object DestinationMessages {

  sealed trait ViewMessages { _: i18n =>

    //noinspection ScalaStyle
    def destinationType(destination: DestinationType): String = destination match {
      case DestinationType.TaxWarehouse => "Tax warehouse"
      case DestinationType.RegisteredConsignee => "Registered consignee"
      case DestinationType.TemporaryRegisteredConsignee => "Temporary registered consignee"
      case DestinationType.DirectDelivery => "Direct delivery"
      case DestinationType.ExemptedOrganisation => "Exempted organisation"
      case DestinationType.Export => "Export"
      case DestinationType.UnknownDestination => "Unknown destination"
      case DestinationType.CertifiedConsignee => "Certified consignee"
      case DestinationType.TemporaryCertifiedConsignee => "Temporary certified consignee"
      case DestinationType.ReturnToThePlaceOfDispatchOfTheConsignor => "Return to place of dispatch"
    }
    def destinationType(movementScenario: MovementScenario): String = destinationType(movementScenario.destinationType)
  }

  object English extends ViewMessages with BaseEnglish
}
