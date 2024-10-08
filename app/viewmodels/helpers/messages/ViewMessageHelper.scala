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
import views.ViewUtils
import views.html.components._

import javax.inject.Inject

class ViewMessageHelper @Inject()(
                                   appConfig: AppConfig,
                                   messagesHelper: MessagesHelper,
                                   list: list,
                                   link: link,
                                   p: p,
                                   summary_list: summary_list,
                                   warning_text: warning_text,
                                   bullets: bullets,
                                   h2: h2) extends DateUtils with TagFluency {

  def constructSplitMessageErrorContent(message: MessageCache)(implicit messages: Messages): Html = {
    message.errorMessage.map { errMsg =>
      errMsg.relatedMessageType match {
        case Some("IE825") => p()(Html(messages(s"messages.IE704.IE825.error.p")))
        case _ => Empty.asHtml
      }
    }.getOrElse(Empty.asHtml)
  }

  def constructMovementInformation(messageCache: MessageCache)(implicit messages: Messages): Html = {
    val is704ForIE815 = messageCache.errorMessage.exists(_.relatedMessageType.contains("IE815"))
    //In some cases (according to QA data) the LRN may not be in the message itself, but it's in the error message so we may
    //need to fallback on that
    val lrnFromErrorMessage = messageCache.errorMessage.flatMap(_.ie704.body.attributes.flatMap(_.lrn))
    val optMessageTypeRow = messagesHelper.messageTypeKey(messageCache.message).map( value =>
      Seq(messages("viewMessage.table.messageType.label") -> messages(value))
    ).getOrElse(Seq.empty)
    HtmlFormat.fill(Seq(
      if(is704ForIE815) Some(h2("messages.IE704.IE815.h2")) else None,
      Some(summary_list(optMessageTypeRow ++ Seq(
        if(!is704ForIE815) Some(messages("viewMessage.table.arc.label") -> messages(messageCache.message.arc.getOrElse(""))) else None,
        Some(messages("viewMessage.table.lrn.label") -> messages(messageCache.message.lrn.orElse(lrnFromErrorMessage).getOrElse("")))
      ).flatten))
    ).flatten)
  }

  def constructAdditionalInformation(message: Message, movement: Option[GetMovementResponse])(implicit request: DataRequest[_], messages: Messages): Html =
    messagesHelper.additionalInformationKey(message) -> message.messageType match {
      case Some(_) -> "IE871" if movement.exists(!_.isBeingViewedByConsignee) =>
        Empty.asHtml
      case Some(key) -> "IE802" if (movement.exists(_.isBeingViewedByConsignor) && message.messageRole == 2) =>
        p() { Html(messages(key + ".consignor")) }
      case Some(key) -> _ =>
        p() { Html(messages(key)) }
      case _ =>
        Empty.asHtml
    }

  //scalastyle:off cyclomatic.complexity method.length
  def constructActions(messageCache: MessageCache, movement: Option[GetMovementResponse])(implicit request: DataRequest[_], messages: Messages): Html = {
    val message = messageCache.message
    lazy val reportOfReceiptLink: Html = link(
      link = appConfig.emcsTfeReportAReceiptUrl(request.ern, message.arc.getOrElse("")),
      messageKey = "viewMessage.link.reportOfReceipt.description", id = Some("submit-report-of-receipt"))
    lazy val explainDelayLink: Html = link(
      link = appConfig.emcsTfeExplainDelayUrl(request.ern, message.arc.getOrElse("")),
      messageKey = "viewMessage.link.explainDelay.description", id = Some("submit-explain-delay")
    )
    lazy val changeDestinationLink: Html = link(
      link = controllers.cod.routes.ChangeOfDestinationController.onPageLoad(request.ern, message.arc.getOrElse(""), message.sequenceNumber.getOrElse(1)).url,
      messageKey = "viewMessage.link.changeDestination.description", id = Some("submit-change-destination")
    )
    lazy val explainShortageExcessLink: Html = link(
      link = appConfig.emcsTfeExplainShortageOrExcessUrl(request.ern, message.arc.getOrElse("")),
      messageKey = "viewMessage.link.explainShortageExcess.description", id = Some("submit-shortage-excess")
    )
    lazy val viewMovementLink: Html = link(
      link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, message.arc.getOrElse("")).url,
      messageKey = "viewMessage.link.viewMovement.description", id = Some("view-movement")
    )
    lazy val printMessageLink: Html = link(
      link = "#print-dialogue", messageKey = "viewMessage.link.printMessage.description", id = Some("print-link"), classes = "govuk-link js-visible"
    )
    lazy val deleteMessageLink: Html = link(
      link = controllers.messages.routes.DeleteMessageController.onPageLoad(request.ern, message.uniqueMessageIdentifier).url,
      messageKey = "viewMessage.link.deleteMessage.description", id = Some("delete-message")
    )
    val actionLinks = (message.messageType, message.submittedByRequestingTrader, message.messageRole,
      messageCache.errorMessage.flatMap(_.relatedMessageType)) match {
      case ("IE801", true, _, _) =>
        Seq(viewMovementLink, changeDestinationLink, printMessageLink, deleteMessageLink)
      case ("IE802", false, 1, _) =>
        Seq(changeDestinationLink, explainDelayLink,viewMovementLink, printMessageLink, deleteMessageLink)
      case ("IE802", false, 2, _) if movement.exists(_.isBeingViewedByConsignee) =>
        Seq(viewMovementLink, reportOfReceiptLink, explainDelayLink, printMessageLink, deleteMessageLink)
      case ("IE802", false, 2, _) =>
        Seq(viewMovementLink, printMessageLink, deleteMessageLink)
      case ("IE802", false, 3, _) =>
        Seq(changeDestinationLink, explainDelayLink, viewMovementLink, printMessageLink, deleteMessageLink)
      case ("IE829", false, _, _) | ("IE837", true, 2, _) | ("IE839", false, _, _) =>
        Seq(changeDestinationLink, viewMovementLink, printMessageLink, deleteMessageLink)
      case ("IE813", false, _, _) =>
        Seq(reportOfReceiptLink, explainDelayLink, viewMovementLink, printMessageLink, deleteMessageLink)
      case ("IE818", true, _, _) =>
        Seq(explainShortageExcessLink, viewMovementLink, printMessageLink, deleteMessageLink)
      case ("IE837", true, 1, _) =>
        Seq(reportOfReceiptLink, viewMovementLink, printMessageLink, deleteMessageLink)
      case ("IE871", true, _, _) if movement.exists(_.isBeingViewedByConsignee) =>
        Seq(reportOfReceiptLink, viewMovementLink, printMessageLink, deleteMessageLink)
      case (_, _, _, Some("IE815")) =>
        Seq(printMessageLink, deleteMessageLink)
      case _ =>
        Seq(viewMovementLink, printMessageLink, deleteMessageLink)
    }
    list(
      content = actionLinks,
      extraClasses = Some("govuk-!-display-none-print")
    )
  }

  def constructErrors(message: MessageCache)(implicit messages: Messages): Html = {
    message.errorMessage.map {
      failureMessage => {
        val allErrorCodes = failureMessage.ie704.body.functionalError.map(_.errorType)
        val numberOfNonFixableErrors = allErrorCodes.count(!appConfig.recoverableErrorCodes.contains(_))
        failureMessage.relatedMessageType match {
          case Some("IE815") if numberOfNonFixableErrors == 0 && failureMessage.draftMovementExists => Html("")
          case _ => HtmlFormat.fill(Seq(
            h2("messages.errors.heading"),
            summary_list(
              failureMessage.ie704.body.functionalError.map { error =>
                val mappedErrorMessage = Some(s"messages.IE704.error.${error.errorType}").filter(messages.isDefinedAt)
                error.errorType -> mappedErrorMessage.map(messages(_)).getOrElse(error.errorReason)
              }
            )
          ))
        }
      }
    }.getOrElse(Html(""))
  }

  def constructFixErrorsContent(messageCache: MessageCache)(implicit messages: Messages): Html = {
    implicit val msgCache: MessageCache = messageCache
    messageCache.errorMessage.map { failureMessage =>
      val allErrorCodes = failureMessage.ie704.body.functionalError.map(_.errorType)
      val numberOfNonFixableErrors = allErrorCodes.count(!appConfig.recoverableErrorCodes.contains(_))
      failureMessage.relatedMessageType match {
        case Some(relatedMessageType) => HtmlFormat.fill(
          contentForFixingError(relatedMessageType, allErrorCodes.size, numberOfNonFixableErrors, failureMessage.draftMovementExists) ++
            contentForSubmittedVia3rdParty(failureMessage.draftMovementExists, relatedMessageType, msgCache.ern, msgCache.message.arc.getOrElse("")) ++
            Seq(Option.when(relatedMessageType == "IE815")(p()(Html(messages("messages.IE704.IE815.arc.text"))))).flatten ++
            contentForContactingHelpdesk())
        case _ => Html("")
      }
    }.getOrElse(Html(""))

  }

  def showWarningTextIfFixableIE815(messageCache: MessageCache)(implicit messages: Messages): Html = {
    messageCache.errorMessage.map {
      failureMessage =>
        val allErrorCodes = failureMessage.ie704.body.functionalError.map(_.errorType)
        val numberOfNonFixableErrors = allErrorCodes.count(!appConfig.recoverableErrorCodes.contains(_))
        if(numberOfNonFixableErrors == 0 && failureMessage.relatedMessageType.contains("IE815") && failureMessage.draftMovementExists) {
          warning_text(Html(messages("messages.IE704.IE815.fixError.fixable.warning")))
        } else {
          Html("")
        }
    }.getOrElse(Html(""))
  }

  //scalastyle:off
  private[helpers] def contentForFixingError(messageType: String, numberOfErrors: Int, numberOfNonFixableErrors: Int, draftMovementExists: Boolean)
                                            (implicit messages: Messages, messageCache: MessageCache): Seq[Html] = {
    val ern = messageCache.ern
    val arc = messageCache.message.arc.getOrElse("")
    val ver = messageCache.message.sequenceNumber.getOrElse(1)
    val uniqueMessageId = messageCache.message.uniqueMessageIdentifier
    messageType match {
      case "IE815" if numberOfNonFixableErrors == 0 && draftMovementExists =>
        Seq(
          p()(HtmlFormat.fill(
            Seq(
              link(controllers.messages.routes.ViewMessageController.removeMessageAndRedirectToDraftMovement(ern, uniqueMessageId).url, "messages.IE704.IE815.fixError.fixable.link", id = Some("update-draft-movement"), withFullStop = true),
              Html(messages("messages.IE704.IE815.fixError.fixable.link.afterLink"))
            )
          ))
        )
      case "IE815" if numberOfNonFixableErrors > 0 =>
        Seq(
          p()(HtmlFormat.fill(Seq(
            Html(ViewUtils.pluralSingular("messages.IE704.IE815.fixError.nonFixable.text", numberOfErrors)),
            link(appConfig.emcsTfeCreateMovementUrl(ern), "messages.IE704.IE815.fixError.nonFixable.link.createNewMovement", id = Some("create-a-new-movement"), withFullStop = true),
            Html(messages("messages.IE704.IE815.fixError.nonFixable.software"))
          )))
        )
      case "IE815" if !draftMovementExists =>
        Seq(
          p()(Html(messages("messages.IE704.IE815.fixError.fixable.expired.p1"))),
          bullets(Seq(
            Html(messages("messages.IE704.IE815.fixError.fixable.expired.bullet1")),
            Html(messages("messages.IE704.IE815.fixError.fixable.expired.bullet2"))
          )),
          p()(HtmlFormat.fill(Seq(
            Html(messages("messages.IE704.IE815.fixError.fixable.expired.p2.preLink")),
            link(appConfig.emcsTfeCreateMovementUrl(ern), "messages.IE704.IE815.fixError.fixable.expired.p2.link", id = Some("create-a-new-movement"), withFullStop = false),
            Html(messages("messages.IE704.IE815.fixError.fixable.expired.p2.afterLink"))
          )))
        )
      case "IE810" =>
        Seq(
          p()(HtmlFormat.fill(Seq(
            Html(messages("messages.IE704.IE810.fixError.cancel.prefix")),
            link(appConfig.emcsTfeCancelMovementUrl(ern, arc), "messages.IE704.IE810.fixError.cancel.link", id = Some("cancel-movement")),
            Html(messages("messages.IE704.IE810.fixError.cancel.suffix"))
          ))),
          p()(HtmlFormat.fill(Seq(
            Html(messages("messages.IE704.IE810.fixError.changeDestination")),
            link(controllers.cod.routes.ChangeOfDestinationController.onPageLoad(ern, arc, ver).url, "messages.IE704.IE810.fixError.changeDestination.link", withFullStop = true, id = Some("submit-change-destination"))
          )))
        )
      case "IE837" =>
        Seq(p()(HtmlFormat.fill(Seq(
          Html(messages("messages.IE704.IE837.fixError.text")),
          link(appConfig.emcsTfeExplainDelayUrl(ern, arc), "messages.IE704.IE837.fixError.link", withFullStop = true, id = Some("submit-new-explanation-for-delay"))
        ))))
      case "IE871" =>
        Seq(p()(HtmlFormat.fill(Seq(
          Html(messages("messages.IE704.IE871.fixError.text")),
          link(appConfig.emcsTfeExplainShortageOrExcessUrl(ern, arc), "messages.IE704.IE871.fixError.link", withFullStop = true, id = Some("submit-a-new-explanation-for-shortage-or-excess"))
        ))))
      case "IE818" =>
        Seq(p()(HtmlFormat.fill(Seq(
          Html(messages("messages.IE704.IE818.fixError.text")),
          link(
            link = appConfig.emcsTfeReportAReceiptUrl(ern, arc),
            messageKey = "messages.IE704.IE818.fixError.link",
            withFullStop = true,
            id = Some("submit-a-new-report-of-receipt")
          )
        ))))
      case "IE819" =>
        Seq(p()(HtmlFormat.fill(Seq(
          Html(messages("messages.IE704.IE819.fixError.text.1")),
          link(appConfig.emcsTfeAlertOrRejectionUrl(ern, arc), "messages.IE704.IE819.fixError.link", id = Some("submit-a-new-alert-rejection")),
          Html(messages("messages.IE704.IE819.fixError.text.2"))
        ))))
      case "IE813" if draftMovementExists =>
        Seq(p()(HtmlFormat.fill(Seq(
          Html(messages("messages.IE704.IE813.fixError.text")),
          link(controllers.cod.routes.ChangeOfDestinationController.onPageLoad(ern, arc, ver).url, "messages.IE704.IE813.fixError.link", id = Some("submit-change-destination"), withFullStop = true)
        ))))
      case _ => Seq.empty
    }
  }

  private[helpers] def contentForSubmittedVia3rdParty(draftMovementExists: Boolean, relatedMessageType: String, ern: String, arc: String = "", ver: Int = 1)
                                                     (implicit messages: Messages): Seq[Html] = {
    if (!draftMovementExists) {
      relatedMessageType match {
        //It's not possible to know if it was submitted via 3rd Party with EIS.
        //This method will be removed as part of future alignment tickets. For now, only deal with IE815 and set this content to Empty
        case "IE815" => Seq()
        case "IE813" => Seq(p()(HtmlFormat.fill(Seq(
          Html(messages("messages.submittedViaThirdParty.ie813")),
          link(controllers.cod.routes.ChangeOfDestinationController.onPageLoad(ern, arc, ver).url, "messages.submittedViaThirdParty.ie813.link", id = Some("submit-change-destination"), withFullStop = true)
        ))))
        case _ => Seq(p()(Html(messages("messages.submittedViaThirdParty"))))
      }
    } else {
      Seq.empty
    }
  }

  private[helpers] def contentForContactingHelpdesk()(implicit messages: Messages): Seq[Html] = {
    Seq(p()(HtmlFormat.fill(Seq(
      link(appConfig.exciseHelplineUrl, "messages.link.helpline", id = Some("contactHmrc"), isExternal = true),
      Html(messages("messages.link.helpline.text"))
    ))))
  }

}
