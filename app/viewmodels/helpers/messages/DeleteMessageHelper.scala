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

package viewmodels.helpers.messages

import models.response.emcsTfe.messages.Message
import pages.{Page, ViewAllMessagesPage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, SummaryListRow, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import utils.DateUtils
import viewmodels.govuk.TagFluency

import javax.inject.Inject


class DeleteMessageHelper @Inject()(messagesHelper: MessagesHelper) extends DateUtils with TagFluency {

  def constructMessageInformation(message: Message)(implicit messages: Messages): SummaryList = {
    SummaryList(
      rows = Seq(
        SummaryListRow(key = Key(content = Text(messages("deleteMessage.table.message.label"))), value = Value(Text(messages(messagesHelper.messageDescriptionKey(message))))),
        SummaryListRow(key = Key(content = Text(messages("deleteMessage.table.arc.label"))), value = Value(Text(messages(messages(message.arc.getOrElse("")))))),
        SummaryListRow(key = Key(content = Text(messages("deleteMessage.table.lrn.label"))), value = Value(Text(messages(messages(message.lrn.getOrElse("")))))),
        SummaryListRow(key = Key(content = Text(messages("deleteMessage.table.dateAndTimeReceived.label"))), value = Value(Text(message.dateCreatedOnCore.formatDateTimeForUIOutput())))
      ),
      classes = "govuk-!-margin-top-7 govuk-!-margin-bottom-7"
    )
  }

  def options(fromPage: Page)(implicit messages: Messages): Seq[RadioItem] = {
    val key = fromPage match {
      case ViewAllMessagesPage => "deleteMessage.fromMessageInbox.no"
      case _ => "deleteMessage.fromMessagePage.no"
    }

    Seq(
      RadioItem(content = Text(messages("deleteMessage.yes")), value = Some("true")),
      RadioItem(content = Text(messages(key)), value = Some("false"), id = Some("value-no"))
    )
  }

}
