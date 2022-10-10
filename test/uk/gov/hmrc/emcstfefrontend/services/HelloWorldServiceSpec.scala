/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.services

import uk.gov.hmrc.emcstfefrontend.mocks.connectors.{MockEmcsTfeConnector, MockReferenceDataConnector}
import uk.gov.hmrc.emcstfefrontend.models.response.{EmcsTfeResponse, ReferenceDataResponse}
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class HelloWorldServiceSpec extends UnitSpec with MockReferenceDataConnector with MockEmcsTfeConnector {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val service: HelloWorldService = new HelloWorldService(
      mockReferenceDataConnector,
      mockEmcsTfeConnector
    )
  }

  "getMessage" should {
    "return Right" when {
      "connector returns a Right" in new Test {
        val referenceDataConnectorResponse: ReferenceDataResponse = ReferenceDataResponse("test message")
        val emcsTfeConnectorResponse: EmcsTfeResponse = EmcsTfeResponse("test message")

        MockReferenceDataConnector.getMessage().returns(Future.successful(Right(referenceDataConnectorResponse)))
        MockEmcsTfeConnector.getMessage().returns(Future.successful(Right(emcsTfeConnectorResponse)))

        await(service.getMessage().value) shouldBe Right((referenceDataConnectorResponse, emcsTfeConnectorResponse))
      }
    }
    "return Left" when {
      "reference data connector returns a Left" in new Test {
        val referenceDataConnectorResponse: Either[String, ReferenceDataResponse] = Left("error message")

        MockReferenceDataConnector.getMessage().returns(Future.successful(referenceDataConnectorResponse))

        await(service.getMessage().value) shouldBe referenceDataConnectorResponse
      }
      "emcs tfe connector returns a Left" in new Test {
        val referenceDataConnectorResponse: Either[String, ReferenceDataResponse] = Right(ReferenceDataResponse("test message"))
        val emcsTfeConnectorResponse: Either[String, EmcsTfeResponse] = Left("error message")

        MockReferenceDataConnector.getMessage().returns(Future.successful(referenceDataConnectorResponse))
        MockEmcsTfeConnector.getMessage().returns(Future.successful(emcsTfeConnectorResponse))

        await(service.getMessage().value) shouldBe emcsTfeConnectorResponse
      }
    }
  }
}
