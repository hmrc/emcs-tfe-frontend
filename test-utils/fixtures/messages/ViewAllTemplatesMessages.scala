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

object ViewAllTemplatesMessages {

  sealed trait ViewMessages extends BaseEnglish { _: i18n =>
    val heading = "Templates"
    val title = titleHelper(heading)

    val p1 = "Draft movements can be saved as a template when submitting. Templates allow you to edit and reuse most of the movement information."
    val p2: Int => String = max => s"You can save a maximum of $max templates."
    val maxWarning: Int => String = max => s"You have reached the maximum limit of $max templates. If you wish to save a new template you must delete an existing template."

    val tableHeadingDetails = "Template details"
    val tableHeadingActions = "Actions"

    def destination(text: String) = s"Destination: $text"
    def businessName(text: String) = s"Consignee business name: $text"
    def consignee(text: String) = s"Consignee excise registration number (ERN): $text"

    val actionCreate = "Start a draft movement with this template"
    val actionRename = "Rename template"
    val actionDelete = "Delete template"

    val oneTemplateH2 = "1 template found"
    val multipleTemplatesH2: Int => String = count => s"$count templates found"

    val noTemplatesP1 = "You have not created any templates."
    val noTemplatesP2 = "You can create a template by submitting a draft movement. Templates allow you to edit and reuse most of the movement information."

    val createNewMovementButton = "Create a new movement"
  }

  object English extends ViewMessages
}
