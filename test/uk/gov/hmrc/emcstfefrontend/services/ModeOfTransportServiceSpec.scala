/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.services

import uk.gov.hmrc.emcstfefrontend.mocks.connectors.{MockEmcsTfeConnector, MockReferenceDataConnector}
import uk.gov.hmrc.emcstfefrontend.models.response.ReferenceDataResponse
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

  "getMessage" should {
    "return Right" when {
      "connector returns a Right" in new Test {
        val referenceDataConnectorResponse: ReferenceDataResponse = ReferenceDataResponse("test message")

        MockReferenceDataConnector.getMessage().returns(Future.successful(Right(referenceDataConnectorResponse)))

        await(service.getMessage().value) shouldBe Right((referenceDataConnectorResponse))
      }
    }
    "return Left" when {
      "reference data connector returns a Left" in new Test {
        val referenceDataConnectorResponse: Either[String, ReferenceDataResponse] = Left("error message")

        MockReferenceDataConnector.getMessage().returns(Future.successful(referenceDataConnectorResponse))

        await(service.getMessage().value) shouldBe referenceDataConnectorResponse
      }
    }
  }
}
