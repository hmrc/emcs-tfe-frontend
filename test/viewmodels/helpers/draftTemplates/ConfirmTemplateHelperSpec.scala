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

package viewmodels.helpers.draftTemplates

import base.SpecBase
import fixtures.DraftTemplatesFixtures
import fixtures.messages.draftTemplates.ConfirmTemplateMessages.English
import models.common.GuarantorType.{Consignor, Owner}
import models.common.TransportArrangement
import models.common.TransportMode.RoadTransport
import models.common.UnitOfMeasure.Litres20
import models.draftTemplates.{Template, TemplateItem}
import models.movementScenario.MovementScenario
import models.response.referenceData.CnCodeInformation
import play.api.i18n.Messages
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.twirl.api.Html
import viewmodels.helpers.MovementTypeHelper
import viewmodels.helpers.SummaryListHelper.summaryListRowBuilder
import views.BaseSelectors
import views.html.components.list
import views.html.viewMovement.partials.overview_partial

import java.time.Instant

class ConfirmTemplateHelperSpec extends SpecBase with DraftTemplatesFixtures {

  lazy val movementTypeHelper: MovementTypeHelper = app.injector.instanceOf[MovementTypeHelper]
  lazy val overview_partial: overview_partial = app.injector.instanceOf[overview_partial]
  lazy val list: list = app.injector.instanceOf[list]

  val helper: ConfirmTemplateHelper = new ConfirmTemplateHelper(movementTypeHelper, overview_partial, list)

  val testTemplate = createTemplate(testErn, "Template1ID", "Template 1", MovementScenario.UkTaxWarehouse.GB, Some("GB001234567890"))

  object Selectors extends BaseSelectors {
    def summaryListRowKey(i: Int) = s"div.govuk-summary-list__row:nth-of-type($i) > dt"

    def summaryListRowValue(i: Int) = s"div.govuk-summary-list__row:nth-of-type($i) > dd"
  }

