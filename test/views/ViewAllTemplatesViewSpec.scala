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
import fixtures.messages.ViewAllTemplatesMessages.English
import models.requests.DataRequest
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.ViewAllTemplatesView

class ViewAllTemplatesViewSpec extends ViewSpecBase with ViewBehaviours {
  lazy val view: ViewAllTemplatesView = app.injector.instanceOf[ViewAllTemplatesView]
  implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  object Selectors extends BaseSelectors {
    def noTemplates(i: Int) = s"#main-content p:nth-of-type($i)"
  }

  "view" must {

    Seq(English) foreach { messagesForLanguage =>

      implicit val msgs = messages(Seq(messagesForLanguage.lang))

      s"render the view when being rendered in lang code of '${messagesForLanguage.lang.code}'" when {
        "list of templates is empty" when {
          implicit val doc = asDocument(view(Seq.empty))

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.noTemplates(1) -> messagesForLanguage.noTemplatesP1,
            Selectors.noTemplates(2) -> messagesForLanguage.noTemplatesP2
          ))
        }
      }

    }
  }
}
