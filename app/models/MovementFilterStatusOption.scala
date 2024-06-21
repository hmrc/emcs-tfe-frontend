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
import models.common.WithName
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

trait MovementFilterStatusOption extends SelectOptionModel {
  override val code: String = this.toString
}

object MovementFilterStatusOption extends Enumerable.Implicits {
  case object ChooseStatus extends WithName("Choose status") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.status.chooseStatus"
  }

  case object Active extends WithName("Accepted") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.status.active"
  }
  case object Cancelled extends WithName("Cancelled") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.status.cancelled"
  }
  case object DeemedExported extends WithName("DeemedExported") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.status.deemedexported"
  }
  case object Delivered extends WithName("Delivered") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.status.delivered"
  }
  case object Diverted extends WithName("Diverted") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.status.diverted"
  }
  case object Exporting extends WithName("Exporting") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.status.exporting"
  }
  case object ManuallyClosed extends WithName("ManuallyClosed") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.status.manuallyclosed"
  }
  case object PartiallyRefused extends WithName("PartiallyRefused") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.status.partiallyrefused"
  }
  case object Refused extends WithName("Refused") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.status.refused"
  }
  case object Replaced extends WithName("Replaced") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.status.replaced"
  }
  case object Rejected extends WithName("Rejected") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.status.rejected"
  }
  case object Stopped extends WithName("Stopped") with MovementFilterStatusOption {
    override val displayName = "viewAllMovements.filters.status.stopped"
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

  //noinspection ScalaStyle - cyclomatic complexity
  def apply(code: String): MovementFilterStatusOption = code match {
    case ChooseStatus.code => ChooseStatus
    case Active.code => Active
    case Cancelled.code => Cancelled
    case DeemedExported.code => DeemedExported
    case Delivered.code => Delivered
    case Diverted.code => Diverted
    case Exporting.code => Exporting
    case ManuallyClosed.code => ManuallyClosed
    case PartiallyRefused.code => PartiallyRefused
    case Refused.code => Refused
    case Replaced.code => Replaced
    case Rejected.code => Rejected
    case Stopped.code => Stopped
    case invalid => throw new IllegalArgumentException(s"Invalid argument of '$invalid' received which can not be mapped to a MovementFilterStatusOption")
  }

  def selectItems(existingAnswer: Option[MovementFilterStatusOption])(implicit messages: Messages): Seq[SelectItem] =
    values.map {
      value =>
        SelectItem(
          text   = messages(value.displayName),
          value   = Some(value.toString),
          selected = existingAnswer.contains(value)
        )
    }

  def filterNotChooseStatus(value: Option[MovementFilterStatusOption]): Option[MovementFilterStatusOption] = value match {
    case Some(ChooseStatus) => None
    case value => value
  }

  implicit val enumerable: Enumerable[MovementFilterStatusOption] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
