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

package uk.gov.hmrc.emcstfefrontend.services

import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.emcstfefrontend.fixtures.BaseFixtures
import uk.gov.hmrc.emcstfefrontend.mocks.config.MockAppConfig
import uk.gov.hmrc.emcstfefrontend.mocks.connectors.MockGetTraderKnownFactsConnector
import uk.gov.hmrc.emcstfefrontend.models.response.{TraderKnownFactsException, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetTraderKnownFactsServiceSpec extends UnitSpec with BaseFixtures with ScalaFutures with MockAppConfig with MockGetTraderKnownFactsConnector {

  implicit val hc = HeaderCarrier()
  implicit val ec = ExecutionContext.global

  lazy val testService = new GetTraderKnownFactsService(mockGetTraderKnownFactsConnector)

  ".getTraderKnownFacts(ern)" should {
    "return Some(TraderKnownFacts)" when {
      "when Connector returns success from downstream" in {

        MockGetTraderKnownFactsConnector.getTraderKnownFacts(testErn).returns(Future.successful(Right(Some(testMinTraderKnownFacts))))
        testService.getTraderKnownFacts(testErn).futureValue shouldBe Some(testMinTraderKnownFacts)
      }
    }

    "return None" when {
      "when Connector returns success from downstream with no data" in {

        MockGetTraderKnownFactsConnector.getTraderKnownFacts(testErn).returns(Future.successful(Right(None)))
        testService.getTraderKnownFacts(testErn).futureValue shouldBe None
      }
    }

    "throw TraderKnownFactsException" when {
      "when Connector returns failure from downstream" in {

        MockGetTraderKnownFactsConnector.getTraderKnownFacts(testErn).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))
        intercept[TraderKnownFactsException](await(testService.getTraderKnownFacts(testErn))).getMessage shouldBe
          s"No known facts found for trader $testErn"
      }
    }
  }

}
