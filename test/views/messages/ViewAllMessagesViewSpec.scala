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

package views.messages

import base.ViewSpecBase
import fixtures.MessagesFixtures
import fixtures.messages.ViewAllMessagesMessages.English
import models.messages.{MessagesSearchOptions, MessagesSortingSelectOption}
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.messages.{MessagesPaginationHelper, ViewAllMessagesTableHelper}
import views.html.components.table
import views.html.messages.ViewAllMessagesView
import views.{BaseSelectors, ViewBehaviours}

class ViewAllMessagesViewSpec extends ViewSpecBase with ViewBehaviours with MessagesFixtures {

  object Selectors extends BaseSelectors {
    val sortBySelectOption = (i: Int) => s"#sortBy > option:nth-child($i)"
    val sortButton = "#sortBySubmit"

    val tableSelector = "#main-content > div > div > div > table"

    val messageRow = (i: Int) => s"$tableSelector > tbody > tr:nth-child($i) > th > a"
    val messageHintRow = (i: Int) => s"$tableSelector > tbody > tr:nth-child($i) > th > p"
    val statusRow = (i: Int) => s"$tableSelector > tbody > tr:nth-child($i) > td:nth-child(2) > strong"
    val dateOfMessageRow = (i: Int) => s"$tableSelector  > tbody > tr:nth-child($i) > td:nth-child(3)"
    val actionRow = (i: Int) => s"$tableSelector > tbody > tr:nth-child($i) > td:nth-child(4) > a"

    val paginationLink = (i: Int) => s"#main-content nav > ul > li:nth-child($i) > a"
    val nextLink = ".govuk-pagination__next a"
  }

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/messages")

  implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

