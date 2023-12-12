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

package viewmodels.helpers

import models.response.emcsTfe.MovementItem
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.emcstfefrontend.models.response.emcsTfe.GetMovementResponse
import uk.gov.hmrc.govukfrontend.views.html.components.GovukTable
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table, TableRow}
import utils.ExpectedDateOfArrival
import viewmodels.govuk.TagFluency
import views.html.components.{link, list}

import javax.inject.Inject

class ViewMovementItemsHelper @Inject()(list: list,
                                        link: link,
                                        govukTable: GovukTable,
                                       ) extends ExpectedDateOfArrival with TagFluency {

  def constructMovementItems(movement: GetMovementResponse)(implicit messages: Messages): Html = {
    govukTable(Table(
      firstCellIsHeader = true,
      rows = dataRows(movement.items),
      head = headerRow
    ))
  }

  private[viewmodels] def headerRow(implicit messages: Messages): Option[Seq[HeadCell]] = Some(Seq(
    HeadCell(Text(messages("viewMovement.items.table.heading.item"))),
    HeadCell(Text(messages("viewMovement.items.table.heading.description"))),
    HeadCell(Text(messages("viewMovement.items.table.heading.quantity"))),
    HeadCell(Text(messages("viewMovement.items.table.heading.packaging")))
    //TODO: Add in 'Receipt' column as part of ETFE-2882 (dependent on ETFE-2864 & ETFE-2858)
    // HeadCell(Text(messages("viewMovement.items.table.heading.receipt")))
  ))

  private[viewmodels] def dataRows(items: Seq[MovementItem])(implicit messages: Messages): Seq[Seq[TableRow]] =
    items.sortBy(_.itemUniqueReference).map { item =>

      //TODO: Work out the status of the item based on report of receipt global conclusion and its existence in the report of receipt.
      //      ETFE-2882 raised for this (dependent on ETFE-2864 & ETFE-2858)
      //
      // val itemReceiptStatus = ???

      Seq(
        TableRow(
          content = HtmlContent(link(
            link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
            messageKey = messages("viewMovement.items.table.row.item", item.itemUniqueReference)
          )),
          classes = "white-space-nowrap"
        ),
        TableRow(
          content = Text(item.commercialDescription.getOrElse("")),
          classes = "govuk-!-width-one-third"
        ),
        TableRow(
          content = Text(messages(
            "viewMovement.items.table.row.quantity",
            item.quantity.toString(),
            item.unitOfMeasure.map(_.toShortFormatMessage()).getOrElse("")
          ))
        ),
        TableRow(
          content = HtmlContent(list(item.packaging.map(pckg =>
            Html(pckg.typeOfPackage)
          )))
        )
//        TODO: Add in as part of ETFE-2882 (dependent on ETFE-2864 & ETFE-2858)
//        TableRow(
//          content = HtmlContent(itemReceiptStatus),
//          classes = "white-space-nowrap"
//        )
      )
    }
}
