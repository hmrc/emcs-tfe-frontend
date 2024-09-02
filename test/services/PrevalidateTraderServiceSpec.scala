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

package services

import base.SpecBase
import fixtures.{ExciseProductCodeFixtures, PrevalidateTraderFixtures}
import mocks.connectors.MockPrevalidateTraderConnector
import models.prevalidate.EntityGroup
import models.requests.PrevalidateTraderRequest
import models.response.{PrevalidateTraderException, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class PrevalidateTraderServiceSpec extends SpecBase with MockPrevalidateTraderConnector with ExciseProductCodeFixtures with PrevalidateTraderFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new PrevalidateTraderService(mockPrevalidateTraderConnector)

  val ernToCheck = "GBWK002281023"
  val entityGroupToCheck = EntityGroup.UKTrader

  val requestModel: PrevalidateTraderRequest = PrevalidateTraderRequest(ernToCheck, Some(entityGroupToCheck), Some(Seq(testEpcWine, testEpcBeer)))

  ".prevalidateTrader" must {

    "must return PreValidateTraderApiResponse" when {

      "when Connector returns success from downstream" in {

        MockPrevalidateTraderConnector.prevalidateTrader(testErn, requestModel).returns(Future(Right(preValidateApiResponseModel)))

        val actualResults = testService.prevalidateTrader(testErn, ernToCheck, Some(entityGroupToCheck), Some(Seq(testEpcWine, testEpcBeer))).futureValue

        actualResults mustBe preValidateApiResponseModel
      }
    }

    "must throw PrevalidateTraderException" when {

      "when Connector returns failure from downstream" in {

        val expectedResult = "Prevalidate trader result error"

        MockPrevalidateTraderConnector.prevalidateTrader(testErn, requestModel).returns(Future(Left(UnexpectedDownstreamResponseError)))

        val actualResult = intercept[PrevalidateTraderException](
          await(testService.prevalidateTrader(testErn, ernToCheck, Some(entityGroupToCheck), Some(Seq(testEpcWine, testEpcBeer))))
        ).getMessage

        actualResult mustBe expectedResult
      }
    }
  }

}
