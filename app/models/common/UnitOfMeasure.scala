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

package models.common

import play.api.i18n.Messages
import play.api.libs.json.{Reads, __}

sealed trait UnitOfMeasure {

  def toShortFormatMessage()(implicit messages: Messages): String =
    messages(s"unitOfMeasure.$this.short")

  def toLongFormatMessage()(implicit messages: Messages): String =
    messages(s"unitOfMeasure.$this.long")
}

object UnitOfMeasure {

  case object Kilograms extends WithName("kilograms") with UnitOfMeasure

  case object Litres15 extends WithName("litres15") with UnitOfMeasure

  case object Litres20 extends WithName("litres20") with UnitOfMeasure

  case object Thousands extends WithName("thousands") with UnitOfMeasure

  val values: Seq[UnitOfMeasure] = Seq(
    Kilograms,
    Litres15,
    Litres20,
    Thousands
  )

  def apply(code: Int): UnitOfMeasure = code match {
    case 1 => Kilograms
    case 2 => Litres15
    case 3 => Litres20
    case 4 => Thousands
    case code =>
      throw new IllegalArgumentException(s"Invalid argument of '$code' received which can not be mapped to a UnitOfMeasure")
  }

  def unapply(unitOfMeasure: UnitOfMeasure): Int = unitOfMeasure match {
    case Kilograms => 1
    case Litres15 => 2
    case Litres20 => 3
    case Thousands => 4
    case unitOfMeasure =>
      throw new IllegalArgumentException(s"Invalid argument of '$unitOfMeasure' received which can not be mapped to an Int")
  }

  implicit val reads: Reads[UnitOfMeasure] = Reads.at[Int](__).map(UnitOfMeasure.apply)
}
