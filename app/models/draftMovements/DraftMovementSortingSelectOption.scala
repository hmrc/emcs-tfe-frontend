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

package models.draftMovements

import models.SelectOptionModel
import play.api.i18n.Messages
import uk.gov.hmrc.emcstfefrontend.models.WithName
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem
import viewmodels.helpers.SelectItemHelper

sealed trait DraftMovementSortingSelectOption extends SelectOptionModel {
  override val code: String = this.toString
  val sortOrder: String
  val sortField: String
}

object DraftMovementSortingSelectOption {

  case object LrnAscending extends WithName("lrnAsc") with DraftMovementSortingSelectOption {
    override val displayName = "viewDraftMovements.sort.lrnAscending"
    override val sortOrder: String = "A"
    override val sortField: String = "lrn"
  }

  case object LrnDescending extends WithName("lrnDesc") with DraftMovementSortingSelectOption {
    override val displayName: String = "viewDraftMovements.sort.lrnDescending"
    override val sortOrder: String = "D"
    override val sortField: String = "lrn"
  }

  case object Newest extends WithName("newest") with DraftMovementSortingSelectOption {
    override val displayName: String = "viewDraftMovements.sort.newest"
    override val sortOrder: String = "D"
    override val sortField: String = "lastUpdated"
  }

  case object Oldest extends WithName("oldest") with DraftMovementSortingSelectOption {
    override val displayName: String = "viewDraftMovements.sort.oldest"
    override val sortOrder: String = "A"
    override val sortField: String = "lastUpdated"
  }

  val values: Seq[DraftMovementSortingSelectOption] = Seq(
    LrnAscending,
    LrnDescending,
    Newest,
    Oldest
  )

  def apply(code: String): DraftMovementSortingSelectOption = code match {
    case LrnAscending.code => LrnAscending
    case LrnDescending.code => LrnDescending
    case Newest.code => Newest
    case Oldest.code => Oldest
    case invalid => throw new IllegalArgumentException(s"Invalid argument of '$invalid' received which can not be mapped to a DraftMovementSortingSelectOption")
  }

  def constructSelectItems(existingAnswer: Option[String] = None)(implicit messages: Messages): Seq[SelectItem] =
    SelectItemHelper.constructSelectItems(values, None, existingAnswer)
}
