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

import forms.ViewAllMovementsFormProvider
import play.api.i18n.Messages
import models.common.WithName
import play.api.data.Form
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem
import viewmodels.helpers.SelectItemHelper

sealed trait MovementSearchSelectOption extends SelectOptionModel {
  override val code: String = this.toString
}

object MovementSearchSelectOption {

  case object ChooseSearch extends WithName("chooseRefType") with MovementSearchSelectOption {
    override val displayName = "viewAllMovements.search.suffix.chooseRefType"
  }

  case object ARC extends WithName("arc") with MovementSearchSelectOption {
    override val displayName = "viewAllMovements.search.suffix.arc"
  }

  case object LRN extends WithName("lrn") with MovementSearchSelectOption {
    override val displayName: String = "viewAllMovements.search.suffix.lrn"
  }

  case object ERN extends WithName("otherTraderId") with MovementSearchSelectOption {
    override val displayName: String = "viewAllMovements.search.suffix.ern"
  }

  case object Transporter extends WithName("transporterTraderName") with MovementSearchSelectOption {
    override val displayName: String = "viewAllMovements.search.suffix.transporter"
  }

  val values: Seq[MovementSearchSelectOption] = Seq(ARC, LRN, ERN, Transporter)

  def apply(code: String): MovementSearchSelectOption = code match {
    case ChooseSearch.code => ChooseSearch
    case ARC.code => ARC
    case LRN.code => LRN
    case ERN.code => ERN
    case Transporter.code => Transporter
    case invalid => throw new IllegalArgumentException(s"Invalid argument of '$invalid' received which can not be mapped to a MovementSearchSelectOption")
  }

  def filterNotChooseSearch(value: Option[String]): Option[MovementSearchSelectOption] = value.map(apply) match {
    case Some(ChooseSearch) => None
    case value => value
  }

  def constructSelectItems(form: Form[MovementListSearchOptions])(implicit messages: Messages): Seq[SelectItem] = {
    val searchKey = form.data.get(ViewAllMovementsFormProvider.searchKey)
    val searchValue = form.data.get(ViewAllMovementsFormProvider.searchValue)

    // default to ChooseSearch if no searchValue as this is not selectable otherwise
    val existingAnswer = if (searchValue.isDefined && searchValue.exists(_.trim.nonEmpty)) searchKey else None

    SelectItemHelper.constructSelectItems(values, Some(ChooseSearch.displayName), existingAnswer)
  }
}
