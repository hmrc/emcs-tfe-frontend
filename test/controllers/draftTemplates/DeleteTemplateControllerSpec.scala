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
import fixtures.DraftTemplatesFixtures
import forms.draftTemplates.DeleteTemplateFormProvider
import mocks.services.MockDraftTemplatesService
import models.requests.DataRequest
import play.api.data.{Form, FormError}
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, redirectLocation, status}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.draftTemplates.DeleteTemplateView

import scala.concurrent.Future

class DeleteTemplateControllerSpec extends SpecBase
  with FakeAuthAction
  with FeatureSwitching
  with MockDraftTemplatesService
  with DraftTemplatesFixtures
{

  implicit lazy val config: AppConfig = appConfig
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val fakeRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest("GET", "/"))
  implicit val msgs: Messages = messages(fakeRequest)
  lazy val view: DeleteTemplateView = app.injector.instanceOf[DeleteTemplateView]
  lazy val formProvider: DeleteTemplateFormProvider = new DeleteTemplateFormProvider()
  lazy val form: Form[Boolean] = formProvider()

  lazy val testTemplate = fullTemplate.copy(templateId = testTemplateId)

  trait Test {
    val controller: DeleteTemplateController = new DeleteTemplateController(
      app.injector.instanceOf[MessagesControllerComponents],
      FakeSuccessAuthAction,
      new FakeDataRetrievalAction(Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
      mockDraftTemplatesService,
      view,
      formProvider
    )

    MockDraftTemplatesService.getTemplate(testErn, testTemplateId).returns(Future.successful(Some(testTemplate)))
  }

  "GET onPageLoad" should {
      "redirect" when {
        "the templates feature is disabled" in new Test {
          disable(TemplatesLink)

          val result = controller.onPageLoad(testErn, testTemplateId)(fakeRequest)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.routes.AccountHomeController.viewAccountHome(testErn).url)
        }
      }

      "return OK and the correct view for a GET" in new Test {
        enable(TemplatesLink)

        val result = controller.onPageLoad(testErn, testTemplateId)(fakeRequest)
        status(result) mustBe OK
        contentAsString(result) mustBe view(form, testTemplate)(fakeRequest, msgs, config).toString
      }
  }

  "POST onSubmit" when {

    "the templates feature is disabled" should {

      "redirect to the account home page" in new Test {
        disable(TemplatesLink)

        val result = controller.onSubmit(testErn, testTemplateId)(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.AccountHomeController.viewAccountHome(testErn).url)
      }

    }

    "the templates feature is enabled" should {

      "return a bad request" when {

        "the user has not selected an option" in new Test {
          enable(TemplatesLink)

          val request = FakeRequest("POST", "/")
          val result = controller.onSubmit(testErn, testTemplateId)(request)
          status(result) mustBe BAD_REQUEST
          contentAsString(result) mustBe view(form.withError(FormError("value", Seq("deleteTemplate.error.required"))), testTemplate)(fakeRequest, msgs, config).toString
        }

      }

      "redirect to the view all templates view" when {

        "the user has said yes to delete" in new Test {
          enable(TemplatesLink)

          MockDraftTemplatesService.delete(testErn, testTemplateId).returns(Future.successful(true))

          val request = FakeRequest("POST", "/").withFormUrlEncodedBody("value" -> "true")
          val result = controller.onSubmit(testErn, testTemplateId)(request)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.draftTemplates.routes.ViewAllTemplatesController.onPageLoad(testErn, None).url)
        }

        "the user has said no to delete" in new Test {
          enable(TemplatesLink)

          MockDraftTemplatesService.delete(testErn, testTemplateId).never()

          val request = FakeRequest("POST", "/").withFormUrlEncodedBody("value" -> "false")
          val result = controller.onSubmit(testErn, testTemplateId)(request)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.draftTemplates.routes.ViewAllTemplatesController.onPageLoad(testErn, None).url)
        }
      }

    }

  }
}
