/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.connectors

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.http.Status._
import uk.gov.hmrc.emcstfefrontend.config.AppConfig
import uk.gov.hmrc.emcstfefrontend.models.response.{ModeOfTransportErrorResponse, ModeOfTransportListModel, ModeOfTransportListResponseModel, ReferenceDataResponse}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReferenceDataConnector @Inject()(http: HttpClient,
                                       config: AppConfig) extends BaseConnector {

  override lazy val logger: Logger = Logger(this.getClass)

  lazy val baseUrl: String = config.referenceDataBaseUrl

  def referenceDataUrl(): String = s"$baseUrl/hello-world"
  def getOtherReferenceDataListUrl(): String = s"$baseUrl/other-reference-data-list"

  def getMessage()(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[Either[String, ReferenceDataResponse]] = {
    http.GET[HttpResponse](referenceDataUrl()).map {
      response => response.status match {
        case OK => response.validateJson[ReferenceDataResponse] match {
          case Some(valid) => Right(valid)
          case None =>
            logger.warn(s"Bad JSON response from reference-data")
            Left("JSON validation error")
        }
        case status =>
          logger.warn(s"Unexpected status from reference-data: $status")
          Left("Unexpected downstream response status")
      }
    }
  }

  def getOtherReferenceDataList()(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[ModeOfTransportListResponseModel] = {
    http.GET[HttpResponse](getOtherReferenceDataListUrl()).map {
      response => response.status match {
        case OK => response.validateJson[ModeOfTransportListModel] match {
          case Some(valid) => valid
          case None =>
            logger.warn(s"[getOtherReferenceDataList] Bad JSON response from reference-data")
            ModeOfTransportErrorResponse(INTERNAL_SERVER_ERROR, "JSON validation error")
        }
        case status =>
          logger.warn(s"[getOtherReferenceDataList] Unexpected status from reference-data: $status")
          ModeOfTransportErrorResponse(status, "Unexpected downstream response status")
      }
    }
  }
}
