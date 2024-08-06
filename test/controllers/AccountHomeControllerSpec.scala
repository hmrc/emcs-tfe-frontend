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
import config.AppConfig
import controllers.predicates.{BetaAllowListActionImpl, FakeAuthAction, FakeDataRetrievalAction}
import mocks.config.MockAppConfig
import mocks.connectors.MockBetaAllowListConnector
import models.common.RoleType.GBWK
import models.requests.DataRequest
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}
import play.api.http.Status
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.AccountHomeView

import scala.concurrent.{ExecutionContext, Future}

class AccountHomeControllerSpec extends SpecBase with FakeAuthAction with MockFactory with MockBetaAllowListConnector with MockAppConfig {

  class Test(navHubEnabled: Boolean = true, homeEnabled: Boolean = true) {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
    implicit val fakeRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest("GET", "/"))
    implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(fakeRequest)

    lazy val view: AccountHomeView = app.injector.instanceOf[AccountHomeView]
    lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

    lazy val betaAllowListAction = new BetaAllowListActionImpl(
      betaAllowListConnector = mockBetaAllowListConnector,
      errorHandler = errorHandler,
      config = mockAppConfig
    )

    val controller: AccountHomeController = new AccountHomeController(
      app.injector.instanceOf[MessagesControllerComponents],
      view,
      FakeSuccessAuthAction,
      new FakeDataRetrievalAction(Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
      betaAllowListAction
    )(ec, appConfig)

    MockedAppConfig.betaAllowListCheckingEnabled.repeat(2).returns(true)
    MockBetaAllowListConnector.check(testErn, "tfeNavHub").returns(Future.successful(Right(navHubEnabled)))
    MockBetaAllowListConnector.check(testErn, "tfeHome").returns(Future.successful(Right(homeEnabled)))
  }

  "GET /trader/:exciseRegistrationNumber/account" when {
    "user is on the private beta list" should {
      "return 200" in new Test {
        val result: Future[Result] = controller.viewAccountHome(testErn)(fakeRequest)

      val expectedView = view(
        ern = testErn,
        roleType = GBWK
      )

      status(result) mustBe Status.OK
      contentAsString(result) mustBe expectedView.toString()
      }
    }

    "user is NOT on the private beta list" should {
      "redirect to legacy at a glance page" in new Test(homeEnabled = false) {
        val result: Future[Result] = controller.viewAccountHome(testErn)(fakeRequest)

        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) shouldBe Some("http://localhost:8080/emcs/trader/GBWKTestErn")
      }
    }
  }
}
