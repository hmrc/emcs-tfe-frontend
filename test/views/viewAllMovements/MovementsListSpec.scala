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

package views.viewAllMovements

import base.ViewSpecBase
import fixtures.MovementListFixtures
import fixtures.messages.ViewAllMovementsMessages.English
import models.MovementFilterDirectionOption.All
import models.MovementSortingSelectOption
import models.response.emcsTfe.GetMovementListResponse
import play.api.i18n.{Messages, MessagesApi}
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.{Pagination, PaginationItem}
import views.ViewBehaviours
import views.html.viewAllMovements.MovementsList

class MovementsListSpec extends ViewSpecBase with ViewBehaviours with MovementListFixtures {

  val sortBySelector = ".sortBy-wrapper"
  val summaryListSelector = ".govuk-summary-list"
  val paginationSelector = ".govuk-pagination"

  val view = app.injector.instanceOf[MovementsList]

  "MovementsList" must {
    "render the correct content when movements list is non-empty" in {
      implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))

      val doc = asDocument(view(
        ern = testErn,
        movementListResponse = getMovementListResponse,
        selectItems = MovementSortingSelectOption.constructSelectItems(),
        pagination = Some(Pagination(
          items = Some(Seq(
            PaginationItem(s"link-1", Some("1")),
            PaginationItem(s"link-2", Some("2")),
            PaginationItem(s"link-3", Some("3"))
          ))
        )),
        directionFilterOption = All
      ))

      doc.selectFirst(s"$sortBySelector select").childrenSize() mustBe MovementSortingSelectOption.values.size
      doc.selectFirst(summaryListSelector) must not be null
      doc.selectFirst(paginationSelector) must not be null
    }
    "render the correct content when movements list is empty" in {
      implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))

      val doc = asDocument(view(
        ern = testErn,
        movementListResponse = GetMovementListResponse(Seq(), 0),
        selectItems = MovementSortingSelectOption.constructSelectItems(),
        pagination = Some(Pagination(
          items = Some(Seq(
            PaginationItem(s"link-1", Some("1")),
            PaginationItem(s"link-2", Some("2")),
            PaginationItem(s"link-3", Some("3"))
          ))
        )),
        directionFilterOption = All
      ))

      doc.selectFirst(s"$sortBySelector select").childrenSize() mustBe MovementSortingSelectOption.values.size
      doc.selectFirst(summaryListSelector) mustBe null
      doc.selectFirst(paginationSelector) mustBe null
    }
  }
}
