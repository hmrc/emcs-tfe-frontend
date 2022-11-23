/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.services

import cats.data.EitherT
import cats.implicits._
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.emcstfefrontend.connectors.ReferenceDataConnector
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

@Singleton
class ModeOfTransportService @Inject()(referenceDataConnector: ReferenceDataConnector) {
  def getMessage()(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): ModeOfTransportResponse = for {
    referenceDataResponse <- EitherT(referenceDataConnector.getMessage())
  } yield (referenceDataResponse)
}
