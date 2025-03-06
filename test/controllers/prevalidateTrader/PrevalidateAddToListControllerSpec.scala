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

package controllers.prevalidateTrader

import base.SpecBase
import controllers.predicates._
import fixtures.{ExciseProductCodeFixtures, ItemFixtures}
import forms.prevalidate.PrevalidateAddToListFormProvider
import mocks.config.MockAppConfig
import mocks.services.MockPrevalidateUserAnswersService
import models.requests.UserAnswersRequest
import models.{Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakePrevalidateNavigator
import pages.prevalidateTrader.{PrevalidateAddToListPage, PrevalidateAddedProductCodesPage, PrevalidateEPCPage}
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.helpers.PrevalidateAddToListHelper
import views.html.prevalidateTrader.PrevalidateAddToListView

import scala.concurrent.Future

class PrevalidateAddToListControllerSpec extends SpecBase
  with FakeAuthAction
  with MockPrevalidateUserAnswersService
  with ItemFixtures
  with ExciseProductCodeFixtures
  with MockAppConfig {

  lazy val formProvider: PrevalidateAddToListFormProvider = new PrevalidateAddToListFormProvider()
  lazy val form: Form[Boolean] = formProvider()
  lazy val view: PrevalidateAddToListView = app.injector.instanceOf[PrevalidateAddToListView]

  lazy val controllerRoute: String = routes.PrevalidateAddToListController.onPageLoad(testErn).url
  lazy val onSubmitCall: Call = routes.PrevalidateAddToListController.onSubmit(testErn)

  val userAnswersWithMaxPrevalidate: UserAnswers =
    (0 until PrevalidateAddedProductCodesPage.MAX).foldLeft(emptyUserAnswers) {
      case (userAnswers, idx) =>
        userAnswers.set(PrevalidateEPCPage(Index(idx)), wineExciseProductCode)
    }

  class Setup(val userAnswers: UserAnswers = emptyUserAnswers, preValidateEnabled: Boolean = true) {

    val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, controllerRoute)
    implicit val userAnswerReq: UserAnswersRequest[AnyContentAsEmpty.type] = userAnswersRequest(request, userAnswers)
    implicit val msgs: Messages = messages(request)

    MockUserAnswersService.get(userAnswers.ern).returns(Future.successful(Some(userAnswers))).anyNumberOfTimes()

    lazy val testController = new PrevalidateAddToListController(
      messagesApi = messagesApi,
      userAnswersService = mockUserAnswersService,
      navigator = new FakePrevalidateNavigator(testOnwardRoute),
      auth = FakeSuccessAuthAction,
      getData = new FakeDataRetrievalAction(Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
      requireData = new PrevalidateTraderDataRetrievalAction(mockUserAnswersService),
      formProvider = formProvider,
      controllerComponents = messagesControllerComponents,
      view = view
    )(ec)
  }

  "PrevalidateAddToList Controller" when {

    "GET onPageLoad" when {

      "return SEE_OTHER and Redirect to First EPC when there are no EPCs added yet" in new Setup() {

        val result = testController.onPageLoad(testErn)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustBe routes.PrevalidateExciseProductCodeController.onPageLoad(testErn, testIndex1, NormalMode).url
      }

      "return OK and the correct view when EPCs are added" in new Setup(
        emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), wineExciseProductCode)
      ) {

        val result = testController.onPageLoad(testErn)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          formOpt = Some(form),
          onSubmitCall = onSubmitCall,
          PrevalidateAddToListHelper.addedEpcs()
        ).toString
      }

      "return OK and the correct view when there are MAX EPCs already added" in new Setup(userAnswersWithMaxPrevalidate) {

        val result = testController.onPageLoad(testErn)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          formOpt = None,
          onSubmitCall = onSubmitCall,
          PrevalidateAddToListHelper.addedEpcs()
        ).toString
      }
    }

    "POST onSubmit" when {

      "redirect to the next page when Yes is submitted" in new Setup(
        emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), wineExciseProductCode)
      ) {

        MockUserAnswersService.set(userAnswers).returns(Future.successful(userAnswers))

        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", "true"))
        val result = testController.onSubmit(testErn)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "redirect to the next page and save the answer when No is submitted" in new Setup(
        emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), wineExciseProductCode)
      ) {

        val savedAnswers = userAnswers.set(PrevalidateAddToListPage, false)
        MockUserAnswersService.set(savedAnswers).returns(Future.successful(savedAnswers))

        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", "false"))
        val result = testController.onSubmit(testErn)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "redirect to the next page when submitted with MAX packages already added" in new Setup(userAnswersWithMaxPrevalidate) {

        val req = FakeRequest(POST, controllerRoute)
        val result = testController.onSubmit(testErn)(req)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual testOnwardRoute.url
      }

      "return a Bad Request and errors when invalid data is submitted" in new Setup() {

        val req = FakeRequest(POST, controllerRoute).withFormUrlEncodedBody(("value", "invalid value"))
        val boundForm = form.bind(Map("value" -> "invalid value"))
        val result = testController.onSubmit(testErn)(req)

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(
          formOpt = Some(boundForm),
          onSubmitCall = onSubmitCall,
          PrevalidateAddToListHelper.addedEpcs()
        ).toString
      }
    }
  }
}
