/*
 * Copyright 2024 HM Revenue & Customs
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
import fixtures.{ExciseProductCodeFixtures, PrevalidateTraderFixtures}
import mocks.connectors.MockHttpClient
import models.requests.PrevalidateTraderRequest
import models.response.JsonValidationError
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}

class PrevalidateTraderConnectorSpec extends SpecBase
  with Status
  with MimeTypes
  with HeaderNames
  with MockHttpClient
  with PrevalidateTraderFixtures
  with ExciseProductCodeFixtures {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val connector = new PrevalidateTraderConnector(mockHttpClient, appConfig)

  val requestModel: PrevalidateTraderRequest = PrevalidateTraderRequest(testErn, Some(testEntityGroup), Some(Seq(testEpcWine, testEpcBeer)))

  "prevalidateTrader" should {

    "return a successful response" when {

      "downstream call is successful" in {

        MockHttpClient.post(url"${appConfig.emcsTfeBaseUrl}/pre-validate-trader/$testErn", Json.toJson(requestModel)).returns(Future.successful(Right(preValidateApiResponseModel)))

        connector.prevalidateTrader(testErn, requestModel).futureValue mustBe Right(preValidateApiResponseModel)
      }
    }

    "return an error response" when {

      "when downstream call fails" in {

        MockHttpClient.post(url"${appConfig.emcsTfeBaseUrl}/pre-validate-trader/$testErn", Json.toJson(requestModel)).returns(Future.successful(Left(JsonValidationError)))

        connector.prevalidateTrader(testErn,requestModel).futureValue mustBe Left(JsonValidationError)
      }
    }
  }
}
