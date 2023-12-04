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

package uk.gov.hmrc.emcstfefrontend.models

import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}
import play.api.libs.json.{JsSuccess, JsValue, Json}
import uk.gov.hmrc.emcstfefrontend.base.SpecBase
import uk.gov.hmrc.emcstfefrontend.models.common.{AddressModel, TraderModel}
import uk.gov.hmrc.emcstfefrontend.models.response.emcsTfe.GetMovementResponse

import java.time.LocalDate


class GetMovementResponseSpec extends SpecBase {


  val model: GetMovementResponse = GetMovementResponse(
    localReferenceNumber = "MyLrn",
    eadStatus = "MyEadStatus",
    consignorTrader = TraderModel(
      traderExciseNumber = "GB12345GTR144",
      traderName = "Current 801 Consignor",
      address = AddressModel(
        streetNumber = None,
        street = Some("Main101"),
        postcode = Some("ZZ78"),
        city = Some("Zeebrugge")
      )
    ),
    dateOfDispatch = LocalDate.parse("2010-03-04"),
    journeyTime = "MyJourneyTime",
    numberOfItems = 0
  )

  val json: JsValue = Json.parse(
    """{
      |  "localReferenceNumber": "MyLrn",
      |  "eadStatus": "MyEadStatus",
      |    "consignorTrader" : {
      |      "traderExciseNumber" : "GB12345GTR144",
      |      "traderName" : "Current 801 Consignor",
      |      "address": {
      |        "street" : "Main101",
      |        "postcode" : "ZZ78",
      |        "city" : "Zeebrugge"
      |      }
      |    },
      |  "dateOfDispatch": "2010-03-04",
      |  "journeyTime": "MyJourneyTime",
      |  "numberOfItems": 0
      |}""".stripMargin)

  "GetMovementResponse" should {
    "read from json" in {
      Json.fromJson[GetMovementResponse](json) shouldBe JsSuccess(model)
    }
    "write to json" in {
      Json.toJson(model) shouldBe json
    }
    "return a formatted date" when {
      "dateOfDispatch is valid" in {
        model.formattedDateOfDispatch shouldBe "04 March 2010"
      }
    }
  }
}