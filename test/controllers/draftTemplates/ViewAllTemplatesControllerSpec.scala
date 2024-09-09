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
import mocks.viewmodels.MockPaginationHelper
import models.requests.DataRequest
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.ViewAllTemplatesView

import scala.concurrent.Future

class ViewAllTemplatesControllerSpec extends SpecBase with FakeAuthAction with FeatureSwitching with MockPaginationHelper {

  implicit lazy val config: AppConfig = appConfig
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val fakeRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest("GET", "/"))
  implicit val msgs: Messages = messages(fakeRequest)
  lazy val view: ViewAllTemplatesView = app.injector.instanceOf[ViewAllTemplatesView]

  val controller: ViewAllTemplatesController = new ViewAllTemplatesController(
    app.injector.instanceOf[MessagesControllerComponents],
    view,
    FakeSuccessAuthAction,
    new FakeDataRetrievalAction(Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
    mockPaginationHelper
  )

  enable(TemplatesLink)

  "GET" when {
    "user can't view draft templates" should {
      "redirect" in {
        MockPaginationHelper.calculatePageCount(30, 10)

        val result: Future[Result] = controller.onPageLoad("XI00123", None)(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.AccountHomeController.viewAccountHome("XI00123").url)
      }
    }

    "user can view draft templates" should {
      "redirect to the index 1 when current index is below the minimum" in {

        MockPaginationHelper.calculatePageCount(30, 10)

        val result: Future[Result] = controller.onPageLoad(testErn, Some(0))(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.draftTemplates.routes.ViewAllTemplatesController.onPageLoad(testErn, None).url)
      }

      "show the correct view and pagination with an index missing" in {

        MockPaginationHelper.constructPaginationForDraftTemplates(index = 1, pageCount = 3)(None)

        MockPaginationHelper.calculatePageCount(30, 10)

        val result: Future[Result] = controller.onPageLoad(testErn, None)(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(
          controller.dummyTemplates,
          None
        ).toString()
      }

      "show the correct view and pagination with an index of 1" in {

        MockPaginationHelper.constructPaginationForDraftTemplates(index = 1, pageCount = 3)(None)

        MockPaginationHelper.calculatePageCount(30, 10)

        val result: Future[Result] = controller.onPageLoad(testErn, Some(1))(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(
          controller.dummyTemplates,
          None
        ).toString()
      }

      "show the correct view and pagination with an index of 2" in {

        MockPaginationHelper.constructPaginationForDraftTemplates(index = 2, pageCount = 3)(None)

        MockPaginationHelper.calculatePageCount(30, 10)

        val result: Future[Result] = controller.onPageLoad(testErn, Some(2))(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(
          controller.dummyTemplates,
          None
        ).toString()
      }

      "show the correct view and pagination with an index of 3" in {

        MockPaginationHelper.constructPaginationForDraftTemplates(index = 3, pageCount = 3)(None)

        MockPaginationHelper.calculatePageCount(30, 10)

        val result: Future[Result] = controller.onPageLoad(testErn, Some(3))(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(
          controller.dummyTemplates,
          None
        ).toString()
      }

      "redirect to the index 1 when current index is above the maximum" in {

        MockPaginationHelper.calculatePageCount(30, 10)

        val result: Future[Result] = controller.onPageLoad(testErn, Some(4))(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.draftTemplates.routes.ViewAllTemplatesController.onPageLoad(testErn, None).url)
      }

      "show the correct view and pagination when movement count is 1 above a multiple of the pageCount" in {

        MockPaginationHelper.constructPaginationForDraftTemplates(index = 3, pageCount = 4)(None)

        MockPaginationHelper.calculatePageCount(31, 10)

        val result: Future[Result] = controller.onPageLoad(testErn, Some(3))(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(
          controller.dummyTemplates,
          None
        ).toString()
      }

      "show the correct view and pagination when movement count is 1 below a multiple of the pageCount" in {

        MockPaginationHelper.constructPaginationForDraftTemplates(index = 3, pageCount = 4)(None)

        MockPaginationHelper.calculatePageCount(39, 10)

        val result: Future[Result] = controller.onPageLoad(testErn, Some(3))(fakeRequest)

        status(result) mustBe OK
        contentAsString(result) mustBe view(
          controller.dummyTemplates,
          None
        ).toString()
      }
    }
  }
}
