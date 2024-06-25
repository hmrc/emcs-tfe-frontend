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

package models.prevalidate

import models.common.Enumerable
import play.api.i18n.Messages
import models.common.WithName
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait EntityGroup {
  val messageKey: String
}

object EntityGroup extends Enumerable.Implicits {
  case object UKTrader extends WithName("UK Record") with EntityGroup {
    override val messageKey: String = "prevalidateTrader.consigneeTraderIdentification.entityGroup.UKRecord"
  }
  case object EUTrader extends WithName("EU Trader") with EntityGroup {
    override val messageKey: String = "prevalidateTrader.consigneeTraderIdentification.entityGroup.EUTrader"
  }
  case object EUWarehouse extends WithName("EU Warehouse") with EntityGroup {
    override val messageKey: String = "prevalidateTrader.consigneeTraderIdentification.entityGroup.EUWarehouse"
  }
  case object EUTemporaryAuthorisation extends WithName("EU Temporary Authorisation") with EntityGroup {
    override val messageKey: String = "prevalidateTrader.consigneeTraderIdentification.entityGroup.EUTemporaryAuthorisation"
  }

  val values: Seq[EntityGroup] = Seq(EUTemporaryAuthorisation, EUTrader, EUWarehouse, UKTrader)

  implicit val enumerable: Enumerable[EntityGroup] =
    Enumerable(values.map(v => v.toString -> v): _*)

  def radioOptions()(implicit messages: Messages): Seq[RadioItem] = {
    values.map {
      value =>
        RadioItem(
          content = Text(messages(messages(value.messageKey))),
          value = Some(value.toString)
        )
    }
  }

}
