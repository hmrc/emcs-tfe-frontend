/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.mocks.connectors

import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfefrontend.connectors.EmcsTfeConnector
import uk.gov.hmrc.emcstfefrontend.models.response.EmcsTfeResponse
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockEmcsTfeConnector extends MockFactory {
  lazy val mockEmcsTfeConnector: EmcsTfeConnector = mock[EmcsTfeConnector]

  object MockEmcsTfeConnector {
    def getMessage(): CallHandler2[HeaderCarrier, ExecutionContext, Future[Either[String, EmcsTfeResponse]]] = {
      (mockEmcsTfeConnector.getMessage()(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *)
    }
  }
}
