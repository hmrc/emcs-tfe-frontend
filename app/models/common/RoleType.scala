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

import config.AppConfig
import utils.Logging

object RoleType extends Logging {
  def fromExciseRegistrationNumber(exciseRegistrationNumber: String): RoleType = exciseRegistrationNumber.take(4) match {
    case "GBWK" => GBWK
    case "XIWK" => XIWK
    case "GBRC" => GBRC
    case "XIRC" => XIRC
    case "XI00" => XI00
    case "GB00" => GB00
    case "XITC" => XITC
    case "XIPA" => XIPA
    case "XIPB" => XIPB
    case "XIPC" => XIPC
    case "XIPD" => XIPD
    case invalidPrefix =>
      logger.error(s"[fromExciseRegistrationNumber] Invalid exciseRegistrationNumber prefix: $invalidPrefix")
      throw new IllegalArgumentException(s"Invalid exciseRegistrationNumber prefix: $invalidPrefix")

  }

  def isGB(ern: String): Boolean = ern.take(2).equals("GB")

  def isXI(ern: String): Boolean = ern.take(2).equals("XI")

  sealed trait RoleType {
    val descriptionKey: String
    val countryCode: String
    val isNorthernIreland: Boolean
    val isGreatBritain: Boolean
    def canCreateNewMovement(appConfig: AppConfig): Boolean
    def isConsignor(appConfig: AppConfig): Boolean = canCreateNewMovement(appConfig)
    val isDutyPaid: Boolean
  }

  case object GBWK extends RoleType {
    override val descriptionKey = "accountHome.roleType.GBWK"
    override val countryCode = "GB"
    override val isNorthernIreland: Boolean = false
    override val isGreatBritain: Boolean = true
    override def canCreateNewMovement(appConfig: AppConfig): Boolean = true
    override val isDutyPaid: Boolean = false
  }

  case object XIWK extends RoleType {
    override val descriptionKey = "accountHome.roleType.XIWK"
    override val countryCode = "XI"
    override val isNorthernIreland: Boolean = true
    override val isGreatBritain: Boolean = false
    override def canCreateNewMovement(appConfig: AppConfig): Boolean = true
    override val isDutyPaid: Boolean = false
  }

  case object GBRC extends RoleType {
    override val descriptionKey = "accountHome.roleType.GBRC"
    override val countryCode = "GB"
    override val isNorthernIreland: Boolean = false
    override val isGreatBritain: Boolean = true
    override def canCreateNewMovement(appConfig: AppConfig): Boolean = true
    override val isDutyPaid: Boolean = false
  }

  case object XIRC extends RoleType {
    override val descriptionKey = "accountHome.roleType.XIRC"
    override val countryCode = "XI"
    override val isNorthernIreland: Boolean = true
    override val isGreatBritain: Boolean = false
    override def canCreateNewMovement(appConfig: AppConfig): Boolean = true
    override val isDutyPaid: Boolean = false
  }

  case object XI00 extends RoleType {
    override val descriptionKey = "accountHome.roleType.XI00"
    override val countryCode = "XI"
    override val isNorthernIreland: Boolean = true
    override val isGreatBritain: Boolean = false
    override def canCreateNewMovement(appConfig: AppConfig): Boolean = false
    override val isDutyPaid: Boolean = false
  }

  case object GB00 extends RoleType {
    override val descriptionKey = "accountHome.roleType.GB00"
    override val countryCode = "GB"
    override val isNorthernIreland: Boolean = false
    override val isGreatBritain: Boolean = true
    override def canCreateNewMovement(appConfig: AppConfig): Boolean = false
    override val isDutyPaid: Boolean = false
  }

  case object XITC extends RoleType {
    override val descriptionKey = "accountHome.roleType.XITC"
    override val countryCode = "XI"
    override val isNorthernIreland: Boolean = true
    override val isGreatBritain: Boolean = false
    override def canCreateNewMovement(appConfig: AppConfig): Boolean = false
    override val isDutyPaid: Boolean = false
  }

  case object XIPA extends RoleType {
    override val descriptionKey = "accountHome.roleType.XIPA"
    override val countryCode = "XI"
    override val isNorthernIreland: Boolean = true
    override val isGreatBritain: Boolean = false
    override def canCreateNewMovement(appConfig: AppConfig): Boolean = true
    override val isDutyPaid: Boolean = true
  }

  case object XIPB extends RoleType {
    override val descriptionKey = "accountHome.roleType.XIPB"
    override val countryCode = "XI"
    override val isNorthernIreland: Boolean = true
    override val isGreatBritain: Boolean = false
    override def canCreateNewMovement(appConfig: AppConfig): Boolean = false
    override val isDutyPaid: Boolean = true
  }

  case object XIPC extends RoleType {
    override val descriptionKey = "accountHome.roleType.XIPC"
    override val countryCode = "XI"
    override val isNorthernIreland: Boolean = true
    override val isGreatBritain: Boolean = false
    override def canCreateNewMovement(appConfig: AppConfig): Boolean = appConfig.enableXIPCInCaM
    override def isConsignor(appConfig: AppConfig): Boolean = true
    override val isDutyPaid: Boolean = true
  }

  case object XIPD extends RoleType {
    override val descriptionKey = "accountHome.roleType.XIPD"
    override val countryCode = "XI"
    override val isNorthernIreland: Boolean = true
    override val isGreatBritain: Boolean = false
    override def canCreateNewMovement(appConfig: AppConfig): Boolean = false
    override val isDutyPaid: Boolean = true
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











