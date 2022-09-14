/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.connectors

import play.api.Logger
import play.api.http.Status._
import uk.gov.hmrc.emcstfefrontend.config.AppConfig
import uk.gov.hmrc.emcstfefrontend.models.response.HelloWorldResponse
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HelloWorldConnector @Inject()(http: HttpClient,
                                    config: AppConfig) extends RawResponseReads {

  lazy val logger: Logger = Logger(this.getClass)

  lazy val baseUrl = config.referenceDataBaseUrl

  def helloWorldUrl(): String = s"$baseUrl/hello-world"

  def getMessage()(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[String, HelloWorldResponse]] = {
    http.GET[HttpResponse](helloWorldUrl()).map {
      response => response.status match {
        case OK => response.json.validate[HelloWorldResponse].fold(
          invalid => {
            logger.warn(s"Bad JSON response from reference-data: ${invalid}")
            Left("JSON validation error")
          },
          valid => Right(valid)
        )
        case status =>
          logger.warn(s"Unexpected status from reference-data: $status")
          Left("Unexpected downstream response status")
      }
    }
  }

}
