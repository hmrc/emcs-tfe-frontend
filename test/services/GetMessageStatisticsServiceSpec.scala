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
import mocks.repositories.MockMessageStatisticsRepository
import models.auth.UserRequest
import models.messages.{MessageStatisticsCache, MessagesSearchOptions}
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.ScalaFutures
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing

import scala.concurrent.{ExecutionContext, Future}

class GetMessageStatisticsServiceSpec extends SpecBase with BaseFixtures with ScalaFutures
  with MockAppConfig
  with MockGetMessageStatisticsConnector
  with MockMessageStatisticsRepository
  with LogCapturing {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val request: UserRequest[_] = userRequest(FakeRequest(), testErn)

  lazy val testService = new GetMessageStatisticsService(mockGetMessageStatisticsConnector, mockMessageStatisticsRepository, mockAppConfig)

  ".getMessageStatistics(ern)" must {

    "return Some(MessageStatistics)" when {

      "the request is NOT from the messages page" when {

        "the cache returns a value" must {

          "return the value from the cache and not call the downstream API" in {

            MockMessageStatisticsRepository.get(testErn).returns(Future.successful(Some(MessageStatisticsCache(testErn, testMessageStatistics))))

            testService.getMessageStatistics(testErn).futureValue mustBe Some(testMessageStatistics)
          }
        }

        "the cache does NOT return a value" must {

          "return the value from Downstream and store the response in the cache" in {

            MockMessageStatisticsRepository.get(testErn).returns(Future.successful(None))
            MockGetMessageStatisticsConnector.getMessageStatistics(testErn).returns(Future.successful(Right(testMessageStatistics)))
            MockMessageStatisticsRepository.set(MessageStatisticsCache(testErn, testMessageStatistics)).returns(Future.successful(true))

            testService.getMessageStatistics(testErn).futureValue mustBe Some(testMessageStatistics)
          }
        }
      }

      "the request is from the Messages page" should {

        "always return the value from Core and not the Cache to ensure up to date" in {

          implicit val request: UserRequest[_] = userRequest(FakeRequest(
            method = "GET",
            path = controllers.messages.routes.ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions()).url
          ), testErn)

          MockMessageStatisticsRepository.get(testErn).never()
          MockGetMessageStatisticsConnector.getMessageStatistics(testErn).returns(Future.successful(Right(testMessageStatistics)))
          MockMessageStatisticsRepository.set(MessageStatisticsCache(testErn, testMessageStatistics)).returns(Future.successful(true))
          testService.getMessageStatistics(testErn).futureValue mustBe Some(testMessageStatistics)
        }
      }
    }

    "return None" when {

      "call to downstream fails, but log a warning message" in {
        MockMessageStatisticsRepository.get(testErn).returns(Future.successful(None))
        MockGetMessageStatisticsConnector.getMessageStatistics(testErn).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        withCaptureOfLoggingFrom(testService.logger) { logs =>
          testService.getMessageStatistics(testErn).futureValue mustBe None
          logs.head.getMessage mustBe s"[GetMessageStatisticsService][getMessageStatistics] No message statistics found for trader $testErn"
        }
      }
    }
  }
}

