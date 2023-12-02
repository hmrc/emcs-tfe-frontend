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

package uk.gov.hmrc.emcstfefrontend.connectors.referenceData

import fixtures.ExciseProductCodeFixtures
import uk.gov.hmrc.emcstfefrontend.base.SpecBase
import uk.gov.hmrc.emcstfefrontend.mocks.connectors.MockHttpClient
import uk.gov.hmrc.emcstfefrontend.models.response.UnexpectedDownstreamResponseError
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetExciseProductCodesConnectorSpec extends SpecBase with MockHttpClient with ExciseProductCodeFixtures {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val connector = new GetExciseProductCodesConnectorImpl(mockHttpClient, appConfig)


  "getExciseProductCodes" - {

    "should return a successful response" - {

      "when downstream call is successful" in {

        val expectedResult = Right(Seq(beerExciseProductCodeJson, wineExciseProductCodeJson))

        MockHttpClient.get(
          url = s"${appConfig.referenceDataBaseUrl}/oracle/epc-codes"
        ).returns(Future.successful(expectedResult))

        val actualResult = connector.getExciseProductCodes().futureValue

        actualResult mustBe expectedResult
      }
    }

    "should return an error response" - {

      "when downstream call fails" in {

        val expectedResult = Left(UnexpectedDownstreamResponseError)

        MockHttpClient.get(
          url = s"${appConfig.referenceDataBaseUrl}/oracle/epc-codes"
        ).returns(Future.successful(expectedResult))

        val actualResult = connector.getExciseProductCodes().futureValue

        actualResult mustBe expectedResult
      }
    }
  }
}