  lazy val view: ViewAllMessagesView = app.injector.instanceOf[ViewAllMessagesView]
  lazy val helper: ViewAllMessagesTableHelper = app.injector.instanceOf[ViewAllMessagesTableHelper]
  lazy val paginationHelper = app.injector.instanceOf[MessagesPaginationHelper]

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))

  lazy val table: table = app.injector.instanceOf[table]

  def asDocument(totalPages: Int, maybeDeletedMessageDescriptionKey: Option[String] = None)
                (implicit messages: Messages, dataRequest: DataRequest[_]): Document =
    Jsoup.parse(view(
      sortSelectItems = MessagesSortingSelectOption.constructSelectItems(),
      allMessages = getMessageResponse.messages,
      totalNumberOfPages = totalPages,
      searchOptions = MessagesSearchOptions(),
      maybeDeletedMessageDescriptionKey = maybeDeletedMessageDescriptionKey
    )(dataRequest, implicitly).toString())


  "when being rendered with no messages" should {

    "show only the no messages text (count of all messages is 0)" when {

      implicit val doc: Document = asDocument(1)(messages, dr.copy(messageStatistics = Some(testMessageStatistics.copy(countOfAllMessages = 0))))

      val expectedElementsForNoMessages = Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.p(1) -> English.noMessages
      )

      behave like pageWithExpectedElementsAndMessages(expectedElementsForNoMessages)

      behave like pageWithElementsNotPresent(Seq(
        Selectors.label("sortBy"),
        Selectors.sortBySelectOption(1),
        Selectors.sortBySelectOption(2),
        Selectors.sortBySelectOption(3),
        Selectors.sortBySelectOption(4),
        Selectors.sortBySelectOption(5),
        Selectors.sortBySelectOption(6),
        Selectors.sortBySelectOption(7),
        Selectors.sortBySelectOption(8),
        Selectors.sortButton,
        Selectors.paginationLink(1)
      ))
    }
  }

  "when being rendered with no pagination" should {

    val expectedElementsForNoPagination = Seq(
      Selectors.title -> English.title,
      Selectors.h1 -> English.heading,

      Selectors.label("sortBy") -> English.sortByLabel,
      Selectors.sortBySelectOption(1) -> English.sortMessageTypeA,
      Selectors.sortBySelectOption(2) -> English.sortMessageTypeD,
      Selectors.sortBySelectOption(3) -> English.sortDateReceivedA,
      Selectors.sortBySelectOption(4) -> English.sortDateReceivedD,
      Selectors.sortBySelectOption(5) -> English.sortIdentifierA,
      Selectors.sortBySelectOption(6) -> English.sortIdentifierD,
      Selectors.sortBySelectOption(7) -> English.sortReadIndicatorA,
      Selectors.sortBySelectOption(8) -> English.sortReadIndicatorD,
      Selectors.sortButton -> English.sortByButton,

      Selectors.messageRow(1) -> "Alert or rejection received",
      Selectors.messageHintRow(1) -> "ARC1001",
      Selectors.statusRow(1) -> "UNREAD",
      Selectors.dateOfMessageRow(1) -> "5 January 2024",
      Selectors.actionRow(1) -> "Delete",

      Selectors.messageRow(2) -> "Error with report of receipt",
      Selectors.messageHintRow(2) -> "LRN1001",
      Selectors.statusRow(2) -> "READ",
      Selectors.dateOfMessageRow(2) -> "6 January 2024",
      Selectors.actionRow(2) -> "Delete"
    )

    "show the correct sort-by and table of messages" when {

      implicit val doc: Document = asDocument(1)

      behave like pageWithExpectedElementsAndMessages(expectedElementsForNoPagination)
    }

    "show the success banner" when {
      implicit val doc: Document = asDocument(1, Some("messages.IE871.true.0.description"))
      val successNotificationBanner = Seq(
        Selectors.notificationBannerTitle -> "Success",
        Selectors.notificationBannerContent -> "Message deleted: Explanation for a shortage or excess submitted successfully"
      )

      behave like pageWithExpectedElementsAndMessages(expectedElementsForNoPagination ++ successNotificationBanner)
    }
  }

  "when being rendered with pagination" should {

    "show the correct sort-by and table of messages" when {

      implicit val doc: Document = asDocument(3)

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,

        Selectors.label("sortBy") -> English.sortByLabel,
        Selectors.sortBySelectOption(1) -> English.sortMessageTypeA,
        Selectors.sortBySelectOption(2) -> English.sortMessageTypeD,
        Selectors.sortBySelectOption(3) -> English.sortDateReceivedA,
        Selectors.sortBySelectOption(4) -> English.sortDateReceivedD,
        Selectors.sortBySelectOption(5) -> English.sortIdentifierA,
        Selectors.sortBySelectOption(6) -> English.sortIdentifierD,
        Selectors.sortBySelectOption(7) -> English.sortReadIndicatorA,
        Selectors.sortBySelectOption(8) -> English.sortReadIndicatorD,
        Selectors.sortButton -> English.sortByButton,

        Selectors.messageRow(1) -> "Alert or rejection received",
        Selectors.messageHintRow(1) -> "ARC1001",
        Selectors.statusRow(1) -> "UNREAD",
        Selectors.dateOfMessageRow(1) -> "5 January 2024",
        Selectors.actionRow(1) -> "Delete",

        Selectors.messageRow(2) -> "Error with report of receipt",
        Selectors.messageHintRow(2) -> "LRN1001",
        Selectors.statusRow(2) -> "READ",
        Selectors.dateOfMessageRow(2) -> "6 January 2024",
        Selectors.actionRow(2) -> "Delete"
      ))

      "have the correct pagination component displayed" in {
        doc.select(Selectors.paginationLink(1)).text() mustBe "1"
        doc.select(Selectors.paginationLink(1)).attr("href") mustBe "/emcs/account/trader/GBWKTestErn/messages?sortBy=dateReceivedD&index=1"
        doc.select(Selectors.paginationLink(2)).text() mustBe "2"
        doc.select(Selectors.paginationLink(2)).attr("href") mustBe "/emcs/account/trader/GBWKTestErn/messages?sortBy=dateReceivedD&index=2"
        doc.select(Selectors.paginationLink(3)).text() mustBe "3"
        doc.select(Selectors.paginationLink(3)).attr("href") mustBe "/emcs/account/trader/GBWKTestErn/messages?sortBy=dateReceivedD&index=3"
        doc.select(Selectors.nextLink).text() mustBe English.next
        doc.select(Selectors.nextLink).attr("href") mustBe "/emcs/account/trader/GBWKTestErn/messages?sortBy=dateReceivedD&index=2"
      }
    }
  }

  "render the nav links" in {
    val document = asDocument(1)
    val homeLink = document.select("#navigation-home-link")
    homeLink.text mustBe "Home"
  }


}
