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


  import play.api.libs.json.{JsSuccess, Json}
  import uk.gov.hmrc.emcstfefrontend.fixtures.ModeOfTransportListFixture
  import uk.gov.hmrc.emcstfefrontend.models.response.referenceData.{ModeOfTransportListModel, ModeOfTransportModel}
  import uk.gov.hmrc.emcstfefrontend.support.UnitSpec


  class ModeOfTransportResponseListSpec extends UnitSpec with ModeOfTransportListFixture {

    "ModeOfTransportResponseList" should {
      "read from json" when {
        "the json is complete" in {
          Json.fromJson[ModeOfTransportListModel](validModeOfTransportListJson) shouldBe JsSuccess(validModeOfTransportResponseListModel)
        }

        "produce the correctly ordered list of options" when {
          "there are three options with including other" in {
            val testMode: ModeOfTransportListModel = ModeOfTransportListModel(
              List(
                validModeOfTransportResponseModel1,
                validModeOfTransportResponseModel2,
                ModeOfTransportModel(typeName = "TransportMode", code = "4", description = "Test option")
              )
            )

            testMode.orderedOptions.size shouldBe 3
            testMode.orderedOptions.head.description shouldBe "Test option"
            testMode.orderedOptions(1).description shouldBe "Postal consignment"
            testMode.orderedOptions.last.description shouldBe "Other"
          }
        }

      }
      "write to json" when {

        "the model is complete" in {
          Json.toJson(validModeOfTransportResponseListModel) shouldBe validModeOfTransportListJson
        }
      }

    }
  }