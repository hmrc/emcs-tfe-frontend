/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.models.response

import play.api.libs.json.{Json, OFormat}

case class EmcsTfeResponse(message: String)

object EmcsTfeResponse {
  implicit val format: OFormat[EmcsTfeResponse] = Json.format
}
