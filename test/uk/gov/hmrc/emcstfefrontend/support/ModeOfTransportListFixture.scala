/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.support

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.emcstfefrontend.models.response.{ModeOfTransportResponse, ModeOfTransportResponseList}

object ModeOfTransportListFixture {

  val validOtherDataReferenceJson: JsValue = Json.parse(
    """
      |{
      |   "typeName":"TransportMode",
      |   "code":"0",
      |   "description":"Other"
      |}
			|""".stripMargin)

  val validOtherDataReferenceModel1: ModeOfTransportResponse =
    ModeOfTransportResponse(typeName = "TransportMode", code = "0", description = "Other")


  val validOtherDataReferenceModel2: ModeOfTransportResponse =
    ModeOfTransportResponse(typeName = "TransportMode", code = "5", description = "Postal consignment")


  val validOtherDataReferenceListJson: JsValue = Json.parse(
    """
      | {
      |   "transportList":
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

  val validOtherDataReferenceListModel: ModeOfTransportResponseList = ModeOfTransportResponseList(
    List(validOtherDataReferenceModel1,validOtherDataReferenceModel2)
  )

}