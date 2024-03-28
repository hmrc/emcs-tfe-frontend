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

package controllers.prevalidateTrader

import base.SpecBase
import controllers.predicates.{FakeAuthAction, FakeBetaAllowListAction, FakeDataRetrievalAction, PrevalidateTraderDataRetrievalAction}
import fixtures.{ExciseProductCodeFixtures, ItemFixtures}
import mocks.services.{MockPrevalidateTraderService, MockPrevalidateUserAnswersService}
import models.{ExciseProductCode, Index, NormalMode, UserAnswers}
import models.requests.UserAnswersRequest
import navigation.FakeNavigators.FakePrevalidateNavigator
import pages.prevalidateTrader.{PrevalidateConsigneeTraderIdentificationPage, PrevalidateEPCPage}
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.prevalidateTrader.PrevalidateTraderResultsView

import scala.collection.immutable.Seq
import scala.concurrent.Future

class PrevalidateTraderResultsControllerSpec extends SpecBase
  with FakeAuthAction
  with MockPrevalidateUserAnswersService
  with ItemFixtures
  with ExciseProductCodeFixtures
  with MockPrevalidateTraderService {

  lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  lazy val view = app.injector.instanceOf[PrevalidateTraderResultsView]
  lazy val controllerRoute: String = routes.PrevalidateTraderResultsController.onPageLoad(testErn).url
  def exciseProductPageRoute(idx: Index = 0): Call = routes.PrevalidateExciseProductCodeController.onPageLoad(testErn, idx, NormalMode)
  lazy val addToListPageRoute: Call = routes.PrevalidateAddToListController.onPageLoad(testErn)

  class Setup(val userAnswers: UserAnswers = emptyUserAnswers) {

    val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, controllerRoute)
    implicit val userAnswerReq: UserAnswersRequest[AnyContentAsEmpty.type] = userAnswersRequest(request, userAnswers)
    implicit val msgs: Messages = messages(request)

    MockUserAnswersService.get(userAnswers.ern).returns(Future.successful(Some(userAnswers))).anyNumberOfTimes()

    lazy val controller = new PrevalidateTraderResultsController(
      messagesApi = messagesApi,
      userAnswersService = mockUserAnswersService,
      betaAllowList = new FakeBetaAllowListAction,
      navigator = new FakePrevalidateNavigator(testOnwardRoute),
      auth = FakeSuccessAuthAction,
      getData = new FakeDataRetrievalAction(testMinTraderKnownFacts, testMessageStatistics),
      prevalidateTraderService = mockPrevalidateTraderService,
      requireData = new PrevalidateTraderDataRetrievalAction(mockUserAnswersService),
      controllerComponents = messagesControllerComponents,
      view = view
    )
  }

  "PrevalidateTraderResults Controller" when {

    "must return OK and the correct view for a GET when there is no ERN" in new Setup() {

      val result = controller.onPageLoad(testErn)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        ernOpt = None,
        addCodeCall = exciseProductPageRoute(),
        approved = Seq.empty,
        notApproved = Seq.empty
      ).toString()
    }

    "must return OK and the correct view for a GET when the correct addCodeCall when below the max codes allowed" in new Setup(
      emptyUserAnswers
        .set(PrevalidateEPCPage(0), wineExciseProductCode)
        .set(PrevalidateEPCPage(1), wineExciseProductCode)
        .set(PrevalidateEPCPage(2), wineExciseProductCode)
        .set(PrevalidateEPCPage(3), wineExciseProductCode)
        .set(PrevalidateEPCPage(4), wineExciseProductCode)
        .set(PrevalidateEPCPage(5), wineExciseProductCode)
        .set(PrevalidateEPCPage(6), wineExciseProductCode)
        .set(PrevalidateEPCPage(7), wineExciseProductCode)
        .set(PrevalidateEPCPage(8), wineExciseProductCode)
    ) {

      val result = controller.onPageLoad(testErn)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        ernOpt = Some(testErn),
        addCodeCall = exciseProductPageRoute(9),
        approved = Seq.empty,
        notApproved = Seq.empty
      ).toString()
    }

    "must return OK and the correct view for a GET when the correct addCodeCall when on the max codes allowed" in new Setup(
      emptyUserAnswers
        .set(PrevalidateEPCPage(0), wineExciseProductCode)
        .set(PrevalidateEPCPage(1), wineExciseProductCode)
        .set(PrevalidateEPCPage(2), wineExciseProductCode)
        .set(PrevalidateEPCPage(3), wineExciseProductCode)
        .set(PrevalidateEPCPage(4), wineExciseProductCode)
        .set(PrevalidateEPCPage(5), wineExciseProductCode)
        .set(PrevalidateEPCPage(6), wineExciseProductCode)
        .set(PrevalidateEPCPage(7), wineExciseProductCode)
        .set(PrevalidateEPCPage(8), wineExciseProductCode)
        .set(PrevalidateEPCPage(9), wineExciseProductCode)
    ) {

      val result = controller.onPageLoad(testErn)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        ernOpt = Some(testErn),
        addCodeCall = addToListPageRoute,
        approved = Seq.empty,
        notApproved = Seq.empty
      ).toString()
    }

    "must return OK and the correct view for a GET when the correct addCodeCall when above the max codes allowed" in new Setup(
      emptyUserAnswers
        .set(PrevalidateEPCPage(0), wineExciseProductCode)
        .set(PrevalidateEPCPage(1), wineExciseProductCode)
        .set(PrevalidateEPCPage(2), wineExciseProductCode)
        .set(PrevalidateEPCPage(3), wineExciseProductCode)
        .set(PrevalidateEPCPage(4), wineExciseProductCode)
        .set(PrevalidateEPCPage(5), wineExciseProductCode)
        .set(PrevalidateEPCPage(6), wineExciseProductCode)
        .set(PrevalidateEPCPage(7), wineExciseProductCode)
        .set(PrevalidateEPCPage(8), wineExciseProductCode)
        .set(PrevalidateEPCPage(9), wineExciseProductCode)
        .set(PrevalidateEPCPage(10), wineExciseProductCode)
    ) {

      val result = controller.onPageLoad(testErn)(request)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        ernOpt = Some(testErn),
        addCodeCall = addToListPageRoute,
        approved = Seq.empty,
        notApproved = Seq.empty
      ).toString()
    }
  }
}
