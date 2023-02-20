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
import play.api.i18n.MessagesApi
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier}
import uk.gov.hmrc.emcstfefrontend.config.EnrolmentKeys
import uk.gov.hmrc.emcstfefrontend.controllers.predicates.FakeSelectExciseNumbersAuthAction
import uk.gov.hmrc.emcstfefrontend.fixtures.messages.EN
import uk.gov.hmrc.emcstfefrontend.mocks.connectors.MockEmcsTfeConnector
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.emcstfefrontend.views.html.ExciseNumbersPage
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class IndexControllerSpec extends UnitSpec with FakeSelectExciseNumbersAuthAction {

  val oneEnrolment = Set(
    Enrolment(
      key = EnrolmentKeys.EMCS_ENROLMENT,
      identifiers = Seq(EnrolmentIdentifier(EnrolmentKeys.ERN, testErn)),
      state = EnrolmentKeys.ACTIVATED
    )
  )

  val twoEnrolments = oneEnrolment + Enrolment(
      key = EnrolmentKeys.EMCS_ENROLMENT,
      identifiers = Seq(EnrolmentIdentifier(EnrolmentKeys.ERN, testErn + "_2")),
      state = EnrolmentKeys.ACTIVATED
    )

  class Test(enrolments: Set[Enrolment]) extends MockEmcsTfeConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
    implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")
    implicit val messages = app.injector.instanceOf[MessagesApi].preferred(Seq(EN.lang))

    val view = app.injector.instanceOf[ExciseNumbersPage]
    val fakeAuthSuccess = new FakeSuccessSelectExciseNumbersAuthAction(enrolments)

    val controller: IndexController = new IndexController(
      app.injector.instanceOf[MessagesControllerComponents],
      view,
      fakeAuthSuccess
    )
  }

  ".exciseNumber()" when {

    "Auth returns one EMCS enrolment" should {

      "return 303 and redirect movements-in page" in new Test(oneEnrolment) {

        val result: Future[Result] = controller.exciseNumber()(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.ViewMovementListController.viewMovementList(testErn).url)
      }
    }

    "Auth returns no EMCS enrolments" should {

      "return 303 and redirect to the unauthorised page" in new Test(Set()) {

        val result: Future[Result] = controller.exciseNumber()(fakeRequest)

        status(result) shouldBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some(errors.routes.UnauthorisedController.unauthorised().url)
      }
    }

    "Auth returns more than one EMCS enrolment" should {

      "return OK and render the ExciseNumbers page" in new Test(twoEnrolments) {

        val result: Future[Result] = controller.exciseNumber()(fakeRequest)

        status(result) shouldBe Status.OK
        Html(contentAsString(result)) shouldBe view(Set(testErn, testErn + "_2"))
      }
    }
  }
}
