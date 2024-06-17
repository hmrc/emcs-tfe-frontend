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
import uk.gov.hmrc.emcstfefrontend.models.WithName
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem

sealed trait EntityGroup

object EntityGroup extends Enumerable.Implicits {
  case object UKTrader extends WithName("UK Record") with EntityGroup
  case object EUTrader extends WithName("EU Trader") with EntityGroup
  case object EUWarehouse extends WithName("EU Warehouse") with EntityGroup
  case object EUTemporaryAuthorisation extends WithName("EU Temporary Authorisation") with EntityGroup

  val values: Seq[EntityGroup] = Seq(EUTemporaryAuthorisation, EUTrader, EUWarehouse, UKTrader)

  implicit val enumerable: Enumerable[EntityGroup] =
    Enumerable(values.map(v => v.toString -> v): _*)

  def radioOptions()(implicit messages: Messages): Seq[RadioItem] = {
    values.map {
      value =>
        RadioItem(
          content = Text(messages(s"prevalidateTrader.consigneeTraderIdentification.entityGroup.${value.toString.replaceAll(" ", "")}")),
          value = Some(value.toString)
        )
    }
  }

}
