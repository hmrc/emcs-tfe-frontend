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

package views

import base.ViewSpecBase
import fixtures.GetMovementResponseFixtures
import fixtures.messages.ViewMovementMessages.English
import models.common.{DestinationType, TraderModel}
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels._
import viewmodels.helpers.{ViewMovementActionsHelper, ViewMovementHelper}
import views.html.viewMovement.ViewMovementView

import java.time.{LocalDate, LocalDateTime}
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class ViewMovementViewSpec extends ViewSpecBase with ViewBehaviours with GetMovementResponseFixtures {

  val view: ViewMovementView = app.injector.instanceOf[ViewMovementView]

  val helper: ViewMovementHelper = app.injector.instanceOf[ViewMovementHelper]
  val actionLinkHelper: ViewMovementActionsHelper = app.injector.instanceOf[ViewMovementActionsHelper]
  val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  implicit val fakeRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest("GET", "/"), ern = "GBRC123456789")
  implicit val messages: Messages = messages(fakeRequest)
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  val consignorErn = "GBWKConsignor"
  val consigneeErn = "GBWKConsignee"

  object Selectors extends BaseSelectors {
    val subNavigationTabSelected = s"main nav.moj-sub-navigation a[aria-current=page]"

    def actionLink(i: Int) = s"main #actions > ul > li:nth-child($i) > a"

    def summaryCardRowKey(i: Int) = s"main div.govuk-summary-card div.govuk-summary-list__row:nth-of-type($i) > dt"

    def summaryCardAtIndexRowKey(cardIndex: Int, rowIndex: Int) = s"main div.govuk-summary-card:nth-of-type($cardIndex) div.govuk-summary-list__row:nth-of-type($rowIndex) > dt"

    def summaryCardTitle(i: Int) = s"main div.govuk-summary-card:nth-of-type($i) .govuk-summary-card__title"

    def historyTimelineLink(i: Int) = s"main #history > ol > li:nth-child($i) > h2 > a"
  }

  "The ViewMovementView" when {

    Seq(English) foreach { messagesForLanguage =>

      s"being rendered in lang code of '${messagesForLanguage.lang.code}'" when {

        "rendering the overview tab" should {

          "display the navigation and overview card" when {

            implicit val doc: Document = Jsoup.parse(
              view(
                testErn,
                testArc,
                getMovementResponseModel,
                SubNavigationTab.values,
                Overview,
                helper.movementCard(Overview, getMovementResponseModel).futureValue,
                Seq.empty[TimelineEvent],
                testMessageStatistics
              ).toString()
            )

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.title -> messagesForLanguage.title(testArc, messagesForLanguage.overviewTabHeading),
              Selectors.h2(1) -> messagesForLanguage.arcSubheading,
              Selectors.h1 -> testArc,

              Selectors.subNavigationTabSelected -> messagesForLanguage.overviewTabHeading,

              Selectors.summaryCardTitle(1) -> messagesForLanguage.overviewCardTitle,
              Selectors.summaryCardRowKey(1) -> messagesForLanguage.overviewCardLrn,
              Selectors.summaryCardRowKey(2) -> messagesForLanguage.overviewCardEadStatus,
              Selectors.summaryCardRowKey(3) -> messagesForLanguage.overviewCardDateOfDispatch,
              Selectors.summaryCardRowKey(4) -> messagesForLanguage.overviewCardExpectedDate,
              Selectors.summaryCardRowKey(5) -> messagesForLanguage.overviewCardConsignor,
              Selectors.summaryCardRowKey(6) -> messagesForLanguage.overviewCardNumberOfItems,
              Selectors.summaryCardRowKey(7) -> messagesForLanguage.overviewCardTransporting
            ))

          }

          "display the action links for a consignor" when {
            val consignorRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest("GET", "/"), ern = consignorErn)

            val testMovement = getMovementResponseModel
              .copy(
                consignorTrader = getMovementResponseModel.consignorTrader.copy(traderExciseNumber = Some(consignorErn)),
                consigneeTrader = Some(TraderModel(traderExciseNumber = Some(consigneeErn), None, None, None, None)),
                dateOfDispatch = LocalDate.now.plusDays(1),
                destinationType = DestinationType.Export
              )

            implicit val doc: Document = Jsoup.parse(
              view(
                consignorErn,
                testArc,
                testMovement,
                SubNavigationTab.values,
                Overview,
                helper.movementCard(Overview, testMovement)(consignorRequest, messages, hc, ec).futureValue,
                Seq.empty[TimelineEvent],
                testMessageStatistics
              )(consignorRequest, messages).toString()
            )

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.actionLink(1) -> messagesForLanguage.actionLinkCancelMovement,
              Selectors.actionLink(2) -> messagesForLanguage.actionLinkChangeOfDestination,
              Selectors.actionLink(3) -> messagesForLanguage.actionLinkExplainDelay,
              Selectors.actionLink(4) -> messagesForLanguage.actionLinkExplainShortageOrExcess,
              Selectors.actionLink(5) -> messagesForLanguage.actionLinkPrint
            ))
          }

          "display the action links for a consignee" when {
            val consigneeRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest("GET", "/"), ern = consigneeErn)

            val testMovement = getMovementResponseModel
              .copy(
                consigneeTrader = Some(TraderModel(traderExciseNumber = Some(consigneeErn), None, None, None, None)),
                consignorTrader = getMovementResponseModel.consignorTrader.copy(traderExciseNumber = Some(consignorErn)),
                dateOfDispatch = LocalDate.now.plusDays(1),
                destinationType = DestinationType.Export
              )

            implicit val doc: Document = Jsoup.parse(
              view(
                consigneeErn,
                testArc,
                testMovement,
                SubNavigationTab.values,
                Overview,
                helper.movementCard(Overview, testMovement)(consigneeRequest, messages, hc, ec).futureValue,
                Seq.empty[TimelineEvent],
                testMessageStatistics
              )(consigneeRequest, messages).toString()
            )

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.actionLink(1) -> messagesForLanguage.actionLinkAlertOrRejection,
              Selectors.actionLink(2) -> messagesForLanguage.actionLinkSubmitReportOfReceipt,
              Selectors.actionLink(3) -> messagesForLanguage.actionLinkExplainDelay,
              Selectors.actionLink(4) -> messagesForLanguage.actionLinkExplainShortageOrExcess,
              Selectors.actionLink(5) -> messagesForLanguage.actionLinkPrint
            ))
          }

          "display history timeline events" when {
            val eventDate = LocalDateTime.now()

            implicit val doc: Document = Jsoup.parse(
              view(
                testErn,
                testArc,
                getMovementResponseModel,
                SubNavigationTab.values,
                Overview,
                helper.movementCard(Overview, getMovementResponseModel).futureValue,
                Seq(
                  TimelineEvent(eventType = "someEvent1", title = "Movement created", dateTime = eventDate, url = s"event/someEvent1/id/1"),
                  TimelineEvent(eventType = "someEvent2", title = "Destination changed", dateTime = eventDate, url = s"event/someEvent2/id/2"),
                  TimelineEvent(eventType = "someEvent3", title = "Report of receipt submitted", dateTime = eventDate, url = s"event/someEvent3/id/3")
                ),
                testMessageStatistics
              ).toString()
            )

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.historyTimelineLink(1) -> "Movement created",
              Selectors.historyTimelineLink(2) -> "Destination changed",
              Selectors.historyTimelineLink(3) -> "Report of receipt submitted"
            ))
          }
        }

        "render the movement tab" when {

          implicit val doc: Document = Jsoup.parse(
            view(
              testErn,
              testArc,
              getMovementResponseModel,
              SubNavigationTab.values,
              Movement,
              helper.movementCard(Movement, getMovementResponseModel).futureValue,
              Seq.empty[TimelineEvent],
              testMessageStatistics
            ).toString()
          )

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title(testArc, messagesForLanguage.movementTabHeading),
            Selectors.h2(1) -> messagesForLanguage.arcSubheading,
            Selectors.h1 -> testArc,

            Selectors.subNavigationTabSelected -> messagesForLanguage.movementTabHeading,

            Selectors.summaryCardTitle(1) -> messagesForLanguage.movementSummaryCardTitle,
            Selectors.summaryCardAtIndexRowKey(1, 1) -> messagesForLanguage.movementSummaryCardLrn,
            Selectors.summaryCardAtIndexRowKey(1, 2) -> messagesForLanguage.movementSummaryCardEADStatus,
            Selectors.summaryCardAtIndexRowKey(1, 3) -> messagesForLanguage.movementSummaryCardReceiptStatus,
            Selectors.summaryCardAtIndexRowKey(1, 4) -> messagesForLanguage.movementSummaryCardMovementType,
            Selectors.summaryCardAtIndexRowKey(1, 5) -> messagesForLanguage.movementSummaryCardMovementDirection,

            Selectors.summaryCardTitle(2) -> messagesForLanguage.movementTimeAndDateCardTitle,
            Selectors.summaryCardAtIndexRowKey(2, 1) -> messagesForLanguage.movementTimeAndDateCardDateOfDispatch,
            Selectors.summaryCardAtIndexRowKey(2, 2) -> messagesForLanguage.movementTimeAndDateCardTimeOfDispatch,
            Selectors.summaryCardAtIndexRowKey(2, 3) -> messagesForLanguage.movementTimeAndDateCardDateOfArrival,

            Selectors.summaryCardTitle(3) -> messagesForLanguage.movementInvoiceCardTitle,
            Selectors.summaryCardAtIndexRowKey(3, 1) -> messagesForLanguage.movementInvoiceCardReference,
            Selectors.summaryCardAtIndexRowKey(3, 2) -> messagesForLanguage.movementInvoiceCardDateOfIssue
          ))

        }

        "rendering the Delivery tab" should {
          "display the navigation and delivery cards" when {
            implicit val doc: Document = Jsoup.parse(
              view(
                testErn,
                testArc,
                getMovementResponseModel,
                SubNavigationTab.values,
                Delivery,
                helper.movementCard(Delivery, getMovementResponseModel).futureValue,
                Seq.empty[TimelineEvent],
                testMessageStatistics
              ).toString()
            )

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.title -> messagesForLanguage.title(testArc, messagesForLanguage.deliveryTabHeading),
              Selectors.h2(1) -> messagesForLanguage.arcSubheading,
              Selectors.h1 -> testArc,
              Selectors.h2(2) -> messagesForLanguage.deliveryDetailsHeading,

              Selectors.subNavigationTabSelected -> messagesForLanguage.deliveryTabHeading,

              Selectors.summaryCardTitle(1) -> messagesForLanguage.deliveryConsignorCardTitle,
              Selectors.summaryCardAtIndexRowKey(1, 1) -> messagesForLanguage.deliveryCardBusinessName,
              Selectors.summaryCardAtIndexRowKey(1, 2) -> messagesForLanguage.deliveryCardERN,
              Selectors.summaryCardAtIndexRowKey(1, 3) -> messagesForLanguage.deliveryCardAddress,
              Selectors.summaryCardTitle(2) -> messagesForLanguage.deliveryPlaceOfDispatchCardTitle,
              Selectors.summaryCardAtIndexRowKey(2, 1) -> messagesForLanguage.deliveryCardBusinessName,
              Selectors.summaryCardAtIndexRowKey(2, 2) -> messagesForLanguage.deliveryPlaceOfDispatchCardERN,
              Selectors.summaryCardAtIndexRowKey(2, 3) -> messagesForLanguage.deliveryCardAddress,
              Selectors.summaryCardTitle(3) -> messagesForLanguage.deliveryConsigneeCardTitle,
              Selectors.summaryCardAtIndexRowKey(3, 1) -> messagesForLanguage.deliveryCardBusinessName,
              Selectors.summaryCardAtIndexRowKey(3, 2) -> messagesForLanguage.deliveryCardERN,
              Selectors.summaryCardAtIndexRowKey(3, 3) -> messagesForLanguage.deliveryCardAddress,
              Selectors.summaryCardTitle(4) -> messagesForLanguage.deliveryPlaceOfDestinationCardTitle,
              Selectors.summaryCardAtIndexRowKey(4, 1) -> messagesForLanguage.deliveryCardBusinessName,
              Selectors.summaryCardAtIndexRowKey(4, 2) -> messagesForLanguage.deliveryCardERN,
              Selectors.summaryCardAtIndexRowKey(4, 3) -> messagesForLanguage.deliveryCardAddress
            ))

          }
        }
      }
    }
  }

}

