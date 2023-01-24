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

package uk.gov.hmrc.emcstfefrontend.utils

import java.time.{Instant, LocalDate, LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter

trait DateUtils {
  implicit class LocalDateExtensions(date: LocalDate) {
    def formatDateForUIOutput(): String = {
      val f = DateTimeFormatter.ofPattern("dd MMMM yyyy")
      f.format(date)
    }
  }

  implicit class InstantExtensions(date: Instant) {
    def formatDateForUIOutput(): String = {
      val f = DateTimeFormatter.ofPattern("dd MMMM yyyy")
      f.format(LocalDateTime.ofInstant(date, ZoneId.of("UTC")))
    }
  }
}
