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

package models.movementScenario

import models.common.{DestinationType, Enumerable, RoleType, WithName}
import models.requests.DataRequest
import models.response.emcsTfe.GetMovementResponse
import models.response.{InvalidDestinationTypeException, InvalidUserTypeException}
import utils.Logging

sealed trait MovementScenario {
  def destinationType: DestinationType

  def movementType(implicit request: DataRequest[_]): MovementType

  val stringValue: String
}

object MovementScenario extends Enumerable.Implicits with Logging {

  //noinspection ScalaStyle - Cyclomatic Complexity
  def getMovementScenarioFromMovement(movementResponse: GetMovementResponse): MovementScenario = {
    movementResponse.destinationType match {
      case DestinationType.TaxWarehouse =>
          if (movementResponse.deliveryPlaceTrader.map(_.traderExciseNumber).exists(RoleType.isGB) ||
            movementResponse.deliveryPlaceTrader.map(_.traderExciseNumber).exists(RoleType.isXI)) {
          MovementScenario.GbTaxWarehouse
        } else {
          MovementScenario.EuTaxWarehouse
        }
      case DestinationType.RegisteredConsignee => MovementScenario.RegisteredConsignee
      case DestinationType.TemporaryRegisteredConsignee => MovementScenario.TemporaryRegisteredConsignee
      case DestinationType.DirectDelivery => MovementScenario.DirectDelivery
      case DestinationType.ExemptedOrganisation => MovementScenario.ExemptedOrganisation
      case DestinationType.Export =>
        if (movementResponse.deliveryPlaceCustomsOfficeReferenceNumber.exists(RoleType.isGB) ||
              movementResponse.deliveryPlaceCustomsOfficeReferenceNumber.exists(RoleType.isXI)) {
          MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk
        } else {
          MovementScenario.ExportWithCustomsDeclarationLodgedInTheEu
        }
      case DestinationType.UnknownDestination => MovementScenario.UnknownDestination
      case answer@(DestinationType.ReturnToThePlaceOfDispatchOfTheConsignor | DestinationType.CertifiedConsignee | DestinationType.TemporaryCertifiedConsignee) =>
        // TODO: These are Duty Paid answers which we don't currently have a design for (as of 29/11/23).
        // We need a solid design for this, whether or not that's handled here is undecided yet, but for now we can throw an error in this case.
        throw InvalidDestinationTypeException(s"[MovementScenario][getMovementScenarioFromMovement] invalid DestinationType: $answer")
    }
  }

  /**
   * emcs: direct_export / import_for_direct_export
   */
  case object ExportWithCustomsDeclarationLodgedInTheUk extends WithName("exportWithCustomsDeclarationLodgedInTheUk") with MovementScenario {

    def destinationType: DestinationType = DestinationType.Export

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.DirectExport
      case (_, true) => MovementType.ImportDirectExport
      case _ =>
        logger.error(s"[movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "export with customs declaration lodged in the United Kingdom"
  }

  /**
   * emcs: tax_warehouse_uk_to_uk / import_for_taxwarehouse_uk
   */
  case object GbTaxWarehouse extends WithName("gbTaxWarehouse") with MovementScenario {

    def destinationType: DestinationType = DestinationType.TaxWarehouse

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToUk
      case (_, true) => MovementType.ImportUk
      case _ =>
        logger.error(s"[movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "tax warehouse in Great Britain"
  }

  /**
   * emcs: direct_delivery / import_for_direct_delivery
   */
  case object DirectDelivery extends WithName("directDelivery") with MovementScenario {

    def destinationType: DestinationType = DestinationType.DirectDelivery

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "direct delivery"
  }

  /**
   * emcs: tax_warehouse_uk_to_eu / import_for_taxwarehouse_eu
   */
  case object EuTaxWarehouse extends WithName("euTaxWarehouse") with MovementScenario {

    def destinationType: DestinationType = DestinationType.TaxWarehouse

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "tax warehouse in the European Union"
  }

  /**
   * emcs: exempted_organisation / import_for_exempted_organisation
   */
  case object ExemptedOrganisation extends WithName("exemptedOrganisation") with MovementScenario {

    def destinationType: DestinationType = DestinationType.ExemptedOrganisation

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "exempted organisation"
  }

  /**
   * emcs: indirect_export / import_for_indirect_export
   */
  case object ExportWithCustomsDeclarationLodgedInTheEu extends WithName("exportWithCustomsDeclarationLodgedInTheEu") with MovementScenario {

    def destinationType: DestinationType = DestinationType.Export

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.IndirectExport
      case (_, true) => MovementType.ImportIndirectExport
      case _ =>
        logger.error(s"[movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "export with customs declaration lodged in the European Union"
  }

  /**
   * emcs: registered_consignee / import_for_registered_consignee
   */
  case object RegisteredConsignee extends WithName("registeredConsignee") with MovementScenario {

    def destinationType: DestinationType = DestinationType.RegisteredConsignee

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "registered consignee"
  }

  /**
   * emcs: temp_registered_consignee / import_for_temp_registered_consignee
   */
  case object TemporaryRegisteredConsignee extends WithName("temporaryRegisteredConsignee") with MovementScenario {

    def destinationType: DestinationType = DestinationType.TemporaryRegisteredConsignee

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportEu
      case _ =>
        logger.error(s"[movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "temporary registered consignee"

  }

  /**
   * emcs: unknown_destination / import_for_unknown_destination
   */
  case object UnknownDestination extends WithName("unknownDestination") with MovementScenario {

    def destinationType: DestinationType = DestinationType.UnknownDestination

    def movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToEu
      case (_, true) => MovementType.ImportUnknownDestination
      case _ =>
        logger.error(s"[movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
    }

    override val stringValue: String = "unknown destination"
  }

  def valuesUk: Seq[MovementScenario] = Seq(
    ExportWithCustomsDeclarationLodgedInTheUk,
    GbTaxWarehouse
  )

  def valuesEu: Seq[MovementScenario] = Seq(
    DirectDelivery,
    ExemptedOrganisation,
    ExportWithCustomsDeclarationLodgedInTheEu,
    ExportWithCustomsDeclarationLodgedInTheUk,
    RegisteredConsignee,
    EuTaxWarehouse,
    GbTaxWarehouse,
    TemporaryRegisteredConsignee,
    UnknownDestination
  )

  val values: Seq[MovementScenario] = (valuesUk ++ valuesEu).distinct

  implicit val enumerable: Enumerable[MovementScenario] = Enumerable(values.map(v => v.toString -> v): _*)
}
