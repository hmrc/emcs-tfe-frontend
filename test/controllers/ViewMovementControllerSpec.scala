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
import fixtures.GetMovementResponseFixtures
import mocks.services.MockGetMovementService
import models.response.MovementException
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.status
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.helpers.{TimelineHelper, ViewMovementHelper}
import views.html.viewMovement.ViewMovementPage

import scala.concurrent.Future

class ViewMovementControllerSpec extends SpecBase with FakeAuthAction with GetMovementResponseFixtures with MockGetMovementService {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  lazy val controller: ViewMovementController = new ViewMovementController(
    app.injector.instanceOf[MessagesControllerComponents],
    FakeSuccessAuthAction,
    new FakeDataRetrievalAction(testMinTraderKnownFacts, testMessageStatistics),
    mockGetMovementService,
    app.injector.instanceOf[ViewMovementPage],
    app.injector.instanceOf[ErrorHandler],
    app.injector.instanceOf[ViewMovementHelper],
    app.injector.instanceOf[TimelineHelper]
  )

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
        "connector call is successful" in {

          MockGetMovementService
            .getMovement(testErn, testArc)
            .returns(Future.successful(getMovementResponseModel))

          val result: Future[Result] = method()

          status(result) shouldBe Status.OK
        }
      }
      "return 500" when {
        "connector call is unsuccessful" in {

          MockGetMovementService
            .getMovement(testErn, testArc)
            .returns(Future.failed(MovementException("bang")))

          val result: Future[Result] = method()

          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }
    }
  }


}
