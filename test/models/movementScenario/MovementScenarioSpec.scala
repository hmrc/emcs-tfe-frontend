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

import base.SpecBase
import fixtures.GetMovementResponseFixtures
import models.common.DestinationType._
import models.common.{AddressModel, DestinationType, TraderModel}
import models.movementScenario.MovementScenario.{EuTaxWarehouse, ExportWithCustomsDeclarationLodgedInTheEu, ExportWithCustomsDeclarationLodgedInTheUk, UkTaxWarehouse}
import models.requests.DataRequest
import models.response.InvalidUserTypeException
import play.api.test.FakeRequest

class MovementScenarioSpec extends SpecBase with GetMovementResponseFixtures {

  val warehouseKeeperDataRequest: DataRequest[_] = dataRequest(FakeRequest(), ern = "GBWK123")
  val dutyPaidDataRequest: DataRequest[_] = dataRequest(FakeRequest(), ern = "XIPA123")
  val registeredConsignorDataRequest: DataRequest[_] = dataRequest(FakeRequest(), ern = "GBRC123")
  val nonWKRCDataRequest: DataRequest[_] = dataRequest(FakeRequest(), ern = "XI00123")

  "getMovementScenarioFromMovement" when {

    "DestinationType is Export" must {
      "return ExportWithCustomsDeclarationLodgedInTheUk" when {
        "deliveryPlaceCustomsOfficeReferenceNumber starts with GB" in {
          MovementScenario.getMovementScenarioFromMovement(getMovementResponseModel.copy(
            deliveryPlaceCustomsOfficeReferenceNumber = Some("GBWK123"),
            destinationType = Export
          )) mustBe ExportWithCustomsDeclarationLodgedInTheUk
        }

        "deliveryPlaceCustomsOfficeReferenceNumber starts with XI" in {
          MovementScenario.getMovementScenarioFromMovement(getMovementResponseModel.copy(
            deliveryPlaceCustomsOfficeReferenceNumber = Some("XIWK123"),
            destinationType = Export
          )) mustBe ExportWithCustomsDeclarationLodgedInTheUk
        }
      }

      "return ExportWithCustomsDeclarationLodgedInTheEu" when {

        "deliveryPlaceCustomsOfficeReferenceNumber does not start with GB or XI" in {
          MovementScenario.getMovementScenarioFromMovement(getMovementResponseModel.copy(
            deliveryPlaceCustomsOfficeReferenceNumber = Some("FRWK123"),
            destinationType = Export
          )) mustBe ExportWithCustomsDeclarationLodgedInTheEu
        }

        "deliveryPlaceCustomsOfficeReferenceNumber is not present" in {
          MovementScenario.getMovementScenarioFromMovement(getMovementResponseModel.copy(
            deliveryPlaceCustomsOfficeReferenceNumber = None,
            destinationType = DestinationType.Export
          )) mustBe ExportWithCustomsDeclarationLodgedInTheEu
        }
      }
    }

    "DestinationType is TaxWarehouse" must {
      "return GbTaxWarehouse" when {

        "deliveryPlaceTrader.traderExciseNumber starts with GB" in {
          MovementScenario.getMovementScenarioFromMovement(getMovementResponseModel.copy(
            deliveryPlaceTrader = Some(TraderModel(
              traderExciseNumber = Some("GBWK345GTR145"),
              traderName = Some("Current 801 Consignee"),
              address = Some(AddressModel(
                streetNumber = None,
                street = Some("Main101"),
                postcode = Some("ZZ78"),
                city = Some("Zeebrugge")
              )),
              vatNumber = Some("GB123456789"),
              eoriNumber = None
            )),
            destinationType = TaxWarehouse
          )) mustBe UkTaxWarehouse.GB
        }

        "deliveryPlaceTrader.traderExciseNumber starts with XI" in {
          MovementScenario.getMovementScenarioFromMovement(getMovementResponseModel.copy(
            deliveryPlaceTrader = Some(TraderModel(
              traderExciseNumber = Some("XIWK345GTR145"),
              traderName = Some("Current 801 Consignee"),
              address = Some(AddressModel(
                streetNumber = None,
                street = Some("Main101"),
                postcode = Some("ZZ78"),
                city = Some("Zeebrugge")
              )),
              vatNumber = Some("XI123456789"),
              eoriNumber = None
            )),
            destinationType = TaxWarehouse
          )) mustBe UkTaxWarehouse.NI
        }
      }

      "return EuTaxWarehouse" when {
        "deliveryPlaceTrader.traderExciseNumber does not start with GB or XI" in {
          MovementScenario.getMovementScenarioFromMovement(getMovementResponseModel.copy(
            deliveryPlaceTrader = Some(TraderModel(
              traderExciseNumber = Some("FRWK345GTR145"),
              traderName = Some("Current 801 Consignee"),
              address = Some(AddressModel(
                streetNumber = None,
                street = Some("Main101"),
                postcode = Some("ZZ78"),
                city = Some("Zeebrugge")
              )),
              vatNumber = Some("FR123456789"),
              eoriNumber = None
            )),
            destinationType = TaxWarehouse
          )) mustBe EuTaxWarehouse
        }

        "deliveryPlaceTrader.traderExciseNumber is not present" in {
          MovementScenario.getMovementScenarioFromMovement(getMovementResponseModel.copy(
            deliveryPlaceTrader = None,
            destinationType = TaxWarehouse
          )) mustBe EuTaxWarehouse
        }
      }
    }

    "when DestinationType is DirectDelivery" must {
      "return DirectDelivery" in {
        MovementScenario.getMovementScenarioFromMovement(getMovementResponseModel.copy(
          destinationType = DirectDelivery
        )) mustBe MovementScenario.DirectDelivery
      }
    }

    "DestinationType is ExemptedOrganisation" must {
      "return ExemptedOrganisation" in {
        MovementScenario.getMovementScenarioFromMovement(getMovementResponseModel.copy(
          destinationType = ExemptedOrganisation
        )) mustBe MovementScenario.ExemptedOrganisation
      }
    }

    "DestinationType is RegisteredConsignee" must {
      "return RegisteredConsignee" in {
        MovementScenario.getMovementScenarioFromMovement(getMovementResponseModel.copy(
          destinationType = RegisteredConsignee
        )) mustBe MovementScenario.RegisteredConsignee
      }
    }

    "DestinationType is TemporaryRegisteredConsignee" must {
      "return TemporaryRegisteredConsignee" in {
        MovementScenario.getMovementScenarioFromMovement(getMovementResponseModel.copy(
          destinationType = TemporaryRegisteredConsignee
        )) mustBe MovementScenario.TemporaryRegisteredConsignee
      }
    }

    "DestinationType is UnknownDestination" must {
      "return UnknownDestination" in {
        MovementScenario.getMovementScenarioFromMovement(getMovementResponseModel.copy(
          destinationType = UnknownDestination
        )) mustBe MovementScenario.UnknownDestination
      }
    }

    "DestinationType is CertifiedConsignee" must {
      "return MovementScenario.CertifiedConsignee" in {
        MovementScenario.getMovementScenarioFromMovement(getMovementResponseModel.copy(
          destinationType = DestinationType.CertifiedConsignee
        )) mustBe MovementScenario.CertifiedConsignee
      }
    }

    "DestinationType is TemporaryCertifiedConsignee" must {
      "return MovementScenario.TemporaryCertifiedConsignee" in {
        MovementScenario.getMovementScenarioFromMovement(getMovementResponseModel.copy(
          destinationType = DestinationType.TemporaryCertifiedConsignee
        )) mustBe MovementScenario.TemporaryCertifiedConsignee
      }
    }

    "DestinationType is ReturnToThePlaceOfDispatchOfTheConsignor" must {
      "return MovementScenario.ReturnToThePlaceOfDispatchOfTheConsignor" in {
        MovementScenario.getMovementScenarioFromMovement(getMovementResponseModel.copy(
          destinationType = DestinationType.ReturnToThePlaceOfDispatchOfTheConsignor
        )) mustBe MovementScenario.ReturnToThePlaceOfDispatchOfTheConsignor
      }
    }
  }

