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

import models.requests.DataRequest
import models.response.emcsTfe.GetMovementResponse
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.html.components.GovukTable
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table, TableRow}
import utils.ExpectedDateOfArrival
import viewmodels.govuk.TagFluency
import views.html.components.{h2, link, list}

import javax.inject.{Inject, Singleton}

@Singleton
class ViewMovementItemsHelper @Inject()(list: list,
                                        link: link,
                                        h2: h2,
                                        govukTable: GovukTable
                                       ) extends ExpectedDateOfArrival with TagFluency {

  def constructMovementItems(movement: GetMovementResponse)(implicit request: DataRequest[_], messages: Messages): Html = {
    HtmlFormat.fill(Seq(
      h2(messages("viewMovement.items.h2"), "govuk-heading-l"),
      govukTable(Table(
        caption = Some(messages("viewMovement.items.table.caption")),
        captionClasses = "govuk-visually-hidden",
        firstCellIsHeader = true,
        rows = dataRows(movement),
        head = headerRow
      ))
    ))
  }

  private[viewmodels] def headerRow(implicit messages: Messages): Option[Seq[HeadCell]] = Some(Seq(
    HeadCell(Text(messages("viewMovement.items.table.heading.item"))),
    HeadCell(Text(messages("viewMovement.items.table.heading.description"))),
    HeadCell(Text(messages("viewMovement.items.table.heading.quantity"))),
    HeadCell(Text(messages("viewMovement.items.table.heading.packaging"))),
    HeadCell(Text(messages("viewMovement.items.table.heading.receipt")))
  ))

  private[viewmodels] def dataRows(movement: GetMovementResponse)(implicit request: DataRequest[_], messages: Messages): Seq[Seq[TableRow]] =
    movement.items.sortBy(_.itemUniqueReference).map { item =>

      val itemReceiptStatus = movement.reportOfReceipt.fold(Html(messages("viewMovement.items.receiptStatus.notReceipted"))){ ror =>
        ror.individualItems.find(_.eadBodyUniqueReference == item.itemUniqueReference) match {
          case Some(item) if item.unsatisfactoryReasons.nonEmpty =>
            list(item.unsatisfactoryReasons.map(reason =>
              Html(messages(s"viewMovement.items.receiptStatus.${reason.reason}"))
            ))
          case _ => Html(messages(s"viewMovement.items.receiptStatus.satisfactory"))
        }
      }

      Seq(
        TableRow(
          content = HtmlContent(link(
            link = controllers.routes.ItemDetailsController.onPageLoad(request.ern, movement.arc, item.itemUniqueReference).url,
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
        ),
        TableRow(
          content = HtmlContent(itemReceiptStatus),
          classes = "white-space-nowrap"
        )
      )
    }
}
