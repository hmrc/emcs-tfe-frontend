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

package views

import base.SpecBase
import controllers.routes
import fixtures.MovementListFixtures
import fixtures.messages.ViewAllMovementsMessages.English
import forms.ViewAllMovementsFormProvider
import models.{MovementListSearchOptions, MovementSortingSelectOption}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.matchers.should.Matchers.convertToStringShouldWrapper
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination._
import viewmodels.MovementsListTableHelper
import views.html.components.table
import views.html.viewAllMovements.ViewAllMovements


class ViewAllMovementsViewSpec extends SpecBase with MovementListFixtures {

  object Selectors extends BaseSelectors {
    val headingLinkRow = (i: Int) => s"#main-content tr:nth-child($i) > td:nth-child(1) > h2 > a"

    val selectOption = (i: Int) => s"#sortBy > option:nth-child($i)"
    val consignorRow = (i: Int) => s"#main-content tr:nth-child($i) > td:nth-child(1) > ul > li:nth-child(1)"
    val dateOfDispatchRow = (i: Int) => s"#main-content tr:nth-child($i) > td:nth-child(1) > ul > li:nth-child(2)"
    val statusTagRow = (i: Int) => s"#main-content tr:nth-child($i) > td:nth-child(2) > strong"
    val paginationLink = (i: Int) => s"#main-content nav > ul > li:nth-child($i) > a"
    val previousLink = ".govuk-pagination__prev a"
    val nextLink = ".govuk-pagination__next a"
  }

  abstract class TestFixture(pagination: Option[Pagination])(implicit messages: Messages) {

    implicit val fakeRequest = FakeRequest("GET", "/movements")

    val view: ViewAllMovements = app.injector.instanceOf[ViewAllMovements]
    val helper: MovementsListTableHelper = app.injector.instanceOf[MovementsListTableHelper]
    val formProvider: ViewAllMovementsFormProvider = app.injector.instanceOf[ViewAllMovementsFormProvider]

    val table: table = app.injector.instanceOf[table]

    lazy val html: Html = view(
      form = formProvider(),
      action = routes.ViewAllMovementsController.onPageLoad("ern", MovementListSearchOptions()),
      ern = testErn,
      movements = getMovementListResponse.movements,
      selectItems = MovementSortingSelectOption.constructSelectItems(),
      pagination = pagination
    )

    lazy val document: Document = Jsoup.parse(html.toString)
  }

  "The ViewAllMovementsPage view" when {

    implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))

    s"being rendered for with no pagination should show the correct content" in new TestFixture(None) {

      document.title shouldBe English.title
      document.select(Selectors.h1).text() shouldBe English.heading

      document.select(Selectors.label("sortBy")).text() shouldBe English.sortByLabel
      document.select(Selectors.selectOption(1)).text() shouldBe English.sortArcAscending
      document.select(Selectors.selectOption(2)).text() shouldBe English.sortArcDescending
      document.select(Selectors.selectOption(3)).text() shouldBe English.sortNewest
      document.select(Selectors.selectOption(4)).text() shouldBe English.sortOldest
      document.select(Selectors.button).text() shouldBe English.sortByButton

      document.select(Selectors.headingLinkRow(1)).text() shouldBe getMovementListResponse.movements(0).arc
      document.select(Selectors.headingLinkRow(1)).attr("href") shouldBe getMovementListResponse.movements(0).viewMovementUrl(testErn).url
      document.select(Selectors.consignorRow(1)).text() shouldBe English.consignor(getMovementListResponse.movements(0).otherTraderID)
      document.select(Selectors.dateOfDispatchRow(1)).text() shouldBe English.dateOfDispatch(getMovementListResponse.movements(0).formattedDateOfDispatch)
      document.select(Selectors.statusTagRow(1)).text() shouldBe getMovementListResponse.movements(0).movementStatus

      document.select(Selectors.headingLinkRow(2)).text() shouldBe getMovementListResponse.movements(1).arc
      document.select(Selectors.headingLinkRow(2)).attr("href") shouldBe getMovementListResponse.movements(1).viewMovementUrl(testErn).url
      document.select(Selectors.consignorRow(2)).text() shouldBe English.consignor(getMovementListResponse.movements(1).otherTraderID)
      document.select(Selectors.dateOfDispatchRow(2)).text() shouldBe English.dateOfDispatch(getMovementListResponse.movements(1).formattedDateOfDispatch)
      document.select(Selectors.statusTagRow(2)).text() shouldBe getMovementListResponse.movements(1).movementStatus
    }

    s"being rendered for with pagination should show the correct content and pagination links" in new TestFixture(Some(Pagination(
      items = Some(Seq(
        PaginationItem(s"link-1", Some("1")),
        PaginationItem(s"link-2", Some("2")),
        PaginationItem(s"link-3", Some("3"))
      )),
      previous = Some(PaginationLink("previous-link")),
      next = Some(PaginationLink("next-link"))
    ))) {



      document.title shouldBe English.title
      document.select(Selectors.h1).text() shouldBe English.heading

      document.select(Selectors.label("sortBy")).text() shouldBe English.sortByLabel
      document.select(Selectors.selectOption(1)).text() shouldBe English.sortArcAscending
      document.select(Selectors.selectOption(2)).text() shouldBe English.sortArcDescending
      document.select(Selectors.selectOption(3)).text() shouldBe English.sortNewest
      document.select(Selectors.selectOption(4)).text() shouldBe English.sortOldest
      document.select(Selectors.button).text() shouldBe English.sortByButton

      document.select(Selectors.headingLinkRow(1)).text() shouldBe getMovementListResponse.movements(0).arc
      document.select(Selectors.headingLinkRow(1)).attr("href") shouldBe getMovementListResponse.movements(0).viewMovementUrl(testErn).url
      document.select(Selectors.consignorRow(1)).text() shouldBe English.consignor(getMovementListResponse.movements(0).otherTraderID)
      document.select(Selectors.dateOfDispatchRow(1)).text() shouldBe English.dateOfDispatch(getMovementListResponse.movements(0).formattedDateOfDispatch)
      document.select(Selectors.statusTagRow(1)).text() shouldBe getMovementListResponse.movements(0).movementStatus

      document.select(Selectors.headingLinkRow(2)).text() shouldBe getMovementListResponse.movements(1).arc
      document.select(Selectors.headingLinkRow(2)).attr("href") shouldBe getMovementListResponse.movements(1).viewMovementUrl(testErn).url
      document.select(Selectors.consignorRow(2)).text() shouldBe English.consignor(getMovementListResponse.movements(1).otherTraderID)
      document.select(Selectors.dateOfDispatchRow(2)).text() shouldBe English.dateOfDispatch(getMovementListResponse.movements(1).formattedDateOfDispatch)
      document.select(Selectors.statusTagRow(2)).text() shouldBe getMovementListResponse.movements(1).movementStatus

      document.select(Selectors.previousLink).text() shouldBe English.previous
      document.select(Selectors.previousLink).attr("href") shouldBe "previous-link"
      document.select(Selectors.paginationLink(1)).text() shouldBe "1"
      document.select(Selectors.paginationLink(1)).attr("href") shouldBe "link-1"
      document.select(Selectors.paginationLink(2)).text() shouldBe "2"
      document.select(Selectors.paginationLink(2)).attr("href") shouldBe "link-2"
      document.select(Selectors.paginationLink(3)).text() shouldBe "3"
      document.select(Selectors.paginationLink(3)).attr("href") shouldBe "link-3"
      document.select(Selectors.nextLink).text() shouldBe English.next
      document.select(Selectors.nextLink).attr("href") shouldBe "next-link"
    }
  }
}
