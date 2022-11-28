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
  import uk.gov.hmrc.emcstfefrontend.models.response.ModeOfTransportResponseList
  import uk.gov.hmrc.emcstfefrontend.support.ModeOfTransportListFixture.{validOtherDataReferenceListJson, validOtherDataReferenceListModel}
  import uk.gov.hmrc.emcstfefrontend.support.UnitSpec


  class ModeOfTransportResponseListSpec extends UnitSpec {

    "OtherDataReferenceSpecList" should {
      "read from json" when {
        "the json is complete" in {
          Json.fromJson[ModeOfTransportResponseList](validOtherDataReferenceListJson) shouldBe JsSuccess(validOtherDataReferenceListModel)
        }
      }
      "write to json" when {
        "the model is complete" in {
          Json.toJson(validOtherDataReferenceListModel) shouldBe validOtherDataReferenceListJson
        }
      }
    }
  }