/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.services

import uk.gov.hmrc.emcstfefrontend.connectors.HelloWorldConnector
import uk.gov.hmrc.emcstfefrontend.models.response.HelloWorldResponse
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HelloWorldService @Inject()(connector: HelloWorldConnector) {
  def getMessage()(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[String, HelloWorldResponse]] = connector.getMessage()
}
