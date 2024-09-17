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

package connectors.referenceData

import base.SpecBase
import fixtures.BaseFixtures
import mocks.config.MockAppConfig
import mocks.connectors.MockHttpClient
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}

class GetTraderKnownFactsConnectorSpec extends SpecBase with BaseFixtures with ScalaFutures with MockAppConfig with MockHttpClient {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  lazy val connector = new GetTraderKnownFactsConnector(mockHttpClient, mockAppConfig)
  val baseUrl = "http://test-BaseUrl"

  "check" must {
    "return a successful response" when {
      "downstream call is successful" in {
        MockedAppConfig.traderKnownFactsReferenceDataBaseUrl.returns(baseUrl)

        MockHttpClient.get(url"$baseUrl?exciseRegistrationId=$testErn")
          .returns(Future.successful(Right(Some(testMinTraderKnownFacts))))

        connector.getTraderKnownFacts(testErn).futureValue mustBe Right(Some(testMinTraderKnownFacts))
      }
    }

    "return an error response" when {
      "downstream call fails" in {
        MockedAppConfig.traderKnownFactsReferenceDataBaseUrl.returns(baseUrl)

        MockHttpClient.get(url"$baseUrl?exciseRegistrationId=$testErn")
          .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        connector.getTraderKnownFacts(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}
