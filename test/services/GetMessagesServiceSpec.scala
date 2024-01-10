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
import fixtures.{BaseFixtures, MessagesFixtures}
import mocks.config.MockAppConfig
import mocks.connectors.MockGetMessagesConnector
import models.messages.MessagesSearchOptions
import models.response.{JsonValidationError, MessagesException, UnexpectedDownstreamResponseError}
import org.scalatest.concurrent.ScalaFutures
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

class GetMessagesServiceSpec extends SpecBase with BaseFixtures with ScalaFutures with MockAppConfig with MockGetMessagesConnector with MessagesFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  lazy val testService = new GetMessagesService(mockGetMessagesConnector)

  val searchOptions = Some(MessagesSearchOptions())

  ".getMessages" must {

    "return GetMessagesResponse" when {

      "when Connector returns success from downstream" in {
        MockGetMessagesConnector.getMessages(testErn, searchOptions).returns(Future.successful(Right(getMessageResponse)))
        testService.getMessages(testErn, searchOptions).futureValue mustBe getMessageResponse
      }
    }

    "throw MessagesException" when {

      "when Connector returns json validation failure from downstream with no data" in {
        MockGetMessagesConnector.getMessages(testErn, searchOptions).returns(Future.successful(Left(JsonValidationError)))
        intercept[MessagesException](await(testService.getMessages(testErn, searchOptions))).getMessage mustBe
          s"Error occurred when fetching messages for trader $testErn"
      }

      "when Connector returns any other failure from downstream" in {
        MockGetMessagesConnector.getMessages(testErn, searchOptions).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))
        intercept[MessagesException](await(testService.getMessages(testErn, searchOptions))).getMessage mustBe
          s"Error occurred when fetching messages for trader $testErn"
      }
    }
  }

}

