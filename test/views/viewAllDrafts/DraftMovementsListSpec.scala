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

package views.viewAllDrafts

import base.ViewSpecBase
import fixtures.DraftMovementsFixtures
import fixtures.messages.ViewAllMovementsMessages.English
import models.MovementSortingSelectOption
import play.api.i18n.{Messages, MessagesApi}
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.{Pagination, PaginationItem}
import views.ViewBehaviours
import views.html.viewAllDrafts.DraftMovementsList

class DraftMovementsListSpec extends ViewSpecBase with ViewBehaviours with DraftMovementsFixtures {

  val sortBySelector = ".sortBy-wrapper"
  val summaryListSelector = ".govuk-summary-list"
  val paginationSelector = ".govuk-pagination"

  lazy val twoDraftMovements = Seq(draftMovementModelMax, draftMovementModelMin)

  lazy val view = app.injector.instanceOf[DraftMovementsList]

  "DraftMovementsList" must {
    "render the correct content when movements list is non-empty" in {
      implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))

      val doc = asDocument(view(
        ern = testErn,
        movements = twoDraftMovements,
        selectItems = MovementSortingSelectOption.constructSelectItems(),
        pagination = Some(Pagination(
          items = Some(Seq(
            PaginationItem(s"link-1", Some("1")),
            PaginationItem(s"link-2", Some("2")),
            PaginationItem(s"link-3", Some("3"))
          ))
        )),
        totalMovements = twoDraftMovements.size
      ))

      doc.selectFirst(s"$sortBySelector select").childrenSize() mustBe MovementSortingSelectOption.values.size
      doc.selectFirst(summaryListSelector) must not be null
      doc.selectFirst(paginationSelector) must not be null
    }
    "render the correct content when movements list is empty" in {
      implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))

      val doc = asDocument(view(
        ern = testErn,
        movements = Seq(),
        selectItems = MovementSortingSelectOption.constructSelectItems(),
        pagination = Some(Pagination(
          items = Some(Seq(
            PaginationItem(s"link-1", Some("1")),
            PaginationItem(s"link-2", Some("2")),
            PaginationItem(s"link-3", Some("3"))
          ))
        )),
        totalMovements = 0
      ))

      doc.selectFirst(s"$sortBySelector select").childrenSize() mustBe MovementSortingSelectOption.values.size
      doc.select(summaryListSelector).isEmpty mustBe true
      doc.select(paginationSelector).isEmpty mustBe true
    }
  }
}
