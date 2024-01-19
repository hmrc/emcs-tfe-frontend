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
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

// TODO: this isn't currently being sent downstream are there are outstanding queries about it
trait MovementFilterStatusOption extends SelectOptionModel {
  override val code: String = this.toString
}

object MovementFilterStatusOption extends Enumerable.Implicits {
  case object ChooseStatus extends WithName("chooseStatus") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.chooseStatus"
  }

  case object Active extends WithName("active") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.active"
  }
  case object Cancelled extends WithName("cancelled") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.cancelled"
  }
  case object DeemedExported extends WithName("deemedExported") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.deemedExported"
  }
  case object Delivered extends WithName("delivered") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.delivered"
  }
  case object Diverted extends WithName("diverted") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.diverted"
  }
  case object Exporting extends WithName("exporting") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.exporting"
  }
  case object ManuallyClosed extends WithName("manuallyClosed") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.manuallyClosed"
  }
  case object PartiallyRefused extends WithName("partiallyRefused") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.partiallyRefused"
  }
  case object Refused extends WithName("refused") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.refused"
  }
  case object Replaced extends WithName("replaced") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.replaced"
  }
  case object Rejected extends WithName("rejected") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.rejected"
  }
  case object Stopped extends WithName("stopped") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.stopped"
  }

  val values: Seq[MovementFilterStatusOption] = Seq(
    ChooseStatus,
    Active,
    Cancelled,
    DeemedExported,
    Delivered,
    Diverted,
    Exporting,
    ManuallyClosed,
    PartiallyRefused,
    Refused,
    Replaced,
    Rejected,
    Stopped
  )

  def selectItems(implicit messages: Messages): Seq[SelectItem] =
    values.map {
      value =>
        SelectItem(
          text   = messages(s"viewAllMovements.filters.status.${value.toString}"),
          value   = Some(value.toString)
        )
    }

  def toOption(value: MovementFilterStatusOption): Option[MovementFilterStatusOption] = value match {
    case ChooseStatus => None
    case value => Some(value)
  }

  implicit val enumerable: Enumerable[MovementFilterStatusOption] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
