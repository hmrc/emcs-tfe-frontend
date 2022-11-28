/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.models.response

import play.api.libs.json.{Format, Json}

case class ModeOfTransportResponse(typeName: String, code: String, description: String)

object ModeOfTransportResponse {
  implicit val format: Format[ModeOfTransportResponse] = Json.format[ModeOfTransportResponse]
}