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

import java.time.{LocalDate, LocalTime}
import java.time.format.DateTimeFormatter

trait DateUtils {
  implicit class LocalDateExtensions(date: LocalDate) {
    def formatDateForUIOutput(): String = {
      val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
      formatter.format(date)
    }
  }

  implicit class LocalTimeExtensions(time: LocalTime) {
    def formatTimeForUIOutput(): String = {
      val formatter = DateTimeFormatter.ofPattern("h:mm a")
      formatter.format(time)
    }
  }
}
