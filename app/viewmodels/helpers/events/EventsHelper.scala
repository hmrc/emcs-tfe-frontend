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

import models.EventTypes._
import models.response.emcsTfe.GetMovementResponse
import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Empty
import viewmodels.helpers.TimelineHelper
import views.html.components.{link, p}

import javax.inject.Inject

class EventsHelper @Inject()(
                              timelineHelper: TimelineHelper,
                              eventHelper: MovementEventHelper,
                              link: link,
                              p: p) {

  def constructEventInformation(event: MovementHistoryEvent, movement: GetMovementResponse)(implicit messages: Messages): Html = {
    (event.eventType, event.messageRole) match {
      case (IE801, _)  => ie801Html(event, movement)
      case (IE802, 1) | (IE802, 3) => ie802Html(event)
      case _ => Empty.asHtml
    }
  }

  private def ie801Html(event: MovementHistoryEvent, movement: GetMovementResponse)(implicit messages: Messages): Html = {
    implicit val _movement: GetMovementResponse = movement

    HtmlFormat.fill(
      Seq(
        p(classes = "govuk-body-l")(Html(
          messages(s"${timelineHelper.getEventBaseKey(event)}.p1", event.sequenceNumber)
        )),
        printPage(linkContentKey = "movementHistoryEvent.printLink", linkTrailingMessageKey = "movementHistoryEvent.printMessage"),
        eventHelper.movementInformationCard(),
        eventHelper.consignorInformationCard(),
        eventHelper.placeOfDispatchInformationCard(),
        eventHelper.importInformationCard(),
        eventHelper.consigneeInformationCard(),
        eventHelper.exemptedOrganisationInformationCard(),
        eventHelper.placeOfDestinationInformationCard(),
        eventHelper.exportInformationCard(),
        eventHelper.guarantorInformationCard(),
        eventHelper.journeyInformationCard(),
        eventHelper.transportArrangerInformationCard(),
        eventHelper.firstTransporterInformationCard(),
        eventHelper.transportUnitsInformationCard(),
        eventHelper.itemsInformationCard(),
        eventHelper.sadInformationCard(),
        eventHelper.documentsInformationCard()
      )
    )
  }

  private def ie802Html(event: MovementHistoryEvent)(implicit messages: Messages): Html =
    HtmlFormat.fill(
      Seq(
        p(classes = "govuk-body-l")(Html(messages(s"${timelineHelper.getEventBaseKey(event)}.p1"))),
        printPage(linkContentKey = "movementHistoryEvent.printLink", linkTrailingMessageKey = "movementHistoryEvent.printMessage")
      )
    )

  private def printPage(linkContentKey: String, linkTrailingMessageKey: String)(implicit messages: Messages): Html = {
    p(classes = "govuk-body no-print")(
      HtmlFormat.fill(
        Seq(
          link(classes = "govuk-link", link ="javascript:if(window.print)window.print()", id = Some("print-link"), messageKey=linkContentKey),
          Html(messages(linkTrailingMessageKey))
        )
      )
    )
  }

}
