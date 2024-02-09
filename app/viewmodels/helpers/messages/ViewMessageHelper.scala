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

import config.AppConfig
import models.requests.DataRequest
import models.response.emcsTfe.messages.Message
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.html.components.GovukTable
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Empty, HtmlContent, NonEmptyHtml, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{Table, TableRow}
import utils.DateUtils
import viewmodels.govuk.TagFluency
import views.html.components.{link, list, p}

import javax.inject.Inject

class ViewMessageHelper @Inject()(
                                   appConfig: AppConfig,
                                   messagesHelper: MessagesHelper,
                                   list: list,
                                   link: link,
                                   p: p,
                                   govukTable: GovukTable) extends DateUtils with TagFluency {

  private[viewmodels] def dataRows(message: Message)
                                  (implicit messages: Messages): Seq[Seq[TableRow]] = {
    Seq(
      Seq(
        TableRow(content = Text(messages("viewMessage.table.messageType.label"))),
        TableRow(content = Text(messages(messagesHelper.formattedMessageType(message))))
      ),
      Seq(
        TableRow(content = Text(messages("viewMessage.table.arc.label"))),
        TableRow(content = Text(message.arc.getOrElse("")))
      ),
      Seq(
        TableRow(content = Text(messages("viewMessage.table.lrn.label"))),
        TableRow(content = Text(message.lrn.getOrElse("")))
      )
    )
  }

  def constructTable(message: Message)(implicit messages: Messages): Html = {
    govukTable(
      Table(
        firstCellIsHeader = true,
        head = None,
        rows = dataRows(message)
      )
    )
  }

  def constructInformation(message: Message)(implicit messages: Messages): Html = {
    message.messageType -> message.submittedByRequestingTrader match {
      case "IE813" -> false =>
        p() {
          Html(messages("messages.IE813.false.0.p1"))
        }
      case _ =>
        Empty.asHtml
    }
  }

  def constructActions(message: Message)(implicit request: DataRequest[_], messages: Messages): Html = {

    def viewMovementLink(): Html =
      link(
        link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, message.arc.getOrElse("")).url,
        messageKey = "viewMessage.link.viewMovement.description",
        id = Some("view-movement")
      )

    def printMessageLink(): Html =
      link(
        link = "#print-dialogue",
        messageKey = "viewMessage.link.printMessage.description",
        id = Some("print-link")
      )

    def deleteMessageLink(): Html =
      link(
        link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
        messageKey = "viewMessage.link.deleteMessage.description",
        id = Some("delete-message")
      )

    val actionLinksContent = message.messageType -> message.submittedByRequestingTrader match {
      case _ -> _ =>
        Seq(viewMovementLink(), printMessageLink(), deleteMessageLink())
    }

    list(
      content = actionLinksContent,
      extraClasses = Some("govuk-!-display-none-print")
    )
  }

}
