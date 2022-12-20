/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.emcstfefrontend.controllers

import play.api.http.Status
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.emcstfefrontend.views.html.ViewMovementPage
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class ViewMovementControllerSpec extends UnitSpec {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

    val controller: ViewMovementController = new ViewMovementController(
      app.injector.instanceOf[MessagesControllerComponents],
      app.injector.instanceOf[ViewMovementPage],
      ec
    )
  }

  "GET /" should {
    "return 200" in new Test {

      val result: Future[Result] = controller.viewMovement()(fakeRequest)

      status(result) shouldBe Status.OK
    }
  }
}
