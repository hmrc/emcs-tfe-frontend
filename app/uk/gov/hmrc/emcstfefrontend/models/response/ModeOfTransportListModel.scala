/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.models.response

import play.api.libs.json.{Json, OFormat}

sealed trait ModeOfTransportListResponseModel

case class ModeOfTransportListModel(transportList: List[ModeOfTransportModel]) extends ModeOfTransportListResponseModel

object ModeOfTransportListModel {
  implicit val format: OFormat[ModeOfTransportListModel] = Json.format
}

case class ModeOfTransportErrorResponse(status: Int, reason: String) extends ModeOfTransportListResponseModel

object ModeOfTransportErrorResponse {
  implicit val format: OFormat[ModeOfTransportErrorResponse] = Json.format
}
