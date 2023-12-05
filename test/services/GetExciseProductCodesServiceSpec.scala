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

import fixtures.ExciseProductCodeFixtures
import play.api.test.Helpers._
import base.SpecBase
import mocks.connectors.MockGetExciseProductCodesConnector
import models.response.{ExciseProductCodesException, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetExciseProductCodesServiceSpec extends SpecBase with MockGetExciseProductCodesConnector with ExciseProductCodeFixtures {

  implicit val hc = HeaderCarrier()
  implicit val ec = ExecutionContext.global

  lazy val testService = new GetExciseProductCodesService(mockGetExciseProductCodesConnector)

  ".getExciseProductCodes" should {

    "should return Seq[ExciseProductCode]" when {

      "when Connector returns success from downstream" in {

        val expectedResult = Seq(
          beerExciseProductCode,
          wineExciseProductCode
        )

        MockGetExciseProductCodesConnector.getExciseProductCodes().returns(Future(Right(Seq(beerExciseProductCode, wineExciseProductCode))))

        val actualResults = testService.getExciseProductCodes().futureValue

        actualResults mustBe expectedResult
      }
    }

    "should throw ExciseProductCodesException" when {

      "when Connector returns failure from downstream" in {

        val expectedResult = "No excise product codes retrieved"

        MockGetExciseProductCodesConnector.getExciseProductCodes().returns(Future(Left(UnexpectedDownstreamResponseError)))

        val actualResult = intercept[ExciseProductCodesException](await(testService.getExciseProductCodes())).getMessage

        actualResult mustBe expectedResult
      }
    }
  }
}
