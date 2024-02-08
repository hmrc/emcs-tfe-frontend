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

package views.messages

import base.ViewSpecBase
import fixtures.MessagesFixtures
import fixtures.messages.ViewMessageMessages.English
import models.messages.MessageCache
import models.requests.DataRequest
import models.response.emcsTfe.messages.Message
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.messages._
import views.html.messages.ViewMessage
import views.{BaseSelectors, ViewBehaviours}

class ViewMessageSpec extends ViewSpecBase with ViewBehaviours with MessagesFixtures {

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))
  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/message")
  implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

  lazy val view: ViewMessage = app.injector.instanceOf[ViewMessage]
  lazy val helper: MessagesHelper = app.injector.instanceOf[MessagesHelper]
  lazy val viewMessageHelper: ViewMessageHelper = app.injector.instanceOf[ViewMessageHelper]

  object Selectors extends BaseSelectors {
    val viewMovementLink = "#view-movement"
    val printMessageLink = "#print-link"
    val deleteMessageLink = "#delete-message"
    val reportOfReceiptLink = "#submit-report-of-receipt"
    val explainDelayLink = "#submit-explain-delay"
  }

  def asDocument(message: Message)(implicit messages: Messages): Document = Jsoup.parse(view(
    MessageCache(
      ern = testErn,
      message = message
    )
  ).toString())

  Seq(
    ie819ReceivedAlert, ie819ReceivedReject, ie819SubmittedAlert, ie819SubmittedReject,
    ie810ReceivedCancellation, ie810SubmittedCancellation,
    ie813ReceivedChangeDestination
  ).foreach{ msg =>

    s"when being rendered with a ${msg.message.messageType} ${msg.messageSubTitle} msg" should {
      implicit val doc: Document = asDocument(msg.message)

      "show the correct table content" when {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.title -> s"${msg.messageTitle} - Excise Movement and Control System - GOV.UK",
            Selectors.h1 -> msg.messageTitle,

            Selectors.tableRow(1, 1) -> English.labelMessageType,
            Selectors.tableRow(1, 2) -> msg.messageSubTitle,
            Selectors.tableRow(2, 1) -> English.labelArc,
            Selectors.tableRow(2, 2) -> message1.arc.getOrElse(""),
            Selectors.tableRow(3, 1) -> English.labelLrn,
            Selectors.tableRow(3, 2) -> message1.lrn.getOrElse("")
          )
        )
      }

      "show the correct actions" when {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.viewMovementLink -> English.viewMovementLinkText,
            Selectors.printMessageLink -> English.printMessageLinkText,
            Selectors.deleteMessageLink -> English.deleteMessageLinkText
          )
        )

        if (msg.reportOfReceiptLink) {
          behave like pageWithExpectedElementsAndMessages(
            Seq(
              Selectors.reportOfReceiptLink -> English.reportOfReceiptLinkText
            )
          )
        }

        if (msg.explainDelayLink) {
          behave like pageWithExpectedElementsAndMessages(
            Seq(
              Selectors.explainDelayLink -> English.explainDelayLinkText
            )
          )
        }
      }
    }
  }

  s"when an IE813 message" should {
    "contain other information" when {
      implicit val doc: Document = asDocument(ie813ReceivedChangeDestination.message)

      behave like pageWithExpectedElementsAndMessages(
        Seq(
          Selectors.p(1) -> "The destination of the movement has been changed."
        )
      )
    }
  }

}
