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

package uk.gov.hmrc.emcstfefrontend.views

/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import play.api.test.Helpers.contentAsString
import play.twirl.api.Html
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.emcstfefrontend.views.html.ModeOfTransportPage

class ModeOfTransportPageViewSpec extends UnitSpec {
  val modeOfTransportPage: ModeOfTransportPage = app.injector.instanceOf[ModeOfTransportPage]
  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  "The ModeOfTransportPage view" should {

    val heading =  "How will this movement be transported?"
    val testData = Seq(("1", "test selection"),("0", "Other"))
    lazy val page: Html = modeOfTransportPage(testData)(FakeRequest(), implicitly)
    lazy val document: Document = Jsoup.parse(contentAsString(page))

    s"have the heading $heading" in {
      document.select("h1").text() shouldBe heading
    }

    s"have the correct radio options" in {
      document.getElementsByClass("govuk-radios__label").first().text shouldBe "test selection"
      document.getElementsByClass("govuk-radios__item").first().child(0).attr("value") shouldBe "1"
      document.getElementsByClass("govuk-radios__label").last().text shouldBe "Other"
      document.getElementsByClass("govuk-radios__item").last().child(0).attr("value") shouldBe "0"
    }
  }
}
