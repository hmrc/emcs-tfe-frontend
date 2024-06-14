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
import fixtures.{GetMovementResponseFixtures, GetSubmissionFailureMessageFixtures, MessagesFixtures}
import models.messages.MessageCache
import models.requests.DataRequest
import models.response.emcsTfe.GetMovementResponse
import models.response.emcsTfe.messages.Message
import models.response.emcsTfe.messages.submissionFailure.GetSubmissionFailureMessageResponse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.messages._
import views.html.messages.ViewMessageView
import views.{BaseSelectors, ViewBehaviours}

class ViewMessageViewSpec extends ViewSpecBase
  with ViewBehaviours
  with MessagesFixtures
  with GetMovementResponseFixtures
  with GetSubmissionFailureMessageFixtures {

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))
  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/message")
  implicit val dr: DataRequest[_] = dataRequest(fakeRequest)

  lazy val view: ViewMessageView = app.injector.instanceOf[ViewMessageView]
  lazy val helper: MessagesHelper = app.injector.instanceOf[MessagesHelper]
  lazy val viewMessageHelper: ViewMessageHelper = app.injector.instanceOf[ViewMessageHelper]

  object Selectors extends BaseSelectors {
    val viewMovementLink = "#view-movement"
    val printMessageLink = "#print-link"
    val deleteMessageLink = "#delete-message"
    val reportOfReceiptLink = "#submit-report-of-receipt"
    val explainDelayLink = "#submit-explain-delay"
    val changeDestinationLink = "#submit-change-destination"
    val explainShortageLink = "#submit-shortage-excess"
    override val link: Int => String = i => s"main p:nth-of-type($i) a"

    def summaryRowKey(i: Int, j: Int): String = s"main dl:nth-of-type($i) div:nth-of-type($j) dt"

    def summaryRowValue(i: Int, j: Int): String = s"main dl:nth-of-type($i) div:nth-of-type($j) dd"
  }

  def asDocument(
                  message: Message,
                  optErrorMessage: Option[GetSubmissionFailureMessageResponse] = None,
                  optMovement: Option[GetMovementResponse] = None
                )
                (implicit messages: Messages): Document = Jsoup.parse(view(
    MessageCache(
      ern = testErn,
      message = message,
      errorMessage = optErrorMessage
    ),
    movement = optMovement
  ).toString())

  def movementActionsLinksTest(withViewMovementLink: Boolean = true)(implicit doc: Document): Unit = {
    behave like pageWithExpectedElementsAndMessages(
      Seq(
        if(withViewMovementLink) Some(Selectors.viewMovementLink -> English.viewMovementLinkText) else None,
        Some(Selectors.printMessageLink -> English.printMessageLinkText),
        Some(Selectors.deleteMessageLink -> English.deleteMessageLinkText)
      ).flatten
    )
  }

  Seq(
    ie801ReceivedMovement, ie801SubmittedMovement,
    ie802ReminderToChangeDestination, ie802ReminderToProvideDestination,
    ie803ReceivedChangeDestination, ie803ReceivedSplit,
    ie818ReceivedReportOfReceipt, ie818SubmittedReportOfReceipt,
    ie819ReceivedAlert, ie819ReceivedReject, ie819SubmittedAlert, ie819SubmittedReject,
    ie810ReceivedCancellation, ie810SubmittedCancellation,
    ie813ReceivedChangeDestination, ie813SubmittedChangeDestination,
    ie829ReceivedCustomsAcceptance,
    ie837SubmittedExplainDelayROR, ie837SubmittedExplainDelayCOD,
    ie839ReceivedCustomsRejection,
    ie881ReceivedManualClosure
  ).foreach { msg =>

    s"when being rendered with a ${msg.message.messageType} ${msg.messageTitle} ${msg.messageSubTitle} msg" should {
      implicit val doc: Document = asDocument(msg.message)

      "show the correct title and H1" when {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.title -> s"${msg.messageTitle} - Excise Movement and Control System - GOV.UK",
            Selectors.h1 -> msg.messageTitle,
          )
        )
      }

      "show the correct table content" when {
        msg.messageSubTitle match {
          case Some(subTitle) =>
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.summaryRowKey(1) -> English.labelMessageType,
                Selectors.summaryRowValue(1) -> subTitle,
                Selectors.summaryRowKey(2) -> English.labelArc,
                Selectors.summaryRowValue(2) -> message1.arc.getOrElse(""),
                Selectors.summaryRowKey(3) -> English.labelLrn,
                Selectors.summaryRowValue(3) -> message1.lrn.getOrElse("")
              )
            )
          case None =>
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.summaryRowKey(1) -> English.labelArc,
                Selectors.summaryRowValue(1) -> message1.arc.getOrElse(""),
                Selectors.summaryRowKey(2) -> English.labelLrn,
                Selectors.summaryRowValue(2) -> message1.lrn.getOrElse("")
              )
            )
        }
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

        if (msg.changeDestinationLink) {
          behave like pageWithExpectedElementsAndMessages(
            Seq(
              Selectors.changeDestinationLink -> English.changeDestinationLinkText
            )
          )
        }

        if (msg.explainShortageExcess) {
          behave like pageWithExpectedElementsAndMessages(
            Seq(
              Selectors.explainShortageLink -> English.explainShortageExcessLinkText
            )
          )
        }
      }
    }
  }

  "when being rendered with an IE802 Reminder for report of receipt message" when {

    val testMessage = ie802ReminderToReportReceipt

    "the logged in user is not the consignee of the movement" should {

      val movementWithLoggedInUserAsConsignor = Some(getMovementResponseModel
        .copy(
          consignorTrader = getMovementResponseModel.consignorTrader.copy(traderExciseNumber = Some(testErn)),
          consigneeTrader = getMovementResponseModel.consigneeTrader.map(_.copy(traderExciseNumber = Some("GB00000000000")))
        )
      )

      implicit val doc: Document = asDocument(testMessage.message, optMovement = movementWithLoggedInUserAsConsignor)

      "show the correct title and H1" when {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.title -> s"${testMessage.messageTitle} - Excise Movement and Control System - GOV.UK",
            Selectors.h1 -> testMessage.messageTitle,
          )
        )
      }

      "show the correct table content" when {
        testMessage.messageSubTitle match {
          case Some(subTitle) =>
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.summaryRowKey(1) -> English.labelMessageType,
                Selectors.summaryRowValue(1) -> subTitle,
                Selectors.summaryRowKey(2) -> English.labelArc,
                Selectors.summaryRowValue(2) -> message1.arc.getOrElse(""),
                Selectors.summaryRowKey(3) -> English.labelLrn,
                Selectors.summaryRowValue(3) -> message1.lrn.getOrElse("")
              )
            )
          case None =>
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.summaryRowKey(1) -> English.labelArc,
                Selectors.summaryRowValue(1) -> message1.arc.getOrElse(""),
                Selectors.summaryRowKey(2) -> English.labelLrn,
                Selectors.summaryRowValue(2) -> message1.lrn.getOrElse("")
              )
            )
        }
      }

      "show the correct actions" when {
        movementActionsLinksTest()

        behave like pageWithElementsNotPresent(Seq(Selectors.reportOfReceiptLink))

        if (testMessage.explainDelayLink) {
          behave like pageWithExpectedElementsAndMessages(
            Seq(
              Selectors.explainDelayLink -> English.explainDelayLinkText
            )
          )
        }

        if (testMessage.changeDestinationLink) {
          behave like pageWithExpectedElementsAndMessages(
            Seq(
              Selectors.changeDestinationLink -> English.changeDestinationLinkText
            )
          )
        }
      }
    }

    "the logged in user is the consignee of the movement" should {

      val movementWithLoggedInUserAsConsignor = Some(getMovementResponseModel
        .copy(
          consignorTrader = getMovementResponseModel.consignorTrader.copy(traderExciseNumber = Some("GB00000000000")),
          consigneeTrader = getMovementResponseModel.consigneeTrader.map(_.copy(traderExciseNumber = Some(testErn)))
        )
      )

      implicit val doc: Document = asDocument(testMessage.message, optMovement = movementWithLoggedInUserAsConsignor)

      "show the correct title and H1" when {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.title -> s"${testMessage.messageTitle} - Excise Movement and Control System - GOV.UK",
            Selectors.h1 -> testMessage.messageTitle,
          )
        )
      }

      "show the correct table content" when {
        testMessage.messageSubTitle match {
          case Some(subTitle) =>
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.summaryRowKey(1) -> English.labelMessageType,
                Selectors.summaryRowValue(1) -> subTitle,
                Selectors.summaryRowKey(2) -> English.labelArc,
                Selectors.summaryRowValue(2) -> message1.arc.getOrElse(""),
                Selectors.summaryRowKey(3) -> English.labelLrn,
                Selectors.summaryRowValue(3) -> message1.lrn.getOrElse("")
              )
            )
          case None =>
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.summaryRowKey(1) -> English.labelArc,
                Selectors.summaryRowValue(1) -> message1.arc.getOrElse(""),
                Selectors.summaryRowKey(2) -> English.labelLrn,
                Selectors.summaryRowValue(2) -> message1.lrn.getOrElse("")
              )
            )
        }
      }

      "show the correct actions" when {
        movementActionsLinksTest()

        if (testMessage.reportOfReceiptLink) {
          behave like pageWithExpectedElementsAndMessages(
            Seq(
              Selectors.reportOfReceiptLink -> English.reportOfReceiptLinkText
            )
          )
        }

        if (testMessage.explainDelayLink) {
          behave like pageWithExpectedElementsAndMessages(
            Seq(
              Selectors.explainDelayLink -> English.explainDelayLinkText
            )
          )
        }

        if (testMessage.changeDestinationLink) {
          behave like pageWithExpectedElementsAndMessages(
            Seq(
              Selectors.changeDestinationLink -> English.changeDestinationLinkText
            )
          )
        }
      }
    }
  }

  "when being rendered with an IE871 message" should {
    "the logged in user is the consignor of the movement" when {
      val testMessage = ie871SubmittedShortageExcessAsAConsignor

      val movementWithLoggedInUserAsConsignor = Some(getMovementResponseModel
        .copy(
          consignorTrader = getMovementResponseModel.consignorTrader.copy(traderExciseNumber = Some(testErn)),
          consigneeTrader = getMovementResponseModel.consigneeTrader.map(_.copy(traderExciseNumber = Some("GB00000000000")))
        )
      )

      implicit val doc: Document = asDocument(testMessage.message, optMovement = movementWithLoggedInUserAsConsignor)

      "show the correct title and H1" when {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.title -> s"${testMessage.messageTitle} - Excise Movement and Control System - GOV.UK",
            Selectors.h1 -> testMessage.messageTitle,
          )
        )
      }

      "show the correct table content" when {
        testMessage.messageSubTitle match {
          case Some(subTitle) =>
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.summaryRowKey(1) -> English.labelMessageType,
                Selectors.summaryRowValue(1) -> subTitle,
                Selectors.summaryRowKey(2) -> English.labelArc,
                Selectors.summaryRowValue(2) -> message1.arc.getOrElse(""),
                Selectors.summaryRowKey(3) -> English.labelLrn,
                Selectors.summaryRowValue(3) -> message1.lrn.getOrElse("")
              )
            )
          case None =>
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.summaryRowKey(1) -> English.labelArc,
                Selectors.summaryRowValue(1) -> message1.arc.getOrElse(""),
                Selectors.summaryRowKey(2) -> English.labelLrn,
                Selectors.summaryRowValue(2) -> message1.lrn.getOrElse("")
              )
            )
        }
      }

      "show the correct actions" when {
        movementActionsLinksTest()

        if (testMessage.reportOfReceiptLink) {
          behave like pageWithExpectedElementsAndMessages(
            Seq(
              Selectors.reportOfReceiptLink -> English.reportOfReceiptLinkText
            )
          )
        }

        if (testMessage.explainDelayLink) {
          behave like pageWithExpectedElementsAndMessages(
            Seq(
              Selectors.explainDelayLink -> English.explainDelayLinkText
            )
          )
        }

        if (testMessage.changeDestinationLink) {
          behave like pageWithExpectedElementsAndMessages(
            Seq(
              Selectors.changeDestinationLink -> English.changeDestinationLinkText
            )
          )
        }
      }
    }
    "the logged in user is the consignee of the movement" when {
      val testMessage = ie871SubmittedShortageExcessAsAConsignor

      val movementWithLoggedInUserAsConsignee = Some(getMovementResponseModel
        .copy(
          consignorTrader = getMovementResponseModel.consignorTrader.copy(traderExciseNumber = Some("GB00000000000")),
          consigneeTrader = getMovementResponseModel.consigneeTrader.map(_.copy(traderExciseNumber = Some(testErn)))
        )
      )

      implicit val doc: Document = asDocument(testMessage.message, optMovement = movementWithLoggedInUserAsConsignee)

      "show the correct title and H1" when {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.title -> s"${testMessage.messageTitle} - Excise Movement and Control System - GOV.UK",
            Selectors.h1 -> testMessage.messageTitle,
          )
        )
      }

      "show the correct table content" when {
        testMessage.messageSubTitle match {
          case Some(subTitle) =>
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.summaryRowKey(1) -> English.labelMessageType,
                Selectors.summaryRowValue(1) -> subTitle,
                Selectors.summaryRowKey(2) -> English.labelArc,
                Selectors.summaryRowValue(2) -> message1.arc.getOrElse(""),
                Selectors.summaryRowKey(3) -> English.labelLrn,
                Selectors.summaryRowValue(3) -> message1.lrn.getOrElse("")
              )
            )
          case None =>
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.summaryRowKey(1) -> English.labelArc,
                Selectors.summaryRowValue(1) -> message1.arc.getOrElse(""),
                Selectors.summaryRowKey(2) -> English.labelLrn,
                Selectors.summaryRowValue(2) -> message1.lrn.getOrElse("")
              )
            )
        }
      }

      "show the correct actions" when {
        movementActionsLinksTest()

        if (testMessage.reportOfReceiptLink) {
          behave like pageWithExpectedElementsAndMessages(
            Seq(
              Selectors.reportOfReceiptLink -> English.reportOfReceiptLinkText
            )
          )
        }

        if (testMessage.explainDelayLink) {
          behave like pageWithExpectedElementsAndMessages(
            Seq(
              Selectors.explainDelayLink -> English.explainDelayLinkText
            )
          )
        }

        if (testMessage.changeDestinationLink) {
          behave like pageWithExpectedElementsAndMessages(
            Seq(
              Selectors.changeDestinationLink -> English.changeDestinationLinkText
            )
          )
        }
      }
    }
  }

  s"when an IE802 reminder to change destination" should {
    "contain other information" when {
      implicit val doc: Document = asDocument(ie802ReminderToChangeDestination.message)

      behave like pageWithExpectedElementsAndMessages(
        Seq(
          Selectors.p(1) -> "This movement requires a change of destination."
        )
      )
    }
  }

  s"when an IE802 reminder to receipt" should {
    "contain other information" when {
      implicit val doc: Document = asDocument(ie802ReminderToReportReceipt.message)

      behave like pageWithExpectedElementsAndMessages(
        Seq(
          Selectors.p(1) -> "You have received a movement but we have not yet received your Report of Receipt. However if you have sent a Report of Receipt within the last 7 days, please ignore this reminder."
        )
      )
    }
  }

  s"when an IE802 reminder to provide destination" should {
    "contain other information" when {
      implicit val doc: Document = asDocument(ie802ReminderToProvideDestination.message)

      behave like pageWithExpectedElementsAndMessages(
        Seq(
          Selectors.p(1) -> "Your movement has been submitted successfully."
        )
      )
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

  s"when an IE829 message" should {
    "contain other information" when {
      implicit val doc: Document = asDocument(ie829ReceivedCustomsAcceptance.message)

      behave like pageWithExpectedElementsAndMessages(
        Seq(
          Selectors.p(1) -> "Your movement has been accepted for export."
        )
      )
    }
  }

  s"when an IE839 message" should {
    "contain other information" when {
      implicit val doc: Document = asDocument(ie839ReceivedCustomsRejection.message)

      behave like pageWithExpectedElementsAndMessages(
        Seq(
          Selectors.p(1) -> "Your movement has been rejected for export."
        )
      )
    }
  }

  s"when an IE881 message" should {
    "contain other information" when {
      implicit val doc: Document = asDocument(ie881ReceivedManualClosure.message)

      behave like pageWithExpectedElementsAndMessages(
        Seq(
          Selectors.p(1) -> "The movement has been manually closed by the member state of the consignor due to a problem."
        )
      )
    }
  }

  "when being rendered for a IE704" should {

    import GetSubmissionFailureMessageResponseFixtures._
    import IE704ModelFixtures._

    def movementInformationTest(testMessage: TestMessage, withArc: Boolean = true)(implicit doc: Document): Unit = {
      behave like pageWithExpectedElementsAndMessages(
        Seq(
          Selectors.title -> s"${testMessage.messageTitle} - Excise Movement and Control System - GOV.UK",
          Selectors.h1 -> testMessage.messageTitle,
          Selectors.summaryRowKey(if(withArc) 2 else 1) -> English.labelLrn,
          Selectors.summaryRowValue(if(withArc) 2 else 1) -> testMessage.message.lrn.get
        )
      )

      if(withArc) {
        pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.summaryRowKey(1) -> English.labelArc,
            Selectors.summaryRowValue(1) -> testMessage.message.arc.get
          )
        )
      }
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


    "for a IE810 related message type" must {

      def cancelOrChangeDestinationContent()(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.p(1) -> English.cancelMovement,
            Selectors.id("cancel-movement") -> English.cancelMovementLink,
            Selectors.p(2) -> English.changeDestination,
            Selectors.id("submit-change-destination") -> English.changeDestinationLink
          )
        )
      }

      "render the correct content (when non-fixable) - portal" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704PortalSubmission,
          relatedMessageType = Some("IE810"),
          draftMovementExists = true
        )
        implicit val doc: Document = asDocument(ie704ErrorCancellationIE810.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorCancellationIE810)
        errorRowsTest(failureMessageResponse)
        cancelOrChangeDestinationContent()
        helplineLinkTest(3)
        movementActionsLinksTest()
      }

      "render the correct content (when non-fixable) - 3rd party" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          relatedMessageType = Some("IE810")
        )
        implicit val doc: Document = asDocument(ie704ErrorCancellationIE810.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorCancellationIE810)
        errorRowsTest(failureMessageResponse)
        cancelOrChangeDestinationContent()
        thirdPartySubmissionTest(3)
        helplineLinkTest(4)
        movementActionsLinksTest()
      }
    }

    "for a IE837 related message type" must {

      def submitNewExplainDelayContentTest(pIndex: Int)(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.p(pIndex) -> English.submitNewExplainDelay,
            Selectors.id("submit-new-explanation-for-delay") -> English.submitNewExplainDelayLink
          )
        )
      }

      "render the correct content (when non-fixable) - portal" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704PortalSubmission,
          relatedMessageType = Some("IE837"),
          draftMovementExists = true
        )
        implicit val doc: Document = asDocument(ie704ErrorExplainDelayIE837.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorExplainDelayIE837)
        errorRowsTest(failureMessageResponse)
        submitNewExplainDelayContentTest(1)
        helplineLinkTest(2)
        movementActionsLinksTest()
      }

      "render the correct content (when non-fixable) - 3rd party" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          relatedMessageType = Some("IE837")
        )
        implicit val doc: Document = asDocument(ie704ErrorExplainDelayIE837.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorExplainDelayIE837)
        errorRowsTest(failureMessageResponse)
        submitNewExplainDelayContentTest(1)
        thirdPartySubmissionTest(2)
        helplineLinkTest(3)
        movementActionsLinksTest()
      }
    }

    "for a IE871 related message type" must {

      def submitNewExplanationOfShortageOrExcessContentTest(pIndex: Int)(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.p(pIndex) -> English.submitNewExplanationOfShortageOrExcess,
            Selectors.id("submit-a-new-explanation-for-shortage-or-excess") -> English.submitNewExplanationOfShortageOrExcessLink
          )
        )
      }

      "render the correct content (when non-fixable) - portal" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704PortalSubmission,
          relatedMessageType = Some("IE871"),
          draftMovementExists = true
        )
        implicit val doc: Document = asDocument(ie704ErrorExplainShortageOrExcessIE871.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorExplainShortageOrExcessIE871)
        errorRowsTest(failureMessageResponse)
        submitNewExplanationOfShortageOrExcessContentTest(1)
        helplineLinkTest(2)
        movementActionsLinksTest()
      }

      "render the correct content (when non-fixable) - 3rd party" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          relatedMessageType = Some("IE871")
        )
        implicit val doc: Document = asDocument(ie704ErrorExplainShortageOrExcessIE871.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorExplainShortageOrExcessIE871)
        errorRowsTest(failureMessageResponse)
        submitNewExplanationOfShortageOrExcessContentTest(1)
        thirdPartySubmissionTest(2)
        helplineLinkTest(3)
        movementActionsLinksTest()
      }
    }

    "for a IE818 related message type" must {

      def submitNewReportOfReceiptTest(pIndex: Int)(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.p(pIndex) -> English.submitNewReportOfReceipt,
            Selectors.id("submit-a-new-report-of-receipt") -> English.submitNewReportOfReceiptLink
          )
        )
      }

      "render the correct content (when non-fixable) - portal" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704PortalSubmission,
          relatedMessageType = Some("IE818"),
          draftMovementExists = true
        )
        implicit val doc: Document = asDocument(ie704ErrorReportOfReceiptIE818.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorReportOfReceiptIE818)
        errorRowsTest(failureMessageResponse)
        submitNewReportOfReceiptTest(1)
        helplineLinkTest(2)
        movementActionsLinksTest()
      }

      "render the correct content (when non-fixable) - 3rd party" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          relatedMessageType = Some("IE818")
        )
        implicit val doc: Document = asDocument(ie704ErrorReportOfReceiptIE818.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorReportOfReceiptIE818)
        errorRowsTest(failureMessageResponse)
        submitNewReportOfReceiptTest(1)
        thirdPartySubmissionTest(2)
        helplineLinkTest(3)
        movementActionsLinksTest()
      }
    }

    "for a IE819 related message type" must {

      def submitNewAlertRejectionContentTest(pIndex: Int)(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.p(pIndex) -> English.submitNewAlertRejection,
            Selectors.id("submit-a-new-alert-rejection") -> English.submitNewAlertRejectionLink
          )
        )
      }

      "render the correct content (when non-fixable) - portal" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704PortalSubmission,
          relatedMessageType = Some("IE819"),
          draftMovementExists = true
        )
        implicit val doc: Document = asDocument(ie704ErrorAlertRejectionIE819.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorAlertRejectionIE819)
        errorRowsTest(failureMessageResponse)
        submitNewAlertRejectionContentTest(1)
        movementActionsLinksTest()
      }

      "render the correct content (when non-fixable) - 3rd party" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          relatedMessageType = Some("IE819")
        )
        implicit val doc: Document = asDocument(ie704ErrorAlertRejectionIE819.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorAlertRejectionIE819)
        errorRowsTest(failureMessageResponse)
        submitNewAlertRejectionContentTest(1)
        thirdPartySubmissionTest(2)
        movementActionsLinksTest()
      }
    }

    "for a IE825 related message type" must {

      def splitMovementContentTest(pIndex: Int)(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.p(pIndex) -> English.splitMovementError
          )
        )
      }

      "render the correct content (when non-fixable) - portal" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704 = ie704PortalSubmission,
          relatedMessageType = Some("IE825"),
          draftMovementExists = true
        )
        implicit val doc: Document = asDocument(ie704ErrorSplitMovementIE825.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorSplitMovementIE825)
        errorRowsTest(failureMessageResponse)
        splitMovementContentTest(1)
        helplineLinkTest(2)
        movementActionsLinksTest()
      }

      "render the correct content (when non-fixable) - 3rd party" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          relatedMessageType = Some("IE825")
        )
        implicit val doc: Document = asDocument(ie704ErrorSplitMovementIE825.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorSplitMovementIE825)
        errorRowsTest(failureMessageResponse)
        splitMovementContentTest(1)
        thirdPartySubmissionTest(2)
        helplineLinkTest(3)
        movementActionsLinksTest()
      }
    }
    "for a IE813 related message type" must {

      def submitNewChangeDestinationContentTest(pIndex: Int)(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.p(pIndex) -> English.submitNewChangeDestination,
            Selectors.link(pIndex) -> English.submitNewChangeDestinationLink
          )
        )
      }

      def thirdPartySubmissionTest(index: Int)(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.p(index) -> English.ie813thirdParty,
            Selectors.link(index) -> English.ie813thirdPartyLink
          )
        )
      }

      "render the correct content (when non-fixable) - portal" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704PortalSubmission,
          relatedMessageType = Some("IE813"),
          draftMovementExists = true
        )
        implicit val doc: Document = asDocument(ie704ErrorChangeDestinationIE813.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorChangeDestinationIE813)
        errorRowsTest(failureMessageResponse)
        submitNewChangeDestinationContentTest(1)
        movementActionsLinksTest()
      }

      "render the correct content (when non-fixable) - 3rd party" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          relatedMessageType = Some("IE813")
        )
        implicit val doc: Document = asDocument(ie704ErrorChangeDestinationIE813.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorChangeDestinationIE813)
        errorRowsTest(failureMessageResponse)
        thirdPartySubmissionTest(1)
        movementActionsLinksTest()
      }
    }

    "for a IE815 related message type" must {

      def submitNewMovementContentTest(pIndex: Int, isSingularWording: Boolean)(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.p(pIndex) -> (if(isSingularWording) English.submitNewMovementSingularError else English.submitNewMovementMultipleErrors),
            Selectors.id("create-a-new-movement") -> English.createNewMovementLink
          )
        )
      }

      def arcContentTest(pIndex: Int)(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.p(pIndex) -> English.arcText
          )
        )
      }

      def updateMovementDraftContentTest(pIndex: Int)(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.p(pIndex) -> English.updateMovementLink
          )
        )
      }

      def warningTextContentTest()(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.warningText -> English.warningTextWhenFixable
          )
        )
      }

      def fixableDraftMovementDoesNotExistTest()(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.p(1) -> English.fixableDraftExpiredP1,
            Selectors.bullet(1) -> English.fixableDraftExpiredBullet1,
            Selectors.bullet(2) -> English.fixableDraftExpiredBullet2,
            Selectors.p(2) -> English.fixableDraftExpiredP2
          )
        )
      }

      "render the correct content (when non-fixable) - draft movement exists (singular)" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704 = ie704PortalSubmission.copy(
            body = IE704BodyFixtures.ie704BodyModel.copy(
              functionalError = Seq(
                IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(errorType = "4411", errorReason = "You are not approved on SEED to dispatch energy products. Please check that the correct excise product code is selected and amend your entry.")
              )
            )
          ),
          relatedMessageType = Some("IE815"),
          draftMovementExists = true
        )
        implicit val doc: Document = asDocument(ie704ErrorCreateMovementIE815.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorCreateMovementIE815, withArc = false)
        errorRowsTest(failureMessageResponse)
        submitNewMovementContentTest(1, isSingularWording = true)
        arcContentTest(2)
        helplineLinkTest(3)
        movementActionsLinksTest(withViewMovementLink = false)
      }

      "render the correct content (when non-fixable plural) - draft movement exists" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704 = ie704PortalSubmission.copy(
            body = IE704BodyFixtures.ie704BodyModel.copy(
              functionalError = Seq(
                IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(errorType = "4411", errorReason = "You are not approved on SEED to dispatch energy products. Please check that the correct excise product code is selected and amend your entry."),
                IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(errorType = "4403", errorReason = "The consignor Excise Registration Number you have entered is not recognised by SEED. Please amend your entry."),
                IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(errorType = "4411", errorReason = "You are not approved on SEED to dispatch energy products. Please check that the correct excise product code is selected and amend your entry."),
              )
            )
          ),
          relatedMessageType = Some("IE815"),
          draftMovementExists = true
        )
        implicit val doc: Document = asDocument(ie704ErrorCreateMovementIE815.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorCreateMovementIE815, withArc = false)
        errorRowsTest(failureMessageResponse)
        submitNewMovementContentTest(1, isSingularWording = false)
        arcContentTest(2)
        helplineLinkTest(3)
        movementActionsLinksTest(withViewMovementLink = false)
      }

      "render the correct content (when non-fixable - singular) - draft movement does not exist" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704 = IE704ModelFixtures.ie704ModelModel.copy(
            body = IE704BodyFixtures.ie704BodyModel.copy(
              functionalError = Seq(
                IE704FunctionalErrorFixtures.ie704FunctionalErrorModel.copy(errorType = "4411", errorReason = "You are not approved on SEED to dispatch energy products. Please check that the correct excise product code is selected and amend your entry.")
              )
            )
          ),
          relatedMessageType = Some("IE815")
        )
        implicit val doc: Document = asDocument(ie704ErrorCreateMovementIE815.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorCreateMovementIE815, withArc = false)
        errorRowsTest(failureMessageResponse)
        submitNewMovementContentTest(1, isSingularWording = true)
        arcContentTest(2)
        helplineLinkTest(3)
        movementActionsLinksTest(withViewMovementLink = false)
      }

      "render the correct content (when fixable) - draft movement exists" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          ie704 =ie704PortalSubmission,
          relatedMessageType = Some("IE815"),
          draftMovementExists = true
        )
        implicit val doc: Document = asDocument(ie704ErrorCreateMovementIE815.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorCreateMovementIE815, withArc = false)
        updateMovementDraftContentTest(1)
        arcContentTest(2)
        helplineLinkTest(3)
        movementActionsLinksTest(withViewMovementLink = false)
        warningTextContentTest()
      }

      "render the correct content (when fixable) - draft movement does not exist" when {
        val failureMessageResponse = getSubmissionFailureMessageResponseModel.copy(
          relatedMessageType = Some("IE815")
        )
        implicit val doc: Document = asDocument(ie704ErrorCreateMovementIE815.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorCreateMovementIE815, withArc = false)
        fixableDraftMovementDoesNotExistTest()
        arcContentTest(3)
        helplineLinkTest(4)
        movementActionsLinksTest(withViewMovementLink = false)
      }
    }
  }
}
