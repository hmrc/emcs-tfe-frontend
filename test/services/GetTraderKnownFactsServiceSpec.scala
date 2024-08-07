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

import base.SpecBase
import fixtures.BaseFixtures
import mocks.config.MockAppConfig
import mocks.connectors.MockGetTraderKnownFactsConnector
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetTraderKnownFactsServiceSpec extends SpecBase with BaseFixtures with ScalaFutures with MockAppConfig with MockGetTraderKnownFactsConnector {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new GetTraderKnownFactsService(mockGetTraderKnownFactsConnector)

  ".getTraderKnownFacts(ern)" must {
    "return Some(TraderKnownFacts)" when {
      "when Connector returns success from downstream" in {

        MockGetTraderKnownFactsConnector.getTraderKnownFacts(testErn).returns(Future.successful(Right(Some(testMinTraderKnownFacts))))
        testService.getTraderKnownFacts(testErn).futureValue mustBe Some(testMinTraderKnownFacts)
      }
    }

    "return None" when {

      "when Connector returns success from downstream with no data" in {

        MockGetTraderKnownFactsConnector.getTraderKnownFacts(testErn).returns(Future.successful(Right(None)))
        testService.getTraderKnownFacts(testErn).futureValue mustBe None
      }

      "when Connector returns failure from downstream" in {

        MockGetTraderKnownFactsConnector.getTraderKnownFacts(testErn).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))
        testService.getTraderKnownFacts(testErn).futureValue mustBe None
      }
    }
  }
}
