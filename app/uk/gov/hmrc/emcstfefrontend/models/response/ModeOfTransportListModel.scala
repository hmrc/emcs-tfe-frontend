/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.models.response

import play.api.libs.json.{Json, OFormat}

sealed trait ModeOfTransportListResponseModel

case class ModeOfTransportListModel(otherRefdata: List[ModeOfTransportModel]) extends ModeOfTransportListResponseModel {

  private def dropAndAppendOtherOption(transportOptions: Seq[(String,String)]): Seq[(String, String)] = {
    val otherOption = Seq(transportOptions(0))
    transportOptions.drop(1) ++ otherOption
  }

  val orderedOptions: Seq[(String, String)] =
    dropAndAppendOtherOption(otherRefdata.map(value => (value.code,  value.description)).sortBy(_._1))
}

object ModeOfTransportListModel {
  implicit val format: OFormat[ModeOfTransportListModel] = Json.format
}

case class ModeOfTransportErrorResponse(status: Int, reason: String) extends ModeOfTransportListResponseModel

object ModeOfTransportErrorResponse {
  implicit val format: OFormat[ModeOfTransportErrorResponse] = Json.format
}
