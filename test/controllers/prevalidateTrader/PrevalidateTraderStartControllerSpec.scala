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

package controllers.prevalidateTrader

import base.SpecBase
import controllers.predicates.{BetaAllowListActionImpl, FakeAuthAction, FakeDataRetrievalAction}
import mocks.config.MockAppConfig
import mocks.connectors.MockBetaAllowListConnector
import mocks.services.MockPrevalidateUserAnswersService
import models.UserAnswers
import play.api.mvc.MessagesControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.prevalidateTrader.PrevalidateTraderStartView

import scala.concurrent.Future

class PrevalidateTraderStartControllerSpec
  extends SpecBase
    with FakeAuthAction
    with MockPrevalidateUserAnswersService
    with MockBetaAllowListConnector
    with MockAppConfig {

  lazy val view: PrevalidateTraderStartView = app.injector.instanceOf[PrevalidateTraderStartView]

  class Test(navHubEnabled: Boolean = true, preValidateEnabled: Boolean = true) {

    lazy val betaAllowListAction = new BetaAllowListActionImpl(
      betaAllowListConnector = mockBetaAllowListConnector,
      errorHandler = errorHandler,
      config = mockAppConfig
    )

    lazy val controller: PrevalidateTraderStartController = new PrevalidateTraderStartController(
      mcc = app.injector.instanceOf[MessagesControllerComponents],
      auth = FakeSuccessAuthAction,
      getData = new FakeDataRetrievalAction(testMinTraderKnownFacts, testMessageStatistics),
      betaAllowList = betaAllowListAction,
      userAnswersService = mockUserAnswersService,
      view = view
    )(ec, appConfig)

    MockedAppConfig.betaAllowListCheckingEnabled.repeat(2).returns(true)
    MockBetaAllowListConnector.check(testErn, "tfeNavHub").returns(Future.successful(Right(navHubEnabled)))
    MockBetaAllowListConnector.check(testErn, "tfePreValidate").returns(Future.successful(Right(preValidateEnabled)))
  }

  "StartPrevalidateTrader Controller" when {

    "calling .onPageLoad()" when {

      "user is on the private beta list" should {
        "render the view" in new Test {

          val request = FakeRequest(GET, routes.PrevalidateTraderStartController.onPageLoad(testErn).url)
          val result = controller.onPageLoad(testErn)(request)

          status(result) mustEqual OK
          contentAsString(result) mustEqual view(routes.PrevalidateTraderStartController.onSubmit(testErn))(dataRequest(request), messages(request)).toString
        }
      }

      "user is NOT on the private beta list" should {
        "redirect back to legacy" in new Test(preValidateEnabled = false) {

          val request = FakeRequest(GET, routes.PrevalidateTraderStartController.onPageLoad(testErn).url)
          val result = controller.onPageLoad(testErn)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual "http://localhost:8080/emcs/trader/GBWKTestErn/prevalidate"
        }
      }

    }

    "calling .onSubmit()" must {

      "user is on the private beta list" should {

        "redirect when data exists" in new Test {

          MockUserAnswersService.get(testErn).returns(Future.successful(Some(emptyUserAnswers)))

          val request = FakeRequest(POST, routes.PrevalidateTraderStartController.onSubmit(testErn).url)
          val result = controller.onSubmit(testErn)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(testErn).url
        }

        "redirect when no data exists" in new Test {

          MockUserAnswersService.get(testErn).returns(Future.successful(None))
          MockUserAnswersService.set(UserAnswers(testErn)).returns(Future.successful(emptyUserAnswers))

          val request = FakeRequest(POST, routes.PrevalidateTraderStartController.onSubmit(testErn).url)
          val result = controller.onSubmit(testErn)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(testErn).url
        }

      }

      "user is NOT on the private beta list" should {
        "redirect back to legacy" in new Test(preValidateEnabled = false) {

          val request = FakeRequest(POST, routes.PrevalidateTraderStartController.onSubmit(testErn).url)
          val result = controller.onSubmit(testErn)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some("http://localhost:8080/emcs/trader/GBWKTestErn/prevalidate")
        }
      }
    }
  }
}
