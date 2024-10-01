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

package models.draftTemplates

import models.common._
import models.movementScenario.MovementScenario
import play.api.libs.json._

import java.time.Instant

case class Template(
                     ern: String,
                     templateId: String,
                     templateName: String,
                     data: JsObject,
                     lastUpdated: Instant
                   ) {

  def destinationType: MovementScenario =
    (data \ "info" \ "destinationType")
      .as[MovementScenario]

  def consigneeERN: Option[String] =
    (data \ "consignee" \ "exciseRegistrationNumber").asOpt[String]

  def placeOfDispatch: Option[TraderModel] =
    (data \ "dispatch" \ "dispatchAddress")
      .asOpt[TraderModel]

  def consigneeBusinessName: Option[String] =
    (data \ "consignee" \ "consigneeAddress" \ "businessName")
      .asOpt[String]

  def exportCustomsOfficeCode: Option[String] =
    (data \ "exportInformation" \ "exportCustomsOffice")
      .asOpt[String]

  def importCustomsOfficeCode: Option[String] =
    (data \ "importInformation" \ "importCustomsOfficeCode")
      .asOpt[String]

  def memberState: Option[String] =
    (data \ "consignee" \ "exemptOrganisation" \ "memberState")
      .asOpt[String]

  def guarantorArranger: Option[GuarantorType] =
    (data \ "guarantor" \ "guarantorArranger")
      .asOpt[GuarantorType]

  def guarantorBusinessName: Option[String] =
    (data \ "guarantor" \ "guarantorAddress" \ "businessName")
      .asOpt[String]

  def journeyType: Option[TransportMode] =
    (data \ "journeyType" \ "howMovementTransported")
      .asOpt[TransportMode]

  def transportArranger: Option[TransportArrangement] =
    (data \ "transportArranger" \ "transportArranger")
      .asOpt[TransportArrangement]

  def transportArrangerBusinessName: Option[String] =
    (data \ "transportArranger" \ "transportArrangerAddress" \ "businessName")
      .asOpt[String]

  def firstTransporterBusinessName: Option[String] =
    (data \ "firstTransporter" \ "firstTransporterAddress" \ "businessName")
      .asOpt[String]

  def items: Seq[TemplateItem] =
    (data \ "items" \ "addedItems")
      .as[Seq[TemplateItem]]
}

object Template {
  implicit val format: Format[Template] = Json.format
}

