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

package controllers

import base.SpecBase
import controllers.predicates.{FakeAuthAction, FakeBetaAllowListAction, FakeDataRetrievalAction}
import fixtures.GetMovementResponseFixtures
import mocks.services.MockGetMovementService
import models.response.MovementException
import play.api.mvc.MessagesControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ItemDetailsView

import scala.concurrent.Future

class ItemDetailsControllerSpec extends SpecBase with FakeAuthAction with MockGetMovementService with GetMovementResponseFixtures {

  lazy val view = app.injector.instanceOf[ItemDetailsView]

  lazy val controller: ItemDetailsController = new ItemDetailsController(
    mcc = app.injector.instanceOf[MessagesControllerComponents],
    auth = FakeSuccessAuthAction,
    getData = new FakeDataRetrievalAction(testMinTraderKnownFacts, testMessageStatistics),
    betaAllowList = new FakeBetaAllowListAction,
    view = view,
    movementService = mockGetMovementService
  )

  def onPageLoadUrl(idx: Int): String = routes.ItemDetailsController.onPageLoad(testErn, testArc, idx).url

  "ItemDetails Controller" when {

    "calling .onPageLoad()" must {

      "return OK and the correct view for a GET" in {

        MockGetMovementService.getMovement(testErn, testArc)
          .returns(Future.successful(getMovementResponseModel.copy(items = Seq(item1WithWineAndPackagingAndCnCodeInfo))))

        val request = FakeRequest(GET, onPageLoadUrl(idx = 1))
        val result = controller.onPageLoad(testErn, testArc, 1)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual
          view(item1WithWineAndPackagingAndCnCodeInfo)(dataRequest(request), messages(request)).toString
      }

      s"redirect to ${routes.ViewMovementController.viewMovementItems(testErn, testArc).url}" when {

        "there is an issue retrieving the movement" in {

          MockGetMovementService.getMovement(testErn, testArc)
            .returns(Future.failed(MovementException("bang")))

          val request = FakeRequest(GET, onPageLoadUrl(idx = 1))
          val result = controller.onPageLoad(testErn, testArc, 1)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.ViewMovementController.viewMovementItems(testErn, testArc).url
        }
      }
    }
  }
}
