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

package models.messages

import models.SelectOptionModel
import play.api.i18n.Messages
import uk.gov.hmrc.emcstfefrontend.models.WithName
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem
import viewmodels.helpers.SelectItemHelper

sealed trait MessagesSortingSelectOption extends SelectOptionModel {
  override val code: String = this.toString
  val sortOrder: String
  val sortField: String
}

object MessagesSortingSelectOption {

  case object MessageTypeA extends WithName("messageTypeA") with MessagesSortingSelectOption {
    override val displayName = "viewAllMessages.sort.messageTypeA"
    override val sortOrder: String = "A"
    override val sortField: String = "messagetype"
  }

  case object MessageTypeD extends WithName("messageTypeD") with MessagesSortingSelectOption {
    override val displayName: String = "viewAllMessages.sort.messageTypeD"
    override val sortOrder: String = "D"
    override val sortField: String = "messagetype"
  }

  case object DateReceivedA extends WithName("dateReceivedA") with MessagesSortingSelectOption {
    override val displayName = "viewAllMessages.sort.dateReceivedA"
    override val sortOrder: String = "A"
    override val sortField: String = "datereceived"
  }

  case object DateReceivedD extends WithName("dateReceivedD") with MessagesSortingSelectOption {
    override val displayName: String = "viewAllMessages.sort.dateReceivedD"
    override val sortOrder: String = "D"
    override val sortField: String = "datereceived"
  }

  case object ArcA extends WithName("arcA") with MessagesSortingSelectOption {
    override val displayName = "viewAllMessages.sort.arcA"
    override val sortOrder: String = "A"
    override val sortField: String = "arc"
  }

  case object ArcD extends WithName("arcD") with MessagesSortingSelectOption {
    override val displayName: String = "viewAllMessages.sort.arcD"
    override val sortOrder: String = "D"
    override val sortField: String = "arc"
  }

  case object ReadIndicatorA extends WithName("readIndicatorA") with MessagesSortingSelectOption {
    override val displayName = "viewAllMessages.sort.readIndicatorA"
    override val sortOrder: String = "A"
    override val sortField: String = "readindicator"
  }

  case object ReadIndicatorD extends WithName("readIndicatorD") with MessagesSortingSelectOption {
    override val displayName: String = "viewAllMessages.sort.readIndicatorD"
    override val sortOrder: String = "D"
    override val sortField: String = "readindicator"
  }

  val values: Seq[MessagesSortingSelectOption] = Seq(
    MessageTypeA,
    MessageTypeD,
    DateReceivedA,
    DateReceivedD,
    ArcA,
    ArcD,
    ReadIndicatorA,
    ReadIndicatorD
  )

  def apply(code: String): MessagesSortingSelectOption = code match {
    case MessageTypeA.code => MessageTypeA
    case MessageTypeD.code => MessageTypeD
    case DateReceivedA.code => DateReceivedA
    case DateReceivedD.code => DateReceivedD
    case ArcA.code => ArcA
    case ArcD.code => ArcD
    case ReadIndicatorA.code => ReadIndicatorA
    case ReadIndicatorD.code => ReadIndicatorD

    case invalid => throw new IllegalArgumentException(s"Invalid argument of '$invalid' received which can not be mapped to a MessagesSortingSelectOption")
  }

  def constructSelectItems(existingAnswer: Option[String] = None)(implicit messages: Messages): Seq[SelectItem] =
    SelectItemHelper.constructSelectItems(values, None, existingAnswer)
}
