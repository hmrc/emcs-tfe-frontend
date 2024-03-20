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

package controllers.predicates

import base.SpecBase
import mocks.services.MockPreValidateUserAnswersService
import models.UserAnswers
import models.requests.UserAnswersRequest
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers._

import java.time.Instant
import scala.concurrent.{ExecutionContext, Future}

class PrevalidateTraderUserAnswersActionSpec extends SpecBase with MockFactory with MockPreValidateUserAnswersService {

  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val action: PrevalidateTraderDataRetrievalAction = new PrevalidateTraderDataRetrievalAction(mockUserAnswersService)

  class Harness() {

    lazy val result: Future[Result] = action.invokeBlock(dataRequest(FakeRequest()), { _: UserAnswersRequest[_] =>
      Future.successful(Ok)
    })
  }

  "PrevalidateTraderDataRetrievalAction" should {

    "when data is returned from the UserAnswersService" must {

      "must execute the supplied block" in new Harness() {
        MockUserAnswersService.get(testErn).returns(Future.successful(Some(UserAnswers(testErn, Json.obj(), Instant.now()))))
        status(result) mustBe OK
      }
    }

    "when data is NOT returned from the UserAnswersService" must {

      "must Redirect to the Prevalidate Start controller" in new Harness() {
        MockUserAnswersService.get(testErn).returns(Future.successful(None))
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.prevalidateTrader.routes.PrevalidateTraderStartController.onPageLoad(testErn).url)
      }
    }
  }
}