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

package uk.gov.hmrc.emcstfefrontend.controllers

import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.emcstfefrontend.config.ErrorHandler
import uk.gov.hmrc.emcstfefrontend.mocks.connectors.MockEmcsTfeConnector
import uk.gov.hmrc.emcstfefrontend.models.response.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfefrontend.models.response.emcsTfe.GetMovementResponse
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.emcstfefrontend.views.html.ViewMovementPage
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.concurrent.{ExecutionContext, Future}

class ViewMovementControllerSpec extends UnitSpec {

  trait Test extends MockEmcsTfeConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

    val controller: ViewMovementController = new ViewMovementController(
      app.injector.instanceOf[MessagesControllerComponents],
      mockGetMovementConnector,
      app.injector.instanceOf[ViewMovementPage],
      app.injector.instanceOf[ErrorHandler],
      ec
    )
  }

  "GET /consignment/:exciseRegistrationNumber/:arc" should {
    "return 200" when {
      "connector call is successful" in new Test {
        val ern = "ERN"
        val arc = "ARC"
        val model: GetMovementResponse = GetMovementResponse("", "", "", LocalDate.parse("2008-11-20"), "", 0)

        MockEmcsTfeConnector
          .getMovement()
          .returns(Future.successful(Right(model)))

        val result: Future[Result] = controller.viewMovement(ern, arc)(fakeRequest)

        status(result) shouldBe Status.OK
      }
    }
    "return 500" when {
      "connector call is unsuccessful" in new Test {
        val ern = "ERN"
        val arc = "ARC"

        MockEmcsTfeConnector
          .getMovement()
          .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result: Future[Result] = controller.viewMovement(ern, arc)(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}
