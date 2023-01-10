/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend

import cats.data.EitherT
import uk.gov.hmrc.emcstfefrontend.models.response.{EmcsTfeResponse, ReferenceDataResponse}

import scala.concurrent.Future

package object services {
  type HelloWorldResponse = EitherT[Future, String, (ReferenceDataResponse, EmcsTfeResponse)]
}
