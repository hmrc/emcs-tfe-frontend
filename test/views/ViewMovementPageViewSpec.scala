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
import models.requests.DataRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import viewmodels.helpers.ViewMovementHelper
import viewmodels.{Movement, Overview, SubNavigationTab}
import views.html.viewMovement.ViewMovementPage


class ViewMovementPageViewSpec extends ViewSpecBase with ViewBehaviours with GetMovementResponseFixtures {

  val page: ViewMovementPage = app.injector.instanceOf[ViewMovementPage]
  val helper: ViewMovementHelper = app.injector.instanceOf[ViewMovementHelper]

  implicit val fakeRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest("GET", "/"), ern = "GBRC123456789")
  implicit val messages: Messages = messages(fakeRequest)

  object Selectors extends BaseSelectors {
    val subNavigationTabSelected = s"main nav.moj-sub-navigation a[aria-current=page]"
    def actionLink(i: Int) = s"main #actions > ul > li:nth-child($i) > a"
    def summaryCardRowKey(i: Int) = s"main div.govuk-summary-card div.govuk-summary-list__row:nth-of-type($i) > dt"
    def summaryCardAtIndexRowKey(cardIndex: Int, rowIndex: Int) = s"main div.govuk-summary-card:nth-of-type($cardIndex) div.govuk-summary-list__row:nth-of-type($rowIndex) > dt"
    def summaryCardTitle(i: Int) = s"main div.govuk-summary-card:nth-of-type($i) .govuk-summary-card__title"
  }

  "The ViewMovementPageView" when {

    Seq(English) foreach { messagesForLanguage =>

      s"being rendered in lang code of '${messagesForLanguage.lang.code}'" when {

        "rendering the overview tab" should {

          "display the navigation and overview card" when {

            val view = app.injector.instanceOf[ViewMovementPage]
            implicit val doc: Document = Jsoup.parse(
              view(
                testErn,
                testArc,
                isConsignor = true,
                SubNavigationTab.values,
                Overview,
                helper.movementCard(Overview, getMovementResponseModel)
              ).toString()
            )

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.title -> messagesForLanguage.title,
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
            val view = app.injector.instanceOf[ViewMovementPage]
            implicit val doc: Document = Jsoup.parse(
              view(
                testErn,
                testArc,
                isConsignor = true,
                SubNavigationTab.values,
                Overview,
                helper.movementCard(Overview, getMovementResponseModel)
              ).toString()
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
            val view = app.injector.instanceOf[ViewMovementPage]
            implicit val doc: Document = Jsoup.parse(
              view(
                testErn,
                testArc,
                isConsignor = false,
                SubNavigationTab.values,
                Overview,
                helper.movementCard(Overview, getMovementResponseModel)
              ).toString()
            )

            behave like pageWithExpectedElementsAndMessages(Seq(
              Selectors.actionLink(1) -> messagesForLanguage.actionLinkAlertOrRejection,
              Selectors.actionLink(2) -> messagesForLanguage.actionLinkSubmitReportOfReceipt,
              Selectors.actionLink(3) -> messagesForLanguage.actionLinkExplainDelay,
              Selectors.actionLink(4) -> messagesForLanguage.actionLinkExplainShortageOrExcess,
              Selectors.actionLink(5) -> messagesForLanguage.actionLinkPrint

            ))
          }
        }

        "render the movement tab" when {

          val view = app.injector.instanceOf[ViewMovementPage]
          implicit val doc: Document = Jsoup.parse(
            view(
              testErn,
              testArc,
              isConsignor = false,
              SubNavigationTab.values,
              Movement,
              helper.movementCard(Movement, getMovementResponseModel)
            ).toString()
          )

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
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
      }
    }
  }

}

