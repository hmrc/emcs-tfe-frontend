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

import base.ViewSpecBase
import controllers.routes
import fixtures.MovementListFixtures
import fixtures.messages.ViewAllMovementsMessages.English
import forms.ViewAllMovementsFormProvider
import models.requests.DataRequest
import models.{MovementListSearchOptions, MovementSearchSelectOption, MovementSortingSelectOption}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination._
import viewmodels.MovementsListTableHelper
import views.html.components.table
import views.html.viewAllMovements.ViewAllMovements


class ViewAllMovementsViewSpec extends ViewSpecBase with ViewBehaviours with MovementListFixtures {

  object Selectors extends BaseSelectors {
    val headingLinkRow = (i: Int) => s"#main-content tr:nth-child($i) > td:nth-child(1) > h2 > a"

    val sortBySelectOption = (i: Int) => s"#sortBy > option:nth-child($i)"
    val consignorRow = (i: Int) => s"#main-content tr:nth-child($i) > td:nth-child(1) > ul > li:nth-child(1)"
    val dateOfDispatchRow = (i: Int) => s"#main-content tr:nth-child($i) > td:nth-child(1) > ul > li:nth-child(2)"
    val statusTagRow = (i: Int) => s"#main-content tr:nth-child($i) > td:nth-child(2) > strong"
    val paginationLink = (i: Int) => s"#main-content nav > ul > li:nth-child($i) > a"
    val previousLink = ".govuk-pagination__prev a"
    val nextLink = ".govuk-pagination__next a"

    val searchHeading = "#main-content .govuk-fieldset__legend--m"
    val searchText = "#main-content .govuk-fieldset > p"
    val hiddenSearchBoxLabel = "#main-content .hmrc-search-group .govuk-form-group > label"
    val hiddenSearchSelectLabel = "#main-content .hmrc-search-group-flex .govuk-form-group > label"

    val sortButton = "#sortBySubmit"
    val searchButton = "#searchButton"

    def hiddenInputSearchSelectOption(value: String) = s"#searchKey > option[value=$value]"
  }

  implicit val fakeRequest = FakeRequest("GET", "/movements")

  implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

