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
import fixtures.GetMovementResponseFixtures
import fixtures.messages.ViewMovementMessages.English
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.Html
import views.html.viewMovement.PrintMovementView

class PrintMovementViewSpec extends ViewSpecBase with ViewBehaviours with GetMovementResponseFixtures {

  lazy val view: PrintMovementView = app.injector.instanceOf[PrintMovementView]

  "PrintMovement view" must {

    Seq(English) foreach { messagesForLanguage =>
      implicit val fakeRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest("GET", "/"), ern = "GBRC123456789")
      implicit val msgs: Messages = messages(fakeRequest)
      implicit val doc: Document = Jsoup.parse(
        view(
          testErn,
          testArc,
          getMovementResponseModel,
          Html("")
        ).toString()
      )

      "display the correct content" in {
        doc.select(BaseSelectors.title).text() mustBe messagesForLanguage.printTitle(testArc)
        doc.select(BaseSelectors.h1).text() mustBe messagesForLanguage.printHeading(testArc)
        doc.select("button:nth-of-type(1)").text() mustBe messagesForLanguage.printButton
        doc.select("button:nth-of-type(2)").text() mustBe messagesForLanguage.printButton
      }
    }

  }
}
