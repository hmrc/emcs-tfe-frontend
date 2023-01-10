/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.models.response

import play.api.libs.json.{Json, OFormat}


sealed trait ModeOfTransportListResponseModel

case class ModeOfTransportListModel(otherRefdata: List[ModeOfTransportModel]) extends ModeOfTransportListResponseModel {

  private def moveOtherToBack(transportOptions: Seq[ModeOfTransportModel]): Seq[ModeOfTransportModel] = {
    transportOptions.span(_.code == "0") match {
      case (before, item::after) => item::after ++ before
      case _ => transportOptions
    }
  }

  val orderedOptions: Seq[ModeOfTransportModel] =
    moveOtherToBack(otherRefdata.sortBy(_.code))
}

object ModeOfTransportListModel {
  implicit val format: OFormat[ModeOfTransportListModel] = Json.format
}

case class ModeOfTransportErrorResponse(status: Int, reason: String) extends ModeOfTransportListResponseModel

object ModeOfTransportErrorResponse {
  implicit val format: OFormat[ModeOfTransportErrorResponse] = Json.format
}
