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
import uk.gov.hmrc.emcstfefrontend.models.WithName

sealed trait EventTypes

object EventTypes extends Enumerable.Implicits {
  case object IE801 extends WithName("IE801") with EventTypes
  case object IE802 extends WithName("IE802") with EventTypes
  case object IE803 extends WithName("IE803") with EventTypes
  case object IE807 extends WithName("IE807") with EventTypes
  case object IE810 extends WithName("IE810") with EventTypes
  case object IE813 extends WithName("IE813") with EventTypes
  case object IE815 extends WithName("IE815") with EventTypes
  case object IE818 extends WithName("IE818") with EventTypes
  case object IE819 extends WithName("IE819") with EventTypes
  case object IE829 extends WithName("IE829") with EventTypes
  case object IE837 extends WithName("IE837") with EventTypes
  case object IE839 extends WithName("IE839") with EventTypes
  case object IE840 extends WithName("IE840") with EventTypes
  case object IE871 extends WithName("IE871") with EventTypes
  case object IE881 extends WithName("IE881") with EventTypes
  case object IE905 extends WithName("IE905") with EventTypes

  val values: Seq[EventTypes] = Seq(IE801, IE802, IE803, IE807, IE810, IE813, IE815, IE818, IE819, IE829, IE837, IE839, IE840, IE871, IE881, IE905)

  implicit val enumerable: Enumerable[EventTypes] =
    Enumerable(values.map(v => v.toString -> v): _*)
}