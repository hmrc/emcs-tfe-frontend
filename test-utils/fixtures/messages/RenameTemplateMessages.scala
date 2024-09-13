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

package fixtures.messages

object RenameTemplateMessages {

  sealed trait ViewMessages extends BaseEnglish { _: i18n =>
    val heading = "Enter a new name for this template"
    val title = titleHelper(heading)
    def h2(text: String) = s"This is: $text"
    val hint = "This is for your reference only, to help you identify this movement for future use."
    val confirmAndSave = "Confirm and save"
  }

  object English extends ViewMessages
}
