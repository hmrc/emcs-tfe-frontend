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
import uk.gov.hmrc.govukfrontend.views.html.components.GovukTag
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{Table, TableRow}
import utils.DateUtils
import views.html.viewAllMovements.MovementTableRowContent

class ViewAllMovementsTableHelperSpec extends SpecBase with MovementListFixtures with DateUtils {

  lazy val movementTableRowContent = app.injector.instanceOf[MovementTableRowContent]
  lazy val helper: ViewAllMovementsTableHelper = new ViewAllMovementsTableHelper(movementTableRowContent)
  lazy val movements = getMovementListResponse.movements

  "ViewAllMovementsTableHelper" should {

    implicit val messages = messagesApi.preferred(Seq(English.lang))

    s"rendering for '${English.lang.code}' language code" when {

      Seq(All, GoodsIn, GoodsOut).foreach { direction =>

        s"for direction: $direction" when {

          "calling .dataRows(ern: String, movements: Seq[GetMovementListItem])" must {

            "construct the expected data rows" in {

              helper.constructTable(testErn, movements, direction) mustBe Table(
                firstCellIsHeader = false,
                rows = Seq(
                  Seq(
                    TableRow(
                      content = HtmlContent(movementTableRowContent(testErn, movement1, direction))
                    ),
                    TableRow(
                      content = HtmlContent(new GovukTag().apply(movement1.statusTag())),
                      classes = "govuk-!-text-align-right"
                    )
                  ),
                  Seq(
                    TableRow(
                      content = HtmlContent(movementTableRowContent(testErn, movement2, direction))
                    ),
                    TableRow(
                      content = HtmlContent(new GovukTag().apply(movement2.statusTag())),
                      classes = "govuk-!-text-align-right"
                    )
                  )
                )
              )
            }
          }
        }
      }
    }
  }
}
