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

package models.draftTemplates

import models.common.{DestinationType, Enumerable, WithName}
import utils.Logging

sealed trait MovementScenario {
  def destinationType: DestinationType
}

object MovementScenario extends Enumerable.Implicits with Logging {

  case object ExportWithCustomsDeclarationLodgedInTheUk extends WithName("exportWithCustomsDeclarationLodgedInTheUk") with MovementScenario {
    def destinationType: DestinationType = DestinationType.Export
  }

  object UkTaxWarehouse {

    case object GB extends WithName("gbTaxWarehouse") with MovementScenario {
      def destinationType: DestinationType = DestinationType.TaxWarehouse
    }

    case object NI extends WithName("niTaxWarehouse") with MovementScenario {
      def destinationType: DestinationType = DestinationType.TaxWarehouse
    }

    val values: Seq[MovementScenario] = Seq(GB, NI)
  }

  case object DirectDelivery extends WithName("directDelivery") with MovementScenario {
    def destinationType: DestinationType = DestinationType.DirectDelivery
  }

  case object EuTaxWarehouse extends WithName("euTaxWarehouse") with MovementScenario {
    def destinationType: DestinationType = DestinationType.TaxWarehouse
  }

  case object ExemptedOrganisation extends WithName("exemptedOrganisation") with MovementScenario {
    def destinationType: DestinationType = DestinationType.ExemptedOrganisation
  }

  case object ExportWithCustomsDeclarationLodgedInTheEu extends WithName("exportWithCustomsDeclarationLodgedInTheEu") with MovementScenario {
    def destinationType: DestinationType = DestinationType.Export
  }

  case object RegisteredConsignee extends WithName("registeredConsignee") with MovementScenario {
    def destinationType: DestinationType = DestinationType.RegisteredConsignee
  }

  case object TemporaryRegisteredConsignee extends WithName("temporaryRegisteredConsignee") with MovementScenario {
    def destinationType: DestinationType = DestinationType.TemporaryRegisteredConsignee
  }

  case object CertifiedConsignee extends WithName("certifiedConsignee") with MovementScenario {
    def destinationType: DestinationType = DestinationType.CertifiedConsignee
  }

  case object TemporaryCertifiedConsignee extends WithName("temporaryCertifiedConsignee") with MovementScenario {
    def destinationType: DestinationType = DestinationType.TemporaryCertifiedConsignee
  }

  case object UnknownDestination extends WithName("unknownDestination") with MovementScenario {
    def destinationType: DestinationType = DestinationType.UnknownDestination
  }

  def valuesExportUkAndUkTaxWarehouse: Seq[MovementScenario] = Seq(
    ExportWithCustomsDeclarationLodgedInTheUk
  ) ++ UkTaxWarehouse.values

  def valuesEu: Seq[MovementScenario] = Seq(
    DirectDelivery,
    ExemptedOrganisation,
    ExportWithCustomsDeclarationLodgedInTheEu,
    ExportWithCustomsDeclarationLodgedInTheUk,
    RegisteredConsignee,
    EuTaxWarehouse,
    UkTaxWarehouse.GB,
    UkTaxWarehouse.NI,
    TemporaryRegisteredConsignee,
    UnknownDestination
  )

  def valuesForDutyPaidTraders: Seq[MovementScenario] = Seq(
    CertifiedConsignee,
    TemporaryCertifiedConsignee
  )


  val values: Seq[MovementScenario] = (valuesExportUkAndUkTaxWarehouse ++ valuesEu ++ valuesForDutyPaidTraders).distinct

  implicit val enumerable: Enumerable[MovementScenario] = Enumerable(values.map(v => v.toString -> v): _*)
}
