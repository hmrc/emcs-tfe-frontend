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
import config.ErrorHandler
import controllers.predicates.{FakeAuthAction, FakeDataRetrievalAction}
import fixtures.MovementListFixtures
import fixtures.messages.EN
import mocks.connectors.MockEmcsTfeConnector
import models.auth.UserRequest
import models.requests.DataRequest
import models.response.UnexpectedDownstreamResponseError
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier
import views.html.ViewMovementListPage

import scala.concurrent.{ExecutionContext, Future}

class ViewMovementListControllerSpec extends SpecBase with MovementListFixtures with FakeAuthAction {

  trait Test extends MockEmcsTfeConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")
    implicit val messages = app.injector.instanceOf[MessagesApi].preferred(Seq(EN.lang))

    val view = app.injector.instanceOf[ViewMovementListPage]
    val errorHandler = app.injector.instanceOf[ErrorHandler]

    val controller: ViewMovementListController = new ViewMovementListController(
      app.injector.instanceOf[MessagesControllerComponents],
      FakeSuccessAuthAction,
      new FakeDataRetrievalAction(testMinTraderKnownFacts),
      mockGetMovementListConnector,
      view,
      errorHandler
    )
  }

  "GET /" when {

    "connector call is successful" must {

      "return 200" in new Test {

        MockEmcsTfeConnector
          .getMovementList(testErn)
          .returns(Future.successful(Right(getMovementListResponse)))

        val result: Future[Result] = controller.viewMovementList(testErn)(fakeRequest)

        val dataRequest = DataRequest(
          UserRequest(fakeRequest, testErn, testInternalId, testCredId, hasMultipleErns = false),
          testMinTraderKnownFacts
        )

        status(result) mustBe Status.OK
        Html(contentAsString(result)) mustBe view(testErn, getMovementListResponse)(dataRequest, messages)
      }
    }

    "connector call is unsuccessful" must {

      "return 500" in new Test {

        MockEmcsTfeConnector
          .getMovementList(testErn)
          .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result: Future[Result] = controller.viewMovementList(testErn)(fakeRequest)

        status(result) mustBe Status.INTERNAL_SERVER_ERROR
        Html(contentAsString(result)) mustBe errorHandler.internalServerErrorTemplate
      }
    }
  }
}
