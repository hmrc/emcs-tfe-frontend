/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.mocks.connectors

import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfefrontend.connectors.ReferenceDataConnector
import uk.gov.hmrc.emcstfefrontend.models.response.ReferenceDataResponse
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockReferenceDataConnector extends MockFactory {
  lazy val mockReferenceDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  object MockReferenceDataConnector {
    def getMessage(): CallHandler2[HeaderCarrier, ExecutionContext, Future[Either[String, ReferenceDataResponse]]] = {
      (mockReferenceDataConnector.getMessage()(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *)
    }
  }
}
