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
import mocks.connectors.MockGetMessageStatisticsConnector
import models.response.{JsonValidationError, MessageStatisticsException, UnexpectedDownstreamResponseError}
import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetMessageStatisticsServiceSpec extends SpecBase with BaseFixtures with ScalaFutures with MockAppConfig with MockGetMessageStatisticsConnector {

  implicit val hc = HeaderCarrier()
  implicit val ec = ExecutionContext.global

  lazy val testService = new GetMessageStatisticsService(mockGetMessageStatisticsConnector)

  ".getMessageStatistics(ern)" must {
    "return MessageStatistics" when {
      "when Connector returns success from downstream" in {
        MockGetMessageStatisticsConnector.getMessageStatistics(testErn).returns(Future.successful(Right(testMessageStatistics)))
        testService.getMessageStatistics(testErn).futureValue mustBe testMessageStatistics
      }
    }

    "throw MessageStatisticsException" when {

      "when Connector returns json validation failure from downstream with no data" in {
        MockGetMessageStatisticsConnector.getMessageStatistics(testErn).returns(Future.successful(Left(JsonValidationError)))
        intercept[MessageStatisticsException](await(testService.getMessageStatistics(testErn))).getMessage mustBe
          s"No message statistics found for trader $testErn"
      }

      "when Connector returns any other failure from downstream" in {
        MockGetMessageStatisticsConnector.getMessageStatistics(testErn).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))
        intercept[MessageStatisticsException](await(testService.getMessageStatistics(testErn))).getMessage mustBe
          s"No message statistics found for trader $testErn"
      }
    }
  }

}

