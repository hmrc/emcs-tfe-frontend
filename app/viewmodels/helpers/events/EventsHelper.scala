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
import utils.DateUtils
import viewmodels.helpers.TimelineHelper
import views.html.components.{bullets, link, p}

import javax.inject.Inject

class EventsHelper @Inject()(
                              timelineHelper: TimelineHelper,
                              eventHelper: MovementEventHelper,
                              link: link,
                              p: p,
                              bullets: bullets) extends DateUtils {

  def constructEventInformation(event: MovementHistoryEvent, movement: GetMovementResponse)(implicit messages: Messages): Html = {
    (event.eventType, event.messageRole) match {
      case (IE801, _) => ie801Html(event, movement)
      case (IE802, _) => ie802Html(event)
      case (IE803, _) => ie803Html(event, movement)
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

  private def ie803Html(event: MovementHistoryEvent, movement: GetMovementResponse)(implicit messages: Messages): Html =
    HtmlFormat.fill(
      Seq(
        movement.notificationOfDivertedMovement.map { notificationOfDivertedMovement =>
          if(event.messageRole == 2) {
            //Split movement
            HtmlFormat.fill(Seq(
              p(classes = "govuk-body-l")(Html(messages(s"${timelineHelper.getEventBaseKey(event)}.p1", notificationOfDivertedMovement.notificationDateAndTime.toLocalDate.formatDateForUIOutput()))),
              p()(Html(messages(s"${timelineHelper.getEventBaseKey(event)}.p2"))),
              bullets(notificationOfDivertedMovement.downstreamArcs.map(Html(_)))
            ))
          } else {
            //Diverted movement
            HtmlFormat.fill(Seq(
              p(classes = "govuk-body-l")(Html(messages(s"${timelineHelper.getEventBaseKey(event)}.p1"))),
              p()(Html(messages(s"${timelineHelper.getEventBaseKey(event)}.p2", notificationOfDivertedMovement.notificationDateAndTime.toLocalDate.formatDateForUIOutput())))
            ))
          }
        },
        Some(printPage(linkContentKey = "movementHistoryEvent.printLink", linkTrailingMessageKey = "movementHistoryEvent.printMessage"))
      ).flatten
    )

  private def printPage(linkContentKey: String, linkTrailingMessageKey: String)(implicit messages: Messages): Html = {
    p(classes = "govuk-body js-visible govuk-!-display-none-print")(
      HtmlFormat.fill(
        Seq(
          link(classes = "govuk-link", link = "#", id = Some("print-link"), messageKey = linkContentKey),
          Html(messages(linkTrailingMessageKey))
        )
      )
    )
  }

}
