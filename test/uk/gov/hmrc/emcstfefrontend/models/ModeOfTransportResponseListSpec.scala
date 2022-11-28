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
  import uk.gov.hmrc.emcstfefrontend.models.response.{ModeOfTransportErrorResponse, ModeOfTransportResponseList}
  import uk.gov.hmrc.emcstfefrontend.support.ModeOfTransportListFixture.{modeOfTransportError, modeOfTransportErrorJson, validModeOfTransportListJson, validModeOfTransportResponseListModel}
  import uk.gov.hmrc.emcstfefrontend.support.UnitSpec


  class ModeOfTransportResponseListSpec extends UnitSpec {

    "ModeOfTransportResponseList" should {
      "read from json" when {
        "the json is complete" in {
          Json.fromJson[ModeOfTransportResponseList](validModeOfTransportListJson) shouldBe JsSuccess(validModeOfTransportResponseListModel)
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