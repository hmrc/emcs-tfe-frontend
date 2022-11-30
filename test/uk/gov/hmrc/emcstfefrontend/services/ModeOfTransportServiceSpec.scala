/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.services

import play.api.http.Status.INTERNAL_SERVER_ERROR
import uk.gov.hmrc.emcstfefrontend.mocks.connectors.{MockEmcsTfeConnector, MockReferenceDataConnector}
import uk.gov.hmrc.emcstfefrontend.models.response.{ModeOfTransportErrorResponse, ReferenceDataResponse}
import uk.gov.hmrc.emcstfefrontend.support.ModeOfTransportListFixture.validModeOfTransportResponseListModel
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ModeOfTransportServiceSpec extends UnitSpec with MockReferenceDataConnector with MockEmcsTfeConnector {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val service: ModeOfTransportService = new ModeOfTransportService(
      mockReferenceDataConnector
    )
  }

  "getOtherDataReferenceList" should {
    "return a successful other reference data list" when {
      "connector returns a success" in new Test {
        val referenceDataConnectorResponse: ReferenceDataResponse = ReferenceDataResponse("test message")

        MockReferenceDataConnector.getOtherReferenceDataList().returns(Future.successful(validModeOfTransportResponseListModel))

        await(service.getOtherDataReferenceList(hc, ec)) shouldBe validModeOfTransportResponseListModel
      }
    }
    "return a un - successful other reference data list" when {
      "reference data connector returns a failure" in new Test {
        val referenceDataConnectorResponse: Either[String, ReferenceDataResponse] = Left("error message")

        MockReferenceDataConnector.getOtherReferenceDataList().returns(Future.successful(ModeOfTransportErrorResponse(INTERNAL_SERVER_ERROR, "issue encountered")))

        await(service.getOtherDataReferenceList(hc, ec)) shouldBe ModeOfTransportErrorResponse(INTERNAL_SERVER_ERROR, "issue encountered")
      }
    }
  }
}
