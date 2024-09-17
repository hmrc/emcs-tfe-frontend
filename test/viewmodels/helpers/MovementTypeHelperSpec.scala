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

package viewmodels.helpers

import base.SpecBase
import models.common.DestinationType.ReturnToThePlaceOfDispatchOfTheConsignor
import models.movementScenario.MovementScenario
import models.movementScenario.MovementScenario._
import models.requests.DataRequest
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing
import utils.Logging

import scala.concurrent.ExecutionContext

class MovementTypeHelperSpec extends SpecBase with LogCapturing with Logging {

  implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "GBRC345GTR145")
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  val helper: MovementTypeHelper = new MovementTypeHelper()

  implicit lazy val messages: Messages = messages(request)

  "getMovementType" should {
    "show Return to the place of dispatch" when {
      s"the destination type is $ReturnToThePlaceOfDispatchOfTheConsignor" in {
        val result = helper.getMovementType(
          request.userTypeFromErn,
          MovementScenario.ReturnToThePlaceOfDispatchOfTheConsignor,
          Some(testPlaceOfDispatchTrader),
          isBeingViewedByConsignor = true,
          isBeingViewedByConsignee = false
        )
        result mustBe "Return to the place of dispatch of the consignor"
      }
    }

    "show GB tax warehouse to Y" when {
      "the users ERN starts with GBWK and the logged in user is the consignor" in {
        implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "GBWK123456789")
        val result = helper.getMovementType(
          request.userTypeFromErn,
          MovementScenario.UkTaxWarehouse.GB,
          None,
          isBeingViewedByConsignor = true,
          isBeingViewedByConsignee = false
        )
        result mustBe "Great Britain tax warehouse to tax warehouse in Great Britain"
      }
    }

    "show GB tax warehouse to Y for XIWK ERN (dispatch place is GB)" when {
      Seq(
        UkTaxWarehouse.GB -> "Great Britain tax warehouse to tax warehouse in Great Britain",
        DirectDelivery -> "Great Britain tax warehouse to direct delivery",
        RegisteredConsignee -> "Great Britain tax warehouse to registered consignee",
        TemporaryRegisteredConsignee -> "Great Britain tax warehouse to temporary registered consignee",
        ExemptedOrganisation -> "Great Britain tax warehouse to exempted organisation",
        UnknownDestination -> "Great Britain tax warehouse to unknown destination"
      ).foreach { destinationTypeToMessage =>

        s"when the destination type is ${destinationTypeToMessage._1}" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789")

          val result = helper.getMovementType(
            request.userTypeFromErn,
            destinationTypeToMessage._1,
            Some(testPlaceOfDispatchTrader),
            isBeingViewedByConsignor = true,
            isBeingViewedByConsignee = false
          )

          result mustBe destinationTypeToMessage._2
        }

      }
    }

    "show XI tax warehouse to Y for XIWK ERN (dispatch place is XI)" when {
      Seq(
        UkTaxWarehouse.GB -> "Northern Ireland tax warehouse to tax warehouse in Great Britain",
        DirectDelivery -> "Northern Ireland tax warehouse to direct delivery",
        RegisteredConsignee -> "Northern Ireland tax warehouse to registered consignee",
        TemporaryRegisteredConsignee -> "Northern Ireland tax warehouse to temporary registered consignee",
        ExemptedOrganisation -> "Northern Ireland tax warehouse to exempted organisation",
        UnknownDestination -> "Northern Ireland tax warehouse to unknown destination"
      ).foreach { destinationTypeToMessage =>

        s"the destination type is ${destinationTypeToMessage._1}" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789")

          val result = helper.getMovementType(
            request.userTypeFromErn,
            destinationTypeToMessage._1,
            Some(testPlaceOfDispatchTrader.copy(
              traderExciseNumber = Some("XI00345GTR145"),
              vatNumber = Some("GB123456789"),
              eoriNumber = None
            )),
            isBeingViewedByConsignor = true,
            isBeingViewedByConsignee = false
          )

          result mustBe destinationTypeToMessage._2
        }

      }
    }

    "show non-UK movement to Y for XIWK ERN (dispatch place is neither GB not XI)" when {
      Seq(
        UkTaxWarehouse.GB -> "Movement from outside the United Kingdom to tax warehouse in Great Britain",
        DirectDelivery -> "Movement from outside the United Kingdom to direct delivery",
        RegisteredConsignee -> "Movement from outside the United Kingdom to registered consignee",
        TemporaryRegisteredConsignee -> "Movement from outside the United Kingdom to temporary registered consignee",
        ExemptedOrganisation -> "Movement from outside the United Kingdom to exempted organisation",
        UnknownDestination -> "Movement from outside the United Kingdom to unknown destination"
      ).foreach {
        case (destinationType, message) =>
          implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789")

          val result = helper.getMovementType(
            request.userTypeFromErn,
            destinationType,
            Some(testPlaceOfDispatchTrader.copy(
              traderExciseNumber = Some("FR00345GTR145"),
              vatNumber = Some("GB123456789"),
              eoriNumber = None
            )),
            isBeingViewedByConsignor = true,
            isBeingViewedByConsignee = false
          )

          result mustBe message
      }
    }

    "show movement to Y for XIWK ERN" when {
      Seq(
        UkTaxWarehouse.GB -> "Movement to tax warehouse in Great Britain",
        DirectDelivery -> "Movement to direct delivery",
        RegisteredConsignee -> "Movement to registered consignee",
        TemporaryRegisteredConsignee -> "Movement to temporary registered consignee",
        ExemptedOrganisation -> "Movement to exempted organisation",
        UnknownDestination -> "Movement to unknown destination"
      ).foreach {
        case (destinationType, message) =>

          s"when the destination type is $destinationType" when {
            "placeOfDispatchTrader.traderExciseNumber is missing" in {
              implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789")

              val result = helper.getMovementType(
                request.userTypeFromErn,
                destinationType,
                Some(testPlaceOfDispatchTrader.copy(
                  traderExciseNumber = None,
                  vatNumber = Some("GB123456789"),
                  eoriNumber = None
                )),
                isBeingViewedByConsignor = true,
                isBeingViewedByConsignee = false
              )

              result mustBe message
            }

            "placeOfDispatchTrader is missing" in {
              implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789")

              val result = helper.getMovementType(
                request.userTypeFromErn,
                destinationType,
                None,
                isBeingViewedByConsignor = true,
                isBeingViewedByConsignee = false
              )

              result mustBe message
            }
          }

      }
    }

    "show movement to Y for GBWK ERN" when {
      "logged in user is consignee" in {
        implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "GBWK123456789")

        val result = helper.getMovementType(
          request.userTypeFromErn,
          UkTaxWarehouse.GB,
          Some(testPlaceOfDispatchTrader),
          isBeingViewedByConsignor = false,
          isBeingViewedByConsignee = true
        )

        result mustBe "Movement to tax warehouse in Great Britain"
      }
    }

    Seq("GB00", "XI00").foreach {
      ernPrefix =>
        s"show movement to Y for $ernPrefix ERN" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = s"${ernPrefix}123456789")

          val result = helper.getMovementType(
            request.userTypeFromErn,
            RegisteredConsignee,
            Some(testPlaceOfDispatchTrader),
            isBeingViewedByConsignor = true,
            isBeingViewedByConsignee = false
          )

          result mustBe "Movement to registered consignee"
        }
    }

    "show Import for GB/XI" when {

      "the users ERN starts with GBRC" in {
        implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "GBRC123456789")

        val result = helper.getMovementType(
          request.userTypeFromErn,
          RegisteredConsignee,
          Some(testPlaceOfDispatchTrader),
          isBeingViewedByConsignor = true,
          isBeingViewedByConsignee = false
        )

        result mustBe "Import for registered consignee"
      }

      "the users ERN starts with XIRC" in {
        implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "XIRC123456789")

        val result = helper.getMovementType(
          request.userTypeFromErn,
          RegisteredConsignee,
          Some(testPlaceOfDispatchTrader),
          isBeingViewedByConsignor = true,
          isBeingViewedByConsignee = false
        )

        result mustBe "Import for registered consignee"
      }

    }

    "show the destination type only" when {

      "the users ERN starts with GBWK and the movement type is an Export (Customs declaration in UK)" in {
        implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "GBWK123456789")

        val result = helper.getMovementType(
          request.userTypeFromErn,
          ExportWithCustomsDeclarationLodgedInTheUk,
          Some(testPlaceOfDispatchTrader),
          isBeingViewedByConsignor = true,
          isBeingViewedByConsignee = false
        )

        result mustBe "Export with customs declaration lodged in the United Kingdom"
      }

      "the users ERN starts with GBWK and the movement type is an Export (Customs declaration in EU)" in {
        implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "GBWK123456789")

        val result = helper.getMovementType(
          request.userTypeFromErn,
          ExportWithCustomsDeclarationLodgedInTheEu,
          Some(testPlaceOfDispatchTrader),
          isBeingViewedByConsignor = true,
          isBeingViewedByConsignee = false
        )

        result mustBe "Export with customs declaration lodged in the European Union"
      }

      "the users ERN starts with XIWK and the movement type is an Export (Customs declaration in UK)" in {
        implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789")

        val result = helper.getMovementType(
          request.userTypeFromErn,
          ExportWithCustomsDeclarationLodgedInTheUk,
          Some(testPlaceOfDispatchTrader),
          isBeingViewedByConsignor = true,
          isBeingViewedByConsignee = false
        )

        result mustBe "Export with customs declaration lodged in the United Kingdom"
      }

      "the users ERN starts with XIWK and the movement type is an Export (Customs declaration in EU)" in {
        implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "XIWK123456789")

        val result = helper.getMovementType(
          request.userTypeFromErn,
          ExportWithCustomsDeclarationLodgedInTheEu,
          Some(testPlaceOfDispatchTrader),
          isBeingViewedByConsignor = true,
          isBeingViewedByConsignee = false
        )

        result mustBe "Export with customs declaration lodged in the European Union"
      }
    }

    "cater for Duty Paid scenarios" when {

      "user is XIPA" when {

        "DestinationType is CertifiedConsignee" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "XIPA00123456")

          val result = helper.getMovementType(
            request.userTypeFromErn,
            CertifiedConsignee,
            Some(testPlaceOfDispatchTrader),
            isBeingViewedByConsignor = true,
            isBeingViewedByConsignee = false
          )

          result mustBe "Northern Ireland certified consignor to certified consignee in European Union"
        }

        "DestinationType is TemporaryRegisteredConsignee" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "XIPA00123456")

          val result = helper.getMovementType(
            request.userTypeFromErn,
            TemporaryCertifiedConsignee,
            Some(testPlaceOfDispatchTrader),
            isBeingViewedByConsignor = true,
            isBeingViewedByConsignee = false
          )

          result mustBe "Northern Ireland certified consignor to temporary certified consignee in European Union"
        }
      }

      "user is XIPC" when {

        "DestinationType is CertifiedConsignee" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "XIPC00123456")

          val result = helper.getMovementType(
            request.userTypeFromErn,
            CertifiedConsignee,
            Some(testPlaceOfDispatchTrader),
            isBeingViewedByConsignor = true,
            isBeingViewedByConsignee = false
          )

          result mustBe "Northern Ireland temporary certified consignor to certified consignee in European Union"
        }

        "DestinationType is TemporaryRegisteredConsignee" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "XIPC00123456")

          val result = helper.getMovementType(
            request.userTypeFromErn,
            TemporaryCertifiedConsignee,
            Some(testPlaceOfDispatchTrader),
            isBeingViewedByConsignor = true,
            isBeingViewedByConsignee = false
          )

          result mustBe "Northern Ireland temporary certified consignor to temporary certified consignee in European Union"
        }
      }

      "user is XIPB" in {
        implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "XIPB00123456")

        val result = helper.getMovementType(
          request.userTypeFromErn,
          CertifiedConsignee,
          Some(testPlaceOfDispatchTrader),
          isBeingViewedByConsignor = true,
          isBeingViewedByConsignee = false
        )

        result mustBe "European Union duty paid movement to Northern Ireland certified consignee"
      }

      "user is XIPD" in {
        implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "XIPD00123456")

        val result = helper.getMovementType(
          request.userTypeFromErn,
          CertifiedConsignee,
          Some(testPlaceOfDispatchTrader),
          isBeingViewedByConsignor = true,
          isBeingViewedByConsignee = false
        )

        result mustBe "European Union duty paid movement to Northern Ireland temporary certified consignee"
      }
    }

    "cater for XITC" in {
      implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "XITC00123456")

      val result = helper.getMovementType(
        request.userTypeFromErn,
        CertifiedConsignee,
        Some(testPlaceOfDispatchTrader),
        isBeingViewedByConsignor = true,
        isBeingViewedByConsignee = false
      )

      result mustBe "European Union duty-suspended movement to Northern Ireland temporary registered consignee"
    }

    "return Movement to Y when the user type / destination type can't be matched" in {
      implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"), ern = "GB00123456789")

      val result = helper.getMovementType(
        request.userTypeFromErn,
        EuTaxWarehouse,
        Some(testPlaceOfDispatchTrader),
        isBeingViewedByConsignor = true,
        isBeingViewedByConsignee = false
      )

      result mustBe "Movement to tax warehouse in the European Union"
    }
  }
}
