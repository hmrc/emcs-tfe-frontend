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
import mocks.services.MockGetTraderKnownFactsService
import models.auth.UserRequest
import models.requests.DataRequest
import org.scalatest.concurrent.ScalaFutures
import play.api.mvc.ActionTransformer
import play.api.test.FakeRequest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRetrievalActionSpec
  extends SpecBase
    with BaseFixtures
    with ScalaFutures
    with MockGetTraderKnownFactsService {

  lazy val dataRetrievalAction: ActionTransformer[UserRequest, DataRequest] =
    new DataRetrievalActionImpl(mockGetTraderKnownFactsService).apply()

  "Data Retrieval Action" when {

    "there is data in the cache" must {
      "build a TraderKnownFacts object and add it to the request" in {
        MockGetTraderKnownFactsService.getTraderKnownFacts(testErn).returns(Future.successful(testMinTraderKnownFacts))

        val result = dataRetrievalAction.refine(UserRequest(FakeRequest(), testErn, testInternalId, testCredId, false)).futureValue.value

        result.traderKnownFacts mustBe testMinTraderKnownFacts
      }
    }
  }
}
