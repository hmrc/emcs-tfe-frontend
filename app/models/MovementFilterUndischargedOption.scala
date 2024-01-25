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

package models

import models.common.Enumerable
import play.api.i18n.Messages
import uk.gov.hmrc.emcstfefrontend.models.WithName
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewmodels.govuk.checkbox._

trait MovementFilterUndischargedOption extends SelectOptionModel {
  override val code: String = this.toString
}

object MovementFilterUndischargedOption extends Enumerable.Implicits {

  case object Undischarged extends WithName("Y") with MovementFilterUndischargedOption {
    override val displayName = "viewAllMovements.filters.undischarged.undischarged"
  }

  val values: Seq[MovementFilterUndischargedOption] = Seq(Undischarged)

  def checkboxItems(implicit messages: Messages): Seq[CheckboxItem] =
    values.zipWithIndex.map {
      case (value, index) =>
        CheckboxItemViewModel(
          content = Text(messages(value.displayName)),
          fieldId = "undischargedMovements",
          index   = index,
          value   = value.toString
        )
    }

  def apply(code: String): MovementFilterUndischargedOption = code match {
    case Undischarged.code => Undischarged
    case invalid => throw new IllegalArgumentException(s"Invalid argument of '$invalid' received which can not be mapped to a MovementFilterUndischargedOption")
  }

  implicit val enumerable: Enumerable[MovementFilterUndischargedOption] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
