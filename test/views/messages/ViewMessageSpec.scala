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
import models.response.emcsTfe.messages.submissionFailure.{GetSubmissionFailureMessageResponse, IE704FunctionalError}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.messages._
import views.html.messages.ViewMessage
import views.{BaseSelectors, ViewBehaviours}

class ViewMessageSpec extends ViewSpecBase
  with ViewBehaviours
  with MessagesFixtures
  with GetMovementResponseFixtures
  with GetSubmissionFailureMessageFixtures {

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
    ie801ReceivedMovement, ie801SubmittedMovement,
    ie803ReceivedChangeDestination, ie803ReceivedSplit,
    ie818ReceivedReportOfReceipt, ie818SubmittedReportOfReceipt,
    ie819ReceivedAlert, ie819ReceivedReject, ie819SubmittedAlert, ie819SubmittedReject,
    ie810ReceivedCancellation, ie810SubmittedCancellation,
    ie813ReceivedChangeDestination, ie813SubmittedChangeDestination,
    ie829ReceivedCustomsAcceptance,
    ie837SubmittedExplainDelayROR, ie837SubmittedExplainDelayCOD,
    ie839ReceivedCustomsRejection
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

  "when being rendered with an IE871 message" should {
    "the logged in user is the consignor of the movement" when {
      val testMessage = ie871SubmittedShortageExcessAsAConsignor

      val movementWithLoggedInUserAsConsignor = Some(getMovementResponseModel
        .copy(
          consignorTrader = getMovementResponseModel.consignorTrader.copy(traderExciseNumber = testErn),
          consigneeTrader = getMovementResponseModel.consigneeTrader.map(_.copy(traderExciseNumber = "GB00000000000"))
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
          consignorTrader = getMovementResponseModel.consignorTrader.copy(traderExciseNumber = "GB00000000000"),
          consigneeTrader = getMovementResponseModel.consigneeTrader.map(_.copy(traderExciseNumber = testErn))
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

  "when being rendered for a IE704" should {

    def movementInformationTest(testMessage: TestMessage)(implicit doc: Document): Unit = {
      behave like pageWithExpectedElementsAndMessages(
        Seq(
          Selectors.title -> s"${testMessage.messageTitle} - Excise Movement and Control System - GOV.UK",
          Selectors.h1 -> testMessage.messageTitle,
          Selectors.summaryRowKey(1) -> English.labelArc,
          Selectors.summaryRowValue(1) -> testMessage.message.arc.get,
          Selectors.summaryRowKey(2) -> English.labelLrn,
          Selectors.summaryRowValue(2) -> testMessage.message.lrn.get
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


    "for a IE810 related message type" must {

      def cancelOrChangeDestinationContent()(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.p(1) -> English.cancelMovement,
            Selectors.link(1) -> English.cancelMovementLink,
            Selectors.p(2) -> English.changeDestination,
            Selectors.link(2) -> English.changeDestinationLink
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
        implicit val doc: Document = asDocument(ie704ErrorCancellationIE810.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorCancellationIE810)
        errorRowsTest(failureMessageResponse)
        cancelOrChangeDestinationContent()
        helplineLinkTest(3)
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
            Selectors.link(pIndex) -> English.submitNewExplainDelayLink
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
          relatedMessageType = Some("IE837")
        )
        implicit val doc: Document = asDocument(ie704ErrorExplainDelayIE837.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorExplainDelayIE837)
        errorRowsTest(failureMessageResponse)
        submitNewExplainDelayContentTest(1)
        helplineLinkTest(2)
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
            Selectors.link(pIndex) -> English.submitNewExplanationOfShortageOrExcessLink
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
          relatedMessageType = Some("IE871")
        )
        implicit val doc: Document = asDocument(ie704ErrorExplainShortageOrExcessIE871.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorExplainShortageOrExcessIE871)
        errorRowsTest(failureMessageResponse)
        submitNewExplanationOfShortageOrExcessContentTest(1)
        helplineLinkTest(2)
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
            Selectors.link(pIndex) -> English.submitNewReportOfReceiptLink
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
          relatedMessageType = Some("IE818")
        )
        implicit val doc: Document = asDocument(ie704ErrorReportOfReceiptIE818.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorReportOfReceiptIE818)
        errorRowsTest(failureMessageResponse)
        submitNewReportOfReceiptTest(1)
        helplineLinkTest(2)
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

    "for a IE837 related message type" must {

      def submitNewExplainDelayContentTest(pIndex: Int)(implicit doc: Document): Unit = {
        behave like pageWithExpectedElementsAndMessages(
          Seq(
            Selectors.p(pIndex) -> English.submitNewExplainDelay,
            Selectors.link(pIndex) -> English.submitNewExplainDelayLink
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
          relatedMessageType = Some("IE837")
        )
        implicit val doc: Document = asDocument(ie704ErrorCancellationIE837.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorCancellationIE837)
        errorRowsTest(failureMessageResponse)
        submitNewExplainDelayContentTest(1)
        helplineLinkTest(2)
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
          relatedMessageType = Some("IE837")
        )
        implicit val doc: Document = asDocument(ie704ErrorCancellationIE837.message, optErrorMessage = Some(failureMessageResponse))
        movementInformationTest(ie704ErrorCancellationIE837)
        errorRowsTest(failureMessageResponse)
        submitNewExplainDelayContentTest(1)
        thirdPartySubmissionTest(2)
        helplineLinkTest(3)
        movementActionsLinksTest()
      }
    }
  }
}
