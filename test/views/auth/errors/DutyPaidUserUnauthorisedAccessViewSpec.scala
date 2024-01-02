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

package views.auth.errors

import base.ViewSpecBase
import fixtures.messages.DutyPaidUnauthorisedMessages.English
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.{BaseSelectors, ViewBehaviours}
import views.html.auth.errors.DutyPaidUserUnauthorisedAccessView

class DutyPaidUserUnauthorisedAccessViewSpec extends ViewSpecBase with ViewBehaviours with BaseSelectors {
  lazy val view: DutyPaidUserUnauthorisedAccessView = app.injector.instanceOf[DutyPaidUserUnauthorisedAccessView]
  lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  implicit lazy val messages: Messages = messagesApi.preferred(FakeRequest())

  "The view" must {

    "render the correct content" when {

      implicit val doc: Document = Jsoup.parse(view()(request, messages(request)).toString())

      behave like pageWithExpectedElementsAndMessages(
        Seq(
          title -> English.title,
          h1 -> English.heading,
          p(1) -> English.message1,
          p(2) -> English.message2,
          p(3) -> English.message3
        )
      )
    }

  }
}
