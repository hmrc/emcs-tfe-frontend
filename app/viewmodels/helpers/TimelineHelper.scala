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

package viewmodels.helpers

import models.EventTypes._
import models.common.DestinationType
import models.response.emcsTfe.GetMovementResponse
import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import play.api.i18n.Messages
import utils.Logging
import viewmodels.TimelineEvent

import javax.inject.Inject

class TimelineHelper @Inject()() extends Logging {

  def timeline(historyEvents: Seq[MovementHistoryEvent])(implicit messages: Messages): Seq[TimelineEvent] =
    historyEvents.map { event =>
      TimelineEvent(
        eventType = event.eventType,
        title = messages(s"${getEventBaseKey(event)}.label"),
        dateTime = event.eventDate,
        url = getHistoryEventUrl(event)
      )
    }.sortBy(_.dateTime)

  def getEventTitleKey(event: MovementHistoryEvent, movement: GetMovementResponse): String =
    event.eventType match {
      case IE818 if movement.destinationType == DestinationType.Export => s"${getEventBaseKey(event)}.label.export"
      case _ => s"${getEventBaseKey(event)}.label"
    }

  def getEventUrlDescription(event: MovementHistoryEvent): String =
    s"${getEventBaseKey(event)}.urlLabel"

  def getEventBaseKey(event: MovementHistoryEvent): String = {
    val base = s"movementHistoryEvent.${event.eventType}"

    val suffix = (event.eventType, event.messageRole) match {
      case (IE801, 0) => if (event.isFirstEventTypeInHistory) "first" else "further"
      case (IE802, 1) => "cod"
      case (IE802, 2) => "ror"
      case (IE802, 3) => "des"
      case (IE803, 1) => "diverted"
      case (IE803, 2) => "split"
      case _ => ""
    }

    if (suffix.isEmpty) base else s"$base.$suffix"
  }

  def getHistoryEventUrl(event: MovementHistoryEvent)(implicit messages: Messages): String =
    s"event/${event.eventId}/${messages(getEventUrlDescription(event))}"

}
