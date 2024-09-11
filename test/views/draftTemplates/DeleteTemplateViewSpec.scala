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

package views.draftTemplates

import base.ViewSpecBase
import config.AppConfig
import fixtures.DraftTemplatesFixtures
import fixtures.messages.draftTemplates.DeleteTemplateMessages.English
import forms.draftTemplates.DeleteTemplateFormProvider
import models.movementScenario.MovementScenario
import models.requests.DataRequest
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.draftTemplates.DeleteTemplateView
import views.{BaseSelectors, ViewBehaviours}

class DeleteTemplateViewSpec extends ViewSpecBase with ViewBehaviours with DraftTemplatesFixtures {
  lazy val view: DeleteTemplateView = app.injector.instanceOf[DeleteTemplateView]
  lazy val formProvider: DeleteTemplateFormProvider = new DeleteTemplateFormProvider

  implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  object Selectors extends BaseSelectors

  "view" must {
    Seq(English) foreach { messagesForLanguage =>
      implicit val msgs = messages(Seq(messagesForLanguage.lang))
      implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

      s"render the view when being rendered in lang code of '${messagesForLanguage.lang.code}'" must {

        val testTemplate = createTemplate(testErn, "a template id", "a template name", MovementScenario.UkTaxWarehouse.GB, None)

        implicit val doc = asDocument(view(formProvider(), testTemplate))

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> testTemplate.templateName,
          Selectors.h1 -> messagesForLanguage.h1,
          Selectors.radioButton(1) -> messagesForLanguage.radioButton1,
          Selectors.radioButton(2) -> messagesForLanguage.radioButton2,
          Selectors.button -> messagesForLanguage.button
        ))
      }
    }
  }
}
