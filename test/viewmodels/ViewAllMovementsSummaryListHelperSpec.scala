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

package viewmodels

import base.SpecBase
import fixtures.MovementListFixtures
import fixtures.messages.ViewAllMovementsMessages.English
import models.MovementFilterDirectionOption._
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.html.components.GovukTag
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.{Pagination, PaginationItem, PaginationLink}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow, Value}
import utils.DateUtils
import views.html.viewAllMovements.MovementSummaryListKeyContent

class ViewAllMovementsSummaryListHelperSpec extends SpecBase with MovementListFixtures with DateUtils {

  lazy val keyContent = app.injector.instanceOf[MovementSummaryListKeyContent]
  lazy val helper: ViewAllMovementsSummaryListHelper = new ViewAllMovementsSummaryListHelper(keyContent)
  lazy val movements = getMovementListResponse.movements

  "ViewAllMovementsTableHelper" should {

    s"rendering for '${English.lang.code}' language code" when {

      implicit val messages: Messages = messagesApi.preferred(Seq(English.lang))

      Seq(All, GoodsIn, GoodsOut).foreach { direction =>

        s"for direction: $direction" when {

          "calling .dataRows(ern: String, movements: Seq[GetMovementListItem])" must {

            "construct the expected data rows" in {

              helper.constructSummaryListRows(testErn, movements, direction) mustBe
                Seq(
                  SummaryListRow(
                    key = Key(
                      HtmlContent(keyContent(testErn, movement1, direction)),
                      classes = "govuk-!-width-two-thirds"
                    ),
                    value = Value(
                      HtmlContent(new GovukTag().apply(movement1.statusTag())),
                      classes = "govuk-!-text-align-right"
                    )
                  ),
                  SummaryListRow(
                    key = Key(
                      HtmlContent(keyContent(testErn, movement2, direction)),
                      classes = "govuk-!-width-two-thirds"
                    ),
                    value = Value(
                      HtmlContent(new GovukTag().apply(movement2.statusTag())),
                      classes = "govuk-!-text-align-right"
                    )
                  )
                )
            }
          }
        }
      }

    }

    ".generatePageTitle" when {
      implicit val msgs: Messages = messages(FakeRequest())

      "totalMovements is 0" must {
        "return the correct message" when {
          "neither filtered nor searchTerm are defined" in {
            helper.generatePageTitle(
              totalMovements = 0,
              searchValue = None,
              sortByDisplayName = "sortField",
              hasFilterApplied = false,
              pagination = None
            ) mustBe "No results -"
          }
          "filtered" in {
            helper.generatePageTitle(
              totalMovements = 0,
              searchValue = None,
              sortByDisplayName = "sortField",
              hasFilterApplied = true,
              pagination = None
            ) mustBe "No filtered results -"
          }
          "searchTerm is defined" in {
            helper.generatePageTitle(
              totalMovements = 0,
              searchValue = Some("beans"),
              sortByDisplayName = "sortField",
              hasFilterApplied = false,
              pagination = None
            ) mustBe "No results for beans -"
          }
          "filtered and searchTerm is defined" in {
            helper.generatePageTitle(
              totalMovements = 0,
              searchValue = Some("beans"),
              sortByDisplayName = "sortField",
              hasFilterApplied = true,
              pagination = None
            ) mustBe "No filtered results for beans -"
          }
        }
      }

      "totalMovements is 1" must {
        "return the correct message" when {
          "neither filtered nor searchTerm are defined" in {
            helper.generatePageTitle(
              totalMovements = 1,
              searchValue = None,
              sortByDisplayName = "sortField",
              hasFilterApplied = false,
              pagination = None
            ) mustBe "1 result -"
          }
          "filtered" in {
            helper.generatePageTitle(
              totalMovements = 1,
              searchValue = None,
              sortByDisplayName = "sortField",
              hasFilterApplied = true,
              pagination = None
            ) mustBe "1 filtered result -"
          }
          "searchTerm is defined" in {
            helper.generatePageTitle(
              totalMovements = 1,
              searchValue = Some("beans"),
              sortByDisplayName = "sortField",
              hasFilterApplied = false,
              pagination = None
            ) mustBe "1 result for beans -"
          }
          "filtered and searchTerm is defined" in {
            helper.generatePageTitle(
              totalMovements = 1,
              searchValue = Some("beans"),
              sortByDisplayName = "sortField",
              hasFilterApplied = true,
              pagination = None
            ) mustBe "1 filtered result for beans -"
          }
        }
      }

      "totalMovements is not 1 or 0" must {
        "return the correct message" when {
          "neither filtered nor searchTerm are defined" in {
            helper.generatePageTitle(
              totalMovements = 2,
              searchValue = None,
              sortByDisplayName = "sortField",
              hasFilterApplied = false,
              pagination = None
            ) mustBe "2 results sorted by sortField -"
          }
          "filtered" in {
            helper.generatePageTitle(
              totalMovements = 2,
              searchValue = None,
              sortByDisplayName = "sortField",
              hasFilterApplied = true,
              pagination = None
            ) mustBe "2 filtered results sorted by sortField -"
          }
          "searchTerm is defined" in {
            helper.generatePageTitle(
              totalMovements = 2,
              searchValue = Some("beans"),
              sortByDisplayName = "sortField",
              hasFilterApplied = false,
              pagination = None
            ) mustBe "2 results for beans sorted by sortField -"
          }
          "filtered and searchTerm is defined" in {
            helper.generatePageTitle(
              totalMovements = 2,
              searchValue = Some("beans"),
              sortByDisplayName = "sortField",
              hasFilterApplied = true,
              pagination = None
            ) mustBe "2 filtered results for beans sorted by sortField -"
          }
        }
      }

      "totalMovements is 3 pages of results and on 2nd page" must {

        val threePagePagination = (Some(Pagination(
          items = Some(Seq(
            PaginationItem(s"link-1", Some("1")),
            PaginationItem(s"link-2", Some("2"), current = Some(true)),
            PaginationItem(s"link-3", Some("3"))
          )),
          previous = Some(PaginationLink("previous-link")),
          next = Some(PaginationLink("next-link"))
        )))

        "return the correct message" when {
          "neither filtered nor searchTerm are defined" in {
            helper.generatePageTitle(
              totalMovements = 30,
              searchValue = None,
              sortByDisplayName = "sortField",
              hasFilterApplied = false,
              pagination = threePagePagination
            ) mustBe "30 results (page 2 of 3) sorted by sortField -"
          }
          "filtered" in {
            helper.generatePageTitle(
              totalMovements = 30,
              searchValue = None,
              sortByDisplayName = "sortField",
              hasFilterApplied = true,
              pagination = threePagePagination
            ) mustBe "30 filtered results (page 2 of 3) sorted by sortField -"
          }
          "searchTerm is defined" in {
            helper.generatePageTitle(
              totalMovements = 30,
              searchValue = Some("beans"),
              sortByDisplayName = "sortField",
              hasFilterApplied = false,
              pagination = threePagePagination
            ) mustBe "30 results (page 2 of 3) for beans sorted by sortField -"
          }
          "filtered and searchTerm is defined" in {
            helper.generatePageTitle(
              totalMovements = 30,
              searchValue = Some("beans"),
              sortByDisplayName = "sortField",
              hasFilterApplied = true,
              pagination = threePagePagination
            ) mustBe "30 filtered results (page 2 of 3) for beans sorted by sortField -"
          }
        }
      }
    }
  }
}
