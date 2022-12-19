/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.support

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.emcstfefrontend.models.response.{ModeOfTransportErrorResponse, ModeOfTransportListModel, ModeOfTransportModel}

object ModeOfTransportListFixture {

  val validModeOfTransportJson: JsValue = Json.parse(
    """
      |{
      |   "typeName":"TransportMode",
      |   "code":"0",
      |   "description":"Other"
      |}
			|""".stripMargin)

  val modeOfTransportErrorJson: JsValue = Json.parse(
    """
      |{
      |   "status":404,
      |   "reason":"error"
      |}
			|""".stripMargin)

  val validModeOfTransportResponseModel1: ModeOfTransportModel =
    ModeOfTransportModel(typeName = "TransportMode", code = "0", description = "Other")

  val modeOfTransportError: ModeOfTransportErrorResponse =
    ModeOfTransportErrorResponse(status = 404, reason = "error")


  val validModeOfTransportResponseModel2: ModeOfTransportModel =
    ModeOfTransportModel(typeName = "TransportMode", code = "5", description = "Postal consignment")


  val validModeOfTransportListJson: JsValue = Json.parse(
    """
      | {
      |   "otherRefdata":
      |     [
      |       {
      |          "typeName":"TransportMode",
      |          "code":"0",
      |          "description":"Other"
      |       },
      |       {
      |          "typeName":"TransportMode",
      |          "code":"5",
      |          "description":"Postal consignment"
      |       }
      |     ]
      | }
			|""".stripMargin)

  val validModeOfTransportResponseListModel: ModeOfTransportListModel = ModeOfTransportListModel(
    List(validModeOfTransportResponseModel1,validModeOfTransportResponseModel2)
  )

}