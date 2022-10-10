/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.connectors

import play.api.Logger
import play.api.http.Status._
import uk.gov.hmrc.emcstfefrontend.config.AppConfig
import uk.gov.hmrc.emcstfefrontend.models.response.EmcsTfeResponse
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmcsTfeConnector @Inject()(http: HttpClient,
                                 config: AppConfig) extends BaseConnector {

  override lazy val logger: Logger = Logger(this.getClass)

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def emcsTfeUrl(): String = s"$baseUrl/hello-world"

  def getMessage()(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[String, EmcsTfeResponse]] = {
    http.GET[HttpResponse](emcsTfeUrl()).map {
      response => response.status match {
        case OK => response.validateJson[EmcsTfeResponse] match {
          case Some(valid) => Right(valid)
          case None =>
            logger.warn(s"Bad JSON response from emcs-tfe")
            Left("JSON validation error")
        }
        case status =>
          logger.warn(s"Unexpected status from emcs-tfe: $status")
          Left("Unexpected downstream response status")
      }
    }
  }

}
