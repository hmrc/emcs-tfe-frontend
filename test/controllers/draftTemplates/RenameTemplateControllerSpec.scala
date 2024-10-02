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
import forms.draftTemplates.RenameTemplateFormProvider
import mocks.services.MockDraftTemplatesService
import models.requests.DataRequest
import play.api.data.{Form, FormError}
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import views.html.draftTemplates.RenameTemplateView

import scala.concurrent.Future

class RenameTemplateControllerSpec
  extends SpecBase
    with FakeAuthAction
    with FeatureSwitching
    with MockDraftTemplatesService
    with DraftTemplatesFixtures
     {

       implicit lazy val config: AppConfig = appConfig
       implicit val hc: HeaderCarrier = HeaderCarrier()
       implicit val fakeRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest("GET", "/"))
       implicit val msgs: Messages = messages(fakeRequest)
       lazy val view: RenameTemplateView = app.injector.instanceOf[RenameTemplateView]
       lazy val formProvider: RenameTemplateFormProvider = new RenameTemplateFormProvider()
       lazy val form: Form[String] = formProvider()

       val controller: RenameTemplateController = new RenameTemplateController(
         app.injector.instanceOf[MessagesControllerComponents],
         view,
         FakeSuccessAuthAction,
         new FakeDataRetrievalAction(Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
         mockDraftTemplatesService,
         formProvider
       )

       enable(TemplatesLink)



  "Rename Template Controller" when {

    "calling .onPageLoad()" when {

      "user can't view rename template" should {
        "redirect" in {
          disable(TemplatesLink)

          val result: Future[Result] = controller.onPageLoad(testErn, "1")(fakeRequest)

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.routes.AccountHomeController.viewAccountHome(testErn).url)
        }
      }

      "user can view draft templates" should {

        "show the correct view" in {
          enable(TemplatesLink)

          MockDraftTemplatesService.getTemplate(testErn, "1").returns(Future.successful(Some(fullTemplate)))

          val result: Future[Result] = controller.onPageLoad(testErn, "1")(fakeRequest)

          status(result) mustBe OK
          contentAsString(result) mustBe view(
            form,
            controllers.draftTemplates.routes.RenameTemplateController.onSubmit(testErn, "1"),
            fullTemplate
          ).toString()
        }


      }
    }

    "calling .onSubmit()" when {
      "form validation fails" must {

        "render a BadRequest with errors" when {

          "new name is not unique" in {
            MockDraftTemplatesService.getTemplate(testErn, "1").returns(Future.successful(Some(fullTemplate)))
            MockDraftTemplatesService
              .doesExist(testErn, "my name")
              .returns(Future.successful(true))

            val request = FakeRequest(POST, "/")
            val result = controller.onSubmit(testErn, "1")(request.withFormUrlEncodedBody("value" -> "my name"))

            val form = formProvider()
              .fill("my name")
              .withError(FormError("value", Seq("renameTemplate.error.notUnique")))

            status(result) mustEqual BAD_REQUEST
            contentAsString(result) mustEqual view(
              form,
              controllers.draftTemplates.routes.RenameTemplateController.onSubmit(testErn, "1"),
              fullTemplate
            ).toString
          }
        }
      }
    }
  }
}
