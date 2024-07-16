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
import models.response.InvalidUserTypeException
import models.response.emcsTfe.GetMovementResponse
import utils.Logging

sealed trait MovementScenario {
  def destinationType: DestinationType
  def movementType(implicit request: DataRequest[_]): MovementType
}

object MovementScenario extends Enumerable.Implicits with Logging {

  //noinspection ScalaStyle - Cyclomatic Complexity
  def getMovementScenarioFromMovement(movementResponse: GetMovementResponse): MovementScenario = {
    logger.debug(s"[getMovementScenarioFromMovement] destinationType: ${movementResponse.destinationType}")
    movementResponse.destinationType match {
      case DestinationType.TaxWarehouse =>
        if (movementResponse.deliveryPlaceTrader.flatMap(_.traderExciseNumber).exists(RoleType.isGB)) {
          MovementScenario.UkTaxWarehouse.GB
        }
        else if (movementResponse.deliveryPlaceTrader.flatMap(_.traderExciseNumber).exists(RoleType.isXI)) {
          MovementScenario.UkTaxWarehouse.NI
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
      case DestinationType.CertifiedConsignee => MovementScenario.CertifiedConsignee
      case DestinationType.TemporaryCertifiedConsignee => MovementScenario.TemporaryCertifiedConsignee
      case DestinationType.ReturnToThePlaceOfDispatchOfTheConsignor => MovementScenario.ReturnToThePlaceOfDispatchOfTheConsignor
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
  }

  /**
   * emcs: tax_warehouse_uk_to_uk / import_for_taxwarehouse_uk
   */
  object UkTaxWarehouse {

    private def _destinationType: DestinationType = DestinationType.TaxWarehouse

    private def _movementType(implicit request: DataRequest[_]): MovementType = (request.isWarehouseKeeper, request.isRegisteredConsignor) match {
      case (true, _) => MovementType.UkToUk
      case (_, true) => MovementType.ImportUk
      case _ =>
        logger.error(s"[movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
        throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
    }


    case object GB extends WithName("gbTaxWarehouse") with MovementScenario {

      def destinationType: DestinationType = _destinationType

      def movementType(implicit request: DataRequest[_]): MovementType = _movementType

    }

    case object NI extends WithName("niTaxWarehouse") with MovementScenario {

      def destinationType: DestinationType = _destinationType

      def movementType(implicit request: DataRequest[_]): MovementType = _movementType

    }

    val values: Seq[MovementScenario] = Seq(GB, NI)
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
  }


  case object CertifiedConsignee extends WithName("certifiedConsignee") with MovementScenario {

    def destinationType: DestinationType = DestinationType.CertifiedConsignee

    def movementType(implicit request: DataRequest[_]): MovementType = if (request.isCertifiedConsignor) {
      MovementType.UkToEu
    } else {
      logger.error(s"[movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
      throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
    }
  }


  case object TemporaryCertifiedConsignee extends WithName("temporaryCertifiedConsignee") with MovementScenario {

    def destinationType: DestinationType = DestinationType.TemporaryCertifiedConsignee

    def movementType(implicit request: DataRequest[_]): MovementType = if (request.isCertifiedConsignor) {
      MovementType.UkToEu
    } else {
      logger.error(s"[movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
      throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
    }
  }

  case object ReturnToThePlaceOfDispatchOfTheConsignor extends WithName("returnToThePlaceOfDispatchOfTheConsignor") with MovementScenario {

    def destinationType: DestinationType = DestinationType.ReturnToThePlaceOfDispatchOfTheConsignor

    def movementType(implicit request: DataRequest[_]): MovementType = if (request.isCertifiedConsignor) {
      MovementType.UkToEu
    } else {
      logger.error(s"[movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
      throw InvalidUserTypeException(s"[MovementScenario][movementType] invalid UserType for MOV journey: ${request.userTypeFromErn}")
    }
  }


  def valuesUk: Seq[MovementScenario] = Seq(
    ExportWithCustomsDeclarationLodgedInTheUk,
    UkTaxWarehouse.GB,
    UkTaxWarehouse.NI
  )

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
    UnknownDestination,
    CertifiedConsignee,
    TemporaryCertifiedConsignee,
    ReturnToThePlaceOfDispatchOfTheConsignor
  )

  val values: Seq[MovementScenario] = (valuesUk ++ valuesEu).distinct

  implicit val enumerable: Enumerable[MovementScenario] = Enumerable(values.map(v => v.toString -> v): _*)
}
