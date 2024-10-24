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

package views.viewAllMovements

import base.ViewSpecBase
import controllers.routes
import fixtures.MovementListFixtures
import fixtures.messages.ViewAllMovementsMessages
import fixtures.messages.ViewAllMovementsMessages.English
import forms.ViewAllMovementsFormProvider
import models.MovementFilterDirectionOption.{All, GoodsIn, GoodsOut}
import models._
import models.requests.DataRequest
import models.response.emcsTfe.GetMovementListResponse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.data.Form
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination._
import viewmodels.helpers.SelectItemHelper
import views.html.components.table
import views.html.viewAllMovements.ViewAllMovementsView
import views.{BaseSelectors, ViewBehaviours}


class ViewAllMovementsViewSpec extends ViewSpecBase with ViewBehaviours with MovementListFixtures {

  object Selectors extends BaseSelectors {
    val headingLinkRow = (i: Int) => s"#main-content .govuk-summary-list__row:nth-of-type($i) > dt > h2 > a"

    val sortBySelectOption = (i: Int) => s"#sortBy > option:nth-of-type($i)"
    val otherTraderIdRow = (i: Int) => s"#main-content .govuk-summary-list__row:nth-of-type($i) > dt > ul > li:nth-of-type(1)"
    val dateOfDispatchRow = (i: Int) => s"#main-content .govuk-summary-list__row:nth-of-type($i) > dt > ul > li:nth-of-type(2)"
    val statusTagRow = (i: Int) => s"#main-content .govuk-summary-list__row:nth-of-type($i) > dd > strong"
    val paginationLink = (i: Int) => s"#main-content nav > ul > li:nth-of-type($i) > a"
    val previousLink = ".govuk-pagination__prev a"
    val nextLink = ".govuk-pagination__next a"

    val searchHeading = "#main-content .govuk-fieldset__legend--m"
    val searchText = "#main-content .govuk-fieldset > p"
    val hiddenSearchBoxLabel = "#main-content .hmrc-search-group .govuk-form-group > label"
    val hiddenSearchSelectLabel = "#main-content .hmrc-search-group-flex .govuk-form-group > label"

    val sortButton = "#sortBySubmit"
    val searchButton = "#searchButton"

    def hiddenInputSearchSelectOption(value: String) = s"#searchKey > option[value=$value]"

