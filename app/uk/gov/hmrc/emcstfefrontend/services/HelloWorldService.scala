/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.services

import cats.data.EitherT
import cats.implicits._
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.emcstfefrontend.connectors.{EmcsTfeConnector, ReferenceDataConnector}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext

@Singleton
class HelloWorldService @Inject()(referenceDataConnector: ReferenceDataConnector, emcsTfeConnector: EmcsTfeConnector) {
  def getMessage()(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): HelloWorldResponse = for {
    referenceDataResponse <- EitherT(referenceDataConnector.getMessage())
    emcsTfeResponse <- EitherT(emcsTfeConnector.getMessage())
  } yield (referenceDataResponse, emcsTfeResponse)
}
