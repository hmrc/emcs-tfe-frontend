/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.models.response

import play.api.libs.json.{Json, OFormat}

case class ReferenceDataResponse(message: String)

object ReferenceDataResponse {
  implicit val format: OFormat[ReferenceDataResponse] = Json.format
}
