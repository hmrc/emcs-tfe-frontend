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

package viewmodels

import models.EventTypes

import java.time.LocalDateTime

case class TimelineEvent(eventType: EventTypes, title: String, dateTime: LocalDateTime, url: String) {
  def id(idx: Int = 0): String = title.replace(" ", "-").toLowerCase + (if(idx > 0) s"-${idx + 1}" else "")
}
