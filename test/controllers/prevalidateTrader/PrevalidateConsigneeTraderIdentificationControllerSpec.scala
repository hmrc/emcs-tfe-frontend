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
import controllers.predicates.{BetaAllowListActionImpl, FakeAuthAction, FakeDataRetrievalAction, PrevalidateTraderDataRetrievalAction}
import forms.prevalidate.PrevalidateConsigneeTraderIdentificationFormProvider
import mocks.config.MockAppConfig
import mocks.connectors.MockBetaAllowListConnector
import mocks.services.MockPrevalidateUserAnswersService
import models.prevalidate.{EntityGroup, PrevalidateTraderModel}
import navigation.FakeNavigators.FakePrevalidateNavigator
import pages.prevalidateTrader.PrevalidateConsigneeTraderIdentificationPage
import play.api.data.FormError
import play.api.mvc.MessagesControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.prevalidateTrader.PrevalidateConsigneeTraderIdentificationView

import scala.concurrent.Future

class PrevalidateConsigneeTraderIdentificationControllerSpec
  extends SpecBase
    with FakeAuthAction
    with MockPrevalidateUserAnswersService
    with MockBetaAllowListConnector
    with MockAppConfig {

  lazy val view: PrevalidateConsigneeTraderIdentificationView = app.injector.instanceOf[PrevalidateConsigneeTraderIdentificationView]
  lazy val formProvider: PrevalidateConsigneeTraderIdentificationFormProvider = app.injector.instanceOf[PrevalidateConsigneeTraderIdentificationFormProvider]

  class Setup(navHubEnabled: Boolean = true, preValidateEnabled: Boolean = true) {

    lazy val betaAllowListAction = new BetaAllowListActionImpl(
      betaAllowListConnector = mockBetaAllowListConnector,
      errorHandler = errorHandler,
      config = mockAppConfig
    )

    lazy val controller: PrevalidateConsigneeTraderIdentificationController = new PrevalidateConsigneeTraderIdentificationController(
      controllerComponents = app.injector.instanceOf[MessagesControllerComponents],
      auth = FakeSuccessAuthAction,
      getData = new FakeDataRetrievalAction(testMinTraderKnownFacts, Some(testMessageStatistics)),
      betaAllowList = betaAllowListAction,
      userAnswersAction = new PrevalidateTraderDataRetrievalAction(mockUserAnswersService),
      userAnswersService = mockUserAnswersService,
      navigator = new FakePrevalidateNavigator(testOnwardRoute),
      formProvider = formProvider,
      view = view
    )(ec, appConfig)

    MockedAppConfig.betaAllowListCheckingEnabled.repeat(2).returns(true)
    MockBetaAllowListConnector.check(testErn, "tfeNavHub").returns(Future.successful(Right(navHubEnabled)))
    MockBetaAllowListConnector.check(testErn, "tfePreValidate").returns(Future.successful(Right(preValidateEnabled)))
  }


  val testAnswer = PrevalidateTraderModel(ern = "GB00123456789", entityGroup = EntityGroup.UKTrader)

  "ConsigneeTraderIdentification Controller" when {

    "calling .onPageLoad()" when {
      "user is on the private beta list" should {
        "render the view" when {
          "previous data exists in user answers" in new Setup {
            MockUserAnswersService
              .get(testErn)
              .returns(Future.successful(Some(emptyUserAnswers.set(PrevalidateConsigneeTraderIdentificationPage, testAnswer))))

            val request = FakeRequest(GET, routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(testErn).url)
            val result = controller.onPageLoad(testErn)(request)

            status(result) mustEqual OK
            contentAsString(result) mustEqual view(
              form = formProvider().fill(testAnswer),
              action = routes.PrevalidateConsigneeTraderIdentificationController.onSubmit(testErn)
            )(userAnswersRequest(request), messages(request)).toString
          }

          "no previous data exists in user answers" in new Setup {
            MockUserAnswersService
              .get(testErn)
              .returns(Future.successful(Some(emptyUserAnswers)))

            val request = FakeRequest(GET, routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(testErn).url)
            val result = controller.onPageLoad(testErn)(request)

            status(result) mustEqual OK
            contentAsString(result) mustEqual view(
              form = formProvider(),
              action = routes.PrevalidateConsigneeTraderIdentificationController.onSubmit(testErn)
            )(userAnswersRequest(request), messages(request)).toString
          }
        }
      }
      "user is NOT on the private beta list" should {
        "redirect to legacy" in new Setup(preValidateEnabled = false) {
          val request = FakeRequest(GET, routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(testErn).url)
          val result = controller.onPageLoad(testErn)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual "http://localhost:8080/emcs/trader/GBWKTestErn/prevalidate"
        }
      }
    }

    "calling .onSubmit()" when {
      "user is on the private beta list" should {
        "form validation fails" must {

          "render the view with the bad value" when {

            "previous user answers exist" in new Setup {

              MockUserAnswersService
                .get(testErn)
                .returns(Future.successful(Some(emptyUserAnswers.set(PrevalidateConsigneeTraderIdentificationPage, testAnswer))))

              val request = FakeRequest(POST, routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(testErn).url)
              val result = controller.onSubmit(testErn)(request.withFormUrlEncodedBody("ern" -> "GBWK123", "entityGroup" -> testEntityGroup.toString))

              val form = formProvider()
                .fill(PrevalidateTraderModel("GBWK123", testEntityGroup))
                .withError(FormError("ern", Seq("prevalidateTrader.consigneeTraderIdentification.ern.error.invalidRegex")))

              status(result) mustEqual BAD_REQUEST
              contentAsString(result) mustEqual view(
                form = form,
                action = controllers.prevalidateTrader.routes.PrevalidateConsigneeTraderIdentificationController.onSubmit(testErn)
              )(userAnswersRequest(request), messages(request)).toString
            }

            "no previous user answers exist" in new Setup {

              MockUserAnswersService
                .get(testErn)
                .returns(Future.successful(Some(emptyUserAnswers)))

              val request = FakeRequest(POST, routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(testErn).url)
              val result = controller.onSubmit(testErn)(request.withFormUrlEncodedBody("ern" -> "GBWK123", "entityGroup" -> testEntityGroup.toString))

              val form = formProvider()
                .fill(PrevalidateTraderModel("GBWK123", testEntityGroup))
                .withError(FormError("ern", Seq("prevalidateTrader.consigneeTraderIdentification.ern.error.invalidRegex")))

              status(result) mustEqual BAD_REQUEST
              contentAsString(result) mustEqual view(
                form = form,
                action = controllers.prevalidateTrader.routes.PrevalidateConsigneeTraderIdentificationController.onSubmit(testErn)
              )(userAnswersRequest(request), messages(request)).toString
            }
          }

          "render the view when form values are missing" in new Setup {

            MockUserAnswersService.get(testErn).returns(Future.successful(Some(emptyUserAnswers)))

            val request = FakeRequest(POST, routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(testErn).url)
            val result = controller.onSubmit(testErn)(request)

            val form = formProvider()
              .withError(FormError("ern", Seq("prevalidateTrader.consigneeTraderIdentification.ern.error.required")))
              .withError(FormError("entityGroup", Seq("prevalidateTrader.consigneeTraderIdentification.entityGroup.error.required")))

            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual view(
              form = form,
              action = controllers.prevalidateTrader.routes.PrevalidateConsigneeTraderIdentificationController.onSubmit(testErn)
            )(userAnswersRequest(request), messages(request)).toString
          }
        }
        "form validation passes" when {
          "data saves to repository" must {
            "redirect" in new Setup {

              MockUserAnswersService
                .get(testErn)
                .returns(Future.successful(Some(emptyUserAnswers)))

              MockUserAnswersService
                .set(emptyUserAnswers.set(PrevalidateConsigneeTraderIdentificationPage, PrevalidateTraderModel("GBWK123456789", testEntityGroup)))
                .returns(Future.successful(emptyUserAnswers))

              val request = FakeRequest(POST, routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(testErn).url)
              val result = controller.onSubmit(testErn)(request.withFormUrlEncodedBody("ern" -> "GBWK123456789", "entityGroup" -> testEntityGroup.toString))

              status(result) mustEqual SEE_OTHER
              redirectLocation(result).value mustEqual testOnwardRoute.url
            }
          }
        }
      }
      "user is NOT on the private beta list" should {
        "redirect to legacy" in new Setup(preValidateEnabled = false) {
          val request = FakeRequest(POST, routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(testErn).url)
          val result = controller.onSubmit(testErn)(request.withFormUrlEncodedBody("value" -> PrevalidateTraderModel(testErn, EntityGroup.UKTrader).toString))

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual "http://localhost:8080/emcs/trader/GBWKTestErn/prevalidate"
        }
      }
    }
  }
}
