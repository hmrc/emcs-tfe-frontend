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

import models.MovementFilterDirectionOption
import models.response.emcsTfe.GetMovementListItem
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.html.components.GovukTag
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{Table, TableRow}
import utils.DateUtils
import viewmodels.govuk.TagFluency
import views.html.viewAllMovements.MovementTableRowContent

import javax.inject.Inject

class ViewAllMovementsTableHelper @Inject()(movementTableRowContent: MovementTableRowContent) extends DateUtils with TagFluency {

  private[viewmodels] def dataRows(ern: String, movements: Seq[GetMovementListItem], directionFilterOption: MovementFilterDirectionOption)
                                  (implicit messages: Messages): Seq[Seq[TableRow]] =
    movements.map { movement =>
      Seq(
        TableRow(
          content = HtmlContent(movementTableRowContent(ern, movement, directionFilterOption))
        ),
        TableRow(
          content = HtmlContent(new GovukTag().apply(movement.statusTag())),
          classes = "govuk-!-text-align-right"
        )
      )
    }

  def constructTable(ern: String, movements: Seq[GetMovementListItem], directionFilterOption: MovementFilterDirectionOption)
                    (implicit messages: Messages): Table =
    Table(
      rows = dataRows(ern, movements, directionFilterOption)(messages)
    )
}
