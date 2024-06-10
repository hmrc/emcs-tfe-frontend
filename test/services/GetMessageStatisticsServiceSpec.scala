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
import models.auth.UserRequest
import models.messages.MessagesSearchOptions
import models.response.{JsonValidationError, MessageStatisticsException, UnexpectedDownstreamResponseError}
import org.scalatest.concurrent.ScalaFutures
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetMessageStatisticsServiceSpec extends SpecBase with BaseFixtures with ScalaFutures with MockAppConfig with MockGetMessageStatisticsConnector {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val request: UserRequest[_] = userRequest(FakeRequest(), testErn)

  lazy val testService = new GetMessageStatisticsService(mockGetMessageStatisticsConnector, mockAppConfig)

  ".getMessageStatistics(ern)" must {

    "return Some(MessageStatistics)" when {

      "the MessagesStatisticsNotification feature switch is enabled" in {
        MockedAppConfig.messageStatisticsNotificationEnabled.returns(true)
        MockGetMessageStatisticsConnector.getMessageStatistics(testErn).returns(Future.successful(Right(testMessageStatistics)))
        testService.getMessageStatistics(testErn).futureValue mustBe Some(testMessageStatistics)
      }

      "the MessagesStatisticsNotification feature switch is disabled BUT the request is from the Messages page" in {

        implicit val request: UserRequest[_] = userRequest(FakeRequest(
          method = "GET",
          path = controllers.messages.routes.ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions()).url
        ), testErn)

        MockedAppConfig.messageStatisticsNotificationEnabled.returns(false)
        MockGetMessageStatisticsConnector.getMessageStatistics(testErn).returns(Future.successful(Right(testMessageStatistics)))
        testService.getMessageStatistics(testErn).futureValue mustBe Some(testMessageStatistics)
      }
    }

    "return None" when {

      "the MessagesStatisticsNotification feature switch is disabled AND not on the Messages page" in {
        MockedAppConfig.messageStatisticsNotificationEnabled.returns(false)
        testService.getMessageStatistics(testErn).futureValue mustBe None
      }
    }

    "throw MessageStatisticsException" when {

      "when Connector returns json validation failure from downstream with no data" in {
        MockedAppConfig.messageStatisticsNotificationEnabled.returns(true)
        MockGetMessageStatisticsConnector.getMessageStatistics(testErn).returns(Future.successful(Left(JsonValidationError)))
        intercept[MessageStatisticsException](await(testService.getMessageStatistics(testErn))).getMessage mustBe
          s"No message statistics found for trader $testErn"
      }

      "when Connector returns any other failure from downstream" in {
        MockedAppConfig.messageStatisticsNotificationEnabled.returns(true)
        MockGetMessageStatisticsConnector.getMessageStatistics(testErn).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))
        intercept[MessageStatisticsException](await(testService.getMessageStatistics(testErn))).getMessage mustBe
          s"No message statistics found for trader $testErn"
      }
    }
  }

}

