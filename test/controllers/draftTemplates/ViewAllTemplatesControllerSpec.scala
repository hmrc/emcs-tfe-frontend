/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.draftTemplates

import base.SpecBase
import controllers.predicates.{FakeAuthAction, FakeDataRetrievalAction}
import models.requests.DataRequest
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.ViewAllTemplatesView

import scala.concurrent.{ExecutionContext, Future}

class ViewAllTemplatesControllerSpec extends SpecBase with FakeAuthAction {
  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
    implicit val fakeRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest("GET", "/"))
    implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(fakeRequest)

    lazy val view: ViewAllTemplatesView = app.injector.instanceOf[ViewAllTemplatesView]

    val controller: ViewAllTemplatesController = new ViewAllTemplatesController(
      app.injector.instanceOf[MessagesControllerComponents],
      view,
      FakeSuccessAuthAction,
      new FakeDataRetrievalAction(Some(testMinTraderKnownFacts), Some(testMessageStatistics))
    )(ec)
  }

  "GET" when {
    "user can't view draft templates" should {
      "redirect" in new Test {
        val result: Future[Result] = controller.onPageLoad("XI00123")(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.AccountHomeController.viewAccountHome("XI00123").url)
      }
    }

    "user can view draft templates" should {
      "return 200" in new Test {
        val result: Future[Result] = controller.onPageLoad(testErn)(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(Seq()).toString()
      }
    }
  }
}
