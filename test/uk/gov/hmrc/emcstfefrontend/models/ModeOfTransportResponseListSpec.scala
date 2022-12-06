/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

  package uk.gov.hmrc.emcstfefrontend.models


  import play.api.libs.json.{JsSuccess, Json}
  import uk.gov.hmrc.emcstfefrontend.models.response.{ModeOfTransportErrorResponse, ModeOfTransportListModel, ModeOfTransportModel}
  import uk.gov.hmrc.emcstfefrontend.support.ModeOfTransportListFixture.{modeOfTransportError, modeOfTransportErrorJson, validModeOfTransportListJson, validModeOfTransportResponseListModel, validModeOfTransportResponseModel1, validModeOfTransportResponseModel2}
  import uk.gov.hmrc.emcstfefrontend.support.UnitSpec


  class ModeOfTransportResponseListSpec extends UnitSpec {

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
    "ModeOfTransportResponseError" should {
      "read from json" when {

        "the json is complete" in {
          Json.fromJson[ModeOfTransportErrorResponse](modeOfTransportErrorJson) shouldBe JsSuccess(modeOfTransportError)
        }

      }
    }
  }