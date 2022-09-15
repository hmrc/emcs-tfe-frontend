/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.mocks.connectors

import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfefrontend.connectors.HelloWorldConnector
import uk.gov.hmrc.emcstfefrontend.models.response.HelloWorldResponse
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockHelloWorldConnector extends MockFactory {
  lazy val mockConnector: HelloWorldConnector = mock[HelloWorldConnector]

  object MockConnector {
    def getMessage(): CallHandler2[HeaderCarrier, ExecutionContext, Future[Either[String, HelloWorldResponse]]] = {
      (mockConnector.getMessage()(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *)
    }
  }
}
