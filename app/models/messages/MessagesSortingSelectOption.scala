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
import models.common.WithName
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

  case object IdentifierA extends WithName("identifierA") with MessagesSortingSelectOption {
    override val displayName = "viewAllMessages.sort.identifierA"
    override val sortOrder: String = "A"
    override val sortField: String = "arc"
  }

  case object IdentifierD extends WithName("identifierD") with MessagesSortingSelectOption {
    override val displayName: String = "viewAllMessages.sort.identifierD"
    override val sortOrder: String = "D"
    override val sortField: String = "arc"
  }

  case object ReadIndicatorA extends WithName("readIndicatorA") with MessagesSortingSelectOption {
    override val displayName = "viewAllMessages.sort.readIndicatorA"
    override val sortOrder: String = "D" // readindicator is a boolean, 0=unread & 1=read, so flip sort order
    override val sortField: String = "readindicator"
  }

  case object ReadIndicatorD extends WithName("readIndicatorD") with MessagesSortingSelectOption {
    override val displayName: String = "viewAllMessages.sort.readIndicatorD"
    override val sortOrder: String = "A" // readindicator is a boolean, 0=unread & 1=read, so flip sort order
    override val sortField: String = "readindicator"
  }

  val values: Seq[MessagesSortingSelectOption] = Seq(
    MessageTypeA,
    MessageTypeD,
    DateReceivedA,
    DateReceivedD,
    IdentifierA,
    IdentifierD,
    ReadIndicatorA,
    ReadIndicatorD
  )

  def apply(code: String): MessagesSortingSelectOption = code match {
    case MessageTypeA.code => MessageTypeA
    case MessageTypeD.code => MessageTypeD
    case DateReceivedA.code => DateReceivedA
    case DateReceivedD.code => DateReceivedD
    case IdentifierA.code => IdentifierA
    case IdentifierD.code => IdentifierD
    case ReadIndicatorA.code => ReadIndicatorA
    case ReadIndicatorD.code => ReadIndicatorD

    case invalid => throw new IllegalArgumentException(s"Invalid argument of '$invalid' received which can not be mapped to a MessagesSortingSelectOption")
  }

  def constructSelectItems(existingAnswer: Option[String] = None)(implicit messages: Messages): Seq[SelectItem] =
    SelectItemHelper.constructSelectItems(values, None, existingAnswer)
}
