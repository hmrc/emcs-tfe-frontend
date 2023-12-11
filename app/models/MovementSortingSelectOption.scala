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

package uk.gov.hmrc.emcstfefrontend.models

import play.api.i18n.Messages
import uk.gov.hmrc.emcstfefrontend.viewmodels.helpers.SelectItemHelper
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

sealed trait MovementSortingSelectOption extends SelectOptionModel {
  override val code: String = this.toString
}

object MovementSortingSelectOption {

  case object Arc extends WithName("D") with MovementSortingSelectOption {
    override val displayName = "viewAllMovements.sort.arc"
  }

  case object ArcReverse extends WithName("R") with MovementSortingSelectOption {
    override val displayName: String = "viewAllMovements.sort.arcReverse"
  }

  case object Newest extends WithName("N") with MovementSortingSelectOption {
    override val displayName: String = "viewAllMovements.sort.newest"
  }

  case object Oldest extends WithName("O") with MovementSortingSelectOption {
    override val displayName: String = "viewAllMovements.sort.oldest"
  }

  val values: Seq[MovementSortingSelectOption] = Seq(
    Arc, ArcReverse, Newest, Oldest
  )

  def constructSelectItems(existingAnswer: Option[String] = None)(implicit messages: Messages): Seq[SelectItem] =
    SelectItemHelper.constructSelectItems(values, "", existingAnswer)
}
