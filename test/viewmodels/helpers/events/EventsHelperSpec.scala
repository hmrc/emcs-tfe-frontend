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

package viewmodels.helpers.events

import base.SpecBase
import fixtures.events.MovementEventMessages
import fixtures.messages.AlertRejectionReasonMessages
import fixtures.{GetMovementHistoryEventsResponseFixtures, GetMovementResponseFixtures}
import models.EventTypes
import models.common.DestinationType
import models.common.DestinationType.Export
import models.requests.DataRequest
import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import org.jsoup.Jsoup
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Empty
import utils.DateUtils
import views.BaseSelectors

import java.time.LocalDateTime

class EventsHelperSpec extends SpecBase
  with GetMovementHistoryEventsResponseFixtures
  with GetMovementResponseFixtures
  with DateUtils {

  implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"))

  val helper: EventsHelper = app.injector.instanceOf[EventsHelper]

  implicit lazy val messages: Messages = messages(request)

  object Selectors extends BaseSelectors {
    override val p: Int => String = i => s"p:nth-of-type($i)"
    override def bullet(i: Int, ul: Int): String = s"ul.govuk-list:nth-of-type($ul) li:nth-of-type($i)"
    override def h2(i: Int): String = s"h2:nth-of-type($i)"
  }

  Seq(MovementEventMessages.English -> AlertRejectionReasonMessages.English).foreach {
    case (messagesForLanguage, alertRejectionReasonMessages) =>

    s"when language code of '${messagesForLanguage.lang.code}'" should {

      ".constructEventInformation" when {

        "being called with an invalid event type" must {

          "return empty HTML" in {
            val invalidMovementHistoryEvent = MovementHistoryEvent(EventTypes.Invalid, "", 1, 1, None, true)
            val result = helper.constructEventInformation(invalidMovementHistoryEvent, getMovementResponseModel)
            result mustBe Empty.asHtml
          }
        }

        "being called with event type IE802 and message role 1 (change destination reminder)" must {

          "render the correct HTML" in {

            val result = helper.constructEventInformation(ie802ChangeDestinationEvent, getMovementResponseModel)
            val body = Jsoup.parse(result.toString())

            body.select(Selectors.p(1)).text() mustBe messagesForLanguage.ie802ChangeDestinationP1
            body.select(Selectors.p(2)).text() mustBe messagesForLanguage.printScreenContent
          }
        }

        "being called with event type IE802 and message role 2 (report of receipt reminder)" must {

          "render the correct HTML" in {

            val result = helper.constructEventInformation(ie802EventReportOfReceipt, getMovementResponseModel)
            val body = Jsoup.parse(result.toString())

            body.select(Selectors.p(1)).text() mustBe messagesForLanguage.ie802ReportReceiptP1
            body.select(Selectors.p(2)).text() mustBe messagesForLanguage.printScreenContent
          }
        }

        "being called with event type IE802 and message role 3 (movement destination reminder)" must {

          "render the correct HTML" in {

            val result = helper.constructEventInformation(ie802MovementDestinationEvent, getMovementResponseModel)
            val body = Jsoup.parse(result.toString())

            body.select(Selectors.p(1)).text() mustBe messagesForLanguage.ie802MovementDestinationP1
            body.select(Selectors.p(2)).text() mustBe messagesForLanguage.printScreenContent
          }
        }

        "being called with event type IE803 and message role 1 (diverted movement notification)" must {

          "render the correct HTML" in {

            val result = helper.constructEventInformation(ie803MovementDiversionEvent, getMovementResponseModel)
            val body = Jsoup.parse(result.toString())

            body.select(Selectors.p(1)).text() mustBe messagesForLanguage.ie803MovementDivertedP1
            body.select(Selectors.p(2)).text() mustBe messagesForLanguage.ie803MovementDivertedP2("5 June 2024")
            body.select(Selectors.p(3)).text() mustBe messagesForLanguage.printScreenContent
          }
        }

        "being called with event type IE803 and message role 2 (split movement notification)" must {

          "render the correct HTML" in {

            val result = helper.constructEventInformation(ie803MovementSplitEvent, getMovementResponseModel)
            val body = Jsoup.parse(result.toString())

            body.select(Selectors.p(1)).text() mustBe messagesForLanguage.ie803MovementSplitP1("5 June 2024")
            body.select(Selectors.p(2)).text() mustBe messagesForLanguage.ie803MovementSplitP2
            body.select(Selectors.bullet(1)).text() mustBe testArc
            body.select(Selectors.bullet(2)).text() mustBe (testArc.dropRight(1) + "1")
            body.select(Selectors.p(3)).text() mustBe messagesForLanguage.printScreenContent
          }
        }

        "being called with event type IE818 and destination type Export" must {

          "render the correct HTML" in {

            val result = helper.constructEventInformation(ie818Event, getMovementResponseModel.copy(destinationType = DestinationType.Export))
            val body = Jsoup.parse(result.toString())

            body.select(Selectors.p(1)).text() mustBe messagesForLanguage.ie818P1Export
            body.select(Selectors.p(2)).text() mustBe messagesForLanguage.ie818P2
            body.select(Selectors.p(3)).text() mustBe messagesForLanguage.printScreenContent
          }
        }

        "being called with event type IE818 and destination type not Export" must {

          "render the correct HTML" in {

            DestinationType.values.filterNot(_ == DestinationType.Export).foreach {
              destinationType =>
                val result = helper.constructEventInformation(ie818Event, getMovementResponseModel.copy(destinationType = destinationType))
                val body = Jsoup.parse(result.toString())

                body.select(Selectors.p(1)).text() mustBe messagesForLanguage.ie818P1
                body.select(Selectors.p(2)).text() mustBe messagesForLanguage.ie818P2
                body.select(Selectors.p(3)).text() mustBe messagesForLanguage.printScreenContent
            }
          }
        }

        "being called with event type IE819, where the event is an Alert" when {

          "there are multiple reasons" must {

            "render the correct HTML" in {

              val result = helper.constructEventInformation(ie819AlertEventMultipleReasons, getMovementResponseModel)
              val eventDetails = getMovementResponseModel.notificationOfAlertOrRejection.get.head
              val consigneeDetails = getMovementResponseModel.consigneeTrader.get
              val body = Jsoup.parse(result.toString())

              body.select(Selectors.p(1)).text() mustBe messagesForLanguage.ie819AlertP1
              body.select(Selectors.p(2)).text() mustBe messagesForLanguage.printScreenContent
              body.select(Selectors.h2(1)).text() mustBe messagesForLanguage.ie819AlertH2

              //Alert Details summary
              body.select(Selectors.nthSummaryRowKey(1)).text() mustBe messagesForLanguage.ie819AlertDate
              body.select(Selectors.nthSummaryRowValue(1)).text() mustBe LocalDateTime.parse(ie819AlertEventMultipleReasons.eventDate).toLocalDate.formatDateForUIOutput()

              body.select(Selectors.nthSummaryRowKey(2)).text() mustBe messagesForLanguage.ie819AlertSummaryReasons
              body.select(Selectors.nthSummaryRowValue(2)).select(Selectors.bullet(1)).text() mustBe alertRejectionReasonMessages.consigneeDetailsWrong
              body.select(Selectors.nthSummaryRowValue(2)).select(Selectors.bullet(2)).text() mustBe alertRejectionReasonMessages.goodsTypeWrong
              body.select(Selectors.nthSummaryRowValue(2)).select(Selectors.bullet(3)).text() mustBe alertRejectionReasonMessages.goodsQuantityWrong
              body.select(Selectors.nthSummaryRowValue(2)).select(Selectors.bullet(4)).text() mustBe alertRejectionReasonMessages.other

              body.select(Selectors.nthSummaryRowKey(3)).text() mustBe messagesForLanguage.ie819ConsigneeInformation
              body.select(Selectors.nthSummaryRowValue(3)).text() mustBe eventDetails.alertRejectReason.head.additionalInformation.get

              body.select(Selectors.nthSummaryRowKey(4)).text() mustBe messagesForLanguage.ie819GoodsTypeInformation
              body.select(Selectors.nthSummaryRowValue(4)).text() mustBe eventDetails.alertRejectReason(1).additionalInformation.get

              body.select(Selectors.nthSummaryRowKey(5)).text() mustBe messagesForLanguage.ie819GoodsQuantityInformation
              body.select(Selectors.nthSummaryRowValue(5)).text() mustBe eventDetails.alertRejectReason(2).additionalInformation.get

              body.select(Selectors.nthSummaryRowKey(6)).text() mustBe messagesForLanguage.ie819OtherInformation
              body.select(Selectors.nthSummaryRowValue(6)).text() mustBe eventDetails.alertRejectReason(3).additionalInformation.get

              //Consignee Detail
              body.select(Selectors.h2(2)).text() mustBe "Consignee"
              body.select(Selectors.nthSummaryRowKey(1, n = 2)).text() mustBe "Name"
              body.select(Selectors.nthSummaryRowValue(1, n = 2)).text() mustBe consigneeDetails.traderName.get

              body.select(Selectors.nthSummaryRowKey(2, n = 2)).text() mustBe "Excise Registration Number (ERN)"
              body.select(Selectors.nthSummaryRowValue(2, n = 2)).text() mustBe consigneeDetails.traderExciseNumber.get

              body.select(Selectors.nthSummaryRowKey(3, n = 2)).text() mustBe "Address"
              body.select(Selectors.nthSummaryRowValue(3, n = 2)).text() must include(consigneeDetails.address.get.street.get)
            }
          }

          "there is a single reason" must {

            "render the correct HTML" in {

              val result = helper.constructEventInformation(ie819AlertEvent, getMovementResponseModel)
              val consigneeDetails = getMovementResponseModel.consigneeTrader.get
              val body = Jsoup.parse(result.toString())

              body.select(Selectors.p(1)).text() mustBe messagesForLanguage.ie819AlertP1
              body.select(Selectors.p(2)).text() mustBe messagesForLanguage.printScreenContent

              //Alert Details summary
              body.select(Selectors.h2(1)).text() mustBe messagesForLanguage.ie819AlertH2
              body.select(Selectors.nthSummaryRowKey(1)).text() mustBe messagesForLanguage.ie819AlertDate
              body.select(Selectors.nthSummaryRowValue(1)).text() mustBe LocalDateTime.parse(ie819AlertEvent.eventDate).toLocalDate.formatDateForUIOutput()

              body.select(Selectors.nthSummaryRowKey(2)).text() mustBe messagesForLanguage.ie819AlertSummaryReason
              body.select(Selectors.nthSummaryRowValue(2)).select(Selectors.bullet(1)).text() mustBe alertRejectionReasonMessages.consigneeDetailsWrong

              //Consignee Detail
              body.select(Selectors.h2(2)).text() mustBe "Consignee"
              body.select(Selectors.nthSummaryRowKey(1, n = 2)).text() mustBe "Name"
              body.select(Selectors.nthSummaryRowValue(1, n = 2)).text() mustBe consigneeDetails.traderName.get

              body.select(Selectors.nthSummaryRowKey(2, n = 2)).text() mustBe "Excise Registration Number (ERN)"
              body.select(Selectors.nthSummaryRowValue(2, n = 2)).text() mustBe consigneeDetails.traderExciseNumber.get

              body.select(Selectors.nthSummaryRowKey(3, n = 2)).text() mustBe "Address"
              body.select(Selectors.nthSummaryRowValue(3, n = 2)).text() must include(consigneeDetails.address.get.street.get)
            }
          }
        }

        "being called with event type IE819, where the event is a Rejection" must {

          "render the correct HTML" in {

            val result = helper.constructEventInformation(ie819RejectionEvent, getMovementResponseModel)
            val consigneeDetails = getMovementResponseModel.consigneeTrader.get
            val body = Jsoup.parse(result.toString())

            body.select(Selectors.p(1)).text() mustBe messagesForLanguage.ie819RejectionP1
            body.select(Selectors.p(2)).text() mustBe messagesForLanguage.printScreenContent

            //Rejection Details summary
            body.select(Selectors.h2(1)).text() mustBe messagesForLanguage.ie819RejectionH2
            body.select(Selectors.nthSummaryRowKey(1)).text() mustBe messagesForLanguage.ie819RejectionDate
            body.select(Selectors.nthSummaryRowValue(1)).text() mustBe LocalDateTime.parse(ie819RejectionEvent.eventDate).toLocalDate.formatDateForUIOutput()

            body.select(Selectors.nthSummaryRowKey(2)).text() mustBe messagesForLanguage.ie819RejectionSummaryReason
            body.select(Selectors.nthSummaryRowValue(2)).select(Selectors.bullet(1)).text() mustBe alertRejectionReasonMessages.goodsQuantityWrong

            //Consignee Detail
            body.select(Selectors.h2(2)).text() mustBe "Consignee"
            body.select(Selectors.nthSummaryRowKey(1, n = 2)).text() mustBe "Name"
            body.select(Selectors.nthSummaryRowValue(1, n = 2)).text() mustBe consigneeDetails.traderName.get

            body.select(Selectors.nthSummaryRowKey(2, n = 2)).text() mustBe "Excise Registration Number (ERN)"
            body.select(Selectors.nthSummaryRowValue(2, n = 2)).text() mustBe consigneeDetails.traderExciseNumber.get

            body.select(Selectors.nthSummaryRowKey(3, n = 2)).text() mustBe "Address"
            body.select(Selectors.nthSummaryRowValue(3, n = 2)).text() must include(consigneeDetails.address.get.street.get)
          }
        }

        "being called with event type IE829 (movement accepted by customs)" must {
          "render the correct HTML" in {
            val testMovement = getMovementResponseModel.copy(destinationType = Export)

            val result = helper.constructEventInformation(ie829MovementAcceptedCustomsEvent, testMovement)
            val body = Jsoup.parse(result.toString())

            body.select(Selectors.p(1)).text() mustBe messagesForLanguage.ie829Paragraph1
            body.select(Selectors.p(2)).text() mustBe messagesForLanguage.printScreenContent

            val summaryLists = body.getElementsByClass("govuk-summary-list")

            val exportDetails = summaryLists.get(0)
            val exportDetailsSummaryListRows = exportDetails.getElementsByClass("govuk-summary-list__row")
            exportDetailsSummaryListRows.get(0).getElementsByTag("dt").text() mustBe "Accepted date"
            exportDetailsSummaryListRows.get(0).getElementsByTag("dd").text() mustBe "5 February 2024"
            exportDetailsSummaryListRows.get(1).getElementsByTag("dt").text() mustBe "Sender customs office reference number"
            exportDetailsSummaryListRows.get(1).getElementsByTag("dd").text() mustBe "GB000101"
            exportDetailsSummaryListRows.get(2).getElementsByTag("dt").text() mustBe "Sender customs officer"
            exportDetailsSummaryListRows.get(2).getElementsByTag("dd").text() mustBe "John Doe"
            exportDetailsSummaryListRows.get(3).getElementsByTag("dt").text() mustBe "Document reference number"
            exportDetailsSummaryListRows.get(3).getElementsByTag("dd").text() mustBe "645564546"

            val consigneeDetails = summaryLists.get(1)
            val consigneeDetailsSummaryListRows = consigneeDetails.getElementsByClass("govuk-summary-list__row")
            consigneeDetailsSummaryListRows.get(0).getElementsByTag("dt").text() mustBe "Name"
            consigneeDetailsSummaryListRows.get(0).getElementsByTag("dd").text() mustBe "PEAR Supermarket"
            consigneeDetailsSummaryListRows.get(1).getElementsByTag("dt").text() mustBe "Identification number"
            consigneeDetailsSummaryListRows.get(1).getElementsByTag("dd").text() mustBe "BE345345345"
            consigneeDetailsSummaryListRows.get(2).getElementsByTag("dt").text() mustBe "Address"
            consigneeDetailsSummaryListRows.get(2).getElementsByTag("dd").text() mustBe "Angels Business Park Bradford BD1 3NN"
            consigneeDetailsSummaryListRows.get(3).getElementsByTag("dt").text() mustBe "EORI number"
            consigneeDetailsSummaryListRows.get(3).getElementsByTag("dd").text() mustBe "GB00000578901"
          }
        }
      }
    }
  }
}
