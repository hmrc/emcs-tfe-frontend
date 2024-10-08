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

package connectors.emcsTfe

import base.SpecBase
import fixtures.GetMovementResponseFixtures
import mocks.connectors.MockHttpClient
import models.response.JsonValidationError
import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}

class GetMovementConnectorSpec extends SpecBase
  with Status with MimeTypes with HeaderNames with MockHttpClient with GetMovementResponseFixtures {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val connector = new GetMovementConnector(mockHttpClient, appConfig)

  "getMovement" should {

    "return a successful response" when {

      "when downstream call is successful for the latest movement" in {

        MockHttpClient.get(url"${appConfig.emcsTfeBaseUrl}/movement/ern/arc?forceFetchNew=true").returns(Future.successful(Right(getMovementResponseModel)))

        connector.getMovement(exciseRegistrationNumber = "ern", arc = "arc").futureValue mustBe Right(getMovementResponseModel)
      }

      "when downstream call is successful for a particular movement sequence" in {

        MockHttpClient.get(url"${appConfig.emcsTfeBaseUrl}/movement/ern/arc?forceFetchNew=true&sequenceNumber=2")
          .returns(Future.successful(Right(getMovementResponseModel)))

        connector.getMovement(exciseRegistrationNumber = "ern", arc = "arc", Some(2)).futureValue mustBe Right(getMovementResponseModel)
      }

    }

    "return an error response" when {

      "when downstream call fails for the latest movement" in {

        MockHttpClient.get(url"${appConfig.emcsTfeBaseUrl}/movement/ern/arc?forceFetchNew=true").returns(Future.successful(Left(JsonValidationError)))

        connector.getMovement(exciseRegistrationNumber = "ern", arc = "arc").futureValue mustBe Left(JsonValidationError)
      }

      "when downstream call fails for a particular movement sequence" in {

        MockHttpClient.get(url"${appConfig.emcsTfeBaseUrl}/movement/ern/arc?forceFetchNew=true&sequenceNumber=2").returns(Future.successful(Left(JsonValidationError)))

        connector.getMovement(exciseRegistrationNumber = "ern", arc = "arc", Some(2)).futureValue mustBe Left(JsonValidationError)
      }
    }
  }
}
