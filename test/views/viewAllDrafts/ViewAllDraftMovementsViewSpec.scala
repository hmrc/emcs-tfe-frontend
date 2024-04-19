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

package views.viewAllDrafts

import base.ViewSpecBase
import fixtures.DraftMovementsFixtures
import fixtures.messages.ViewAllDraftMovementsMessages.English
import forms.ViewAllDraftMovementsFormProvider
import models.draftMovements.{DraftMovementSortingSelectOption, GetDraftMovementsSearchOptions}
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination._
import viewmodels.helpers.SelectItemHelper
import views.html.components.table
import views.html.viewAllDrafts.ViewAllDraftMovementsView
import views.{BaseSelectors, ViewBehaviours}


class ViewAllDraftMovementsViewSpec extends ViewSpecBase with ViewBehaviours with DraftMovementsFixtures {

  object Selectors extends BaseSelectors {
    val headingLinkRow = "#main-content tr > td div:nth-child(1) > h2 > a"
    val destinationTypeRow = "#main-content tr > td div:nth-child(1) > ul > li:nth-child(1)"
    val consigneeRow = "#main-content tr > td div:nth-child(1) > ul > li:nth-child(2)"
    val dateOfDispatchRow = "#main-content tr > td div:nth-child(1) > ul > li:nth-child(3)"
    val statusDraftRow = "#main-content div.govuk-\\!-text-align-right > p.govuk-body > strong.govuk-tag.govuk-tag--grey"
    val statusErrorRow = "#main-content div.govuk-\\!-text-align-right > p.govuk-body > strong.govuk-tag.govuk-tag--red"
    val sortBySelectOption = (i: Int) => s"#sortBy > option:nth-child($i)"
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

    private val filtersSection = "aside"
    val filtersHeading: String = s"$filtersSection h2"
    val filtersButton: String = s"$filtersSection button"
    val filtersErrors: String = s"$filtersSection .govuk-form-group:nth-of-type(1) legend"
    val filtersErrorsOption1: String = s"$filtersSection .govuk-form-group:nth-of-type(1) .govuk-checkboxes__item:nth-of-type(1) label"
    val filtersDirectionOption2: String = s"$filtersSection .govuk-form-group:nth-of-type(1) .govuk-checkboxes__item:nth-of-type(2) label"
    val filtersUndischarged: String = s"$filtersSection .govuk-form-group:nth-of-type(2) legend"
    val filtersDestinationTypeOption: Int => String = i => s"$filtersSection .govuk-form-group:nth-of-type(2) .govuk-checkboxes__item:nth-of-type($i) label"
    val filtersExciseProduct: String = s"$filtersSection .govuk-form-group:nth-of-type(3) label"
    val filtersExciseProductChoose: String = s"$filtersSection .govuk-form-group:nth-of-type(3) select option:nth-of-type(1)"
    val filtersDispatchedFrom: String = s"$filtersSection .govuk-form-group:nth-of-type(4) legend"
    val filtersDispatchedFromDay: String = s"$filtersSection .govuk-form-group:nth-of-type(4) label[for$$=day]"
    val filtersDispatchedFromMonth: String = s"$filtersSection .govuk-form-group:nth-of-type(4) label[for$$=month]"
    val filtersDispatchedFromYear: String = s"$filtersSection .govuk-form-group:nth-of-type(4) label[for$$=year]"
    val filtersDispatchedTo: String = s"$filtersSection .govuk-form-group:nth-of-type(5) legend"
    val filtersDispatchedToDay: String = s"$filtersSection .govuk-form-group:nth-of-type(5) label[for$$=day]"
    val filtersDispatchedToMonth: String = s"$filtersSection .govuk-form-group:nth-of-type(5) label[for$$=month]"
    val filtersDispatchedToYear: String = s"$filtersSection .govuk-form-group:nth-of-type(5) label[for$$=year]"
  }

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/movements")

  implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

