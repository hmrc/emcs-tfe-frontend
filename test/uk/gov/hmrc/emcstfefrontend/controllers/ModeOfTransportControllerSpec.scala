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
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.emcstfefrontend.config.ErrorHandler
import uk.gov.hmrc.emcstfefrontend.controllers.predicates.FakeAuthAction
import uk.gov.hmrc.emcstfefrontend.fixtures.ModeOfTransportListFixture
import uk.gov.hmrc.emcstfefrontend.mocks.services.MockModeOfTransportService
import uk.gov.hmrc.emcstfefrontend.models.response.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.emcstfefrontend.views.html.ModeOfTransportPage
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ModeOfTransportControllerSpec extends UnitSpec with ModeOfTransportListFixture with FakeAuthAction {

  trait Test extends MockModeOfTransportService {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")
    implicit val postRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("POST", "/")

    val controller: ModeOfTransportController = new ModeOfTransportController(
      mcc = app.injector.instanceOf[MessagesControllerComponents],
      service = mockService,
      modeOfTransportPage = app.injector.instanceOf[ModeOfTransportPage],
      errorHandler = app.injector.instanceOf[ErrorHandler]
    )
  }

  "GET /mode-of-transport" should {
    "return 200" when {
      "service returns a success full model" in new Test {

        MockService.getOtherDataReferenceList().returns(Future.successful(Right(validModeOfTransportResponseListModel)))

        val result = controller.modeOfTransport()(fakeRequest)

        status(result) shouldBe Status.OK
      }
    }
    "return 500" when {
      "service returns a un-succesful model" in new Test {

        MockService.getOtherDataReferenceList().returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = controller.modeOfTransport()(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      }
    }
  }
  "POST /mode-of-transport" should {
    "return 303" when {
      "service returns a Right" in new Test {

        val result = controller.onSubmit()(postRequest)

        status(result) shouldBe Status.SEE_OTHER
      }
    }
  }
}