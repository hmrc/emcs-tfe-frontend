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

package services

import mocks.connectors.{MockEmcsTfeConnector, MockReferenceDataConnector}
import models.response.{ErrorResponse, UnexpectedDownstreamResponseError}
import models.response.emcsTfe.EmcsTfeResponse
import models.response.referenceData.ReferenceDataResponse
import support.UnitSpec
import services.HelloWorldService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class HelloWorldServiceSpec extends UnitSpec with MockReferenceDataConnector with MockEmcsTfeConnector {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val service: HelloWorldService = new HelloWorldService(
      mockReferenceDataHelloConnector,
      mockEmcsHelloConnector
    )
  }

  "getMessage" should {
    "return Right" when {
      "connectors return a successful response" in new Test {
        val referenceDataConnectorResponse: ReferenceDataResponse = ReferenceDataResponse("test message")
        val emcsTfeConnectorResponse: EmcsTfeResponse = EmcsTfeResponse("test message")

        MockReferenceDataConnector.hello().returns(Future.successful(Right(referenceDataConnectorResponse)))
        MockEmcsTfeConnector.hello().returns(Future.successful(Right(emcsTfeConnectorResponse)))

        await(service.getMessage().value) shouldBe Right((referenceDataConnectorResponse, emcsTfeConnectorResponse))
      }
    }
    "return Left" when {
      "reference data connector returns a Left" in new Test {
        val referenceDataConnectorResponse: Either[ErrorResponse, ReferenceDataResponse] = Left(UnexpectedDownstreamResponseError)

        MockReferenceDataConnector.hello().returns(Future.successful(referenceDataConnectorResponse))

        await(service.getMessage().value) shouldBe referenceDataConnectorResponse
      }
      "emcs tfe connector returns a Left" in new Test {
        val referenceDataConnectorResponse: Either[ErrorResponse, ReferenceDataResponse] = Right(ReferenceDataResponse("test message"))
        val emcsTfeConnectorResponse: Either[ErrorResponse, EmcsTfeResponse] = Left(UnexpectedDownstreamResponseError)

        MockReferenceDataConnector.hello().returns(Future.successful(referenceDataConnectorResponse))
        MockEmcsTfeConnector.hello().returns(Future.successful(emcsTfeConnectorResponse))

        await(service.getMessage().value) shouldBe emcsTfeConnectorResponse
      }
    }
  }
}