    private val filtersSection = "aside >"
    val filtersHeading: String = s"$filtersSection h2"
    val applyFiltersButtonTop: String = "#filtersButton-top"
    val clearFiltersLinkTop: String = "#clearFiltersButton-top"
    val applyFiltersButton: String = "#filtersButton"
    val clearFiltersLink: String = "#clearFiltersButton"
    val filtersDirection: String = s"$filtersSection .govuk-form-group:nth-of-type(2) legend"
    val filtersDirectionOption1: String = s"$filtersSection .govuk-form-group:nth-of-type(2) .govuk-checkboxes__item:nth-of-type(1) label"
    val filtersDirectionOption2: String = s"$filtersSection .govuk-form-group:nth-of-type(2) .govuk-checkboxes__item:nth-of-type(2) label"
    val filtersUndischarged: String = s"$filtersSection .govuk-form-group:nth-of-type(3) legend"
    val filtersUndischargedOption1: String = s"$filtersSection .govuk-form-group:nth-of-type(3) .govuk-checkboxes__item:nth-of-type(1) label"
    val filtersStatus: String = s"$filtersSection .govuk-form-group:nth-of-type(4) label"
    val filtersStatusChoose: String = s"$filtersSection .govuk-form-group:nth-of-type(4) select option:nth-of-type(1)"
    val filtersStatusActive: String = s"$filtersSection .govuk-form-group:nth-of-type(4) select option:nth-of-type(2)"
    val filtersStatusCancelled: String = s"$filtersSection .govuk-form-group:nth-of-type(4) select option:nth-of-type(3)"
    val filtersStatusDeemedExported: String = s"$filtersSection .govuk-form-group:nth-of-type(4) select option:nth-of-type(4)"
    val filtersStatusDelivered: String = s"$filtersSection .govuk-form-group:nth-of-type(4) select option:nth-of-type(5)"
    val filtersStatusDiverted: String = s"$filtersSection .govuk-form-group:nth-of-type(4) select option:nth-of-type(6)"
    val filtersStatusExporting: String = s"$filtersSection .govuk-form-group:nth-of-type(4) select option:nth-of-type(7)"
    val filtersStatusManuallyClosed: String = s"$filtersSection .govuk-form-group:nth-of-type(4) select option:nth-of-type(8)"
    val filtersStatusPartiallyRefused: String = s"$filtersSection .govuk-form-group:nth-of-type(4) select option:nth-of-type(9)"
    val filtersStatusRefused: String = s"$filtersSection .govuk-form-group:nth-of-type(4) select option:nth-of-type(10)"
    val filtersStatusReplaced: String = s"$filtersSection .govuk-form-group:nth-of-type(4) select option:nth-of-type(11)"
    val filtersStatusRejected: String = s"$filtersSection .govuk-form-group:nth-of-type(4) select option:nth-of-type(12)"
    val filtersStatusStopped: String = s"$filtersSection .govuk-form-group:nth-of-type(4) select option:nth-of-type(13)"
    val filtersEpc: String = s"$filtersSection .govuk-form-group:nth-of-type(5) label"
    val filtersEpcChoose: String = s"$filtersSection .govuk-form-group:nth-of-type(5) select option:nth-of-type(1)"
    val filtersCountry: String = s"$filtersSection .govuk-form-group:nth-of-type(6) label"
    val filtersCountryChoose: String = s"$filtersSection .govuk-form-group:nth-of-type(6) select option:nth-of-type(1)"
    val filtersDispatchedFrom: String = s"$filtersSection .govuk-form-group:nth-of-type(7) legend"
    val filtersDispatchedFromDay: String = s"$filtersSection .govuk-form-group:nth-of-type(7) label[for$$=day]"
    val filtersDispatchedFromMonth: String = s"$filtersSection .govuk-form-group:nth-of-type(7) label[for$$=month]"
    val filtersDispatchedFromYear: String = s"$filtersSection .govuk-form-group:nth-of-type(7) label[for$$=year]"
    val filtersDispatchedTo: String = s"$filtersSection .govuk-form-group:nth-of-type(8) legend"
    val filtersDispatchedToDay: String = s"$filtersSection .govuk-form-group:nth-of-type(8) label[for$$=day]"
    val filtersDispatchedToMonth: String = s"$filtersSection .govuk-form-group:nth-of-type(8) label[for$$=month]"
    val filtersDispatchedToYear: String = s"$filtersSection .govuk-form-group:nth-of-type(8) label[for$$=year]"
    val filtersReceiptedFrom: String = s"$filtersSection .govuk-form-group:nth-of-type(9) legend"
    val filtersReceiptedFromDay: String = s"$filtersSection .govuk-form-group:nth-of-type(9) label[for$$=day]"
    val filtersReceiptedFromMonth: String = s"$filtersSection .govuk-form-group:nth-of-type(9) label[for$$=month]"
    val filtersReceiptedFromYear: String = s"$filtersSection .govuk-form-group:nth-of-type(9) label[for$$=year]"
    val filtersReceiptedTo: String = s"$filtersSection .govuk-form-group:nth-of-type(10) legend"
    val filtersReceiptedToDay: String = s"$filtersSection .govuk-form-group:nth-of-type(10) label[for$$=day]"
    val filtersReceiptedToMonth: String = s"$filtersSection .govuk-form-group:nth-of-type(10) label[for$$=month]"
    val filtersReceiptedToYear: String = s"$filtersSection .govuk-form-group:nth-of-type(10) label[for$$=year]"

    val skipToResultsLink: String = "#skip-to-results-link"
    val numberOfResultsFound: String = "#number-of-results-found"
  }

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/movements")

  implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

