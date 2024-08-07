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

package utils

import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent.logger

import java.time.format.DateTimeFormatter
import java.time._
import java.util.Locale
import scala.util.{Failure, Success, Try}

trait DateUtils {

  def parseDateTime(eventDate: String): LocalDateTime = {

    val EisEventDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val ChrisEventDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    Try {
      LocalDateTime.parse(eventDate, EisEventDateFormat)
    } match {
      case Success(parsedDateTime) => parsedDateTime
      case Failure(_) =>
        Try {
          LocalDateTime.parse(eventDate, ChrisEventDateFormat)
        } match {
          case Success(parsedDateTime) => parsedDateTime
          case Failure(_) =>
            logger.error(s"[parseDateTime] - un-parseable date/time received [$eventDate], neither in EIS or ChRIS expected format")
            LocalDate.now.atStartOfDay
        }
    }
  }

  implicit class LocalDateExtensions(date: LocalDate) {
    def formatDateForUIOutput(): String = {
      val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
      formatter.format(date)
    }
  }

  implicit class LocalTimeExtensions(time: LocalTime) {
    def formatTimeForUIOutput(): String = {
      val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.UK)
      formatter.format(time)
    }
  }

  implicit class LocalDateTimeExtensions(dateTime: LocalDateTime) {
    def formatDateTimeForUIOutput(): String = {
      val dateFormat = DateTimeFormatter.ofPattern("d MMMM yyyy 'at' h:mm a", Locale.UK)
      dateTime.format(dateFormat)
    }
  }

  implicit class InstantExtensions(instant: Instant) {
    def formatDateForUIOutput(): String =
      LocalDate.ofInstant(instant, ZoneId.of("UTC")).formatDateForUIOutput()
  }

}
