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

import cats.data.EitherT
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.emcstfefrontend.config.ErrorHandler
import uk.gov.hmrc.emcstfefrontend.mocks.services.MockHelloWorldService
import uk.gov.hmrc.emcstfefrontend.models.response.UnexpectedDownstreamResponseError
import uk.gov.hmrc.emcstfefrontend.models.response.emcsTfe.EmcsTfeResponse
import uk.gov.hmrc.emcstfefrontend.models.response.referenceData.ReferenceDataResponse
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.emcstfefrontend.views.html.HelloWorldPage
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class HelloWorldControllerSpec extends UnitSpec with MockHelloWorldService {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

    val controller: HelloWorldController = new HelloWorldController(
      app.injector.instanceOf[MessagesControllerComponents],
      mockService,
      app.injector.instanceOf[HelloWorldPage],
      app.injector.instanceOf[ErrorHandler],
      ec
    )
  }

  "GET /" should {
    "return 200" when {
      "service returns a Right" in new Test {

        MockService.getMessage().returns(EitherT.fromEither[Future](Right((ReferenceDataResponse("test message 1"), EmcsTfeResponse("test message 2")))))

        val result = controller.helloWorld()(fakeRequest)

        status(result) shouldBe Status.OK
        contentAsString(result) should include("emcs-tfe-frontend")
        contentAsString(result) should include("test message 1")
        contentAsString(result) should include("test message 2")
      }
    }
    "return 500" when {
      "service returns a Left" in new Test {

        MockService.getMessage().returns(EitherT.fromEither[Future](Left(UnexpectedDownstreamResponseError)))

        val result = controller.helloWorld()(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsString(result) should include("Sorry, we’re experiencing technical difficulties")
        contentAsString(result) should include("Please try again in a few minutes.")
      }
    }
  }
}
