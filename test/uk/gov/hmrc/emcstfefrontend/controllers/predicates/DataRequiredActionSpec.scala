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

package uk.gov.hmrc.emcstfefrontend.controllers.predicates

import org.scalatest.EitherValues
import play.api.i18n.MessagesApi
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.emcstfefrontend.models.requests.{DataRequest, OptionalDataRequest}
import uk.gov.hmrc.emcstfefrontend.models.auth.UserRequest
import uk.gov.hmrc.emcstfefrontend.fixtures.BaseFixtures
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRequiredActionSpec
  extends UnitSpec
    with BaseFixtures
    with EitherValues {

  implicit lazy val messagesApi: MessagesApi = app.injector.instanceOf[MessagesApi]

  val fakeRequest: FakeRequest[_] = FakeRequest("GET", s"/movements-in/$testErn")
  val fakeUserRequest: UserRequest[_] = UserRequest(fakeRequest, testErn, testInternalId, testCredId, hasMultipleErns = true)(messagesApi)

  object Harness extends DataRequiredActionImpl {
    def callRefine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = refine(request)
  }

  "Data Required Action" when {

    "there are no TraderKnownFacts" must {

      "return Left and redirect to session expired" in {

        val harness = Harness.callRefine(OptionalDataRequest(fakeUserRequest, None))

        val result = harness.map(_.left.value)

        status(result) shouldBe 303
        redirectLocation(result) shouldBe Some("") // TODO: Where should this redirect to?
      }
    }

    "there are TraderKnownFacts" must {

      "return Right with DataRequest" in {

        val result = await {
          Harness.callRefine(OptionalDataRequest(fakeUserRequest, Some(testMinTraderKnownFacts)))
        }

        result.map(aa => aa.traderKnownFacts) shouldBe Right(testMinTraderKnownFacts)
      }
    }
  }
}
