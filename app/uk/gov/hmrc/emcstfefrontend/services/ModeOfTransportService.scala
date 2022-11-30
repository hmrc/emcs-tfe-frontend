/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.services

import play.api.Logger

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.emcstfefrontend.connectors.ReferenceDataConnector
import uk.gov.hmrc.emcstfefrontend.models.response.{ModeOfTransportErrorResponse, ModeOfTransportListModel, ModeOfTransportListResponseModel}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ModeOfTransportService @Inject()(referenceDataConnector: ReferenceDataConnector) {
  lazy val logger: Logger = Logger(this.getClass)

  def getOtherDataReferenceList(implicit headerCarrier: HeaderCarrier, executionContext: ExecutionContext): Future[ModeOfTransportListResponseModel] = {
    referenceDataConnector.getOtherReferenceDataList().map {
      case success: ModeOfTransportListModel =>
        logger.debug(s"[ModeOfTransportService][getOtherDataReferenceList] - Retrieved Other Data Reference List:\n\n$success")
        success
      case error: ModeOfTransportErrorResponse =>
        logger.error(s"[ModeOfTransportService][getOtherDataReferenceList] - Retrieved Other Data Reference List:\n\n$error")
        ModeOfTransportErrorResponse(error.status, error.reason)
    }
  }
}
