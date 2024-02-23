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
import pages.{Page, ViewAllMessagesPage, ViewMessagePage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, SummaryListRow, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import utils.DateUtils
import viewmodels.govuk.TagFluency

import javax.inject.Inject


class DeleteMessageHelper @Inject()() extends DateUtils with TagFluency {

  //scalastyle:off
  def getMessageTitleKey(message: Message): String =
    (message.messageType, message.sequenceNumber, message.messageRole) match {
      case ("IE801", Some(1), 0) => s"deleteMessage.${message.messageType}.first.label"
      case ("IE801", _, 0) => s"deleteMessage.${message.messageType}.further.label"
      case ("IE802", _, 1) => s"deleteMessage.${message.messageType}.cod.label"
      case ("IE802", _, 2) => s"deleteMessage.${message.messageType}.ror.label"
      case ("IE802", _, 3) => s"deleteMessage.${message.messageType}.des.label"
      case ("IE803", _, 1) => s"deleteMessage.${message.messageType}.diverted.label"
      case ("IE803", _, 2) => s"deleteMessage.${message.messageType}.split.label"
      case ("IE840", _, 1) => s"deleteMessage.${message.messageType}.first.label"
      case ("IE840", _, 2) => s"deleteMessage.${message.messageType}.complementary.label"
      case _ => s"deleteMessage.${message.messageType}.label"
    }


  def constructMessageInformation(message: Message)(implicit messages: Messages): SummaryList = {
    SummaryList(
      rows = Seq(
        SummaryListRow(key = Key(content = Text(messages("deleteMessage.table.message.label"))), value = Value(Text(messages(getMessageTitleKey(message))))),
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
      case ViewMessagePage | _ => "deleteMessage.fromMessagePage.no"
    }

    Seq(
      RadioItem(content = Text(messages("deleteMessage.yes")), value = Some("true")),
      RadioItem(content = Text(messages(key)), value = Some("false"))
    )
  }

}