  "ExportWithCustomsDeclarationLodgedInTheUk" should {

    ".destinationType" must {
      "return Export" in {
        MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk.destinationType mustBe DestinationType.Export
      }
    }
    ".movementType" when {

      "user is a warehouse keeper" must {

        "return DirectExport" in {
          MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk.movementType(warehouseKeeperDataRequest) mustBe MovementType.DirectExport
        }
      }

      "user is a registered consignor" must {
        "return ImportDirectExport" in {
          MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportDirectExport
        }
      }

      "user is not a warehouse keeper or a registered consignor" must {
        "return an error" in {
          intercept[InvalidUserTypeException](MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk.movementType(nonWKRCDataRequest))
        }
      }
    }
  }

  "UkTaxWarehouse.GB" should {

    ".destinationType" must {
      "return TaxWarehouse" in {
        MovementScenario.UkTaxWarehouse.GB.destinationType mustBe DestinationType.TaxWarehouse
      }
    }

    ".movementType" when {
      "user is a warehouse keeper" must {
        "return UkToUk" in {
          MovementScenario.UkTaxWarehouse.GB.movementType(warehouseKeeperDataRequest) mustBe MovementType.UkToUk
        }
      }

      "user is a registered consignor" must {
        "return ImportUk" in {
          MovementScenario.UkTaxWarehouse.GB.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportUk
        }
      }

      "user is not a warehouse keeper or a registered consignor" must {
        "return an error" in {
          intercept[InvalidUserTypeException](MovementScenario.UkTaxWarehouse.GB.movementType(nonWKRCDataRequest))
        }
      }
    }
  }

