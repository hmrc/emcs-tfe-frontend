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

package views

import base.ViewSpecBase
import fixtures.DraftTemplatesFixtures
import fixtures.messages.RenameTemplateMessages.English
import forms.draftTemplates.RenameTemplateFormProvider
import models.requests.DataRequest
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.draftTemplates.RenameTemplateView

class RenameTemplateViewSpec extends ViewSpecBase with ViewBehaviours with DraftTemplatesFixtures {
  lazy val view: RenameTemplateView = app.injector.instanceOf[RenameTemplateView]
  implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  object Selectors extends BaseSelectors {
    val tableSelector = "#main-content table"
  }

  "view" must {

    Seq(English) foreach { messagesForLanguage =>

      implicit val msgs = messages(Seq(messagesForLanguage.lang))
      val form = app.injector.instanceOf[RenameTemplateFormProvider].apply()


      s"render the view when being rendered in lang code of '${messagesForLanguage.lang.code}'" when {
        implicit val doc = asDocument(view(form, controllers.draftTemplates.routes.RenameTemplateController.onSubmit("GBRC123456789", "1"), fullTemplate))

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> messagesForLanguage.h2("my name"),
          Selectors.label("value") -> messagesForLanguage.heading,
          Selectors.hint -> messagesForLanguage.hint,
          Selectors.button -> messagesForLanguage.confirmAndSave
        ))
      }
    }
  }
}
