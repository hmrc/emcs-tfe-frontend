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

package controllers.predicates

import base.SpecBase
import fixtures.BaseFixtures
import mocks.services.{MockGetMessageStatisticsService, MockGetTraderKnownFactsService}
import models.auth.UserRequest
import models.requests.DataRequest
import models.response.{MessageStatisticsException, TraderKnownFactsException}
import org.scalatest.concurrent.ScalaFutures
import play.api.mvc.ActionTransformer
import play.api.test.FakeRequest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRetrievalActionSpec
  extends SpecBase
    with BaseFixtures
    with ScalaFutures
    with MockGetTraderKnownFactsService
    with MockGetMessageStatisticsService {

  lazy val dataRetrievalAction: ActionTransformer[UserRequest, DataRequest] =
    new DataRetrievalActionImpl(mockGetTraderKnownFactsService, mockGetMessageStatisticsService).apply()

  "Data Retrieval Action" must {

    "return a DataRequest" when {
      "downstream calls are successful" in {
        MockGetTraderKnownFactsService.getTraderKnownFacts(testErn).returns(Future.successful(testMinTraderKnownFacts))
        MockGetMessageStatisticsService.getMessageStatistics(testErn).returns(Future.successful(testMessageStatistics))

        val result = dataRetrievalAction.refine(UserRequest(FakeRequest(), testErn, testInternalId, testCredId, false)).futureValue.toOption.get

        result mustBe a[DataRequest[_]]
        result.traderKnownFacts mustBe testMinTraderKnownFacts
        result.messageStatistics mustBe testMessageStatistics
      }
    }

      "must return a TraderKnownFactsException, given the call to getTraderKnownFacts fails" in {
        MockGetTraderKnownFactsService.getTraderKnownFacts(testErn).returns(Future.failed(TraderKnownFactsException("kaboom")))

        intercept[TraderKnownFactsException] {
          await(dataRetrievalAction.refine(UserRequest(FakeRequest(), testErn, testInternalId, testCredId, false)))
        }
      }

      "must return a MessageStatisticsException, given the call to getMessageStatistics fails" in {
        MockGetTraderKnownFactsService.getTraderKnownFacts(testErn).returns(Future.successful(testMinTraderKnownFacts))
        MockGetMessageStatisticsService.getMessageStatistics(testErn).returns(Future.failed(MessageStatisticsException("kablam")))

        intercept[MessageStatisticsException] {
          await(dataRetrievalAction.refine(UserRequest(FakeRequest(), testErn, testInternalId, testCredId, false)))
        }
      }
    }
}