  "UkTaxWarehouse.NI" should {

    ".destinationType" must {
      "return TaxWarehouse" in {
        MovementScenario.UkTaxWarehouse.NI.destinationType mustBe DestinationType.TaxWarehouse
      }
    }

    ".movementType" when {
      "user is a warehouse keeper" must {
        "return UkToUk" in {
          MovementScenario.UkTaxWarehouse.NI.movementType(warehouseKeeperDataRequest) mustBe MovementType.UkToUk
        }
      }

      "user is a registered consignor" must {
        "return ImportUk" in {
          MovementScenario.UkTaxWarehouse.NI.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportUk
        }
      }

      "user is not a warehouse keeper or a registered consignor" must {
        "return an error" in {
          intercept[InvalidUserTypeException](MovementScenario.UkTaxWarehouse.NI.movementType(nonWKRCDataRequest))
        }
      }
    }
  }

  "DirectDelivery" should {

    ".destinationType" must {
      "return DirectDelivery" in {
        MovementScenario.DirectDelivery.destinationType mustBe DestinationType.DirectDelivery
      }
    }
    ".movementType" when {
      "user is a warehouse keeper" must {
        "return UkToEu" in {
          MovementScenario.DirectDelivery.movementType(warehouseKeeperDataRequest) mustBe MovementType.UkToEu
        }
      }
      "user is a registered consignor" must {
        "return ImportEu" in {
          MovementScenario.DirectDelivery.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportEu
        }
      }
      "user is not a warehouse keeper or a registered consignor" must {
        "return an error" in {
          intercept[InvalidUserTypeException](MovementScenario.DirectDelivery.movementType(nonWKRCDataRequest))
        }
      }
    }
  }

  "EuTaxWarehouse" should {

    ".destinationType" must {
      "return TaxWarehouse" in {
        MovementScenario.EuTaxWarehouse.destinationType mustBe DestinationType.TaxWarehouse
      }
    }
    ".movementType" when {
      "user is a warehouse keeper" must {
        "return UkToEu" in {
          MovementScenario.EuTaxWarehouse.movementType(warehouseKeeperDataRequest) mustBe MovementType.UkToEu
        }
      }
      "user is a registered consignor" must {
        "return ImportEu" in {
          MovementScenario.EuTaxWarehouse.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportEu
        }
      }
      "user is not a warehouse keeper or a registered consignor" must {
        "return an error" in {
          intercept[InvalidUserTypeException](MovementScenario.EuTaxWarehouse.movementType(nonWKRCDataRequest))
        }
      }
    }
  }

  "ExemptedOrganisation" should {

    ".destinationType" must {
      "return ExemptedOrganisation" in {
        MovementScenario.ExemptedOrganisation.destinationType mustBe DestinationType.ExemptedOrganisation
      }
    }

    ".movementType" when {

      "user is a warehouse keeper" must {
        "return UkToEu" in {
          MovementScenario.ExemptedOrganisation.movementType(warehouseKeeperDataRequest) mustBe MovementType.UkToEu
        }
      }

      "user is a registered consignor" must {
        "return ImportEu" in {
          MovementScenario.ExemptedOrganisation.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportEu
        }
      }

      "user is not a warehouse keeper or a registered consignor" must {
        "return an error" in {
          intercept[InvalidUserTypeException](MovementScenario.ExemptedOrganisation.movementType(nonWKRCDataRequest))
        }
      }
    }
  }

  "ExportWithCustomsDeclarationLodgedInTheEu" should {

    ".destinationType" must {
      "return Export" in {
        MovementScenario.ExportWithCustomsDeclarationLodgedInTheEu.destinationType mustBe DestinationType.Export
      }
    }

    ".movementType" when {
      "user is a warehouse keeper" must {
        "return IndirectExport" in {
          MovementScenario.ExportWithCustomsDeclarationLodgedInTheEu.movementType(warehouseKeeperDataRequest) mustBe MovementType.IndirectExport
        }
      }

      "user is a registered consignor" must {
        "return ImportIndirectExport" in {
          MovementScenario.ExportWithCustomsDeclarationLodgedInTheEu.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportIndirectExport
        }
      }

      "user is not a warehouse keeper or a registered consignor" must {
        "return an error" in {
          intercept[InvalidUserTypeException](MovementScenario.ExportWithCustomsDeclarationLodgedInTheEu.movementType(nonWKRCDataRequest))
        }
      }
    }
  }

