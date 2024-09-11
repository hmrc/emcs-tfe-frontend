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

package fixtures.messages.draftTemplates

import fixtures.messages.{BaseEnglish, i18n}

object DeleteTemplateMessages {

  sealed trait DeleteTemplateMessages extends BaseEnglish { _: i18n =>
    val heading = "Are you sure you want to delete this template?"
    val title = titleHelper(heading)
    val h1 = heading
    val radioButton1 = "Yes"
    val radioButton2 = "No"
    val button = "Confirm"
  }

  object English extends DeleteTemplateMessages
}
