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

import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import play.api.i18n.Messages
import utils.{DateUtils, Logging}
import viewmodels.TimelineEvent

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import javax.inject.Inject
import scala.util.Try

class TimelineHelper @Inject()() extends Logging with DateUtils {

  def timeline(historyEvents: Seq[MovementHistoryEvent])(implicit messages: Messages): Seq[TimelineEvent] =
    historyEvents.map { event =>
      TimelineEvent(
        eventType = event.eventType,
        title = messages(getEventTitleKey(event)),
        dateTime = parseDateTime(event.eventDate),
        url = getHistoryEventUrl(event)
      )
    }.sortBy(_.dateTime)


  def getEventTitleKey(event: MovementHistoryEvent): String = {
    (event.eventType, event.sequenceNumber, event.messageRole) match {
      case ("IE801", 1, 0) => s"movementHistoryEvent.${event.eventType}.first.label"
      case ("IE801", _, 0) => s"movementHistoryEvent.${event.eventType}.further.label"
      case ("IE802", _, 1) => s"movementHistoryEvent.${event.eventType}.cod.label"
      case ("IE802", _, 2) => s"movementHistoryEvent.${event.eventType}.ror.label"
      case ("IE802", _, 3) => s"movementHistoryEvent.${event.eventType}.des.label"
      case ("IE803", _, 1) => s"movementHistoryEvent.${event.eventType}.diverted.label"
      case ("IE803", _, 2) => s"movementHistoryEvent.${event.eventType}.split.label"
      case ("IE840", _, 1) => s"movementHistoryEvent.${event.eventType}.first.label"
      case ("IE840", _, 2) => s"movementHistoryEvent.${event.eventType}.complementary.label"
      case _ => s"movementHistoryEvent.${event.eventType}.label"
    }

  }

  def parseDateTime(eventDate: String): LocalDateTime =
    Try {
      LocalDateTime.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
    }.getOrElse{
      logger.error(s"[parseDateTime] - un-parseable date/time received [$eventDate]")
      LocalDate.now.atStartOfDay
    }

  def getHistoryEventUrl(event: MovementHistoryEvent): String =
    s"event/${event.eventType}/id/${event.eventDate.hashCode >>> 1}"

}
