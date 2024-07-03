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

package views.events

import base.ViewSpecBase
import fixtures.events.MovementEventMessages.English
import fixtures.{GetMovementHistoryEventsResponseFixtures, GetMovementResponseFixtures}
import models.common.DestinationType.TemporaryRegisteredConsignee
import models.common.GuarantorType.Owner
import models.common.SubmitterType.Consignor
import models.common.TransportArrangement.OwnerOfGoods
import models.common.{TraderModel, TransportMode}
import models.requests.DataRequest
import models.response.emcsTfe.DelayReasonType.Accident
import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import models.response.emcsTfe.{GetMovementResponse, HeaderEadEsadModel, NotificationOfDelayModel, TransportModeModel}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.events.HistoryEventView
import views.{BaseSelectors, ViewBehaviours}

import java.time.LocalDateTime

class HistoryEventViewSpec extends ViewSpecBase
  with ViewBehaviours
  with GetMovementResponseFixtures
  with GetMovementHistoryEventsResponseFixtures {

  val view: HistoryEventView = app.injector.instanceOf[HistoryEventView]

  implicit val fakeRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest("GET", "/"))

  implicit val messages: Messages = messages(fakeRequest)

  object Selectors extends BaseSelectors {
    val eventMessageTimestamp: String = id("message-issued")
    val arc: String = id("arc")

  }

  def createDocument(event: MovementHistoryEvent, movement: GetMovementResponse): Document = {
    Jsoup.parse(
      view(event, movement, Seq.empty).toString()
    )
  }

  "HistoryEventView" when {

    Seq(English) foreach { messagesForLanguage =>

      s"being rendered in lang code of '${messagesForLanguage.lang.code}'" when {

        "rendering an IE801 event" should {

          "render the main detail section of the view" should {
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.title -> messagesForLanguage.ie801Title,
                Selectors.h1 -> messagesForLanguage.ie801Heading,
                Selectors.eventMessageTimestamp -> messagesForLanguage.messageIssued(),
                Selectors.arc -> messagesForLanguage.arc(getMovementResponseModel.arc),
                Selectors.p(1) -> messagesForLanguage.ie801P1,
                Selectors.p(2) -> messagesForLanguage.printScreenContent
              )
            )(createDocument(ie801Event, getMovementResponseModel))
          }

          "render the movement details" in {
            val headingId = "#movement-information-heading"
            val summaryId = "#movement-information-summary"

            val doc = createDocument(ie801Event, getMovementResponseModel.copy(
              eadEsad = eadEsadModel.copy(
                upstreamArc = Some("ARC1234567890")
              )
            ))

            doc.select(headingId).text() mustBe "Movement information"
            doc.select(summaryId).isEmpty mustBe false
          }

          "render the consignor details" in {
            val headingId = "#consignor-information-heading"
            val summaryId = "#consignor-information-summary"

            val doc = createDocument(ie801Event, getMovementResponseModel)

            doc.select(headingId).text() mustBe "Consignor"
            doc.select(summaryId).isEmpty mustBe false
          }

          "render the place of dispatch details" in {
            val headingId = "#place-of-dispatch-information-heading"
            val summaryId = "#place-of-dispatch-information-summary"

            val doc = createDocument(ie801Event, getMovementResponseModel)

            doc.select(headingId).text() mustBe "Place of dispatch"
            doc.select(summaryId).isEmpty mustBe false
          }

          "render the import details" in {
            val headingId = "#import-information-heading"
            val summaryId = "#import-information-summary"

            val doc = createDocument(ie801Event, getMovementResponseModel.copy(
              dispatchImportOfficeReferenceNumber = Some("test123")
            ))

            doc.select(headingId).text() mustBe "Import"
            doc.select(summaryId).isEmpty mustBe false
          }

          "render the consignee details" in {
            val headingId = "#consignee-information-heading"
            val summaryId = "#consignee-information-summary"

            val doc = createDocument(ie801Event, getMovementResponseModel)

            doc.select(headingId).text() mustBe "Consignee"
            doc.select(summaryId).isEmpty mustBe false
          }

          "render the exempted organisation details" in {
            val headingId = "#exempted-organisation-information-heading"
            val summaryId = "#exempted-organisation-information-summary"

            val doc = createDocument(ie801Event, getMovementResponseModel.copy(
              memberStateCode = Some("GB"),
              serialNumberOfCertificateOfExemption = Some("1234567890")
            ))

            doc.select(headingId).text() mustBe "Exempted organisation"
            doc.select(summaryId).isEmpty mustBe false
          }

          "render the place of destination details" in {
            val headingId = "#place-of-destination-information-heading"
            val summaryId = "#place-of-destination-information-summary"

            val doc = createDocument(ie801Event, getMovementResponseModel)

            doc.select(headingId).text() mustBe "Place of destination"
            doc.select(summaryId).isEmpty mustBe false
          }

          "render the export details" in {
            val headingId = "#export-information-heading"
            val summaryId = "#export-information-summary"

            val doc = createDocument(ie801Event, getMovementResponseModel)

            doc.select(headingId).text() mustBe "Export"
            doc.select(summaryId).isEmpty mustBe false
          }

          "render the guarantor details" in {
            val headingId = "#guarantor-information-heading"
            val summaryId = "#guarantor-information-summary"

            val doc = createDocument(ie801Event, getMovementResponseModel.copy(
              movementGuarantee = getMovementResponseModel.movementGuarantee.copy(
                guarantorTypeCode = Owner
              )
            ))

            doc.select(headingId).text() mustBe "Guarantor details"
            doc.select(summaryId).isEmpty mustBe false
          }

          "render the journey details" in {
            val headingId = "#journey-information-heading"
            val summaryId = "#journey-information-summary"

            val doc = createDocument(ie801Event, getMovementResponseModel.copy(
              transportMode = TransportModeModel(
                transportModeCode = TransportMode.AirTransport,
                complementaryInformation = None
              )
            ))

            doc.select(headingId).text() mustBe "Journey type"
            doc.select(summaryId).isEmpty mustBe false
          }

          "render the transport arranger details" in {
            val headingId = "#transport-arranger-information-heading"
            val summaryId = "#transport-arranger-information-summary"

            val doc = createDocument(
              ie801Event,
              getMovementResponseModel.copy(
                headerEadEsad = HeaderEadEsadModel(
                  sequenceNumber = 1,
                  dateAndTimeOfUpdateValidation = "2023-12-01T12:00:00Z",
                  destinationType = TemporaryRegisteredConsignee,
                  journeyTime = "20 days",
                  transportArrangement = OwnerOfGoods
                ),
                transportArrangerTrader = Some(TraderModel(
                  traderExciseNumber = None,
                  traderName = Some("Mr transport arranger"),
                  address = None,
                  vatNumber = Some("VATarranger"),
                  eoriNumber = None
                ))
              )
            )

            doc.select(headingId).text() mustBe "Transport arranger"
            doc.select(summaryId).isEmpty mustBe false
          }

          "render the first transporter details" in {
            val headingId = "#first-transporter-information-heading"
            val summaryId = "#first-transporter-information-summary"

            val doc = createDocument(ie801Event, getMovementResponseModel)

            doc.select(headingId).text() mustBe "First transporter"
            doc.select(summaryId).isEmpty mustBe false
          }

          "render the SAD details" in {
            val headingId = "#sad-information-heading"
            val summaryId = "#sad-information-summary"

            val doc = createDocument(
              ie801Event,
              getMovementResponseModel.copy(
                eadEsad = eadEsadModel.copy(
                  importSadNumber = Some(Seq("123", "456"))
                )
              )
            )

            doc.select(headingId).text() mustBe "Single Administrative Document(s) (SAD)"
            doc.select(s"$summaryId-1").isEmpty mustBe false
            doc.select(s"$summaryId-2").isEmpty mustBe false
          }

          "render the document certificate details" in {
            val headingId = "#documents-information-heading"
            val summaryId = "#documents-information-summary"

            val doc = createDocument(
              ie801Event,
              getMovementResponseModel.copy(
                eadEsad = eadEsadModel.copy(
                  importSadNumber = Some(Seq("123", "456"))
                )
              )
            )

            doc.select(headingId).text() mustBe "Documents"
            doc.select(s"$summaryId-1").isEmpty mustBe false
            doc.select(s"$summaryId-2").isEmpty mustBe false
          }
        }

        "rendering an IE802 event (Change Destination Reminder)" should {

          "render the main detail section of the view" should {
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.title -> messagesForLanguage.ie802ChangeDestinationTitle,
                Selectors.h1 -> messagesForLanguage.ie802ChangeDestinationHeading,
                Selectors.eventMessageTimestamp -> messagesForLanguage.messageIssued(),
                Selectors.arc -> messagesForLanguage.arc(getMovementResponseModel.arc),
                Selectors.p(1) -> messagesForLanguage.ie802ChangeDestinationP1,
                Selectors.p(2) -> messagesForLanguage.printScreenContent
              )
            )(createDocument(ie802ChangeDestinationEvent, getMovementResponseModel))
          }
        }

        "rendering an IE802 event (Report Of Receipt Reminder)" should {

          "render the main detail section of the view" should {
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.title -> messagesForLanguage.ie802ReportReceiptTitle,
                Selectors.h1 -> messagesForLanguage.ie802ReportReceiptHeading,
                Selectors.eventMessageTimestamp -> messagesForLanguage.messageIssued(),
                Selectors.arc -> messagesForLanguage.arc(getMovementResponseModel.arc),
                Selectors.p(1) -> messagesForLanguage.ie802ReportReceiptP1,
                Selectors.p(2) -> messagesForLanguage.printScreenContent
              )
            )(createDocument(ie802EventReportOfReceipt, getMovementResponseModel))
          }
        }

        "rendering an IE802 event (Movement Destination Reminder)" should {

          "render the main detail section of the view" should {
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.title -> messagesForLanguage.ie802MovementDestinationTitle,
                Selectors.h1 -> messagesForLanguage.ie802MovementDestinationHeading,
                Selectors.eventMessageTimestamp -> messagesForLanguage.messageIssued(),
                Selectors.arc -> messagesForLanguage.arc(getMovementResponseModel.arc),
                Selectors.p(1) -> messagesForLanguage.ie802MovementDestinationP1,
                Selectors.p(2) -> messagesForLanguage.printScreenContent
              )
            )(createDocument(ie802MovementDestinationEvent, getMovementResponseModel))
          }
        }

        "rendering an IE803 event (Diverted Notification)" should {

          "render the main detail section of the view" should {
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.title -> messagesForLanguage.ie803MovementDivertedTitle,
                Selectors.h1 -> messagesForLanguage.ie803MovementDivertedHeading,
                Selectors.eventMessageTimestamp -> messagesForLanguage.messageIssued(),
                Selectors.arc -> messagesForLanguage.arc(getMovementResponseModel.arc),
                Selectors.p(1) -> messagesForLanguage.ie803MovementDivertedP1,
                Selectors.p(2) -> messagesForLanguage.ie803MovementDivertedP2("5 June 2024"),
                Selectors.p(3) -> messagesForLanguage.printScreenContent
              )
            )(createDocument(ie803MovementDiversionEvent, getMovementResponseModel))
          }
        }

        "rendering an IE803 event (Movement Split Notification)" should {

          "render the main detail section of the view" should {
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.title -> messagesForLanguage.ie803MovementSplitTitle,
                Selectors.h1 -> messagesForLanguage.ie803MovementSplitHeading,
                Selectors.eventMessageTimestamp -> messagesForLanguage.messageIssued(),
                Selectors.arc -> messagesForLanguage.arc(getMovementResponseModel.arc),
                Selectors.p(1) -> messagesForLanguage.ie803MovementSplitP1("5 June 2024"),
                Selectors.p(2) -> messagesForLanguage.ie803MovementSplitP2,
                Selectors.bullet(1) -> testArc,
                Selectors.bullet(2) -> (testArc.dropRight(1) + "1"),
                Selectors.p(3) -> messagesForLanguage.printScreenContent
              )
            )(createDocument(ie803MovementSplitEvent, getMovementResponseModel))
          }
        }

        "rendering an IE807 event (Movement Intercepted/Interrupted Notification)" should {

          "render the main detail section of the view" should {
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.title -> messagesForLanguage.ie807MovementInterceptedTitle,
                Selectors.h1 -> messagesForLanguage.ie807MovementInterceptedHeading,
                Selectors.eventMessageTimestamp -> messagesForLanguage.messageIssued(LocalDateTime.parse(ie810MovementCancelledEvent.eventDate)),
                Selectors.arc -> messagesForLanguage.arc(getMovementResponseModel.arc),
                Selectors.p(1) -> messagesForLanguage.ie807MovementInterceptedP1("FR1234"),
                Selectors.p(2) -> messagesForLanguage.printScreenContent,
                Selectors.summaryRowKey(1) -> messagesForLanguage.IE807MovementInterceptedKey1,
                Selectors.summaryRowValue(1) -> messagesForLanguage.IE807MovementInterceptedValue1,
                Selectors.summaryRowKey(2) -> messagesForLanguage.IE807MovementInterceptedKey2,
                Selectors.summaryRowValue(2) -> messagesForLanguage.IE807MovementInterceptedValue2
              )
            )(createDocument(ie807MovementInterruptedEvent, getMovementResponseModel))
          }
        }

        "rendering an IE810 event (Movement Cancelled Notification)" should {

          "render the main detail section of the view" should {
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.title -> messagesForLanguage.ie810MovementCancelledTitle,
                Selectors.h1 -> messagesForLanguage.ie810MovementCancelledHeading,
                Selectors.eventMessageTimestamp -> messagesForLanguage.messageIssued(LocalDateTime.parse(ie810MovementCancelledEvent.eventDate)),
                Selectors.arc -> messagesForLanguage.arc(getMovementResponseModel.arc),
                Selectors.p(1) -> messagesForLanguage.ie810MovementCancelledP1,
                Selectors.p(2) -> messagesForLanguage.printScreenContent,
                Selectors.summaryRowKey(1) -> messagesForLanguage.IE810MovementCancelledKey1,
                Selectors.summaryRowValue(1) -> messagesForLanguage.IE810MovementCancelledValue1,
                Selectors.summaryRowKey(2) -> messagesForLanguage.IE810MovementCancelledKey2,
                Selectors.summaryRowValue(2) -> messagesForLanguage.IE810MovementCancelledValue2
              )
            )(createDocument(ie810MovementCancelledEvent, getMovementResponseModel))
          }
        }

        "rendering an IE819 event (Alert/Rejection)" should {

          "when the message is an Alert" should {
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.title -> messagesForLanguage.ie819AlertTitle,
                Selectors.h1 -> messagesForLanguage.ie819AlertHeading,
                Selectors.eventMessageTimestamp -> messagesForLanguage.messageIssued(LocalDateTime.parse(ie819AlertEvent.eventDate)),
                Selectors.arc -> messagesForLanguage.arc(getMovementResponseModel.arc),
                Selectors.p(1) -> messagesForLanguage.ie819AlertP1,
                Selectors.p(2) -> messagesForLanguage.printScreenContent
              )
            )(createDocument(ie819AlertEvent, getMovementResponseModel))
          }

          "when the message is a Rejection" should {
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.title -> messagesForLanguage.ie819RejectionTitle,
                Selectors.h1 -> messagesForLanguage.ie819RejectionHeading,
                Selectors.eventMessageTimestamp -> messagesForLanguage.messageIssued(LocalDateTime.parse(ie819RejectionEvent.eventDate)),
                Selectors.arc -> messagesForLanguage.arc(getMovementResponseModel.arc),
                Selectors.p(1) -> messagesForLanguage.ie819RejectionP1,
                Selectors.p(2) -> messagesForLanguage.printScreenContent
              )
            )(createDocument(ie819RejectionEvent, getMovementResponseModel))
          }
        }

        "rendering an IE829 event (Movement accepted by customs)" should {
          "render the main detail section of the view" should {
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.title -> messagesForLanguage.ie829Title,
                Selectors.h1 -> messagesForLanguage.ie829Heading,
                Selectors.eventMessageTimestamp -> messagesForLanguage.messageIssued(LocalDateTime.parse(ie829MovementAcceptedCustomsEvent.eventDate)),
                Selectors.arc -> messagesForLanguage.arc(getMovementResponseModel.arc),
                Selectors.p(1) -> messagesForLanguage.ie829Paragraph1,
                Selectors.p(2) -> messagesForLanguage.printScreenContent
              )
            )(createDocument(ie829MovementAcceptedCustomsEvent, getMovementResponseModel))
          }
        }

        "rendering an IE839 event (Movement rejected by customs)" should {
          "render the main detail section of the view" should {
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.title -> messagesForLanguage.ie839MovementRejectedByCustomsTitle,
                Selectors.h1 -> messagesForLanguage.ie839MovementRejectedByCustomsHeading,
                Selectors.eventMessageTimestamp -> messagesForLanguage.messageIssued(LocalDateTime.parse(ie839MovementRejectedCustomsEvent.eventDate)),
                Selectors.arc -> messagesForLanguage.arc(getMovementResponseModel.arc),
                Selectors.p(1) -> messagesForLanguage.ie839MovementRejectedByCustomsP1WithCustomsOffice("AT002000"),
                Selectors.p(2) -> messagesForLanguage.printScreenContent
              )
            )(createDocument(ie839MovementRejectedCustomsEvent, getMovementResponseModel))
          }
        }

        "rendering an IE905 event (manual closure of a movement)" should {

          "render the main detail section of the view" should {
            behave like pageWithExpectedElementsAndMessages(
              Seq(
                Selectors.title -> messagesForLanguage.ie905ManualClosureResponseTitle,
                Selectors.h1 -> messagesForLanguage.ie905ManualClosureResponseHeading,
                Selectors.eventMessageTimestamp -> messagesForLanguage.messageIssued(),
                Selectors.arc -> messagesForLanguage.arc(getMovementResponseModel.arc),
                Selectors.p(1) -> messagesForLanguage.ie905ManualClosureResponseP1,
                Selectors.p(2) -> messagesForLanguage.printScreenContent
              )
            )(createDocument(ie905ManualClosureResponseEvent, getMovementResponseModel))
          }
        }

        "rendering an IE837 event (Delay)" should {

          val event = ie837DelayEvent(messageRole = 1)

          behave like pageWithExpectedElementsAndMessages(
            Seq(
              Selectors.title -> messagesForLanguage.ie837Title,
              Selectors.h1 -> messagesForLanguage.ie837Heading,
              Selectors.eventMessageTimestamp -> messagesForLanguage.messageIssued(LocalDateTime.parse(event.eventDate)),
              Selectors.arc -> messagesForLanguage.arc(getMovementResponseModel.arc),
              Selectors.p(1) -> messagesForLanguage.ie837Paragraph1,
              Selectors.p(2) -> messagesForLanguage.printScreenContent
            )
          )(createDocument(event, getMovementResponseModel.copy(
            notificationOfDelay = Some(Seq(
              NotificationOfDelayModel(
                submitterIdentification = testErn,
                submitterType = Consignor,
                explanationCode = Accident,
                complementaryInformation = Some("info"),
                dateTime = LocalDateTime.parse(event.eventDate)
              )
            ))
          )))
        }

        "rendering an IE871 event (Shortage or Excess Notification)" should {

          val event = ie871ShortageOrEccessEvent

          behave like pageWithExpectedElementsAndMessages(
            Seq(
              Selectors.title -> messagesForLanguage.ie871Title,
              Selectors.h1 -> messagesForLanguage.ie871Heading,
              Selectors.eventMessageTimestamp -> messagesForLanguage.messageIssued(LocalDateTime.parse(event.eventDate)),
              Selectors.arc -> messagesForLanguage.arc(getMovementResponseModel.arc),
              Selectors.p(1) -> messagesForLanguage.ie871Paragraph1,
              Selectors.p(2) -> messagesForLanguage.printScreenContent
            )
          )(createDocument(event, getMovementResponseModel))
        }
      }
    }
  }

}
