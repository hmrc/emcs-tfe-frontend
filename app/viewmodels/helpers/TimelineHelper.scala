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
import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import play.api.i18n.Messages
import utils.Logging
import viewmodels.TimelineEvent
import viewmodels.helpers.TimelineHelper._

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import javax.inject.Inject
import scala.util.{Failure, Success, Try}

class TimelineHelper @Inject()() extends Logging {

  def timeline(historyEvents: Seq[MovementHistoryEvent])(implicit messages: Messages): Seq[TimelineEvent] =
    historyEvents.map { event =>
      TimelineEvent(
        eventType = event.eventType,
        title = messages(s"${getEventBaseKey(event)}.label"),
        dateTime = parseDateTime(event.eventDate),
        url = getHistoryEventUrl(event)
      )
    }.sortBy(_.dateTime)

  def getEventTitleKey(event: MovementHistoryEvent): String =
    s"${getEventBaseKey(event)}.label"

  def getEventUrlDescription(event: MovementHistoryEvent): String =
    s"${getEventBaseKey(event)}.urlLabel"

  def getEventBaseKey(event: MovementHistoryEvent): String = {
    (event.eventType, event.sequenceNumber, event.messageRole) match {
      case (IE801, 1, 0) => s"movementHistoryEvent.${event.eventType}.first"
      case (IE801, _, 0) => s"movementHistoryEvent.${event.eventType}.further"
      case (IE802, _, 1) => s"movementHistoryEvent.${event.eventType}.cod"
      case (IE802, _, 2) => s"movementHistoryEvent.${event.eventType}.ror"
      case (IE802, _, 3) => s"movementHistoryEvent.${event.eventType}.des"
      case (IE803, _, 1) => s"movementHistoryEvent.${event.eventType}.diverted"
      case (IE803, _, 2) => s"movementHistoryEvent.${event.eventType}.split"
      case (IE840, _, 1) => s"movementHistoryEvent.${event.eventType}.first"
      case (IE840, _, 2) => s"movementHistoryEvent.${event.eventType}.complementary"
      case _ => s"movementHistoryEvent.${event.eventType}"
    }
  }

  def parseDateTime(eventDate: String): LocalDateTime = {
    Try {LocalDateTime.parse(eventDate, EisEventDateFormat)} match {
      case Success(parsedDateTime) => parsedDateTime
      case Failure(_) =>
        Try {LocalDateTime.parse(eventDate, ChrisEventDateFormat)} match {
          case Success(parsedDateTime) => parsedDateTime
          case Failure(_) =>
            logger.error(s"[parseDateTime] - un-parseable date/time received [$eventDate], neither in EIS or ChRIS expected format")
            LocalDate.now.atStartOfDay
        }
    }
  }

  def getHistoryEventUrl(event: MovementHistoryEvent)(implicit messages: Messages): String =
    s"event/${event.eventId}/${messages(getEventUrlDescription(event))}"

}

object TimelineHelper {
  private val EisEventDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  private val ChrisEventDateFormat =DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
}
