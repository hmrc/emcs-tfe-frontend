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

object RoleType {
  def fromExciseRegistrationNumber(exciseRegistrationNumber: String): RoleType = exciseRegistrationNumber.take(4) match {
    case "GBWK" => GBWK
    case "XIWK" => XIWK
    case "GBRC" => GBRC
    case "XIRC" => XIRC
    case "XI00" => XI00
    case "XITC" => XITC
    case "XIPA" => XIPA
    case "XIPB" => XIPB
    case "XIPC" => XIPC
    case "XIPD" => XIPD
    case invalidPrefix => throw new IllegalArgumentException(s"Invalid exciseRegistrationNumber prefix: $invalidPrefix")
  }

  sealed trait RoleType {
    val descriptionKey: String
    val isNorthernIsland: Boolean
    val isConsignor: Boolean
  }

  case object GBWK extends RoleType {
    override val descriptionKey = "accountHome.roleType.GBWK"
    override val isNorthernIsland: Boolean = false
    override val isConsignor: Boolean = true
  }

  case object XIWK extends RoleType {
    override val descriptionKey = "accountHome.roleType.XIWK"
    override val isNorthernIsland: Boolean = true
    override val isConsignor: Boolean = true
  }

  case object GBRC extends RoleType {
    override val descriptionKey = "accountHome.roleType.GBRC"
    override val isNorthernIsland: Boolean = false
    override val isConsignor: Boolean = true

  }

  case object XIRC extends RoleType {
    override val descriptionKey = "accountHome.roleType.XIRC"
    override val isNorthernIsland: Boolean = true
    override val isConsignor: Boolean = true
  }

  case object XI00 extends RoleType {
    override val descriptionKey = "accountHome.roleType.XI00"
    override val isNorthernIsland: Boolean = true
    override val isConsignor: Boolean = false
  }

  case object XITC extends RoleType {
    override val descriptionKey = "accountHome.roleType.XITC"
    override val isNorthernIsland: Boolean = true
    override val isConsignor: Boolean = false
  }

  case object XIPA extends RoleType {
    override val descriptionKey = "accountHome.roleType.XIPA"
    override val isNorthernIsland: Boolean = true
    override val isConsignor: Boolean = true
  }

  case object XIPB extends RoleType {
    override val descriptionKey = "accountHome.roleType.XIPB"
    override val isNorthernIsland: Boolean = true
    override val isConsignor: Boolean = false
  }

  case object XIPC extends RoleType {
    override val descriptionKey = "accountHome.roleType.XIPC"
    override val isNorthernIsland: Boolean = true
    override val isConsignor: Boolean = true
  }

  case object XIPD extends RoleType {
    override val descriptionKey = "accountHome.roleType.XIPD"
    override val isNorthernIsland: Boolean = true
    override val isConsignor: Boolean = false
  }

  val values: Seq[RoleType] = Seq(
    GBWK,
    XIWK,
    GBRC,
    XIRC,
    XI00,
    XITC,
    XIPA,
    XIPB,
    XIPC,
    XIPD
  )

}











