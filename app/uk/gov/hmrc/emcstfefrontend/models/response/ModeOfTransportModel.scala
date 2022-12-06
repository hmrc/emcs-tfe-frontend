/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.models.response

import play.api.libs.json.{Json, OFormat}

case class ModeOfTransportModel(typeName: String, code: String, description: String)

object ModeOfTransportModel {
  implicit val format: OFormat[ModeOfTransportModel] = Json.format
}