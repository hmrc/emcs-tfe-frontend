/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.controllers

import cats.data.EitherT
import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents}
import uk.gov.hmrc.emcstfefrontend.mocks.services.MockModeOfTransportService
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.emcstfefrontend.models.response.ReferenceDataResponse
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.emcstfefrontend.views.html.{ErrorTemplate, ModeOfTransportPage}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ModeOfTransportControllerSpec extends UnitSpec with MockModeOfTransportService {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")
    implicit val postRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("POST", "/")

    val controller: ModeOfTransportController = new ModeOfTransportController(
      app.injector.instanceOf[MessagesControllerComponents],
      mockService,
      app.injector.instanceOf[ModeOfTransportPage],
      app.injector.instanceOf[ErrorTemplate],
      ec
    )
  }

  "GET /" should {
    "return 200" when {
      "service returns a Right" in new Test {

        MockService.getMessage().returns(EitherT.fromEither[Future](Right((ReferenceDataResponse("test message 1")))))

        val result = controller.modeOfTransport()(fakeRequest)

        status(result) shouldBe Status.OK
      }
    }
    "return 500" when {
      "service returns a Left" in new Test {

        MockService.getMessage().returns(EitherT.fromEither[Future](Left("error message")))

        val result = controller.modeOfTransport()(fakeRequest)

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR

      }
    }
    "POST /" should {
      "return 303" when {
        "service returns a Right" in new Test {

          val result = controller.onSubmit()(postRequest)

          status(result) shouldBe Status.SEE_OTHER
        }
      }
    }
  }
}
