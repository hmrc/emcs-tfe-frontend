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
import config.AppConfig
import controllers.predicates.{FakeAuthAction, FakeDataRetrievalAction}
import featureswitch.core.config.{FeatureSwitching, TemplatesLink}
import models.requests.DataRequest
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.PaginationHelper
import views.html.ViewAllTemplatesView

import scala.concurrent.Future

class ViewAllTemplatesControllerSpec extends SpecBase with FakeAuthAction with FeatureSwitching {

  implicit lazy val config: AppConfig = appConfig
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val fakeRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest("GET", "/"))
  implicit val msgs: Messages = messages(fakeRequest)
  lazy val view: ViewAllTemplatesView = app.injector.instanceOf[ViewAllTemplatesView]

  lazy val paginationHelper: PaginationHelper = app.injector.instanceOf[PaginationHelper]

  val controller: ViewAllTemplatesController = new ViewAllTemplatesController(
    app.injector.instanceOf[MessagesControllerComponents],
    view,
    FakeSuccessAuthAction,
    new FakeDataRetrievalAction(Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
    paginationHelper
  )

  enable(TemplatesLink)

  "GET" when {
    "user can't view draft templates" should {
      "redirect" in {
        val result: Future[Result] = controller.onPageLoad("XI00123", None)(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.AccountHomeController.viewAccountHome("XI00123").url)
      }
    }

    "user can view draft templates" should {
      "return 200" in {
        val result: Future[Result] = controller.onPageLoad(testErn, None)(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(
          controller.dummyTemplates,
          paginationHelper.constructPaginationForDraftTemplates(testErn, 1, controller.totalNumberOfTemplates)
        ).toString()
      }
    }
  }
}
