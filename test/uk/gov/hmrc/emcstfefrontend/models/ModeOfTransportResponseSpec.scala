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
import uk.gov.hmrc.emcstfefrontend.models.response.ModeOfTransportModel
import uk.gov.hmrc.emcstfefrontend.support.ModeOfTransportListFixture.{validModeOfTransportJson, validModeOfTransportResponseModel1}
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec


  class ModeOfTransportResponseSpec extends UnitSpec {

    "ModeOfTransportResponse" should {
      "read from json" when {
        "the json is complete" in {
          Json.fromJson[ModeOfTransportModel](validModeOfTransportJson) shouldBe JsSuccess(validModeOfTransportResponseModel1)
        }
      }
      "write to json" when {
        "the model is complete" in {
          Json.toJson(validModeOfTransportResponseModel1) shouldBe validModeOfTransportJson
        }
      }
    }
  }