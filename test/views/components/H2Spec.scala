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

package views.components

import base.SpecBase
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.Html
import views.html.components.h2

class H2Spec extends SpecBase {

  implicit val msgs: Messages = messages(FakeRequest())

  trait Test {
    def id: Option[String] = None


    lazy val h2: h2 = app.injector.instanceOf[h2]
    lazy val html: Html = h2("some content", id = id)
    lazy val document: Document = Jsoup.parse(html.toString)
  }

  "h2" must {
    "have an id" when {
      "an id is supplied" in new Test {
        override def id: Option[String] = Some("an-id")

        document.select("h2").hasAttr("id") mustBe true
        document.select("h2").attr("id") mustBe "an-id"
      }
    }
    "have no id" when {
      "an id is not supplied" in new Test {
        override def id: Option[String] = None

        document.select("h2").hasAttr("id") mustBe false
      }
    }
  }

}
