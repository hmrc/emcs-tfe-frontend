/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.controllers

import cats.data.EitherT
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.emcstfefrontend.mocks.services.MockHelloWorldService
import uk.gov.hmrc.emcstfefrontend.models.response.{EmcsTfeResponse, ReferenceDataResponse}
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.emcstfefrontend.views.html.{ErrorTemplate, HelloWorldPage}
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
      app.injector.instanceOf[ErrorTemplate],
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

        MockService.getMessage().returns(EitherT.fromEither[Future](Left("error message")))

        val result = controller.helloWorld()(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsString(result) should include("Something went wrong!")
        contentAsString(result) should include("Oh no!")
        contentAsString(result) should include("error message")
      }
    }
  }
}
