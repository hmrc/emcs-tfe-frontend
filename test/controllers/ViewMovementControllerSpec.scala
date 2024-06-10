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
import controllers.predicates.{FakeAuthAction, FakeBetaAllowListAction, FakeDataRetrievalAction}
import fixtures.GetMovementResponseFixtures
import mocks.services.MockGetMovementService
import mocks.viewmodels.MockViewMovementHelper
import models.response.{DocumentTypesException, MovementException}
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.status
import play.twirl.api.Html
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.helpers.TimelineHelper
import views.html.viewMovement.ViewMovementView

import scala.concurrent.Future

class ViewMovementControllerSpec extends SpecBase with FakeAuthAction with GetMovementResponseFixtures with MockGetMovementService with MockViewMovementHelper {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  lazy val controller: ViewMovementController = new ViewMovementController(
    app.injector.instanceOf[MessagesControllerComponents],
    FakeSuccessAuthAction,
    new FakeDataRetrievalAction(testMinTraderKnownFacts, Some(testMessageStatistics)),
    new FakeBetaAllowListAction,
    mockGetMovementService,
    app.injector.instanceOf[ViewMovementView],
    app.injector.instanceOf[ErrorHandler],
    mockViewMovementHelper,
    app.injector.instanceOf[TimelineHelper]
  )(ec, appConfig)

  Seq(
    ".viewMovementOverview" -> (() => controller.viewMovementOverview(testErn, testArc)(fakeRequest)),
    ".viewMovementMovement" -> (() => controller.viewMovementMovement(testErn, testArc)(fakeRequest)),
    ".viewMovementDelivery" -> (() => controller.viewMovementDelivery(testErn, testArc)(fakeRequest)),
    ".viewMovementGuarantor" -> (() => controller.viewMovementGuarantor(testErn, testArc)(fakeRequest)),
    ".viewMovementTransport" -> (() => controller.viewMovementTransport(testErn, testArc)(fakeRequest)),
    ".viewMovementItems" -> (() => controller.viewMovementItems(testErn, testArc)(fakeRequest)),
    ".viewMovementDocuments" -> (() => controller.viewMovementDocuments(testErn, testArc)(fakeRequest))
  ).foreach { case (testName, method) =>
    testName should {
      "return 200" when {
        "messagesConnector call is successful" in {
          MockViewMovementHelper.movementCard().returns(Future(Html("")))
          MockGetMovementService
            .getLatestMovementForLoggedInUser(testErn, testArc)
            .returns(Future.successful(getMovementResponseModel))

          val result: Future[Result] = method()

          status(result) shouldBe Status.OK
        }
      }
      "return 500" when {
        "movement messagesConnector call is unsuccessful" in {
          MockGetMovementService
            .getLatestMovementForLoggedInUser(testErn, testArc)
            .returns(Future.failed(MovementException("bang")))

          val result: Future[Result] = method()

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }

        "document messagesConnector call is unsuccessful" in {
          MockViewMovementHelper.movementCard().returns(Future.failed(DocumentTypesException("No document types retrieved")))
          MockGetMovementService
            .getLatestMovementForLoggedInUser(testErn, testArc)
            .returns(Future.successful(getMovementResponseModel))

          val result: Future[Result] = method()

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }
    }
  }


}
