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

package viewmodels.helpers

import models.common.RoleType._
import models.common.TraderModel
import models.movementScenario.MovementScenario
import models.movementScenario.MovementScenario._
import play.api.i18n.Messages
import utils.Logging

import javax.inject.Inject

class MovementTypeHelper @Inject()() extends Logging {

  //scalastyle:off
  private[helpers] def getMovementType(
                                            userType: RoleType,
                                            movementScenario: MovementScenario,
                                            placeOfDispatch: Option[TraderModel],
                                            isBeingViewedByConsignor: Boolean,
                                            isBeingViewedByConsignee: Boolean
                                          )(implicit messages: Messages): String = {
    (userType, movementScenario) match {
      case (_, ReturnToThePlaceOfDispatchOfTheConsignor) =>
        messages("movementType.returnToThePlaceOfDispatchOfTheConsignor")

      case (XIWK, destinationType@(UkTaxWarehouse.GB | UkTaxWarehouse.NI | EuTaxWarehouse | DirectDelivery | RegisteredConsignee | TemporaryRegisteredConsignee | ExemptedOrganisation | UnknownDestination)) =>
        placeOfDispatch match {
          case Some(placeOfDispatch) => placeOfDispatch.traderExciseNumber match {
            case Some(dispatchErn) =>
              if (isGB(dispatchErn)) {
                messages("movementType.gbTaxWarehouseTo", messages(s"movementType.2.$destinationType"))
              } else if (isXI(dispatchErn)) {
                messages("movementType.niTaxWarehouseTo", messages(s"movementType.2.$destinationType"))
              } else {
                messages("movementType.nonUkMovementTo", messages(s"movementType.2.$destinationType"))
              }
            case None =>
              logger.info(s"[getMovementType] Missing place of dispatch ERN for $XIWK")
              messages("movementType.movementTo", messages(s"movementType.2.$destinationType"))
          }
          case None =>
            logger.info(s"[getMovementType] Missing place of dispatch trader for $XIWK")
            messages("movementType.movementTo", messages(s"movementType.2.$destinationType"))
        }

      case (GBRC | XIRC, destinationType) =>
        messages("movementType.importFor", messages(s"movementType.2.$destinationType"))

      case (GBWK | XIWK, destinationType@(ExportWithCustomsDeclarationLodgedInTheUk | ExportWithCustomsDeclarationLodgedInTheEu)) =>
        messages(s"movementType.$destinationType")

      case (GBWK, destinationType) if isBeingViewedByConsignor =>
        messages("movementType.gbTaxWarehouseTo", messages(s"movementType.2.$destinationType"))

      case (GBWK, _) if isBeingViewedByConsignee =>
        messages("movementType.movementToGbTaxWarehouse")

      case (XIPA, destinationType@(CertifiedConsignee | TemporaryCertifiedConsignee | ReturnToThePlaceOfDispatchOfTheConsignor)) =>
        messages(s"movementType.XIPA", messages(s"movementType.2.$destinationType"))

      case (XIPC, destinationType@(CertifiedConsignee | TemporaryCertifiedConsignee | ReturnToThePlaceOfDispatchOfTheConsignor)) =>
        messages(s"movementType.XIPC", messages(s"movementType.2.$destinationType"))

      case (XIPB, (CertifiedConsignee | TemporaryCertifiedConsignee | ReturnToThePlaceOfDispatchOfTheConsignor)) =>
        messages(s"movementType.XIPB")

      case (XIPD, (CertifiedConsignee | TemporaryCertifiedConsignee | ReturnToThePlaceOfDispatchOfTheConsignor)) =>
        messages(s"movementType.XIPD")

      case (XITC, _) =>
        messages("movementType.XITC")

      case (userType, destinationType) =>
        logger.warn(s"[getMovementType] catch-all UserType and movement scenario combination for MOV journey: $userType | $destinationType")
        messages("movementType.movementTo", messages(s"movementType.2.$destinationType"))
    }
  }
  //scalastyle:on

}
