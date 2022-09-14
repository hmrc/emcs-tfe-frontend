/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.services

import uk.gov.hmrc.emcstfefrontend.mocks.connectors.MockHelloWorldConnector
import uk.gov.hmrc.emcstfefrontend.models.response.HelloWorldResponse
import uk.gov.hmrc.emcstfefrontend.testHelpers.UnitSpec
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class HelloWorldServiceSpec extends UnitSpec with MockHelloWorldConnector {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val service: HelloWorldService = new HelloWorldService(
      mockConnector
    )
  }

  "getMessage" should {
    "return Right" when {
      "connector returns a Right" in new Test {
        val result: Either[String, HelloWorldResponse] = Right(HelloWorldResponse("test message"))

        MockConnector.getMessage().returns(Future.successful(result))

        await(service.getMessage()) shouldBe result
      }
    }
    "return Left" when {
      "connector returns a Left" in new Test {
        val result: Either[String, HelloWorldResponse] = Left("error message")

        MockConnector.getMessage().returns(Future.successful(result))

        await(service.getMessage()) shouldBe result
      }
    }
  }
}