  "RegisteredConsignee" should {
    ".destinationType" must {
      "return RegisteredConsignee" in {
        MovementScenario.RegisteredConsignee.destinationType mustBe DestinationType.RegisteredConsignee
      }
    }
    ".movementType" when {
      "user is a warehouse keeper" must {
        "return UkToEu" in {
          MovementScenario.RegisteredConsignee.movementType(warehouseKeeperDataRequest) mustBe MovementType.UkToEu
        }
      }

      "user is a registered consignor" must {
        "return ImportEu" in {
          MovementScenario.RegisteredConsignee.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportEu
        }
      }

      "user is not a warehouse keeper or a registered consignor" must {
        "return an error" in {
          intercept[InvalidUserTypeException](MovementScenario.RegisteredConsignee.movementType(nonWKRCDataRequest))
        }
      }
    }
  }

  "TemporaryRegisteredConsignee" should {

    ".destinationType" must {
      "return TemporaryRegisteredConsignee" in {
        MovementScenario.TemporaryRegisteredConsignee.destinationType mustBe DestinationType.TemporaryRegisteredConsignee
      }
    }

    ".movementType" when {
      "user is a warehouse keeper" must {
        "return UkToEu" in {
          MovementScenario.TemporaryRegisteredConsignee.movementType(warehouseKeeperDataRequest) mustBe MovementType.UkToEu
        }
      }

      "user is a registered consignor" must {
        "return ImportEu" in {
          MovementScenario.TemporaryRegisteredConsignee.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportEu
        }
      }

      "user is not a warehouse keeper or a registered consignor" must {
        "return an error" in {
          intercept[InvalidUserTypeException](MovementScenario.TemporaryRegisteredConsignee.movementType(nonWKRCDataRequest))
        }
      }
    }
  }

  "UnknownDestination" should {

    ".destinationType" must {
      "return UnknownDestination" in {
        MovementScenario.UnknownDestination.destinationType mustBe DestinationType.UnknownDestination
      }
    }
    ".movementType" when {
      "user is a warehouse keeper" must {
        "return UkToEu" in {
          MovementScenario.UnknownDestination.movementType(warehouseKeeperDataRequest) mustBe MovementType.UkToEu
        }
      }

      "user is a registered consignor" must {
        "return ImportUnknownDestination" in {
          MovementScenario.UnknownDestination.movementType(registeredConsignorDataRequest) mustBe MovementType.ImportUnknownDestination
        }
      }

      "user is not a warehouse keeper or a registered consignor" must {
        "return an error" in {
          intercept[InvalidUserTypeException](MovementScenario.UnknownDestination.movementType(nonWKRCDataRequest))
        }
      }
    }
  }

  "CertifiedConsignee" should {

    ".destinationType" must {
      "return CertifiedConsignee" in {
        MovementScenario.CertifiedConsignee.destinationType mustBe DestinationType.CertifiedConsignee
      }
    }
    ".movementType" when {
      "user is DutyPaid" must {
        "return UkToEu" in {
          MovementScenario.CertifiedConsignee.movementType(dutyPaidDataRequest) mustBe MovementType.UkToEu
        }
      }

      "user is not DutyPaid" must {
        "return an error" in {
          intercept[InvalidUserTypeException](MovementScenario.CertifiedConsignee.movementType(warehouseKeeperDataRequest))
        }
      }
    }
  }

  "TemporaryCertifiedConsignee" should {

    ".destinationType" must {
      "return CertifiedConsignee" in {
        MovementScenario.TemporaryCertifiedConsignee.destinationType mustBe DestinationType.TemporaryCertifiedConsignee
      }
    }
    ".movementType" when {
      "user is DutyPaid" must {
        "return UkToEu" in {
          MovementScenario.TemporaryCertifiedConsignee.movementType(dutyPaidDataRequest) mustBe MovementType.UkToEu
        }
      }

      "user is not DutyPaid" must {
        "return an error" in {
          intercept[InvalidUserTypeException](MovementScenario.TemporaryCertifiedConsignee.movementType(warehouseKeeperDataRequest))
        }
      }
    }
  }

  "ReturnToThePlaceOfDispatchOfTheConsignor" should {

    ".destinationType" must {
      "return CertifiedConsignee" in {
        MovementScenario.ReturnToThePlaceOfDispatchOfTheConsignor.destinationType mustBe DestinationType.ReturnToThePlaceOfDispatchOfTheConsignor
      }
    }
    ".movementType" when {
      "user is DutyPaid" must {
        "return UkToEu" in {
          MovementScenario.ReturnToThePlaceOfDispatchOfTheConsignor.movementType(dutyPaidDataRequest) mustBe MovementType.UkToEu
        }
      }

      "user is not DutyPaid" must {
        "return an error" in {
          intercept[InvalidUserTypeException](MovementScenario.ReturnToThePlaceOfDispatchOfTheConsignor.movementType(warehouseKeeperDataRequest))
        }
      }
    }
  }

}
