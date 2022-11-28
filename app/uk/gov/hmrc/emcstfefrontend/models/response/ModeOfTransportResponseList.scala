/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.models.response

import play.api.libs.json.{Json, OFormat}

sealed trait ModeOfTransportResponseListModel

case class ModeOfTransportResponseList(transportList: List[ModeOfTransportResponse]) extends ModeOfTransportResponseListModel

object ModeOfTransportResponseList {
  implicit val format: OFormat[ModeOfTransportResponseList] = Json.format
}

case class ModeOfTransportErrorResponse(status: Int, reason: String) extends ModeOfTransportResponseListModel

object ModeOfTransportErrorResponse {
  implicit val format: OFormat[ModeOfTransportErrorResponse] = Json.format
}
