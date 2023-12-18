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
import connectors.emcsTfe.GetMessageStatisticsConnector
import controllers.predicates.{FakeAuthAction, FakeDataRetrievalAction}
import mocks.connectors.MockEmcsTfeConnector
import models.common.RoleType.GBWK
import models.requests.DataRequest
import org.scalamock.scalatest.MockFactory
import play.api.http.Status
import play.api.i18n.MessagesApi
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.AccountHomePage

import scala.concurrent.{ExecutionContext, Future}

class AccountHomeControllerSpec extends SpecBase with FakeAuthAction with MockFactory {
  val mockGetMessageStatisticsConnector: GetMessageStatisticsConnector = mock[GetMessageStatisticsConnector]

  trait Test extends MockEmcsTfeConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
    implicit val fakeRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest("GET", "/"))
    implicit val messages = app.injector.instanceOf[MessagesApi].preferred(fakeRequest)

    lazy val accountHomePage: AccountHomePage = app.injector.instanceOf[AccountHomePage]
    lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

    val controller: AccountHomeController = new AccountHomeController(
      app.injector.instanceOf[MessagesControllerComponents],
      accountHomePage,
      FakeSuccessAuthAction,
      new FakeDataRetrievalAction(testMinTraderKnownFacts, testMessageStatistics)
    )
  }

  "GET /trader/:exciseRegistrationNumber/account" must {
    "return 200" in new Test {
      val result: Future[Result] = controller.viewAccountHome(testErn)(fakeRequest)

      val expectedPage = accountHomePage(
        ern = testErn,
        roleType = GBWK
      )

      status(result) mustBe Status.OK
      contentAsString(result) mustBe expectedPage.toString()

    }
  }
}
