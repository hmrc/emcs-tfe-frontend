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
import models.messages.MessagesSortingSelectOption
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import viewmodels.helpers.messages.ViewAllMessagesTableHelper
import views.html.components.table
import views.html.messages.ViewAllMessages
import views.{BaseSelectors, ViewBehaviours}


class ViewAllMessagesViewSpec extends ViewSpecBase with ViewBehaviours with MessagesFixtures {

  object Selectors extends BaseSelectors {
    val sortBySelectOption = (i: Int) => s"#sortBy > option:nth-child($i)"
    val sortButton = "#sortBySubmit"

    val messageRow = (i: Int) => s"#main-content > div > div > table > tbody > tr:nth-child($i) > th > a"
    val messageHintRow = (i: Int) => s"#main-content > div > div > table > tbody > tr:nth-child($i) > th > p"
    val statusRow = (i: Int) => s"#main-content > div > div > table > tbody > tr:nth-child($i) > td:nth-child(2) > strong"
    val dateOfMessageRow = (i: Int) => s"#main-content > div > div > table > tbody > tr:nth-child($i) > td:nth-child(3)"
    val actionRow = (i: Int) => s"#main-content > div > div > table > tbody > tr:nth-child($i) > td:nth-child(4) > a"
  }

  implicit val fakeRequest = FakeRequest("GET", "/messages")

  implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

  lazy val view: ViewAllMessages = app.injector.instanceOf[ViewAllMessages]
  lazy val helper: ViewAllMessagesTableHelper = app.injector.instanceOf[ViewAllMessagesTableHelper]

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))

  lazy val table: table = app.injector.instanceOf[table]

  def asDocument()(implicit messages: Messages): Document = Jsoup.parse(view(
    sortSelectItems = MessagesSortingSelectOption.constructSelectItems(),
    allMessages = getMessageResponse.messagesData.messages,
    pageIndex = 1
  ).toString())


  "The ViewAllMessagesPage view" when {

    s"being rendered should show the correct content" when {

      implicit val doc: Document = asDocument()

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.label("sortBy") -> English.sortByLabel,
        Selectors.sortBySelectOption(1) -> English.sortMessageTypeA,
        Selectors.sortBySelectOption(2) -> English.sortMessageTypeD,
        Selectors.sortBySelectOption(3) -> English.sortDateReceivedA,
        Selectors.sortBySelectOption(4) -> English.sortDateReceivedD,
        Selectors.sortBySelectOption(5) -> English.sortArcA,
        Selectors.sortBySelectOption(6) -> English.sortArcD,
        Selectors.sortBySelectOption(7) -> English.sortReadIndicatorA,
        Selectors.sortBySelectOption(8) -> English.sortReadIndicatorD,
        Selectors.sortButton -> English.sortByButton,
        Selectors.messageRow(1) -> "Report of receipt successful submission",
        Selectors.messageHintRow(1) -> "ARC1001",
        Selectors.statusRow(1) -> "UNREAD",
        Selectors.dateOfMessageRow(1) -> "5 January 2024",
        Selectors.actionRow(1) -> "Delete",
        Selectors.messageRow(2) -> "Report of receipt",
        Selectors.messageHintRow(2) -> "LRN1002",
        Selectors.statusRow(2) -> "READ",
        Selectors.dateOfMessageRow(2) -> "6 January 2024",
        Selectors.actionRow(2) -> "Delete"
      ))
    }
  }

}
