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

package views.draftTemplates

import base.ViewSpecBase
import config.AppConfig
import fixtures.DraftTemplatesFixtures
import fixtures.messages.draftTemplates.ConfirmTemplateMessages.English
import forms.draftTemplates.ConfirmTemplateFormProvider
import models.common.UnitOfMeasure.Litres20
import models.common.{GuarantorType, TransportArrangement, TransportMode}
import models.movementScenario.MovementScenario
import models.requests.DataRequest
import models.response.referenceData.CnCodeInformation
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.draftTemplates.ConfirmTemplateView
import views.{BaseSelectors, ViewBehaviours}

class ConfirmTemplateViewSpec extends ViewSpecBase with ViewBehaviours with DraftTemplatesFixtures {
  lazy val view: ConfirmTemplateView = app.injector.instanceOf[ConfirmTemplateView]
  lazy val formProvider: ConfirmTemplateFormProvider = new ConfirmTemplateFormProvider

  implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  object Selectors extends BaseSelectors

  "view" must {
    Seq(English) foreach { messagesForLanguage =>
      implicit val msgs = messages(Seq(messagesForLanguage.lang))
      implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

      s"render the view when being rendered in lang code of '${messagesForLanguage.lang.code}'" must {

        val testTemplate = createTemplate(
          testErn,
          "a-template-id",
          "a template name",
          MovementScenario.UkTaxWarehouse.GB,
          None
        ).copy(
          data = Json.obj(
            "info" -> Json.obj(
              "destinationType" -> MovementScenario.UkTaxWarehouse.GB.toString
            ),
            "consignee" -> Json.obj(
              "exciseRegistrationNumber" -> testErn,
              "consigneeAddress" -> Json.obj(
                "businessName" -> "Consignee Business Name"
              )
            ),
            "guarantor" -> Json.obj(
              "guarantorArranger" -> GuarantorType.Owner.toString,
              "guarantorAddress" -> Json.obj(
                "businessName" -> "Guarantor Business Name"
              )
            ),
            "journeyType" -> Json.obj(
              "howMovementTransported" -> TransportMode.RoadTransport.toString
            ),
            "transportArranger" -> Json.obj(
              "transportArranger" -> TransportArrangement.Other.toString,
              "transportArrangerAddress" -> Json.obj(
                "businessName" -> "Transport arranger business name"
              )
            ),
            "firstTransporter" -> Json.obj(
              "firstTransporterAddress" -> Json.obj(
                "businessName" -> "First transporter business name"
              )
            ),
            "items" -> Json.obj(
              "addedItems" -> Seq(
                Json.obj(
                  "itemExciseProductCode" -> "epc1",
                  "itemCommodityCode"  -> "cncode1",
                  "itemQuantity" -> 10,
                )
              )
            )
          )
        )

        val enrichedTemplateItems = Seq((testTemplate.items.head, CnCodeInformation("cncode1", "", "epc1", "Beer", Litres20)))

        implicit val doc = asDocument(view(formProvider(), testTemplate, enrichedTemplateItems))

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h2(1) -> testTemplate.templateName,
          Selectors.h1 -> messagesForLanguage.h1,
          Selectors.summaryRowKey(1) -> messagesForLanguage.summaryRowKeyMovementType,
          Selectors.summaryRowValue(1) -> "Great Britain tax warehouse to tax warehouse in Great Britain",
          Selectors.summaryRowKey(2) -> messagesForLanguage.summaryRowKeyConsignee,
          Selectors.summaryRowValue(2) -> "Consignee Business Name",
          Selectors.summaryRowKey(3) -> messagesForLanguage.summaryRowKeyConsigneeIdentifier,
          Selectors.summaryRowValue(3) -> testErn,
          Selectors.summaryRowKey(4) -> messagesForLanguage.summaryRowKeyGuarantor,
          Selectors.summaryRowValue(4) -> "Owner of goods Guarantor Business Name",
          Selectors.summaryRowKey(5) -> messagesForLanguage.summaryRowKeyJourneyType,
          Selectors.summaryRowValue(5) -> "Road transport",
          Selectors.summaryRowKey(6) -> messagesForLanguage.summaryRowKeyTransportArranger,
          Selectors.summaryRowValue(6) -> "Other Transport arranger business name",
          Selectors.summaryRowKey(7) -> messagesForLanguage.summaryRowKeyFirstTransporter,
          Selectors.summaryRowValue(7) -> "First transporter business name",
          Selectors.summaryRowKey(8) -> messagesForLanguage.summaryRowKeyItems,
          Selectors.summaryRowValue(8) -> "Item 1: 10 litres of Beer",
          Selectors.radioButton(1) -> messagesForLanguage.radioButton1,
          Selectors.radioButton(2) -> messagesForLanguage.radioButton2,
          Selectors.button -> messagesForLanguage.button
        ))
      }
    }
  }
}
