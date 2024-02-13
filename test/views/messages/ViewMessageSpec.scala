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
import fixtures.messages.ViewMessageMessages.English
import fixtures.{GetSubmissionFailureMessageFixtures, MessagesFixtures}
import models.messages.MessageCache
import models.requests.DataRequest
import models.response.emcsTfe.messages.Message
import models.response.emcsTfe.messages.submissionFailure.{GetSubmissionFailureMessageResponse, IE704FunctionalError}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.messages._
import views.html.messages.ViewMessage
import views.{BaseSelectors, ViewBehaviours}

class ViewMessageSpec extends ViewSpecBase with ViewBehaviours with MessagesFixtures with GetSubmissionFailureMessageFixtures {

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
    override val link: Int => String = i => s"main p:nth-of-type($i) a"
    def summaryRowKey(i: Int, j: Int): String = s"main dl:nth-of-type($i) div:nth-of-type($j) dt"
    def summaryRowValue(i: Int, j: Int): String = s"main dl:nth-of-type($i) div:nth-of-type($j) dd"
  }

  def asDocument(message: Message, optErrorMessage: Option[GetSubmissionFailureMessageResponse] = None)
                (implicit messages: Messages): Document = Jsoup.parse(view(
    MessageCache(
      ern = testErn,
      message = message,
      errorMessage = optErrorMessage
    )
  ).toString())

  def movementActionsLinksTest()(implicit doc: Document): Unit = {
    behave like pageWithExpectedElementsAndMessages(
      Seq(
        Selectors.viewMovementLink -> English.viewMovementLinkText,
        Selectors.printMessageLink -> English.printMessageLinkText,
        Selectors.deleteMessageLink -> English.deleteMessageLinkText
      )
    )
  }

  Seq(
    ie819ReceivedAlert, ie819ReceivedReject, ie819SubmittedAlert, ie819SubmittedReject,
    ie810ReceivedCancellation, ie810SubmittedCancellation,
    ie813ReceivedChangeDestination, ie813SubmittedChangeDestination
  ).foreach { msg =>

    s"when being rendered with a ${msg.message.messageType} ${msg.messageSubTitle} msg" should {
      implicit val doc: Document = asDocument(msg.message)

      "show the correct table content" when {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.title -> s"${msg.messageTitle} - Excise Movement and Control System - GOV.UK",
            Selectors.h1 -> msg.messageTitle,

            Selectors.summaryRowKey(1) -> English.labelMessageType,
            Selectors.summaryRowValue(1) -> msg.messageSubTitle,
            Selectors.summaryRowKey(2) -> English.labelArc,
            Selectors.summaryRowValue(2) -> message1.arc.get,
            Selectors.summaryRowKey(3) -> English.labelLrn,
            Selectors.summaryRowValue(3) -> message1.lrn.get
          )
        )
      }

      "show the correct actions" when {
        movementActionsLinksTest()

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

  "when being rendered for a IE704" should {
    "for a IE810 related message type" must {

      val ie810Message = message2.copy(relatedMessageType = Some("IE810"), arc = Some(testArc))

      def movementInformationTest()(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.title -> s"${ie704ErrorCancellationIE810.messageTitle} - Excise Movement and Control System - GOV.UK",
            Selectors.h1 -> ie704ErrorCancellationIE810.messageTitle,
            Selectors.summaryRowKey(1) -> English.labelArc,
            Selectors.summaryRowValue(1) -> ie810Message.arc.get,
            Selectors.summaryRowKey(2) -> English.labelLrn,
            Selectors.summaryRowValue(2) -> ie810Message.lrn.get
          )
        )
      }

      def errorRowsTest(failureMessageResponse: GetSubmissionFailureMessageResponse)(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.summaryRowKey(2, 1) -> failureMessageResponse.ie704.body.functionalError.head.errorType,
            Selectors.summaryRowValue(2, 1) -> messages(s"messages.IE704.error.${failureMessageResponse.ie704.body.functionalError.head.errorType}")
          )
        )
      }

      def thirdPartySubmissionTest(index: Int)(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.p(index) -> English.thirdParty
          )
        )
      }

      def helplineLinkTest(index: Int)(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.p(index) -> English.helpline,
            Selectors.link(index) -> English.helplineLink
          )
        )
      }

      "render the correct content (when non-fixable) - portal" when {
        val failureMessageResponse = GetSubmissionFailureMessageResponse(
          ie704 = IE704ModelFixtures.ie704ModelModel.copy(
            header = IE704HeaderFixtures.ie704HeaderModel.copy(correlationIdentifier = Some("PORTAL12345")),
            body = IE704BodyFixtures.ie704BodyModel.copy(functionalError = Seq(
              IE704FunctionalError(
                errorType = "4403",
                errorReason = "Oh no! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules",
                errorLocation = Some("/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]"),
                originalAttributeValue = Some("lrnie8155639254")
              )
            ))
          ),
          relatedMessageType = Some("IE810")
        )
        val ie810Message = message2.copy(relatedMessageType = Some("IE810"), arc = Some(testArc))
        implicit val doc: Document = asDocument(ie810Message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest()
        errorRowsTest(failureMessageResponse)
        helplineLinkTest(1)
        movementActionsLinksTest()
      }

      "render the correct content (when non-fixable) - 3rd party" when {
        val failureMessageResponse = GetSubmissionFailureMessageResponse(
          ie704 = IE704ModelFixtures.ie704ModelModel.copy(
            body = IE704BodyFixtures.ie704BodyModel.copy(functionalError = Seq(
              IE704FunctionalError(
                errorType = "4403",
                errorReason = "Oh no! Duplicate LRN The LRN is already known and is therefore not unique according to the specified rules",
                errorLocation = Some("/IE813[1]/Body[1]/SubmittedDraftOfEADESAD[1]/EadEsadDraft[1]/LocalReferenceNumber[1]"),
                originalAttributeValue = Some("lrnie8155639254")
              )
            ))
          ),
          relatedMessageType = Some("IE810")
        )
        val ie810Message = message2.copy(relatedMessageType = Some("IE810"), arc = Some(testArc))
        implicit val doc: Document = asDocument(ie810Message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest()
        errorRowsTest(failureMessageResponse)
        thirdPartySubmissionTest(1)
        helplineLinkTest(2)
        movementActionsLinksTest()
      }
    }
  }

}
