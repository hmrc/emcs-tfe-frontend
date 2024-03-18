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
import controllers.predicates.{FakeAuthAction, FakeBetaAllowListAction, FakeDataRetrievalAction}
import forms.ConsigneeTraderIdentificationFormProvider
import play.api.data.FormError
import play.api.mvc.MessagesControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.prevalidateTrader.ConsigneeTraderIdentificationPage

class ConsigneeTraderIdentificationControllerSpec extends SpecBase with FakeAuthAction {

  lazy val view: ConsigneeTraderIdentificationPage = app.injector.instanceOf[ConsigneeTraderIdentificationPage]
  lazy val formProvider: ConsigneeTraderIdentificationFormProvider = app.injector.instanceOf[ConsigneeTraderIdentificationFormProvider]

  lazy val controller: ConsigneeTraderIdentificationController = new ConsigneeTraderIdentificationController(
    mcc = app.injector.instanceOf[MessagesControllerComponents],
    auth = FakeSuccessAuthAction,
    getData = new FakeDataRetrievalAction(testMinTraderKnownFacts, testMessageStatistics),
    betaAllowList = new FakeBetaAllowListAction,
    formProvider = formProvider,
    view = view
  )

  "ConsigneeTraderIdentification Controller" when {

    "calling .onPageLoad()" must {

      "render the view" in {

        val request = FakeRequest(GET, routes.ConsigneeTraderIdentificationController.onPageLoad(testErn).url)
        val result = controller.onPageLoad(testErn)(request)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form = formProvider(),
          action = controllers.prevalidateTrader.routes.ConsigneeTraderIdentificationController.onSubmit(testErn)
        )(dataRequest(request), messages(request)).toString
      }
    }

    "calling .onSubmit()" when {

      "form validation fails" must {

        "render the view when bad value" in {
          val request = FakeRequest(POST, routes.ConsigneeTraderIdentificationController.onPageLoad(testErn).url)
          val result = controller.onSubmit(testErn)(request.withFormUrlEncodedBody("value" -> "<beans />"))

          val form = formProvider()
            .fill("<beans />")
            .withError(FormError("value", Seq("prevalidateTrader.consigneeTraderIdentification.error.invalidCharacters")))

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(
            form = form,
            action = controllers.prevalidateTrader.routes.ConsigneeTraderIdentificationController.onSubmit(testErn)
          )(dataRequest(request), messages(request)).toString
        }

        "render the view when form missing" in {
          val request = FakeRequest(POST, routes.ConsigneeTraderIdentificationController.onPageLoad(testErn).url)
          val result = controller.onSubmit(testErn)(request)

          val form = formProvider()
            .withError(FormError("value", Seq("prevalidateTrader.consigneeTraderIdentification.error.required")))

          status(result) mustEqual BAD_REQUEST
          contentAsString(result) mustEqual view(
            form = form,
            action = controllers.prevalidateTrader.routes.ConsigneeTraderIdentificationController.onSubmit(testErn)
          )(dataRequest(request), messages(request)).toString
        }
      }

      "form validation passes" when {
        "data saves to repository" must {
          "redirect" in {
            // TODO: update when next page is built
            val request = FakeRequest(POST, routes.ConsigneeTraderIdentificationController.onPageLoad(testErn).url)
            val result = controller.onSubmit(testErn)(request.withFormUrlEncodedBody("value" -> "GB00123456789"))

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual testOnly.controllers.routes.UnderConstructionController.onPageLoad().url
          }
        }
      }
    }
  }
}
