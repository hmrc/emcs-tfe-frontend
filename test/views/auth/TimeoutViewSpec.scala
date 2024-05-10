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

package views.auth

import base.ViewSpecBase
import fixtures.messages.TimeoutMessages
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.auth.TimeoutView
import views.{BaseSelectors, ViewBehaviours}

class TimeoutViewSpec extends ViewSpecBase with ViewBehaviours {

  object Selectors extends BaseSelectors

  val view: TimeoutView = app.injector.instanceOf[TimeoutView]

  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "TimeoutView" when {

    Seq(TimeoutMessages.English) foreach { viewMessages =>

      implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(viewMessages.lang))

      s"being rendered for ${viewMessages.lang.code}" must {

        implicit val doc = asDocument(view())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> viewMessages.titleHelper(viewMessages.heading),
          Selectors.h1 -> viewMessages.heading,
          Selectors.button -> viewMessages.signIn
        ))
      }
    }
  }

}
