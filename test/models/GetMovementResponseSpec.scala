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

/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import base.SpecBase
import fixtures.GetMovementResponseFixtures
import models.common.RoleType
import models.common.RoleType.XITC
import models.response.emcsTfe.GetMovementResponse
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}
import play.api.libs.json.Json
import play.api.test.FakeRequest

class GetMovementResponseSpec extends SpecBase with GetMovementResponseFixtures {

  "GetMovementResponse" should {

    "read from json" in {
      val result = Json.fromJson[GetMovementResponse](getMovementResponseInputJson)

      result.isSuccess shouldBe true
      result.get shouldBe getMovementResponseModel
    }

    ".formattedDateOfDispatch" in {
      getMovementResponseModel.formattedDateOfDispatch shouldBe "20 November 2008"
    }

    ".formattedExpectedDateOfArrival" when {

      "a timeOfDispatch is present on the movement (format HH:mm:ss)" in {
        getMovementResponseModel.formattedExpectedDateOfArrival shouldBe "10 December 2008"
      }

      "a timeOfDispatch is present on the movement (format HH:mm:ss.SSS)" in {
        val model = getMovementResponseModel.eadEsad.copy(timeOfDispatch = Some("00:00:00.00"))
        getMovementResponseModel
          .copy(eadEsad = model)
          .formattedExpectedDateOfArrival shouldBe "10 December 2008"
      }

      "a timeOfDispatch is present on the movement (format HH:mm:ss.SSSSSS)" in {
        val model = getMovementResponseModel.eadEsad.copy(timeOfDispatch = Some("00:00:00.000000"))
        getMovementResponseModel
          .copy(eadEsad = model)
          .formattedExpectedDateOfArrival shouldBe "10 December 2008"
      }

      "a timeOfDispatch is absent from the movement" in {
        val model = getMovementResponseModel.eadEsad.copy(timeOfDispatch = None)

        getMovementResponseModel
          .copy(eadEsad = model)
          .formattedExpectedDateOfArrival shouldBe "10 December 2008"
      }

    }

    "isFromConsignor" should {
      "return true" when {
        "XIPC and Consignor is XIPTA" in {
          implicit val request = dataRequest(FakeRequest(), "XIPC123456789")
          val consignorModel = getMovementResponseModel.consignorTrader.copy(traderExciseNumber = Some("XIPTA123456789"))
          getMovementResponseModel
            .copy(consignorTrader = consignorModel, consigneeTrader = None)
            .isFromConsignor shouldBe true
        }

        RoleType.values.foreach {
          ernPrefix =>
            s"$ernPrefix and Consignor is $ernPrefix" in {
              implicit val request = dataRequest(FakeRequest(), s"${ernPrefix}123456789")
              val consignorModel = getMovementResponseModel.consignorTrader.copy(traderExciseNumber = Some(s"${ernPrefix}123456789"))
              getMovementResponseModel
                .copy(consignorTrader = consignorModel, consigneeTrader = None)
                .isFromConsignor shouldBe true
            }
        }
      }
      "return false" when {
        RoleType.values.foreach {
          ernPrefix =>
            s"$ernPrefix and Consignor is not $ernPrefix" in {
              implicit val request = dataRequest(FakeRequest(), s"${ernPrefix}123456789")
              val consignorModel = getMovementResponseModel.consignorTrader.copy(traderExciseNumber = Some("BEANS123456789"))
              getMovementResponseModel
                .copy(consignorTrader = consignorModel, consigneeTrader = None)
                .isFromConsignor shouldBe false
            }
        }
      }
    }

    "isFromConsignee" should {
      "return true" when {
        RoleType.values.filterNot(_ == XITC).foreach {
          ernPrefix =>
            s"$ernPrefix and Consignee is $ernPrefix" in {
              implicit val request = dataRequest(FakeRequest(), s"${ernPrefix}123456789")
              val consignorModel = getMovementResponseModel.consignorTrader.copy(traderExciseNumber = Some("BEANS123456789"))
              val consigneeModel = getMovementResponseModel.consigneeTrader.get.copy(traderExciseNumber = Some(s"${ernPrefix}123456789"))
              getMovementResponseModel
                .copy(consignorTrader = consignorModel, consigneeTrader = Some(consigneeModel))
                .isFromConsignee shouldBe true
            }
            s"XITC and Consignee is $ernPrefix" in {
              implicit val request = dataRequest(FakeRequest(), "XITC123456789")
              val consignorModel = getMovementResponseModel.consignorTrader.copy(traderExciseNumber = Some("BEANS123456789"))
              val consigneeModel = getMovementResponseModel.consigneeTrader.get.copy(traderExciseNumber = Some(s"${ernPrefix}123456789"))
              getMovementResponseModel
                .copy(consignorTrader = consignorModel, consigneeTrader = Some(consigneeModel))
                .isFromConsignee shouldBe true
            }
        }
        s"XITC and Consignee is XITC" in {
          implicit val request = dataRequest(FakeRequest(), "XITC123456789")
          val consignorModel = getMovementResponseModel.consignorTrader.copy(traderExciseNumber = Some("BEANS123456789"))
          val consigneeModel = getMovementResponseModel.consigneeTrader.get.copy(traderExciseNumber = Some("XITC123456789"))
          getMovementResponseModel
            .copy(consignorTrader = consignorModel, consigneeTrader = Some(consigneeModel))
            .isFromConsignee shouldBe true
        }
      }
      "return false" when {
        RoleType.values.filterNot(_ == XITC).foreach {
          ernPrefix =>
            s"$ernPrefix and Consignee is not $ernPrefix" in {
              implicit val request = dataRequest(FakeRequest(), s"${ernPrefix}123456789")
              val consignorModel = getMovementResponseModel.consignorTrader.copy(traderExciseNumber = Some("BEANS123456789"))
              val consigneeModel = getMovementResponseModel.consigneeTrader.get.copy(traderExciseNumber = Some("BEANS123456789"))
              getMovementResponseModel
                .copy(consignorTrader = consignorModel, consigneeTrader = Some(consigneeModel))
                .isFromConsignee shouldBe false
            }
        }
      }
    }

  }

}