/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.emcstfefrontend.connectors

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.http.Status._
import uk.gov.hmrc.emcstfefrontend.config.AppConfig
import uk.gov.hmrc.emcstfefrontend.models.response.ErrorResponse.{JsonValidationError, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.emcstfefrontend.models.response.{EmcsTfeResponse, ErrorResponse, GetMovementResponse}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmcsTfeConnector @Inject()(http: HttpClient,
                                 config: AppConfig) extends BaseConnector {

  override lazy val logger: Logger = Logger(this.getClass)

  lazy val baseUrl: String = config.emcsTfeBaseUrl

  def hello()(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[ErrorResponse, EmcsTfeResponse]] = {
    def helloUrl(): String = s"$baseUrl/hello-world"
    http.GET[HttpResponse](helloUrl()).map {
      response => response.status match {
        case OK => response.validateJson[EmcsTfeResponse] match {
          case Some(valid) => Right(valid)
          case None =>
            logger.warn(s"Bad JSON response from emcs-tfe")
            Left(JsonValidationError)
        }
        case status =>
          logger.warn(s"Unexpected status from emcs-tfe: $status")
          Left(UnexpectedDownstreamResponseError)
      }
    }
  }

  def getMovement(exciseRegistrationNumber: String, arc: String)(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[ErrorResponse, GetMovementResponse]] = {
    def url: String = s"$baseUrl/get-movement/$exciseRegistrationNumber/$arc"
    http.GET[HttpResponse](url).map {
      response => response.status match {
        case OK => response.validateJson[GetMovementResponse] match {
          case Some(valid) => Right(valid)
          case None =>
            logger.warn(s"Bad JSON response from emcs-tfe")
            Left(JsonValidationError)
        }
        case status =>
          logger.warn(s"Unexpected status from emcs-tfe: $status")
          Left(UnexpectedDownstreamResponseError)
      }
    }
  }

}
