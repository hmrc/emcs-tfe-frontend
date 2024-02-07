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
import viewmodels.helpers.messages.{MessagesHelper, ViewMessageHelper}
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
  }

  def asDocument(message: Message)(implicit messages: Messages): Document = Jsoup.parse(view(
    MessageCache(
      ern = testErn,
      message = message
    )
  ).toString())


  case class TestMessage(message: Message, messageTitle: String, messageSubTitle: String)

  val ie819ReceivedAlert = TestMessage(ie819AlertReceived, "Alert or rejection received", "Alert received")
  val ie819ReceivedReject = TestMessage(ie819RejectReceived, "Alert or rejection received", "Rejection received")
  val ie819SubmittedAlert = TestMessage(ie819AlertSubmitted, "Alert or rejection submitted successfully", "Alert successful submission")
  val ie819SubmittedReject = TestMessage(ie819RejectSubmitted, "Alert or rejection submitted successfully", "Rejection successful submission")
  val ie810ReceivedCancellation = TestMessage(ie810CancellationReceived, "Cancellation received", "Cancellation of movement")
  val ie810SubmittedCancellation = TestMessage(ie810CancellationSubmitted, "Cancellation submitted successfully", "Cancellation of movement successful submission")

  Seq(
    ie819ReceivedAlert, ie819ReceivedReject, ie819SubmittedAlert, ie819SubmittedReject,
    ie810ReceivedCancellation, ie810SubmittedCancellation
  ).foreach{ msg =>

    s"when being rendered with a ${msg.message.messageType} ${msg.messageSubTitle} msg" should {
      "show the correct table content and available actions" when {
        implicit val doc: Document = asDocument(msg.message)

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

        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.viewMovementLink -> English.viewMovementLinkText,
            Selectors.printMessageLink -> English.printMessageLinkText,
            Selectors.deleteMessageLink -> English.deleteMessageLinkText
          )
        )

      }
    }

  }
}
