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
import play.api.i18n.MessagesApi
import play.twirl.api.Html
import uk.gov.hmrc.emcstfefrontend.fixtures.messages.{BaseEnglish, BaseMessages, BaseWelsh}
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.emcstfefrontend.views.html.components.link

class LinkSpec extends UnitSpec {

  abstract class TestFixture(implicit baseMessages: BaseMessages) {

    val url: String = "/foo/bar"
    val messageKey: String = "link.text"
    val id: Option[String] = None
    val isExternal: Boolean = false
    val button: Boolean = false
    val classes: String = "govuk-link"
    val hiddenContent: Option[String] = None
    val midParagraph: Boolean = false
    val endOfParagraph: Boolean = false

    lazy val link: link = app.injector.instanceOf[link]
    lazy val messagesApi = app.injector.instanceOf[MessagesApi]

    implicit lazy val messages = messagesApi.preferred(Seq(baseMessages.lang))

    lazy val html: Html = link(
      url,
      messageKey,
      id,
      isExternal,
      button,
      classes,
      hiddenContent,
      midParagraph,
      endOfParagraph
    )

    lazy val document: Document = Jsoup.parse(html.toString)
    lazy val renderedLink = document.select("a")
  }

  "link" when {

    Seq(BaseEnglish, BaseWelsh) foreach { implicit baseMessages =>

      s"being rendered for '${baseMessages.lang.code}' language code" when {

        "rendered with URL and MessageKey" must {

          "output the expected HTML" in new TestFixture {

            renderedLink.attr("href") shouldBe url
            renderedLink.text() shouldBe messages(messageKey)
            renderedLink.hasClass("govuk-link") shouldBe true
          }
        }

        "has an id" must {

          "output the expected HTML" in new TestFixture {

            override val id: Option[String] = Some("myId")

            renderedLink.attr("id") shouldBe id.get
          }
        }

        "is an external link" must {

          "output the expected HTML" in new TestFixture {

            override val isExternal = true

            renderedLink.text() shouldBe s"${messages(messageKey)} ${baseMessages.opensInNewTab}"
            renderedLink.attr("target") shouldBe "_blank"
            renderedLink.attr("rel") shouldBe "noopener noreferrer"
          }
        }

        "is a to be output as button" must {

          "output the expected HTML" in new TestFixture {

            override val button: Boolean = true

            renderedLink.attr("role") shouldBe "button"
            renderedLink.attr("data-module") shouldBe "govuk-button"
          }
        }

        "has hidden content" must {

          "output the expected HTML" in new TestFixture {

            override val hiddenContent: Option[String] = Some("Accessible Message")

            val span = renderedLink.select("span")

            span.hasClass("govuk-visually-hidden") shouldBe true
            span.text() shouldBe hiddenContent.get
          }
        }

        "is mid sentence" must {

          "output a full stop with a space at the end" in new TestFixture {

            override val midParagraph: Boolean = true

            html.toString().takeRight(2) shouldBe ". "
          }
        }

        "is end of sentence" must {

          "output a full stop with no space at the end" in new TestFixture {

            override val endOfParagraph: Boolean = true

            html.toString().takeRight(1) shouldBe "."
          }
        }
      }
    }
  }
}
