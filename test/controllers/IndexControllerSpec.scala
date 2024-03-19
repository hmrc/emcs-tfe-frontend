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
import config.EnrolmentKeys
import controllers.predicates.FakeSelectExciseNumbersAuthAction
import fixtures.messages.EN
import mocks.connectors.MockEmcsTfeConnector
import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.auth.core.{Enrolment, EnrolmentIdentifier}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.ExciseNumbersView

import scala.concurrent.{ExecutionContext, Future}

class IndexControllerSpec extends SpecBase with FakeSelectExciseNumbersAuthAction {

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
    implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(EN.lang))

    val view = app.injector.instanceOf[ExciseNumbersView]
    val fakeAuthSuccess = new FakeSuccessSelectExciseNumbersAuthAction(enrolments)

    val controller: IndexController = new IndexController(
      app.injector.instanceOf[MessagesControllerComponents],
      view,
      fakeAuthSuccess
    )
  }

  ".exciseNumber()" when {

    "Auth returns one EMCS enrolment" must {

      "return 303 and redirect movements-in page" in new Test(oneEnrolment) {

        val result: Future[Result] = controller.exciseNumber()(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(routes.AccountHomeController.viewAccountHome(testErn).url)
      }
    }

    "Auth returns no EMCS enrolments" must {

      "return 303 and redirect to the unauthorised page" in new Test(Set()) {

        val result: Future[Result] = controller.exciseNumber()(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(errors.routes.UnauthorisedController.unauthorised().url)
      }
    }

    "Auth returns more than one EMCS enrolment" must {

      "return OK and render the ExciseNumbers page" in new Test(twoEnrolments) {

        val result: Future[Result] = controller.exciseNumber()(fakeRequest)

        status(result) mustBe Status.OK
        Html(contentAsString(result)) mustBe view(Set(testErn, testErn + "_2"))
      }
    }
  }
}
