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
import fixtures.GetMovementHistoryEventsResponseFixtures
import mocks.connectors.MockHttpClient
import models.response.JsonValidationError
import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetMovementHistoryEventsConnectorSpec extends SpecBase
  with Status with MimeTypes with HeaderNames with MockHttpClient with GetMovementHistoryEventsResponseFixtures {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val connector = new GetMovementHistoryEventsConnector(mockHttpClient, appConfig)

  "getMovementHistoryEvents" should {

    "return a successful response" when {

      "downstream call is successful" in {

        MockHttpClient.get(s"${appConfig.emcsTfeBaseUrl}/movement-history/ern/arc").returns(Future.successful(Right(getMovementHistoryEventsResponseModel)))

        connector.getMovementHistoryEvents(exciseRegistrationNumber = "ern", arc = "arc").futureValue mustBe Right(getMovementHistoryEventsResponseModel)
      }
    }

    "return an error response" when {

      "when downstream call fails" in {

        MockHttpClient.get(s"${appConfig.emcsTfeBaseUrl}/movement-history/ern/arc").returns(Future.successful(Left(JsonValidationError)))

        connector.getMovementHistoryEvents(exciseRegistrationNumber = "ern", arc = "arc").futureValue mustBe Left(JsonValidationError)
      }
    }
  }
}
