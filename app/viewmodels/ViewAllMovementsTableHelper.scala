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

package uk.gov.hmrc.emcstfefrontend.viewmodels

import play.api.i18n.Messages
import uk.gov.hmrc.emcstfefrontend.models.response.emcsTfe.GetMovementListItem
import uk.gov.hmrc.emcstfefrontend.utils.DateUtils
import uk.gov.hmrc.emcstfefrontend.views.html.components._
import uk.gov.hmrc.govukfrontend.views.html.components.GovukTag
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{Table, TableRow}

import javax.inject.Inject

class ViewAllMovementsTableHelper @Inject()(movementTableRowContent: movementTableRowContent) extends DateUtils {

  private[viewmodels] def dataRows(ern: String, movements: Seq[GetMovementListItem])
                                  (implicit messages: Messages): Seq[Seq[TableRow]] =
    movements.map { movement =>
      Seq(
        TableRow(
          content = HtmlContent(movementTableRowContent(ern, movement))
        ),
        TableRow(
          content = HtmlContent(new GovukTag().apply(movement.statusTag))
        )
      )
    }

  def constructTable(ern: String, movements: Seq[GetMovementListItem])(implicit messages: Messages): Table =
    Table(
      rows = dataRows(ern, movements)(messages)
    )
}