  lazy val view: ViewAllDraftMovementsView = app.injector.instanceOf[ViewAllDraftMovementsView]
  lazy val formProvider: ViewAllDraftMovementsFormProvider = app.injector.instanceOf[ViewAllDraftMovementsFormProvider]

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))

  lazy val table: table = app.injector.instanceOf[table]

  def asDocument(pagination: Option[Pagination])
                (implicit messages: Messages): Document = Jsoup.parse(view(
    form = formProvider(),
    action = controllers.drafts.routes.ViewAllDraftMovementsController.onPageLoad("ern", GetDraftMovementsSearchOptions()),
    ern = testErn,
    movements = Seq(draftMovementModelMax, draftMovementModelMin),
    sortSelectItems = DraftMovementSortingSelectOption.constructSelectItems(),
    exciseItems = SelectItemHelper.constructSelectItems(Seq(GetDraftMovementsSearchOptions.CHOOSE_PRODUCT_CODE), None),
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
        Selectors.searchButton -> English.searchButton,

        Selectors.filtersHeading -> English.filtersHeading,
        Selectors.filtersButton -> English.filtersButton,
        Selectors.filtersErrors -> English.filtersErrors,
        Selectors.filtersErrorsOption1 -> English.filtersErrorsOption1,
        Selectors.filtersUndischarged -> English.filtersDestinationType,
        Selectors.filtersDestinationTypeOption(1) -> English.filtersDestinationTypeOption1,
        Selectors.filtersDestinationTypeOption(2) -> English.filtersDestinationTypeOption2,
        Selectors.filtersDestinationTypeOption(3) -> English.filtersDestinationTypeOption3,
        Selectors.filtersDestinationTypeOption(4) -> English.filtersDestinationTypeOption4,
        Selectors.filtersDestinationTypeOption(5) -> English.filtersDestinationTypeOption5,
        Selectors.filtersDestinationTypeOption(6) -> English.filtersDestinationTypeOption6,
        Selectors.filtersDestinationTypeOption(7) -> English.filtersDestinationTypeOption7,
        Selectors.filtersDestinationTypeOption(8) -> English.filtersDestinationTypeOption8,
        Selectors.filtersDestinationTypeOption(9) -> English.filtersDestinationTypeOption9,
        Selectors.filtersExciseProduct -> English.filtersExciseProduct,
        Selectors.filtersExciseProductChoose -> English.filtersExciseProductChoose,
        Selectors.filtersDispatchedFrom -> English.filtersDispatchedFrom,
        Selectors.filtersDispatchedFromDay -> English.filtersDay,
        Selectors.filtersDispatchedFromMonth -> English.filtersMonth,
        Selectors.filtersDispatchedFromYear -> English.filtersYear,
        Selectors.filtersDispatchedTo -> English.filtersDispatchedTo,
        Selectors.filtersDispatchedToDay -> English.filtersDay,
        Selectors.filtersDispatchedToMonth -> English.filtersMonth,
        Selectors.filtersDispatchedToYear -> English.filtersYear,
        Selectors.headingLinkRow -> draftMovementModelMax.data.lrn,
        Selectors.destinationTypeRow -> English.destinationRowContent(draftMovementModelMax.data.movementScenario.get),
        Selectors.consigneeRow -> English.consigneeRowContent(draftMovementModelMax.data.consigneeReference.get),
        Selectors.dateOfDispatchRow -> English.dispatchDateRowContent(draftMovementModelMax.data.dispatchDate.get),
        Selectors.statusDraftRow -> English.statusDraft,
        Selectors.statusErrorRow -> English.statusError,
        Selectors.label("sortBy") -> English.sortByLabel,
        Selectors.sortBySelectOption(1) -> English.sortLrnAscending,
        Selectors.sortBySelectOption(2) -> English.sortLrnDescending,
        Selectors.sortBySelectOption(3) -> English.sortNewest,
        Selectors.sortBySelectOption(4) -> English.sortOldest,
        Selectors.sortButton -> English.sortByButton
      ))
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
        Selectors.searchButton -> English.searchButton,
        Selectors.label("sortBy") -> English.sortByLabel,
        Selectors.sortBySelectOption(1) -> English.sortLrnAscending,
        Selectors.sortBySelectOption(2) -> English.sortLrnDescending,
        Selectors.sortBySelectOption(3) -> English.sortNewest,
        Selectors.sortBySelectOption(4) -> English.sortOldest,
        Selectors.sortButton -> English.sortByButton
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
    }
  }
}
