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

package viewmodels.helpers.messages

import models.response.emcsTfe.messages.Message
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.html.components.{GovukTable, GovukTag}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table, TableRow}
import uk.gov.hmrc.govukfrontend.views.viewmodels.tag.Tag
import utils.DateUtils
import viewmodels.govuk.TagFluency
import views.html.components.link

import javax.inject.Inject

class ViewAllMessagesTableHelper @Inject()(link: link,
                                           govukTable: GovukTable) extends DateUtils with TagFluency {

  private[viewmodels] def dataRows(allMessages: Seq[Message])
                                  (implicit messages: Messages): Seq[Seq[TableRow]] =
    allMessages.map { aMessage =>
      Seq(
        TableRow(
          content = HtmlContent(
            link(
              link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
              messageKey = formattedMessageDescription(aMessage),
              hintKey = aMessage.arc.orElse(aMessage.lrn)
            )
          )
        ),
        TableRow (
          content = HtmlContent(
            new GovukTag().apply(statusTag(aMessage.readIndicator))
          )
        ),
        TableRow(
          content = HtmlContent(
            aMessage.dateCreatedOnCore.toLocalDate.formatDateForUIOutput()
          )
        ),
        TableRow(
          content = HtmlContent(
            link(
              link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
              messageKey = messages("viewAllMessages.link.message.delete.label")
            )
          )
        )
      )
    }

  def constructTable(allMessages: Seq[Message])(implicit messages: Messages): Html = {
    govukTable(
      Table(
        firstCellIsHeader = true,
        head = Some(Seq(
          HeadCell(content = Text(messages("viewAllMessages.table.column.message.label"))),
          HeadCell(content = Text(messages("viewAllMessages.table.column.status.label"))),
          HeadCell(content = Text(messages("viewAllMessages.table.column.date.label"))),
          HeadCell(content = Text(messages("viewAllMessages.table.column.action.label"))),
        )),
        rows = dataRows(allMessages)
      )
    )
  }

  private def formattedMessageDescription(aMessage: Message)(implicit messages: Messages): String = {
    if (aMessage.isAnErrorMessage) {
      messages(s"viewAllMessages.${aMessage.messageType}.${aMessage.submittedByRequestingTrader}.${aMessage.relatedMessageType.get}.${aMessage.messageRole}.description")
    } else {
      messages(s"viewAllMessages.${aMessage.messageType}.${aMessage.submittedByRequestingTrader}.${aMessage.messageRole}.description")
    }
  }

  private def statusTag(readIndicator: Boolean)(implicit messages: Messages): Tag = if (readIndicator) {
    TagViewModel(Text(messages("viewAllMessages.table.column.status.read.label"))).grey()
  } else {
    TagViewModel(Text(messages("viewAllMessages.table.column.status.unread.label"))).blue()
  }

}
