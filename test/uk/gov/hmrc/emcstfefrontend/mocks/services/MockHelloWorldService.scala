/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.mocks.services

import cats.data.EitherT
import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfefrontend.models.response.{EmcsTfeResponse, ReferenceDataResponse}
import uk.gov.hmrc.emcstfefrontend.services.HelloWorldService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockHelloWorldService extends MockFactory {
  lazy val mockService: HelloWorldService = mock[HelloWorldService]

  object MockService {
    def getMessage(): CallHandler2[HeaderCarrier, ExecutionContext, EitherT[Future, String, (ReferenceDataResponse, EmcsTfeResponse)]] = {
      (mockService.getMessage()(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *)
    }
  }
}