  "ConfirmTemplateHelper" when {
    Seq(English) foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      val template = Template(
        ern = testErn,
        templateId = "Template1ID",
        templateName = "Template 1",
        data = Json.obj(),
        lastUpdated = Instant.now
      )

      ".movementType" must {
        "return a summary list row for the movement type" in {
          implicit val request = dataRequest(FakeRequest("GET", "/"), ern = "GBWK000000000")
          implicit val _template = template.copy(data = Json.obj("info" -> Json.obj("destinationType" -> MovementScenario.UkTaxWarehouse.GB.toString)))

          val result = helper.movementType
          result mustBe summaryListRowBuilder("Movement type", "Great Britain tax warehouse to tax warehouse in Great Britain")
        }
      }

      ".consignee" must {
        "return a summary list row for the consignee when a business name is present and the destination type is not UnknownDestination" in {
           implicit val _template = template.copy(data = Json.obj(
              "info" -> Json.obj("destinationType" -> MovementScenario.DirectDelivery.toString),
              "consignee" -> Json.obj("consigneeAddress" -> Json.obj("businessName" -> "consignee business name"))
           ))

          val result = helper.consignee
          result mustBe Some(summaryListRowBuilder("confirmTemplate.movement.summary.consigneeBusinessName", "consignee business name"))
        }

        "return None when the destination type is UnknownDestination" in {
          implicit val _template = template.copy(data = Json.obj(
            "info" -> Json.obj("destinationType" -> MovementScenario.UnknownDestination.toString),
            "consignee" -> Json.obj("consigneeAddress" -> Json.obj("businessName" -> "consignee business name"))
          ))

          val result = helper.consignee
          result mustBe None
        }

        "return None when there is no consignee business name" in {
          implicit val _template = template.copy(data = Json.obj(
            "info" -> Json.obj("destinationType" -> MovementScenario.DirectDelivery.toString)
          ))

          val result = helper.consignee
          result mustBe None
        }
      }

      ".consigneeERN" must {
        "return a summary list row when a consignee identifier is present and the destination type is not UnknownDestination or ExemptedOrganisation" in {
          implicit val _template = template.copy(data = Json.obj(
            "info" -> Json.obj("destinationType" -> MovementScenario.DirectDelivery.toString),
            "consignee" -> Json.obj("exciseRegistrationNumber" -> "GBWK000000000000")
          ))

          val result = helper.consigneeERN
          result mustBe Some(summaryListRowBuilder("confirmTemplate.movement.summary.consigneeERN", "GBWK000000000000"))
        }

        "return None when the destination type is UnknownDestination" in {
          implicit val _template = template.copy(data = Json.obj(
            "info" -> Json.obj("destinationType" -> MovementScenario.UnknownDestination.toString),
            "consignee" -> Json.obj("exciseRegistrationNumber" -> "GBWK000000000000")
          ))

          val result = helper.consigneeERN
          result mustBe None
        }

        "return None when the destination type is ExemptedOrganisation" in {
          implicit val _template = template.copy(data = Json.obj(
            "info" -> Json.obj("destinationType" -> MovementScenario.ExemptedOrganisation.toString),
            "consignee" -> Json.obj("exciseRegistrationNumber" -> "GBWK000000000000")
          ))

          val result = helper.consigneeERN
          result mustBe None
        }

        "return None when there is no consignee ERN" in {
          implicit val _template = template.copy(data = Json.obj(
            "info" -> Json.obj("destinationType" -> MovementScenario.DirectDelivery.toString)
          ))

          val result = helper.consigneeERN
          result mustBe None
        }
      }

      ".exportOffice" must {
        "return a summary list row when an export office is present and the destination type is ExportWithCustomsDeclarationLodgedInTheUk" in {
          implicit val _template = template.copy(data = Json.obj(
            "info" -> Json.obj("destinationType" -> MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk.toString),
            "exportInformation" -> Json.obj("exportCustomsOffice" -> "GB000000")
          ))

          val result = helper.exportOffice
          result mustBe Some(summaryListRowBuilder("confirmTemplate.movement.summary.exportOffice", "GB000000"))
        }

        "return a summary list row when an export office is present and the destination type is ExportWithCustomsDeclarationLodgedInTheEu" in {
          implicit val _template = template.copy(data = Json.obj(
            "info" -> Json.obj("destinationType" -> MovementScenario.ExportWithCustomsDeclarationLodgedInTheEu.toString),
            "exportInformation" -> Json.obj("exportCustomsOffice" -> "GB000000")
          ))

          val result = helper.exportOffice
          result mustBe Some(summaryListRowBuilder("confirmTemplate.movement.summary.exportOffice", "GB000000"))
        }

        "return None when the destination type is not ExportWithCustomsDeclarationLodgedInTheUk" in {
          implicit val _template = template.copy(data = Json.obj(
            "info" -> Json.obj("destinationType" -> MovementScenario.DirectDelivery.toString),
            "exportInformation" -> Json.obj("exportCustomsOffice" -> "GB000000")
          ))

          val result = helper.exportOffice
          result mustBe None
        }

        "return None when there is no export office" in {
          implicit val _template = template.copy(data = Json.obj(
            "info" -> Json.obj("destinationType" -> MovementScenario.ExportWithCustomsDeclarationLodgedInTheUk.toString)
          ))

          val result = helper.exportOffice
          result mustBe None
        }
      }

      ".importOffice" must {
        "return a summary list row when the user type is GBRC and an import customs office code is present" in {
          implicit val request = dataRequest(FakeRequest("GET", "/"), ern = "GBRC000000000")
          implicit val _template = template.copy(data = Json.obj("importInformation" -> Json.obj("importCustomsOfficeCode" -> "GB000000")))

          val result = helper.importOffice
          result mustBe Some(summaryListRowBuilder("confirmTemplate.movement.summary.importOffice", "GB000000"))
        }

        "return a summary list row when the user type is XIRC and an import customs office code is present" in {
          implicit val request = dataRequest(FakeRequest("GET", "/"), ern = "XIRC000000000")
          implicit val _template = template.copy(data = Json.obj("importInformation" -> Json.obj("importCustomsOfficeCode" -> "XI000000")))

          val result = helper.importOffice
          result mustBe Some(summaryListRowBuilder("confirmTemplate.movement.summary.importOffice", "XI000000"))
        }

        "return None when the user type is not GBRC or XIRC" in {
          implicit val request = dataRequest(FakeRequest("GET", "/"), ern = "GBWK000000000")
          implicit val _template = template.copy(data = Json.obj("importCustomsOfficeCode" -> "GB000000"))

          val result = helper.importOffice
          result mustBe None
        }

        "return None when there is no import customs office code" in {
          implicit val request = dataRequest(FakeRequest("GET", "/"), ern = "GBRC000000000")
          implicit val _template = template.copy(data = Json.obj())

          val result = helper.importOffice
          result mustBe None
        }
      }

      ".exemptedOrganisationOffice" must {
        "return a summary list row when the destination type is ExemptedOrganisation and a member state is present" in {
          implicit val _template = template.copy(data = Json.obj(
            "info" -> Json.obj("destinationType" -> MovementScenario.ExemptedOrganisation.toString),
            "consignee" -> Json.obj("exemptOrganisation" -> Json.obj("memberState" -> "GB00000"))
          ))

          val result = helper.exemptedOrganisationOffice
          result mustBe Some(summaryListRowBuilder("confirmTemplate.movement.summary.exemptedOrganisationOffice", "GB00000"))
        }

        "return None when the destination type is not ExemptedOrganisation" in {
          implicit val _template = template.copy(data = Json.obj(
            "info" -> Json.obj("destinationType" -> MovementScenario.DirectDelivery.toString),
            "consignee" -> Json.obj("exemptOrganisation" -> Json.obj("memberState" -> "GB00000"))
          ))

          val result = helper.exemptedOrganisationOffice
          result mustBe None
        }

        "return None when there is no member state" in {
          implicit val _template = template.copy(data = Json.obj(
            "info" -> Json.obj("destinationType" -> MovementScenario.ExemptedOrganisation.toString),
            "consignee" -> Json.obj("exemptOrganisation" -> Json.obj())
          ))

          val result = helper.exemptedOrganisationOffice
          result mustBe None
        }
      }

      ".guarantor" must {
        "return a summary list row when the guarantor arranger is not Owner" in {
          implicit val _template = template.copy(data = Json.obj("guarantor" -> Json.obj("guarantorArranger" -> Consignor.toString)))

          val result = helper.guarantor
          result mustBe Some(summaryListRowBuilder("confirmTemplate.movement.summary.guarantor", "Consignor"))
        }

        "return a summary list row when the guarantor arranger is Owner and a business name is present" in {
          implicit val _template = template.copy(data = Json.obj(
            "guarantor" -> Json.obj(
              "guarantorArranger" -> Owner.toString,
              "guarantorAddress" -> Json.obj("businessName" -> "guarantor business name")
            )
          ))

          val result = helper.guarantor
          result mustBe Some(summaryListRowBuilder("confirmTemplate.movement.summary.guarantor", Html("Owner of goods<br>guarantor business name")))
        }

        "return None when the guarantor arranger is Owner and there is no business name" in {
          implicit val _template = template.copy(data = Json.obj(
            "guarantorArranger" -> "Owner"
          ))

          val result = helper.guarantor
          result mustBe None
        }

        "return None when there is no guarantor arranger" in {
          implicit val _template = template.copy(data = Json.obj())

          val result = helper.guarantor
          result mustBe None
        }
      }

      ".journeyType" must {
        "return a summary list row when a journey type is present" in {
          implicit val _template = template.copy(data = Json.obj("journeyType" -> Json.obj("howMovementTransported" -> RoadTransport.toString)))

          val result = helper.journeyType
          result mustBe Some(summaryListRowBuilder("confirmTemplate.movement.summary.journeyType", "Road transport"))
        }

        "return None when there is no journey type" in {
          implicit val _template = template.copy(data = Json.obj())

          val result = helper.journeyType
          result mustBe None
        }
      }

      ".transportArranger" must {
        "return a summary list row when the transport arranger is Consignor" in {
          implicit val _template = template.copy(data = Json.obj(
            "transportArranger" -> Json.obj("transportArranger" -> TransportArrangement.Consignor.toString)
          ))

          val result = helper.transportArranger
          result mustBe Some(summaryListRowBuilder("confirmTemplate.movement.summary.transportArranger", "Consignor"))
        }

        "return a summary list row when the transport arranger is Consignee" in {
          implicit val _template = template.copy(data = Json.obj(
            "transportArranger" -> Json.obj("transportArranger" -> TransportArrangement.Consignee.toString)
          ))

          val result = helper.transportArranger
          result mustBe Some(summaryListRowBuilder("confirmTemplate.movement.summary.transportArranger", "Consignee"))
        }

        "return a summary list row when the transport arranger is Other and a business name is present" in {
          implicit val _template = template.copy(data = Json.obj(
            "transportArranger" -> Json.obj(
              "transportArranger" -> TransportArrangement.Other.toString,
                "transportArrangerAddress" -> Json.obj("businessName" -> "Transport Business")
            )
          ))

          val result = helper.transportArranger
          result mustBe Some(summaryListRowBuilder("confirmTemplate.movement.summary.transportArranger", Html("Other<br>Transport Business")))
        }

        "return a summary list row when the transport arranger is OwnerOfGoods and a business name is present" in {
          implicit val _template = template.copy(data = Json.obj(
            "transportArranger" -> Json.obj(
              "transportArranger" -> TransportArrangement.OwnerOfGoods.toString,
              "transportArrangerAddress" -> Json.obj("businessName" -> "Transport Business owner")
            )
          ))

          val result = helper.transportArranger
          result mustBe Some(summaryListRowBuilder("confirmTemplate.movement.summary.transportArranger", Html("Owner of goods<br>Transport Business owner")))
        }

        "return None when the transport arranger is Other and there is no business name" in {
          implicit val _template = template.copy(data = Json.obj(
            "transportArranger" -> TransportArrangement.Other.toString
          ))

          val result = helper.transportArranger
          result mustBe None
        }

        "return None when the transport arranger is OwnerOfGoods and there is no business name" in {
          implicit val _template = template.copy(data = Json.obj(
            "transportArranger" -> TransportArrangement.OwnerOfGoods.toString
          ))

          val result = helper.transportArranger
          result mustBe None
        }

        "return None when there is no transport arranger" in {
          implicit val _template = template.copy(data = Json.obj())

          val result = helper.transportArranger
          result mustBe None
        }
      }

      ".firstTransporter" must {
        "return a summary list row when a first transporter business name is present" in {
          implicit val _template = template.copy(data = Json.obj(
            "firstTransporter" -> Json.obj("firstTransporterAddress" -> Json.obj("businessName" -> "First Transporter Business"))
          ))

          val result = helper.firstTransporter
          result mustBe Some(summaryListRowBuilder("confirmTemplate.movement.summary.firstTransporter", "First Transporter Business"))
        }

        "return None when there is no first transporter business name" in {
          implicit val _template = template.copy(data = Json.obj())

          val result = helper.firstTransporter
          result mustBe None
        }
      }

      ".items" must {
        "return a summary list row with item descriptions when items with CN code information are present" in {
          val itemsWithCnCodeInfo = Seq(
            (TemplateItem("epc1", "cn1", 10), CnCodeInformation("cn1", "Description", "epc1", "Beer", Litres20)),
            (TemplateItem("epc2", "cn2", 20.5), CnCodeInformation("cn2", "Description", "epc2", "Wine", Litres20))
          )

          val result = helper.items(itemsWithCnCodeInfo)

          result mustBe Some(
            summaryListRowBuilder(
              "confirmTemplate.movement.summary.items",
              list(Seq(
                Html("Item 1: 10 litres of Beer"),
                Html("Item 2: 20.5 litres of Wine")
              ), extraClasses = Some("govuk-list--bullet"))
            )
          )
        }

        "return None when no items with CN code information are present" in {
          val itemsWithCnCodeInfo = Seq.empty[(TemplateItem, CnCodeInformation)]

          val result = helper.items(itemsWithCnCodeInfo)
          result mustBe None
        }
      }
    }
  }
}

