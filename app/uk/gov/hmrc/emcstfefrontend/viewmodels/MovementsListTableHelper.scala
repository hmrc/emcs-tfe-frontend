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
import uk.gov.hmrc.emcstfefrontend.views.html.components._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table, TableRow}

import javax.inject.Inject

class MovementsListTableHelper @Inject()(link: link) {

  private[viewmodels] def headerRow(implicit messages: Messages): Option[Seq[HeadCell]] = Some(Seq(
    HeadCell(Text(messages("viewMovementList.table.arc"))),
    HeadCell(Text(messages("viewMovementList.table.consignorName")))
  ))

  private[viewmodels] def dataRows(ern: String, movements: Seq[GetMovementListItem])(implicit messages: Messages): Seq[Seq[TableRow]] = movements.map { movement =>
    Seq(
      TableRow(
        content = HtmlContent(link(
          link = movement.viewMovementUrl(ern).url,
          messageKey = movement.arc
        ))
      ),
      TableRow(
        content = Text(movement.consignorName)
      )
    )
  }

  def constructTable(ern: String, movements: Seq[GetMovementListItem])(implicit messages: Messages): Table =
    Table(
      rows = dataRows(ern, movements)(messages),
      head = headerRow
    )
}
