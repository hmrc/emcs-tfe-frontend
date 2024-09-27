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
import forms.draftTemplates.ConfirmTemplateFormProvider
import mocks.connectors.MockCreateDraftMovementConnector
import mocks.services.{MockDraftTemplatesService, MockGetCnCodeInformationService}
import models.common.UnitOfMeasure.Litres20
import models.draftTemplates.TemplateItem
import models.requests.DataRequest
import models.response.emcsTfe.draftTemplate.DraftMovementCreatedResponse
import models.response.referenceData.CnCodeInformation
import play.api.data.{Form, FormError}
import play.api.http.Status.{BAD_REQUEST, OK, SEE_OTHER}
import play.api.i18n.Messages
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, redirectLocation, status}
import uk.gov.hmrc.http.HeaderCarrier
import views.html.draftTemplates.ConfirmTemplateView

import scala.concurrent.Future

class ConfirmTemplateControllerSpec extends SpecBase
  with FakeAuthAction
  with FeatureSwitching
  with MockDraftTemplatesService
  with MockGetCnCodeInformationService
  with MockCreateDraftMovementConnector
  with DraftTemplatesFixtures
{

  implicit lazy val config: AppConfig = appConfig
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val fakeRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest("GET", "/"))
  implicit val msgs: Messages = messages(fakeRequest)
  lazy val view: ConfirmTemplateView = app.injector.instanceOf[ConfirmTemplateView]
  lazy val formProvider: ConfirmTemplateFormProvider = new ConfirmTemplateFormProvider()
  lazy val form: Form[Boolean] = formProvider()

  lazy val testTemplate = fullTemplate.copy(templateId = testTemplateId)

  trait Test {
    val controller: ConfirmTemplateController = new ConfirmTemplateController(
      app.injector.instanceOf[MessagesControllerComponents],
      FakeSuccessAuthAction,
      new FakeDataRetrievalAction(Some(testMinTraderKnownFacts), Some(testMessageStatistics)),
      mockDraftTemplatesService,
      mockGetCnCodeInformationService,
      view,
      formProvider
    )

    val templateItems = (testTemplate.data \ "items" \ "addedItems").as[Seq[TemplateItem]]
    val enrichedTemplateItems = templateItems.map { item =>
      (item, CnCodeInformation(item.itemCommodityCode, "", item.itemExciseProductCode, "", Litres20))
    }
  }

  "GET onPageLoad" should {
      "redirect" when {
        "the templates feature is disabled" in new Test {
          disable(TemplatesLink)

          MockDraftTemplatesService.getTemplate(testErn, testTemplateId)
            .returns(Future.successful(Some(testTemplate)))

          val result = controller.onPageLoad(testErn, testTemplateId)(fakeRequest)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.routes.AccountHomeController.viewAccountHome(testErn).url)
        }
      }

      "return OK and the correct view for a GET" in new Test {
        enable(TemplatesLink)

        MockDraftTemplatesService.getTemplate(testErn, testTemplateId)
          .returns(Future.successful(Some(testTemplate)))

        MockGetCnCodeInformationService.getCnCodeInformationForTemplateItems(templateItems)
          .returns(Future.successful(enrichedTemplateItems))

        val result = controller.onPageLoad(testErn, testTemplateId)(fakeRequest)
        status(result) mustBe OK
        contentAsString(result) mustBe view(form, testTemplate, enrichedTemplateItems)(fakeRequest, msgs, config).toString
      }
  }

  "POST onSubmit" when {

    "the templates feature is disabled" should {

      "redirect to the account home page" in new Test {
        disable(TemplatesLink)

        MockDraftTemplatesService.getTemplate(testErn, testTemplateId)
          .returns(Future.successful(Some(testTemplate)))

        val result = controller.onSubmit(testErn, testTemplateId)(fakeRequest)
        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.AccountHomeController.viewAccountHome(testErn).url)
      }

    }

    "the templates feature is enabled" should {

      "return a bad request" when {

        "the user has not selected an option" in new Test {
          enable(TemplatesLink)

          MockDraftTemplatesService.getTemplate(testErn, testTemplateId)
            .returns(Future.successful(Some(testTemplate)))

          MockGetCnCodeInformationService.getCnCodeInformationForTemplateItems(templateItems)
            .returns(Future.successful(enrichedTemplateItems))

          val request = FakeRequest("POST", "/")
          val result = controller.onSubmit(testErn, testTemplateId)(request)
          status(result) mustBe BAD_REQUEST
          contentAsString(result) mustBe view(form.withError(FormError("value", Seq("confirmTemplate.error.required"))), testTemplate, enrichedTemplateItems)(fakeRequest, msgs, config).toString
        }

      }

      "redirect to the view all templates view" when {
        "the user has said no to confirm" in new Test {
          enable(TemplatesLink)

          MockDraftTemplatesService.getTemplate(testErn, testTemplateId)
            .returns(Future.successful(Some(testTemplate)))

          val request = FakeRequest("POST", "/").withFormUrlEncodedBody("value" -> "false")
          val result = controller.onSubmit(testErn, testTemplateId)(request)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(controllers.draftTemplates.routes.ViewAllTemplatesController.onPageLoad(testErn, None).url)
        }
      }

      "redirect to CAM deferred movement change page" when {
        "The user has said yes to confirm" in new Test {
          enable(TemplatesLink)

          MockDraftTemplatesService.getTemplate(testErn, testTemplateId)
            .returns(Future.successful(Some(testTemplate)))

          MockDraftTemplatesService.createDraftMovement(testErn, testTemplateId)
            .returns(Future.successful(DraftMovementCreatedResponse(createdDraftId = testDraftId)))

          val request = FakeRequest("POST", "/").withFormUrlEncodedBody("value" -> "true")
          val result = controller.onSubmit(testErn, testTemplateId)(request)
          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(appConfig.emcsTfeChangeDraftDeferredMovementUrl(testErn, testDraftId))
        }
      }
    }
  }
}
