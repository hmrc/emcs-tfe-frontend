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

package viewmodels.draftMovements

import models.SelectOptionModel
import models.common.{Enumerable, WithName}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.checkbox._

sealed trait DraftMovementsErrorsOption extends SelectOptionModel {
  override val code: String = this.toString
}

object DraftMovementsErrorsOption extends Enumerable.Implicits {
  case object DraftHasErrors extends WithName("draftHasErrors") with DraftMovementsErrorsOption {
    override val displayName = "viewAllDraftMovements.filters.errors.draftHasErrors"
  }

  val values: Seq[DraftMovementsErrorsOption] = Seq(DraftHasErrors)

  private val displayValues: Seq[DraftMovementsErrorsOption] = Seq(DraftHasErrors)

  def checkboxItems(implicit messages: Messages): Seq[CheckboxItem] =
    displayValues.zipWithIndex.map {
      case (value, index) =>
        CheckboxItemViewModel(
          content = Text(messages(value.displayName)),
          fieldId = "draftHasErrors",
          index   = index,
          value   = value.toString
        )
    }

  def apply(code: String): DraftMovementsErrorsOption = code match {
    case DraftHasErrors.code => DraftHasErrors
    case invalid => throw new IllegalArgumentException(s"Invalid argument of '$invalid' received which can not be mapped to a DraftMovementsErrorsOption")
  }

  implicit val enumerable: Enumerable[DraftMovementsErrorsOption] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
