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

package uk.gov.hmrc.emcstfefrontend.views.components

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.twirl.api.Html
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.emcstfefrontend.views.html.components.span

class SpanSpec extends UnitSpec {

  trait Test {
    def id: Option[String] = None

    def ariaHidden: Option[Boolean] = None

    lazy val span: span = app.injector.instanceOf[span]
    lazy val html: Html = span("some content", id = id, ariaHidden = ariaHidden)
    lazy val document: Document = Jsoup.parse(html.toString)
  }

  "span" should {
    "have aria-hidden properties" when {
      "aria-hidden is set to true" in new Test {
        override def ariaHidden: Option[Boolean] = Some(true)

        document.select("span").hasAttr("aria-hidden") shouldBe true
        document.select("span").attr("aria-hidden") shouldBe "true"
      }
      "aria-hidden is set to false" in new Test {
        override def ariaHidden: Option[Boolean] = Some(false)

        document.select("span").hasAttr("aria-hidden") shouldBe true
        document.select("span").attr("aria-hidden") shouldBe "false"
      }
    }
    "have no aria-hidden properties" when {
      "aria-hidden is not supplied" in new Test {
        override def ariaHidden: Option[Boolean] = None

        document.select("span").hasAttr("aria-hidden") shouldBe false
      }
    }

    "have an id" when {
      "an id is supplied" in new Test {
        override def id: Option[String] = Some("an-id")

        document.select("span").hasAttr("id") shouldBe true
        document.select("span").attr("id") shouldBe "an-id"
      }
    }
    "have no id" when {
      "an id is not supplied" in new Test {
        override def id: Option[String] = None

        document.select("span").hasAttr("id") shouldBe false
      }
    }
  }

}
