/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.models.response

import play.api.libs.json.{Json, OFormat}

case class ModeOfTransportResponse(modeOfTransport: String)

object ModeOfTransportResponse {
  implicit val format: OFormat[ModeOfTransportResponse] = Json.format
}