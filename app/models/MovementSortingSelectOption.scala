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

package models

import play.api.i18n.Messages
import uk.gov.hmrc.emcstfefrontend.models.WithName
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem
import viewmodels.helpers.SelectItemHelper

sealed trait MovementSortingSelectOption extends SelectOptionModel {
  override val code: String = this.toString
  val sortOrder: String
  val sortField: String
}

object MovementSortingSelectOption {

  case object ArcAscending extends WithName("arcAsc") with MovementSortingSelectOption {
    override val displayName = "viewAllMovements.sort.arcAscending"
    override val sortOrder: String = "A"
    override val sortField: String = "arc"
  }

  case object ArcDescending extends WithName("arcDesc") with MovementSortingSelectOption {
    override val displayName: String = "viewAllMovements.sort.arcDescending"
    override val sortOrder: String = "D"
    override val sortField: String = "arc"
  }

  case object Newest extends WithName("newest") with MovementSortingSelectOption {
    override val displayName: String = "viewAllMovements.sort.newest"
    override val sortOrder: String = "D"
    override val sortField: String = "dateofdispatch"
  }

  case object Oldest extends WithName("oldest") with MovementSortingSelectOption {
    override val displayName: String = "viewAllMovements.sort.oldest"
    override val sortOrder: String = "A"
    override val sortField: String = "dateofdispatch"
  }

  val values: Seq[MovementSortingSelectOption] = Seq(
    ArcAscending,
    ArcDescending,
    Newest,
    Oldest
  )

  def apply(code: String): MovementSortingSelectOption = code match {
    case ArcAscending.code => ArcAscending
    case ArcDescending.code => ArcDescending
    case Newest.code => Newest
    case Oldest.code => Oldest
    case invalid => throw new IllegalArgumentException(s"Invalid argument of '$invalid' received which can not be mapped to a MovementSortingSelectOption")
  }

  def constructSelectItems(existingAnswer: Option[String] = None)(implicit messages: Messages): Seq[SelectItem] =
    SelectItemHelper.constructSelectItems(values, "", existingAnswer)
}
