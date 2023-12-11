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
import mocks.connectors.MockEmcsTfeConnector
import models.common.{AddressModel, TraderModel}
import models.response.UnexpectedDownstreamResponseError
import models.response.emcsTfe.GetMovementResponse
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.ViewMovementPage

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class ViewMovementControllerSpec extends SpecBase with FakeAuthAction {

  trait Test extends MockEmcsTfeConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

    val controller: ViewMovementController = new ViewMovementController(
      app.injector.instanceOf[MessagesControllerComponents],
      FakeSuccessAuthAction,
      new FakeDataRetrievalAction(testMinTraderKnownFacts),
      mockGetMovementConnector,
      app.injector.instanceOf[ViewMovementPage],
      app.injector.instanceOf[ErrorHandler]
    )
  }

  "GET /consignment/:exciseRegistrationNumber/:arc" must {
    "return 200" when {
      "connector call is successful" in new Test {

        val model: GetMovementResponse = GetMovementResponse(
          "",
          "",
          consignorTrader = TraderModel(
            traderExciseNumber = "GB12345GTR144",
            traderName = "MyConsignor",
            address = AddressModel(
              streetNumber = None,
              street = Some("Main101"),
              postcode = Some("ZZ78"),
              city = Some("Zeebrugge")
            )
          ),
          LocalDate.parse("2008-11-20"),
          "",
          0
        )

        MockEmcsTfeConnector
          .getMovement()
          .returns(Future.successful(Right(model)))

        val result: Future[Result] = controller.viewMovement(testErn, testArc)(fakeRequest)

        status(result) mustBe Status.OK
      }
    }
    "return 500" when {
      "connector call is unsuccessful" in new Test {

        MockEmcsTfeConnector
          .getMovement()
          .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result: Future[Result] = controller.viewMovement(testErn, testArc)(fakeRequest)

        status(result) mustBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}
