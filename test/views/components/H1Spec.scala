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
import play.api.i18n.{Lang, Messages}
import play.twirl.api.Html
import views.html.components.h1

class H1Spec extends SpecBase {

  trait Test {
    val classes: String = ""
    val id: Option[String] = None
    val captionMsg: Option[String] = None
    val hiddenContent: Option[String] = None

    implicit val msgs: Messages = messages(Seq(Lang("en")))

    lazy val h1: h1 = app.injector.instanceOf[h1]
    lazy val html: Html = h1(classes, id, captionMsg, hiddenContent)(Html("some content"))
    lazy val document: Document = Jsoup.parse(html.toString)
  }

  "h1" must {
    "have an id when an id is supplied" in new Test {
      override val id: Option[String] = Some("an-id")

      document.select("h1").hasAttr("id") mustBe true
      document.select("h1").attr("id") mustBe "an-id"
    }

    "have no id when an id is not supplied" in new Test {
      override val id: Option[String] = None

      document.select("h1").hasAttr("id") mustBe false
    }

    "have a caption when caption is supplied" in new Test {
      override val captionMsg: Option[String] = Some("caption")

      document.select("h2").text() mustBe "caption"
    }

    "have no caption when caption is NOT supplied" in new Test {
      document.select("h2").size() mustBe 0
    }
  }

}
