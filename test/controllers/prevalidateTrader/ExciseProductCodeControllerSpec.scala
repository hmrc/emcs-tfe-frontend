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
import controllers.predicates.{BetaAllowListActionImpl, FakeAuthAction, FakeDataRetrievalAction, PrevalidateTraderDataRetrievalAction}
import fixtures.{ExciseProductCodeFixtures, ItemFixtures}
import forms.prevalidate.PrevalidateExciseProductCodeFormProvider
import mocks.config.MockAppConfig
import mocks.connectors.MockBetaAllowListConnector
import mocks.services.{MockGetExciseProductCodesService, MockPrevalidateUserAnswersService}
import models.{ExciseProductCode, Index, NormalMode, UserAnswers}
import navigation.FakeNavigators.FakePrevalidateNavigator
import pages.prevalidateTrader.PrevalidateEPCPage
import play.api.data.Form
import play.api.mvc.{AnyContentAsEmpty, Call, Result}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.govukfrontend.views.Aliases.SelectItem
import viewmodels.helpers.SelectItemHelper
import views.html.prevalidateTrader.PrevalidateExciseProductCodeView

import scala.concurrent.Future

class ExciseProductCodeControllerSpec extends SpecBase
  with FakeAuthAction
  with MockPrevalidateUserAnswersService
  with MockGetExciseProductCodesService
  with ItemFixtures
  with ExciseProductCodeFixtures
  with MockBetaAllowListConnector
  with MockAppConfig {

  def action(idx: Index = testIndex1): Call =
    controllers.prevalidateTrader.routes.PrevalidateExciseProductCodeController.onSubmit(testErn, idx, NormalMode)

  val sampleEPCs: Seq[ExciseProductCode] = Seq(beerExciseProductCode, wineExciseProductCode)

  val sampleEPCsSelectOptions: Seq[SelectItem] = SelectItemHelper.constructSelectItems(
    selectOptions = sampleEPCs,
    defaultTextMessageKey = Some("prevalidateTrader.exciseProductCode.select.defaultValue"),
    withEpcDescription = true
  )(messages(FakeRequest()))

  lazy val formProvider: PrevalidateExciseProductCodeFormProvider = new PrevalidateExciseProductCodeFormProvider()
  lazy val form: Form[String] = formProvider.apply(sampleEPCs)
  lazy val view: PrevalidateExciseProductCodeView = app.injector.instanceOf[PrevalidateExciseProductCodeView]
  lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  class Fixture(val userAnswers: Option[UserAnswers], ern: String = testErn, preValidateEnabled: Boolean = true) {

    lazy val betaAllowListAction = new BetaAllowListActionImpl(
      betaAllowListConnector = mockBetaAllowListConnector,
      errorHandler = errorHandler,
      config = mockAppConfig
    )

    lazy val controller = new PrevalidateExciseProductCodeController(
      messagesApi = messagesApi,
      userAnswersService = mockUserAnswersService,
      navigator = new FakePrevalidateNavigator(testOnwardRoute),
      auth = FakeSuccessAuthAction,
      getData = new FakeDataRetrievalAction(testMinTraderKnownFacts, testMessageStatistics),
      betaAllowList = betaAllowListAction,
      requireData = new PrevalidateTraderDataRetrievalAction(mockUserAnswersService),
      formProvider = formProvider,
      controllerComponents = Helpers.stubMessagesControllerComponents(),
      exciseProductCodesService = mockGetExciseProductCodesService,
      view = view
    )(ec, appConfig)

    if (preValidateEnabled) {
      MockUserAnswersService.get(ern).returns(Future.successful(userAnswers))
    }

    MockedAppConfig.betaAllowListCheckingEnabled.repeat(2).returns(true)
    MockBetaAllowListConnector.check(testErn, "tfeNavHub").returns(Future.successful(Right(true)))
    MockBetaAllowListConnector.check(testErn, "tfePreValidate").returns(Future.successful(Right(preValidateEnabled)))
  }

  "ExciseProductCode Controller" must {

    "calling .onPageLoad()" when {
      "user is on the private beta list" should {
        "redirect to Start Pre Validate page when the idx is outside of bounds for a GET" in new Fixture(Some(emptyUserAnswers)) {
          val result = controller.onPageLoad(testErn, testIndex2, NormalMode)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.PrevalidateTraderStartController.onPageLoad(testErn).url)
        }

        "return OK and the correct view for a GET" in new Fixture(Some(emptyUserAnswers)) {

          MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

          val result = controller.onPageLoad(testErn, testIndex1, NormalMode)(request)

          status(result) mustEqual OK
          contentAsString(result) mustEqual
            view(form, action(), sampleEPCsSelectOptions, testIndex1)(userAnswersRequest(request, userAnswers.get), messages(request)).toString
        }

        "must populate the view correctly on a GET when the question has previously been answered" in new Fixture(
          Some(emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), beerExciseProductCode))
        ) {

          MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

          val sampleEPCsSelectOptionsWithBeerSelected = SelectItemHelper.constructSelectItems(
            selectOptions = sampleEPCs,
            defaultTextMessageKey = Some("prevalidateTrader.exciseProductCode.select.defaultValue"),
            existingAnswer = Some(testEpcBeer),
            withEpcDescription = true
          )(messages(request))

          val result = controller.onPageLoad(testErn, testIndex1, NormalMode)(request)

          status(result) mustEqual OK
          contentAsString(result) mustEqual
            view(form.fill(testEpcBeer), action(), sampleEPCsSelectOptionsWithBeerSelected, testIndex1)(userAnswersRequest(request, userAnswers.get), messages(request)).toString
        }

        "must populate the view correctly on a GET when the an EPC has been used on a previous page" in new Fixture(
          Some(emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), beerExciseProductCode))
        ) {

          MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

          val epcs = SelectItemHelper.constructSelectItems(
            selectOptions = Seq(wineExciseProductCode),
            defaultTextMessageKey = Some("prevalidateTrader.exciseProductCode.select.defaultValue"),
            withEpcDescription = true
          )(messages(request))

          val result = controller.onPageLoad(testErn, testIndex2, NormalMode)(request)

          status(result) mustEqual OK
          contentAsString(result) mustEqual
            view(form, action(testIndex2), epcs, testIndex2)(userAnswersRequest(request, userAnswers.get), messages(request)).toString
        }

        "redirect to Prevalidate Start Page for a GET if no existing data is found" in new Fixture(None) {

          val result = controller.onPageLoad(testErn, testIndex1, NormalMode)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.PrevalidateTraderStartController.onPageLoad(testErn).url
        }
      }
      "user is NOT on the private beta list" should {
        "redirect back to legacy" in new Fixture(userAnswers = Some(emptyUserAnswers), preValidateEnabled = false) {
          val result = controller.onPageLoad(testErn, testIndex1, NormalMode)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual "http://localhost:8080/emcs/trader/GBWKTestErn/prevalidate"
        }
      }
    }

    "calling .onSubmit()" when {
      "user is on the private beta list" should {

        "redirect to Start Pre Validate page when the idx is outside of bounds for a POST" in new Fixture(Some(emptyUserAnswers)) {
          val result = controller.onSubmit(testErn, testIndex2, NormalMode)(request.withFormUrlEncodedBody(("excise-product-code", testEpcWine)))

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.PrevalidateTraderStartController.onPageLoad(testErn).url)
        }

        "when valid data is submitted" must {
          "redirect to the next page" must {
            "when there was no previous answer" in new Fixture(Some(emptyUserAnswers)) {
              MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

              MockUserAnswersService.set(
                emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), wineExciseProductCode)
              ).returns(Future.successful(emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), wineExciseProductCode)))

              val result: Future[Result] =
                controller.onSubmit(testErn, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("excise-product-code", testEpcWine)))

              status(result) mustEqual SEE_OTHER
              redirectLocation(result).value mustEqual testOnwardRoute.url
            }
            "when the previous answer is the same as the new answer" in new Fixture(Some(
              emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), wineExciseProductCode)
            )) {
              MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

              val result: Future[Result] =
                controller.onSubmit(testErn, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("excise-product-code", testEpcWine)))

              status(result) mustEqual SEE_OTHER
              redirectLocation(result).value mustEqual testOnwardRoute.url
            }
            "when the previous answer is different to the new answer" in new Fixture(Some(
              emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), tobaccoExciseProductCode)
            )) {
              MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

              MockUserAnswersService.set(
                emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), wineExciseProductCode)
              ).returns(Future.successful(emptyUserAnswers.set(PrevalidateEPCPage(testIndex1), wineExciseProductCode)))

              val result: Future[Result] =
                controller.onSubmit(testErn, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("excise-product-code", testEpcWine)))

              status(result) mustEqual SEE_OTHER
              redirectLocation(result).value mustEqual testOnwardRoute.url
            }
          }
        }

        "return a Bad Request and errors when invalid data is submitted" in new Fixture(Some(emptyUserAnswers)) {

          MockGetExciseProductCodesService.getExciseProductCodes().returns(Future.successful(sampleEPCs))

          val boundForm = form.bind(Map("excise-product-code" -> ""))

          val result = controller.onSubmit(testErn, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("excise-product-code", "")))

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual
            view(boundForm, action(), sampleEPCsSelectOptions, testIndex1)(userAnswersRequest(request, userAnswers.get), messages(request)).toString
        }

        "redirect to Prevalidate Start Page for a POST if no existing data is found" in new Fixture(None) {
          val result = controller.onSubmit(testErn, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("excise-product-code", testEpcWine)))

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual routes.PrevalidateTraderStartController.onPageLoad(testErn).url
        }
      }
      "user is NOT on the private beta list" should {
        "redirect back to legacy" in new Fixture(userAnswers = Some(emptyUserAnswers), preValidateEnabled = false) {
          val result: Future[Result] =
            controller.onSubmit(testErn, testIndex1, NormalMode)(request.withFormUrlEncodedBody(("excise-product-code", testEpcWine)))

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual "http://localhost:8080/emcs/trader/GBWKTestErn/prevalidate"
        }
      }
    }
  }
}