  lazy val view: ViewAllMovements = app.injector.instanceOf[ViewAllMovements]
  lazy val helper: MovementsListTableHelper = app.injector.instanceOf[MovementsListTableHelper]
  lazy val formProvider: ViewAllMovementsFormProvider = app.injector.instanceOf[ViewAllMovementsFormProvider]

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))

  lazy val table: table = app.injector.instanceOf[table]

  def asDocument(pagination: Option[Pagination])(implicit messages: Messages): Document = Jsoup.parse(view(
    form = formProvider(),
    action = routes.ViewAllMovementsController.onPageLoad("ern", MovementListSearchOptions()),
    ern = testErn,
    movements = getMovementListResponse.movements,
    sortSelectItems = MovementSortingSelectOption.constructSelectItems(),
    searchSelectItems = MovementSearchSelectOption.constructSelectItems(),
    pagination = pagination
  ).toString())


  "The ViewAllMovementsPage view" when {

    s"being rendered for with no pagination should show the correct content" when {

      implicit val doc: Document = asDocument(None)

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.searchHeading -> English.searchHeading,
        Selectors.searchText -> English.searchText,
        Selectors.hiddenSearchBoxLabel -> English.searchInputHiddenLabel,
        Selectors.hiddenSearchSelectLabel -> English.searchSelectHiddenLabel,
        Selectors.hiddenInputSearchSelectOption("arc") -> English.searchSelectARC,
        Selectors.hiddenInputSearchSelectOption("lrn") -> English.searchSelectLRN,
        Selectors.hiddenInputSearchSelectOption("otherTraderId") -> English.searchSelectERN,
        Selectors.hiddenInputSearchSelectOption("transporterTraderName") -> English.searchSelectTransporter,
        Selectors.searchButton -> English.searchButton,
        Selectors.label("sortBy") -> English.sortByLabel,
        Selectors.sortBySelectOption(1) -> English.sortArcAscending,
        Selectors.sortBySelectOption(2) -> English.sortArcDescending,
        Selectors.sortBySelectOption(3) -> English.sortNewest,
        Selectors.sortBySelectOption(4) -> English.sortOldest,
        Selectors.sortButton -> English.sortByButton,
        Selectors.headingLinkRow(1) -> getMovementListResponse.movements(0).arc,
        Selectors.consignorRow(1) -> English.consignor(getMovementListResponse.movements(0).otherTraderID),
        Selectors.dateOfDispatchRow(1) -> English.dateOfDispatch(getMovementListResponse.movements(0).formattedDateOfDispatch),
        Selectors.statusTagRow(1) -> getMovementListResponse.movements(0).movementStatus,
        Selectors.headingLinkRow(2) -> getMovementListResponse.movements(1).arc,
        Selectors.consignorRow(2) -> English.consignor(getMovementListResponse.movements(1).otherTraderID),
        Selectors.dateOfDispatchRow(2) -> English.dateOfDispatch(getMovementListResponse.movements(1).formattedDateOfDispatch),
        Selectors.statusTagRow(2) -> getMovementListResponse.movements(1).movementStatus
      ))

      "have the correct links for each movement" in {
        doc.select(Selectors.headingLinkRow(1)).attr("href") mustEqual getMovementListResponse.movements(0).viewMovementUrl(testErn).url
        doc.select(Selectors.headingLinkRow(2)).attr("href") mustEqual getMovementListResponse.movements(1).viewMovementUrl(testErn).url
      }

    }

    s"being rendered for with pagination" when {

      implicit val doc: Document = asDocument(Some(Pagination(
        items = Some(Seq(
          PaginationItem(s"link-1", Some("1")),
          PaginationItem(s"link-2", Some("2")),
          PaginationItem(s"link-3", Some("3"))
        )),
        previous = Some(PaginationLink("previous-link")),
        next = Some(PaginationLink("next-link"))
      )))

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.title,
        Selectors.h1 -> English.heading,
        Selectors.searchHeading -> English.searchHeading,
        Selectors.searchText -> English.searchText,
        Selectors.hiddenSearchBoxLabel -> English.searchInputHiddenLabel,
        Selectors.hiddenSearchSelectLabel -> English.searchSelectHiddenLabel,
        Selectors.hiddenInputSearchSelectOption("arc") -> English.searchSelectARC,
        Selectors.hiddenInputSearchSelectOption("lrn") -> English.searchSelectLRN,
        Selectors.hiddenInputSearchSelectOption("otherTraderId") -> English.searchSelectERN,
        Selectors.hiddenInputSearchSelectOption("transporterTraderName") -> English.searchSelectTransporter,
        Selectors.searchButton -> English.searchButton,
        Selectors.label("sortBy") -> English.sortByLabel,
        Selectors.sortBySelectOption(1) -> English.sortArcAscending,
        Selectors.sortBySelectOption(2) -> English.sortArcDescending,
        Selectors.sortBySelectOption(3) -> English.sortNewest,
        Selectors.sortBySelectOption(4) -> English.sortOldest,
        Selectors.sortButton -> English.sortByButton,
        Selectors.headingLinkRow(1) -> getMovementListResponse.movements(0).arc,
        Selectors.consignorRow(1) -> English.consignor(getMovementListResponse.movements(0).otherTraderID),
        Selectors.dateOfDispatchRow(1) -> English.dateOfDispatch(getMovementListResponse.movements(0).formattedDateOfDispatch),
        Selectors.statusTagRow(1) -> getMovementListResponse.movements(0).movementStatus,
        Selectors.headingLinkRow(2) -> getMovementListResponse.movements(1).arc,
        Selectors.consignorRow(2) -> English.consignor(getMovementListResponse.movements(1).otherTraderID),
        Selectors.dateOfDispatchRow(2) -> English.dateOfDispatch(getMovementListResponse.movements(1).formattedDateOfDispatch),
        Selectors.statusTagRow(2) -> getMovementListResponse.movements(1).movementStatus
      ))

      "should show the correct content and pagination links" in {
        doc.select(Selectors.previousLink).text() mustBe English.previous
        doc.select(Selectors.previousLink).attr("href") mustBe "previous-link"
        doc.select(Selectors.paginationLink(1)).text() mustBe "1"
        doc.select(Selectors.paginationLink(1)).attr("href") mustBe "link-1"
        doc.select(Selectors.paginationLink(2)).text() mustBe "2"
        doc.select(Selectors.paginationLink(2)).attr("href") mustBe "link-2"
        doc.select(Selectors.paginationLink(3)).text() mustBe "3"
        doc.select(Selectors.paginationLink(3)).attr("href") mustBe "link-3"
        doc.select(Selectors.nextLink).text() mustBe English.next
        doc.select(Selectors.nextLink).attr("href") mustBe "next-link"
      }

      "have the correct links for each movement" in {
        doc.select(Selectors.headingLinkRow(1)).attr("href") mustBe getMovementListResponse.movements(0).viewMovementUrl(testErn).url
        doc.select(Selectors.headingLinkRow(2)).attr("href") mustBe getMovementListResponse.movements(1).viewMovementUrl(testErn).url
      }

      "have the correct pagination links" in {
        doc.select(Selectors.paginationLink(1)).attr("href") mustBe "link-1"
        doc.select(Selectors.paginationLink(2)).attr("href") mustBe "link-2"
        doc.select(Selectors.paginationLink(3)).attr("href") mustBe "link-3"
        doc.select(Selectors.nextLink).attr("href") mustBe "next-link"
      }
    }
  }
}
