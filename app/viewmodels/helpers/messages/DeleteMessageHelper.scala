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

import config.AppConfig
import models.response.emcsTfe.messages.Message
import play.api.i18n.Messages
import play.twirl.api.Html
import utils.DateUtils
import viewmodels.govuk.TagFluency
import views.html.components._

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class DeleteMessageHelper @Inject()(
                                     appConfig: AppConfig,
                                     messagesHelper: MessagesHelper,
                                     list: list,
                                     link: link,
                                     p: p,
                                     summary_list: summary_list,
                                     h2: h2) extends DateUtils with TagFluency {


  private def toDateString(dateTime: LocalDateTime): String = {
    val dateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy 'at' h:mm a", Locale.UK)
    dateTime.format(dateFormat)
  }

  def constructMessageInformation(message: Message)(implicit messages: Messages): Html = {
    summary_list(
      Seq(
        messages("deleteMessage.table.message.label") ->  messages(message.messageType),
        messages("deleteMessage.table.arc.label") -> messages(message.arc.getOrElse("")),
        messages("deleteMessage.table.lrn.label") -> messages(message.lrn.getOrElse("")),
        messages("deleteMessage.table.dateAndTimeReceived.label") -> toDateString(message.dateCreatedOnCore)
      )
    )
  }


}