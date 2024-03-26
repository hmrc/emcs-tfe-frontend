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
import fixtures.ExciseProductCodeFixtures
import forms.prevalidateTrader.PrevalidateRemoveExciseProductCodeFormProvider
import mocks.services.MockPrevalidateUserAnswersService
import models.{NormalMode, UserAnswers}
import navigation.FakeNavigators.FakePrevalidateNavigator
import pages.prevalidateTrader.{PrevalidateAddedProductCodesPage, PrevalidateEPCPage}
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, Call, MessagesControllerComponents}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.prevalidateTrader.PrevalidateTraderRemoveExciseProductCodeView

import scala.concurrent.Future

class PrevalidateRemoveExciseProductCodeControllerSpec
  extends SpecBase
  with FakeAuthAction
  with MockPrevalidateUserAnswersService
  with ExciseProductCodeFixtures {

  lazy val view: PrevalidateTraderRemoveExciseProductCodeView = app.injector.instanceOf[PrevalidateTraderRemoveExciseProductCodeView]

  lazy val formProvider: PrevalidateRemoveExciseProductCodeFormProvider = app.injector.instanceOf[PrevalidateRemoveExciseProductCodeFormProvider]

  val getRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, routes.PrevalidateRemoveExciseProductCodeController.onPageLoad(testErn, testIndex1).url)

  val postRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(POST, routes.PrevalidateRemoveExciseProductCodeController.onPageLoad(testErn, testIndex1).url)

  val baseUserAnswers: UserAnswers = emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), wineExciseProductCode)

  val action: Call = routes.PrevalidateRemoveExciseProductCodeController.onSubmit(testErn, testIndex1)

  class Fixture(val userAnswers: Option[UserAnswers], ern: String = testErn) {

    MockUserAnswersService.get(ern).returns(Future.successful(userAnswers))

    lazy val controller: PrevalidateRemoveExciseProductCodeController = new PrevalidateRemoveExciseProductCodeController(
      controllerComponents = app.injector.instanceOf[MessagesControllerComponents],
      auth = FakeSuccessAuthAction,
      getData = new FakeDataRetrievalAction(testMinTraderKnownFacts, testMessageStatistics),
      betaAllowList = new FakeBetaAllowListAction,
      requireData = new PrevalidateTraderDataRetrievalAction(mockUserAnswersService),
      userAnswersService = mockUserAnswersService,
      navigator = new FakePrevalidateNavigator(testOnwardRoute),
      formProvider = formProvider,
      view = view
    )
  }

  ".onPageLoad" should {
    "return OK and the correct view" in new Fixture(Some(baseUserAnswers)) {

      val result = controller.onPageLoad(testErn, testIndex1)(getRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual view(
        form = formProvider(testEpcWine),
        action = action,
        exciseProductCode = testEpcWine
      )(userAnswersRequest(getRequest), messages(getRequest)).toString
    }

    //scalastyle:off
    "redirect back to the add to list page when the index is out of bounds" in new Fixture(Some(baseUserAnswers)) {

      val result = controller.onPageLoad(testErn, testIndex2)(getRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).get mustBe routes.PrevalidateAddToListController.onPageLoad(testErn).url
    }

    "redirect to to the start page when no existing data is found" in new Fixture(None) {

      val result = controller.onPageLoad(testErn, testIndex1)(getRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.PrevalidateTraderStartController.onPageLoad(testErn).url
    }
  }

  ".onSubmit" should {

    "when the user answers yes" must {
      "delete the final EPC and redirect back to the first EPC entry page" in new Fixture(Some(baseUserAnswers)) {

        MockUserAnswersService
          .set(emptyUserAnswers.set(PrevalidateAddedProductCodesPage, Json.arr()))
          .returns(Future.successful(emptyUserAnswers.set(PrevalidateAddedProductCodesPage, Json.arr())))

        val result = controller.onSubmit(testErn, testIndex1)(postRequest.withFormUrlEncodedBody(("value", "true")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).get mustBe routes.PrevalidateExciseProductCodeController.onPageLoad(testErn, testIndex1, NormalMode).url
      }

      "delete an EPC and redirect back add to list page when more exist" in new Fixture(Some(baseUserAnswers.set(PrevalidateEPCPage(testIndex2), beerExciseProductCode))) {

        MockUserAnswersService
          .set(emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), beerExciseProductCode))
          .returns(Future.successful(emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), beerExciseProductCode)))

        val result = controller.onSubmit(testErn, testIndex1)(postRequest.withFormUrlEncodedBody(("value", "true")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).get mustBe routes.PrevalidateAddToListController.onPageLoad(testErn).url
      }
    }

    "when the user answers no" must {

      "redirect back to the add to list and not delete the selected EPC" in new Fixture(Some(baseUserAnswers)) {

        val result = controller.onSubmit(testErn, testIndex1)(postRequest.withFormUrlEncodedBody(("value", "false")))

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).get mustBe routes.PrevalidateAddToListController.onPageLoad(testErn).url
      }
    }


    "return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(baseUserAnswers)) {

      val boundForm = formProvider(testEpcWine).bind(Map("value" -> ""))

      val result = controller.onSubmit(testErn, testIndex1)(postRequest.withFormUrlEncodedBody(("value", "")))

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual view(boundForm, action, testEpcWine)(userAnswersRequest(postRequest), messages(postRequest)).toString
    }

    //scalastyle:off
    "redirect back to the add to list page when the index is out of bounds" in new Fixture(Some(emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), wineExciseProductCode))) {

      val result = controller.onSubmit(testErn, testIndex2)(postRequest.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).get mustBe routes.PrevalidateAddToListController.onPageLoad(testErn).url
    }

    "redirect to to the start page when no existing data is found" in new Fixture(None) {

      val result = controller.onSubmit(testErn, testIndex1)(postRequest.withFormUrlEncodedBody(("value", "true")))

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.PrevalidateTraderStartController.onPageLoad(testErn).url
    }
  }
}