  lazy val view: ViewAllMovementsView = app.injector.instanceOf[ViewAllMovementsView]
  lazy val formProvider: ViewAllMovementsFormProvider = app.injector.instanceOf[ViewAllMovementsFormProvider]

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))

  lazy val table: table = app.injector.instanceOf[table]

  def asDocument(pagination: Option[Pagination],
                 direction: MovementFilterDirectionOption = All,
                 movementListResponse: GetMovementListResponse = getMovementListResponse,
                 searchOptions: MovementListSearchOptions = MovementListSearchOptions(),
                 form: Form[MovementListSearchOptions] = formProvider(),
                 isInitialView: Boolean = false
                )(implicit messages: Messages): Document = Jsoup.parse(view(
    form = form,
    action = routes.ViewAllMovementsController.onPageLoad("ern", MovementListSearchOptions()),
    ern = testErn,
    movementListResponse = movementListResponse,
    sortSelectItems = MovementSortingSelectOption.constructSelectItems(),
    searchSelectItems = MovementSearchSelectOption.constructSelectItems(formProvider()),
    movementStatusItems = MovementFilterStatusOption.selectItems(None),
    exciseProductCodeSelectItems = SelectItemHelper.constructSelectItems(Seq(MovementListSearchOptions.CHOOSE_PRODUCT_CODE), None),
    countrySelectItems = SelectItemHelper.constructSelectItems(Seq(MovementListSearchOptions.CHOOSE_COUNTRY), None),
    pagination = pagination,
    directionFilterOption = direction,
    totalMovements = movementListResponse.count,
    currentFilters = searchOptions,
    isInitialView = isInitialView
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

        Selectors.skipToResultsLink -> English.skipToResults,
        Selectors.numberOfResultsFound -> English.resultsFound(getMovementListResponse.movements.size),

        Selectors.filtersHeading -> English.filtersHeading,
        Selectors.applyFiltersButtonTop -> English.filtersButton,
        Selectors.clearFiltersLinkTop -> English.clearFiltersLink,
        Selectors.applyFiltersButton -> English.filtersButton,
        Selectors.clearFiltersLink -> English.clearFiltersLink,
        Selectors.filtersDirection -> English.filtersDirection,
        Selectors.filtersDirectionOption1 -> English.filtersDirectionOption1,
        Selectors.filtersDirectionOption2 -> English.filtersDirectionOption2,
        Selectors.filtersUndischarged -> English.filtersUndischarged,
        Selectors.filtersUndischargedOption1 -> English.filtersUndischargedOption1,
        Selectors.filtersStatus -> English.filtersStatus,
        Selectors.filtersStatusChoose -> English.filtersStatusChoose,
        Selectors.filtersStatusActive -> English.filtersStatusActive,
        Selectors.filtersStatusCancelled -> English.filtersStatusCancelled,
        Selectors.filtersStatusDeemedExported -> English.filtersStatusDeemedExported,
        Selectors.filtersStatusDelivered -> English.filtersStatusDelivered,
        Selectors.filtersStatusDiverted -> English.filtersStatusDiverted,
        Selectors.filtersStatusExporting -> English.filtersStatusExporting,
        Selectors.filtersStatusManuallyClosed -> English.filtersStatusManuallyClosed,
        Selectors.filtersStatusPartiallyRefused -> English.filtersStatusPartiallyRefused,
        Selectors.filtersStatusRefused -> English.filtersStatusRefused,
        Selectors.filtersStatusReplaced -> English.filtersStatusReplaced,
        Selectors.filtersStatusRejected -> English.filtersStatusRejected,
        Selectors.filtersStatusStopped -> English.filtersStatusStopped,
        Selectors.filtersEpc -> English.filtersEpc,
        Selectors.filtersEpcChoose -> English.filtersEpcChoose,
        Selectors.filtersCountry -> English.filtersCountry,
        Selectors.filtersCountryChoose -> English.filtersCountryChoose,
        Selectors.filtersDispatchedFrom -> English.filtersDispatchedFrom,
        Selectors.filtersDispatchedFromDay -> English.filtersDay,
        Selectors.filtersDispatchedFromMonth -> English.filtersMonth,
        Selectors.filtersDispatchedFromYear -> English.filtersYear,
        Selectors.filtersDispatchedTo -> English.filtersDispatchedTo,
        Selectors.filtersDispatchedToDay -> English.filtersDay,
        Selectors.filtersDispatchedToMonth -> English.filtersMonth,
        Selectors.filtersDispatchedToYear -> English.filtersYear,
        Selectors.filtersReceiptedFrom -> English.filtersReceiptedFrom,
        Selectors.filtersReceiptedFromDay -> English.filtersDay,
        Selectors.filtersReceiptedFromMonth -> English.filtersMonth,
        Selectors.filtersReceiptedFromYear -> English.filtersYear,
        Selectors.filtersReceiptedTo -> English.filtersReceiptedTo,
        Selectors.filtersReceiptedToDay -> English.filtersDay,
        Selectors.filtersReceiptedToMonth -> English.filtersMonth,
        Selectors.filtersReceiptedToYear -> English.filtersYear,

        Selectors.label("sortBy") -> English.sortByLabel,
        Selectors.sortBySelectOption(1) -> English.sortArcAscending,
        Selectors.sortBySelectOption(2) -> English.sortArcDescending,
        Selectors.sortBySelectOption(3) -> English.sortNewest,
        Selectors.sortBySelectOption(4) -> English.sortOldest,
        Selectors.sortButton -> English.sortByButton,
        Selectors.headingLinkRow(1) -> getMovementListResponse.movements(0).arc,
        Selectors.otherTraderIdRow(1) -> English.movementOtherTraderId(getMovementListResponse.movements(0).otherTraderID),
        Selectors.dateOfDispatchRow(1) -> English.dateOfDispatch(getMovementListResponse.movements(0).formattedDateOfDispatch),
        Selectors.statusTagRow(1) -> getMovementListResponse.movements(0).movementStatus,
        Selectors.headingLinkRow(2) -> getMovementListResponse.movements(1).arc,
        Selectors.otherTraderIdRow(2) -> English.movementOtherTraderId(getMovementListResponse.movements(1).otherTraderID),
        Selectors.dateOfDispatchRow(2) -> English.dateOfDispatch(getMovementListResponse.movements(1).formattedDateOfDispatch),
        Selectors.statusTagRow(2) -> getMovementListResponse.movements(1).movementStatus
      ))

      "have the correct links for each movement" in {
        doc.select(Selectors.headingLinkRow(1)).attr("href") mustEqual getMovementListResponse.movements(0).viewMovementUrl(testErn).url
        doc.select(Selectors.headingLinkRow(2)).attr("href") mustEqual getMovementListResponse.movements(1).viewMovementUrl(testErn).url
      }

      "show the correct content based on direction filters" when {

        Seq(
          All -> English.movementOtherTraderId,
          GoodsIn -> English.movementConsignor,
          GoodsOut -> English.movementConsignee
        ).foreach { directionAndExpectedMessage =>

          s"direction is: ${directionAndExpectedMessage._1}" must {
            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.headingLinkRow(1) -> getMovementListResponse.movements(0).arc,
              Selectors.otherTraderIdRow(1) -> directionAndExpectedMessage._2(getMovementListResponse.movements(0).otherTraderID),
              Selectors.dateOfDispatchRow(1) -> English.dateOfDispatch(getMovementListResponse.movements(0).formattedDateOfDispatch),
              Selectors.statusTagRow(1) -> getMovementListResponse.movements(0).movementStatus,
              Selectors.headingLinkRow(2) -> getMovementListResponse.movements(1).arc,
              Selectors.otherTraderIdRow(2) -> directionAndExpectedMessage._2(getMovementListResponse.movements(1).otherTraderID),
              Selectors.dateOfDispatchRow(2) -> English.dateOfDispatch(getMovementListResponse.movements(1).formattedDateOfDispatch),
              Selectors.statusTagRow(2) -> getMovementListResponse.movements(1).movementStatus
            ))(asDocument(None, direction = directionAndExpectedMessage._1))
          }
        }
      }

      "show that there are no search results found" when {
        implicit val doc: Document = asDocument(None, movementListResponse = GetMovementListResponse(Seq(), 0))

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.skipToResultsLink -> English.skipToResults,
          Selectors.numberOfResultsFound -> English.noResultsFound
        ))
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

        Selectors.skipToResultsLink -> English.skipToResults,
        Selectors.numberOfResultsFound -> English.resultsFound(getMovementListResponse.movements.size),

        Selectors.label("sortBy") -> English.sortByLabel,
        Selectors.sortBySelectOption(1) -> English.sortArcAscending,
        Selectors.sortBySelectOption(2) -> English.sortArcDescending,
        Selectors.sortBySelectOption(3) -> English.sortNewest,
        Selectors.sortBySelectOption(4) -> English.sortOldest,
        Selectors.sortButton -> English.sortByButton,
        Selectors.headingLinkRow(1) -> getMovementListResponse.movements(0).arc,
        Selectors.otherTraderIdRow(1) -> English.movementOtherTraderId(getMovementListResponse.movements(0).otherTraderID),
        Selectors.dateOfDispatchRow(1) -> English.dateOfDispatch(getMovementListResponse.movements(0).formattedDateOfDispatch),
        Selectors.statusTagRow(1) -> getMovementListResponse.movements(0).movementStatus,
        Selectors.headingLinkRow(2) -> getMovementListResponse.movements(1).arc,
        Selectors.otherTraderIdRow(2) -> English.movementOtherTraderId(getMovementListResponse.movements(1).otherTraderID),
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

      "show that there are no search results found" when {
        implicit val doc: Document = asDocument(None, movementListResponse = GetMovementListResponse(Seq(), 0))

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.skipToResultsLink -> English.skipToResults,
          Selectors.numberOfResultsFound -> English.noResultsFound
        ))
      }

      "render the nav links" in {
        val homeLink = doc.select("#navigation-home-link")
        homeLink.text mustBe "Home"
      }
    }

    s"being rendered with the result count should show the correct heading for no movements" when {

      val response: GetMovementListResponse = GetMovementListResponse(Seq(), 0)

      implicit val doc: Document = asDocument(None, movementListResponse = response, searchOptions = MovementListSearchOptions(searchValue = Some("beans")))

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.titleWithNoResults,
        Selectors.h1 -> English.headingWithNoResults
      ))
    }

    s"being rendered with the result count should show the correct heading for one movement" when {

      val response: GetMovementListResponse = GetMovementListResponse(Seq(movement1), 1)

      implicit val doc: Document = asDocument(None, movementListResponse = response, searchOptions = MovementListSearchOptions(searchValue = Some("beans")))

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.titleWithOneResult,
        Selectors.h1 -> English.headingWithOneResult
      ))
    }

    s"being rendered with the result count should show the correct heading for two movements" when {

      val response: GetMovementListResponse = GetMovementListResponse(Seq(movement1, movement2), 2)

      implicit val doc: Document = asDocument(None, movementListResponse = response, searchOptions = MovementListSearchOptions(searchValue = Some("beans")))

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.titleWithCount(2),
        Selectors.h1 -> English.headingWithCount(2)
      ))
    }

    s"being rendered with the result count should show the correct heading for 3 pages of movements" when {

      val threePagePagination = (Some(Pagination(
        items = Some(Seq(
          PaginationItem(s"link-1", Some("1")),
          PaginationItem(s"link-2", Some("2"), current = Some(true)),
          PaginationItem(s"link-3", Some("3"))
        )),
        previous = Some(PaginationLink("previous-link")),
        next = Some(PaginationLink("next-link"))
      )))

      val response: GetMovementListResponse = GetMovementListResponse(Seq.fill(30)(movement1), 30)

      implicit val doc: Document = asDocument(threePagePagination, movementListResponse = response, searchOptions = MovementListSearchOptions(searchValue = Some("beans")))

      behave like pageWithExpectedElementsAndMessages(Seq(
        Selectors.title -> English.titleWithCountAndPage(30, 2, 3),
        Selectors.h1 -> English.headingWithCountAndPage(30, 2, 3)
      ))
    }

    "being rendered with errors" when {
      "searchKey is in error" should {
        implicit val doc: Document = asDocument(None, form = formProvider().withError("searchKey", messages(ViewAllMovementsFormProvider.searchKeyRequiredMessage)))

        "show an error summary" in {
          doc.select(".govuk-error-summary__title").text() mustBe "There is a problem"
          doc.select(".govuk-error-summary__body").text() mustBe English.searchKeyErrorMessage
        }

        "show an error message against the field" in {
          doc.select(".hmrc-search-group-wrapper").hasClass("govuk-form-group--error") mustBe true
          doc.select(".hmrc-search-by-dropdown").hasClass("govuk-select--error") mustBe true
          doc.select(".govuk-error-message").text() mustBe ViewAllMovementsMessages.errorHelper(English.searchKeyErrorMessage)
        }
      }

      "searchValue is in error" should {
        implicit val doc: Document = asDocument(None, form = formProvider().withError("searchValue", messages(ViewAllMovementsFormProvider.arcMaxLengthMessage)))

        "show an error summary" in {
          doc.select(".govuk-error-summary__title").text() mustBe "There is a problem"
          doc.select(".govuk-error-summary__body").text() mustBe English.searchValueErrorMessage("ARC", ViewAllMovementsFormProvider.ARC_MAX_LENGTH)
        }

        "show an error message against the field" in {
          doc.select(".govuk-error-message").text() mustBe ViewAllMovementsMessages.errorHelper(English.searchValueErrorMessage("ARC", ViewAllMovementsFormProvider.ARC_MAX_LENGTH))
        }
      }
    }

    "being viewed for the first time" should {
      "should show the title with no count" in {
        implicit val doc: Document = asDocument(None, searchOptions = MovementListSearchOptions(searchValue = Some("beans")), isInitialView = true)

        doc.select(Selectors.title).text() mustBe English.title
      }
    }

    "not being viewed for the first time" should {
      "should show the title with a count" in {
        implicit val doc: Document = asDocument(None, searchOptions = MovementListSearchOptions(searchValue = Some("beans")))

        doc.select(Selectors.title).text() mustBe English.titleWithCount(getMovementListResponse.count)
      }
    }

  }
}
