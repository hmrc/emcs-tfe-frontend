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

import play.api.i18n.Messages
import models.SelectOptionModel
import uk.gov.hmrc.govukfrontend.views.Aliases.SelectItem

object SelectItemHelper {

  def constructSelectItems(selectOptions: Seq[SelectOptionModel],
                           defaultTextMessageKey: Option[String],
                           existingAnswer: Option[String] = None,
                           withEpcDescription: Boolean = false)
                          (implicit messages: Messages): Seq[SelectItem] =
    Seq(
      defaultTextMessageKey.map(default => SelectItem(
        value = Some(""),
        text = messages(default),
        selected = existingAnswer.isEmpty,
        disabled = true
      ))
    ).flatten ++ selectOptions.map { option =>
      val displayValue = if(withEpcDescription) option.displayName else option.displayName.split(":").head
      SelectItem(
        value = Some(option.code),
        text = messages(displayValue),
        selected = existingAnswer.contains(option.code)
      )
    }
}
