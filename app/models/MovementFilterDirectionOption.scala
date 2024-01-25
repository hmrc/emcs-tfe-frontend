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

// TODO: ChRIS requires sentence-case, not lowercase (EIS requires lowercase)
sealed trait MovementFilterDirectionOption extends SelectOptionModel {
  override val code: String = this.toString
}

object MovementFilterDirectionOption extends Enumerable.Implicits {
  case object GoodsIn extends WithName("consignee") with MovementFilterDirectionOption {
    override val displayName = "viewAllMovements.filters.direction.consignee"
  }
  case object GoodsOut extends WithName("consignor") with MovementFilterDirectionOption {
    override val displayName = "viewAllMovements.filters.direction.consignor"
  }
  case object All extends WithName("all") with MovementFilterDirectionOption {
    override val displayName = "viewAllMovements.filters.direction.all"
  }

  val values: Seq[MovementFilterDirectionOption] = Seq(GoodsIn, GoodsOut, All)
  private val displayValues: Seq[MovementFilterDirectionOption] = Seq(GoodsIn, GoodsOut)

  def checkboxItems(implicit messages: Messages): Seq[CheckboxItem] =
    displayValues.zipWithIndex.map {
      case (value, index) =>
        CheckboxItemViewModel(
          content = Text(messages(value.displayName)),
          fieldId = "traderRole",
          index   = index,
          value   = value.toString
        )
    }

  def apply(code: String): MovementFilterDirectionOption = code match {
    case GoodsIn.code => GoodsIn
    case GoodsOut.code => GoodsOut
    case All.code => All
    case invalid => throw new IllegalArgumentException(s"Invalid argument of '$invalid' received which can not be mapped to a MovementFilterDirectionOption")
  }

  def toOptions(option: MovementFilterDirectionOption): Set[MovementFilterDirectionOption] = option match {
    case GoodsIn => Set(GoodsIn)
    case GoodsOut => Set(GoodsOut)
    case All => Set(GoodsOut, GoodsIn)
  }

  implicit val enumerable: Enumerable[MovementFilterDirectionOption] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
