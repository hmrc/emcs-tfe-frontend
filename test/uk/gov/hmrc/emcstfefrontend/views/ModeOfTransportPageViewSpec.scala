/*
 * Copyright 2022 HM Revenue & Customs
 *
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
