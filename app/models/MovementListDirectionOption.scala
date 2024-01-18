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

sealed trait MovementListDirectionOption extends SelectOptionModel {
  override val code: String = this.toString
}

object MovementListDirectionOption extends Enumerable.Implicits {
  case object GoodsIn extends WithName("consignee") with MovementListDirectionOption {
    override val displayName = "viewAllMovements.filters.consignee"
  }
  case object GoodsOut extends WithName("consignor") with MovementListDirectionOption {
    override val displayName = "viewAllMovements.filters.consignor"
  }
  case object Both extends WithName("both") with MovementListDirectionOption {
    override val displayName = "viewAllMovements.filters.both"
  }

  val values: Seq[MovementListDirectionOption] = Seq(GoodsIn, GoodsOut, Both)
  private val displayValues: Seq[MovementListDirectionOption] = Seq(GoodsIn, GoodsOut)

  def checkboxItems(implicit messages: Messages): Seq[CheckboxItem] =
    displayValues.zipWithIndex.map {
      case (value, index) =>
        CheckboxItemViewModel(
          content = Text(messages(s"viewAllMovements.filters.direction.${value.toString}")),
          fieldId = "traderRole",
          index   = index,
          value   = value.toString
        )
    }

  def apply(code: String): MovementListDirectionOption = code match {
    case GoodsIn.code => GoodsIn
    case GoodsOut.code => GoodsOut
    case Both.code => Both
    case invalid => throw new IllegalArgumentException(s"Invalid argument of '$invalid' received which can not be mapped to a MovementListDirectionOptions")
  }

  def toOptions(option: MovementListDirectionOption): Set[MovementListDirectionOption] = option match {
    case GoodsIn => Set(GoodsIn)
    case GoodsOut => Set(GoodsOut)
    case Both => Set(GoodsOut, GoodsIn)
  }

  implicit val enumerable: Enumerable[MovementListDirectionOption] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
