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
import models.messages.MessageCache
import models.requests.DataRequest
import models.response.emcsTfe.GetMovementResponse
import models.response.emcsTfe.messages.Message
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Empty
import utils.DateUtils
import viewmodels.govuk.TagFluency
import views.html.components._

import javax.inject.Inject

class ViewMessageHelper @Inject()(
                                   appConfig: AppConfig,
                                   messagesHelper: MessagesHelper,
                                   list: list,
                                   link: link,
                                   p: p,
                                   summary_list: summary_list,
                                   h2: h2) extends DateUtils with TagFluency {

  def constructMovementInformation(message: Message)(implicit messages: Messages): Html = {
    val optMessageTypeRow = messagesHelper.messageTypeKey(message).map( value =>
      Seq(messages("viewMessage.table.messageType.label") -> messages(value))
    ).getOrElse(Seq.empty)

    summary_list(optMessageTypeRow ++ Seq(
      messages("viewMessage.table.arc.label") -> messages(message.arc.getOrElse("")),
      messages("viewMessage.table.lrn.label") -> messages(message.lrn.getOrElse(""))
    ))
  }

  def constructAdditionalInformation(message: Message, movement: Option[GetMovementResponse])(implicit request: DataRequest[_], messages: Messages): Html =
    messagesHelper.additionalInformationKey(message) -> message.messageType match {
      case Some(_) -> "IE871" if movement.exists(!_.isConsigneeOfMovement(request.ern)) =>
        Empty.asHtml
      case Some(key) -> _ =>
        p() { Html(messages(key)) }
      case _ =>
        Empty.asHtml
    }


  def constructActions(message: Message, movement: Option[GetMovementResponse])(implicit request: DataRequest[_], messages: Messages): Html = {
    def reportOfReceiptLink(): Html = link(
        link = appConfig.emcsTfeReportAReceiptUrl(request.ern, message.arc.getOrElse("")),
        messageKey = "viewMessage.link.reportOfReceipt.description",
        id = Some("submit-report-of-receipt")
      )
    def explainDelayLink(): Html = link(
        link = appConfig.emcsTfeExplainDelayUrl(request.ern, message.arc.getOrElse("")),
        messageKey = "viewMessage.link.explainDelay.description",
        id = Some("submit-explain-delay")
      )
    def changeDestinationLink(): Html = link(
        link = appConfig.emcsTfeChangeDestinationUrl(request.ern, message.arc.getOrElse("")),
        messageKey = "viewMessage.link.changeDestination.description",
        id = Some("submit-change-destination")
      )
    def viewMovementLink(): Html = link(
        link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, message.arc.getOrElse("")).url,
        messageKey = "viewMessage.link.viewMovement.description",
        id = Some("view-movement")
      )
    def printMessageLink(): Html = link(
        link = "#print-dialogue",
        messageKey = "viewMessage.link.printMessage.description",
        id = Some("print-link")
      )
    def deleteMessageLink(): Html = link(
        link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
        messageKey = "viewMessage.link.deleteMessage.description",
        id = Some("delete-message")
      )

    val actionLinks = (message.messageType, message.submittedByRequestingTrader, message.messageRole) match {
      case ("IE801", true, _) =>
        Seq(viewMovementLink(), changeDestinationLink(), printMessageLink(), deleteMessageLink())
      case ("IE829", false, _) | ("IE837", true, 2) | ("IE839", false, _) =>
        Seq(changeDestinationLink(), viewMovementLink(), printMessageLink(), deleteMessageLink())
      case ("IE813", false, _) =>
        Seq(reportOfReceiptLink(), explainDelayLink(), viewMovementLink(), printMessageLink(), deleteMessageLink())
      case ("IE837", true, 1) =>
        Seq(reportOfReceiptLink(), viewMovementLink(), printMessageLink(), deleteMessageLink())
      case ("IE871", true, _) if movement.exists(_.isConsigneeOfMovement(request.ern)) =>
        Seq(reportOfReceiptLink(), viewMovementLink(), printMessageLink(), deleteMessageLink())
      case _ =>
        Seq(viewMovementLink(), printMessageLink(), deleteMessageLink())
    }

    list(
      content = actionLinks,
      extraClasses = Some("govuk-!-display-none-print")
    )
  }

  def constructErrors(message: MessageCache)(implicit messages: Messages): Html = {
    message.errorMessage.map {
      submissionFailureMessage => {
        HtmlFormat.fill(Seq(
          h2("messages.errors.heading"),
          summary_list(
            submissionFailureMessage.ie704.body.functionalError.map { error =>
              error.errorType -> messages(s"messages.IE704.error.${error.errorType}")
            }
          )
        ))
      }
    }.getOrElse(Html(""))
  }

  def constructFixErrorsContent(message: MessageCache)(implicit messages: Messages): Html = {
    message.errorMessage.map { failureMessage =>
      // If the correlation ID starts with PORTAL then it has been submitted via the frontend
      val hasBeenSubmittedVia3rdParty: Boolean = !failureMessage.ie704.header.correlationIdentifier.exists(_.toUpperCase.startsWith("PORTAL"))
      val allErrorCodes = failureMessage.ie704.body.functionalError.map(_.errorType)
      val hasFixableError = allErrorCodes.exists(appConfig.recoverableErrorCodes.contains(_))
      failureMessage.relatedMessageType match {
        case Some(relatedMessageType) => HtmlFormat.fill(
          contentForFixingError(relatedMessageType, hasFixableError, message.ern, message.message.arc.getOrElse("")) ++
            contentForSubmittedVia3rdParty(hasBeenSubmittedVia3rdParty) ++
            contentForContactingHelpdesk(relatedMessageType))
        case _ => Html("")
      }
    }.getOrElse(Html(""))

  }

  private[helpers] def contentForFixingError(messageType: String, hasFixableError: Boolean, ern: String, arc: String)
                                             (implicit messages: Messages): Seq[Html] = {
    messageType match {
      case "IE815" if hasFixableError => Seq(
        Html("placeholder")
      )
      case "IE810" =>
        Seq(
          p()(HtmlFormat.fill(Seq(
            Html(messages("messages.IE704.IE810.fixError.cancel.prefix")),
            link(appConfig.emcsTfeCancelMovementUrl(ern, arc), "messages.IE704.IE810.fixError.cancel.link"),
            Html(messages("messages.IE704.IE810.fixError.cancel.suffix"))
          ))),
          p()(HtmlFormat.fill(Seq(
            Html(messages("messages.IE704.IE810.fixError.changeDestination")),
            link(appConfig.emcsTfeChangeDestinationUrl(ern, arc), "messages.IE704.IE810.fixError.changeDestination.link", withFullStop = true)
          )))
        )
      case "IE837" =>
        Seq(p()(HtmlFormat.fill(Seq(
          Html(messages("messages.IE704.IE837.fixError.text")),
          link(appConfig.emcsTfeExplainDelayUrl(ern, arc), "messages.IE704.IE837.fixError.link", withFullStop = true)
        ))))
      case "IE871" =>
        Seq(p()(HtmlFormat.fill(Seq(
          Html(messages("messages.IE704.IE871.fixError.text")),
          link(appConfig.emcsTfeExplainShortageOrExcessUrl(ern, arc), "messages.IE704.IE871.fixError.link", withFullStop = true)
        ))))
      case _ => Seq.empty
    }
  }

  private[helpers] def contentForSubmittedVia3rdParty(hasBeenSubmittedVia3rdParty: Boolean)
                                                     (implicit messages: Messages): Seq[Html] = {
    if (hasBeenSubmittedVia3rdParty) {
      Seq(p()(Html(messages("messages.submittedViaThirdParty"))))
    } else {
      Seq.empty
    }
  }

  private[helpers] def contentForContactingHelpdesk(messageType: String)
                                                   (implicit messages: Messages): Seq[Html] = {
    messageType match {
      case "IE819" => Seq.empty
      case _ => Seq(p()(HtmlFormat.fill(Seq(
        link(appConfig.exciseHelplineUrl, "messages.link.helpline", id = Some("contactHmrc"), isExternal = true),
        Html(messages("messages.link.helpline.text"))
      ))))
    }
  }
}
