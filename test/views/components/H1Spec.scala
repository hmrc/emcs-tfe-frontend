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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import support.UnitSpec
import views.html.components.h1

class H1Spec extends UnitSpec {

  trait Test {
    def id: Option[String] = None


    lazy val h1: h1 = app.injector.instanceOf[h1]
    lazy val html: Html = h1(Html("some content"), id = id)
    lazy val document: Document = Jsoup.parse(html.toString)
  }

  "h1" should {
    "have an id" when {
      "an id is supplied" in new Test {
        override def id: Option[String] = Some("an-id")

        document.select("h1").hasAttr("id") shouldBe true
        document.select("h1").attr("id") shouldBe "an-id"
      }
    }
    "have no id" when {
      "an id is not supplied" in new Test {
        override def id: Option[String] = None

        document.select("h1").hasAttr("id") shouldBe false
      }
    }
  }

}