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

package views.messages

import base.ViewSpecBase
import controllers.messages.routes.ViewAllMessagesController
import fixtures.MessagesFixtures
import fixtures.messages.ViewAllMessagesMessages.English
import forms.DeleteMessageFormProvider
import models.messages.MessagesSearchOptions
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.{Page, ViewAllMessagesPage, ViewMessagePage}
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.messages.DeleteMessageView
import views.{BaseSelectors, ViewBehaviours}

class DeleteMessageViewSpec extends ViewSpecBase with ViewBehaviours with MessagesFixtures {

  object Selectors extends BaseSelectors {
    private val summaryList = "#main-content > div > div > form > dl"
    val header = "#main-content > div > div > h1"

    val summaryListMessageKey = summaryListChildKey(1)
    val summaryListMessageValue = summaryListChildValue(1)

    val summaryListArcKey = summaryListChildKey(2)
    val summaryListArcValue = summaryListChildValue(2)

    val summaryListLrnKey = summaryListChildKey(3)
    val summaryListLrnValue = summaryListChildValue(3)

    val summaryListDateKey = summaryListChildKey(4)
    val summaryListDateValue = summaryListChildValue(4)

    private val form = "#main-content > div > div > form > .govuk-form-group > fieldset"
    val formMessage = s" $form > legend"

    val yesRadio = s"$form > div > div > label[for=value]"
    val noRadio = s"$form > div > div > label[for=value-no]"

    val confirmButton = "#main-content > div > div > form > .govuk-button-group > :nth-child(1)"
    val returnToMessages = "#main-content > div > div > form > .govuk-button-group > :nth-child(2)"

    private def summaryListChildKey(nthChild: Int): String = s"$summaryList > :nth-child($nthChild) .govuk-summary-list__key"

    private def summaryListChildValue(nthChild: Int): String = s"$summaryList > :nth-child($nthChild) .govuk-summary-list__value"
  }

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/message")

  implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

  lazy val view: DeleteMessageView = app.injector.instanceOf[DeleteMessageView]
  lazy val formProvider: DeleteMessageFormProvider = new DeleteMessageFormProvider

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))

  def viewAsDocument(previousPage: Page): Document = {
    Jsoup.parse(
      view(
        message = message1,
        form = formProvider(),
        returnToMessagesUrl = ViewAllMessagesController.onPageLoad(testErn, MessagesSearchOptions()).url,
        previousPage
      ).toString()
    )
  }

  val expectedElementsAndMessages = Seq(
    Selectors.header -> "Delete message",
    Selectors.summaryListMessageKey -> "Message",
    Selectors.summaryListMessageValue -> "Alert or rejection",
    Selectors.summaryListArcKey -> "Administrative reference code",
    Selectors.summaryListArcValue -> "ARC1001",
    Selectors.summaryListLrnKey -> "Local reference number",
    Selectors.summaryListLrnValue -> "LRN1001",
    Selectors.summaryListDateKey -> "Date and time received",
    Selectors.summaryListDateValue -> "5 January 2024 at 12:00 am",
    Selectors.formMessage -> "Are you sure you want to delete this message?",
    Selectors.yesRadio -> "Yes, delete this message",
    Selectors.confirmButton -> "Confirm",
    Selectors.returnToMessages -> "Return to messages"
  )

  "DeleteMessageView" when {

    val returnUrl = "/emcs/account/trader/GBWKTestErn/messages?sortBy=dateReceivedD&index=1"

    "render correctly, if the user has arrived from the ViewAllMessagesPage" must {
      implicit val document = viewAsDocument(previousPage = ViewAllMessagesPage)

      document.select(Selectors.returnToMessages).attr("href") mustBe returnUrl

      behave like pageWithExpectedElementsAndMessages(
        expectedElementsAndMessages ++ Seq(Selectors.noRadio -> "No, return to all messages")
      )
    }

    "render correctly, if the user has arrived from the ViewMessagePage" must {
      implicit val document = viewAsDocument(previousPage = ViewMessagePage)

      document.select(Selectors.returnToMessages).attr("href") mustBe returnUrl

      behave like pageWithExpectedElementsAndMessages(
        expectedElementsAndMessages ++ Seq(Selectors.noRadio -> "No, return to message")
      )

    }
  }

}
