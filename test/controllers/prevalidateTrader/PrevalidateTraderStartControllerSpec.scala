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
import controllers.predicates.{FakeAuthAction, FakeDataRetrievalAction}
import mocks.config.MockAppConfig
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
    with MockAppConfig {

  lazy val view: PrevalidateTraderStartView = app.injector.instanceOf[PrevalidateTraderStartView]

  trait Test {

    lazy val controller: PrevalidateTraderStartController = new PrevalidateTraderStartController(
      mcc = app.injector.instanceOf[MessagesControllerComponents],
      auth = FakeSuccessAuthAction,
      getData = new FakeDataRetrievalAction(Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
      userAnswersService = mockUserAnswersService,
      view = view
    )(ec)
  }

  "StartPrevalidateTrader Controller" when {

    "calling .onPageLoad()" when {

      "render the view" in new Test {

        val request = FakeRequest(GET, routes.PrevalidateTraderStartController.onPageLoad(testErn).url)
        val result = controller.onPageLoad(testErn)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(routes.PrevalidateTraderStartController.onSubmit(testErn))(dataRequest(request), messages(request)).toString
      }
    }

    "calling .onSubmit()" must {

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
  }
}
