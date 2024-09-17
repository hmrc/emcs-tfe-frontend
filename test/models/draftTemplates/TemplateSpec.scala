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

import base.SpecBase
import fixtures.DraftTemplatesFixtures
import models.movementScenario.MovementScenario
import play.api.libs.json.Json

class TemplateSpec extends SpecBase with DraftTemplatesFixtures {
  val downstreamJsonWithExciseRegistrationNumber: String =
    s"""{
       |  "ern": "$testErn",
       |  "templateId": "1",
       |  "templateName": "my name",
       |  "data": {
       |    "info": {
       |      "destinationType": "${MovementScenario.UkTaxWarehouse.GB}"
       |    },
       |    "consignee": {
       |      "exciseRegistrationNumber": "ern123"
       |    }
       |  },
       |  "lastUpdated": "2020-01-01T00:00:00Z"
       |}""".stripMargin

  val downstreamJsonWithConsigneeExportVat: String =
    s"""{
       |  "ern": "$testErn",
       |  "templateId": "1",
       |  "templateName": "my name",
       |  "data": {
       |    "info": {
       |      "destinationType": "${MovementScenario.UkTaxWarehouse.GB}"
       |    },
       |    "consignee": {
       |      "consigneeExportVat": "vat123"
       |    }
       |  },
       |  "lastUpdated": "2020-01-01T00:00:00Z"
       |}""".stripMargin

  val downstreamJsonWithNoErn: String =
    s"""{
       |  "ern": "$testErn",
       |  "templateId": "1",
       |  "templateName": "my name",
       |  "data": {
       |    "info": {
       |      "destinationType": "${MovementScenario.UkTaxWarehouse.GB}"
       |    },
       |    "consignee": {
       |    }
       |  },
       |  "lastUpdated": "2020-01-01T00:00:00Z"
       |}""".stripMargin

  val downstreamJsonWithNoConsignee: String =
    s"""{
       |  "ern": "$testErn",
       |  "templateId": "1",
       |  "templateName": "my name",
       |  "data": {
       |    "info": {
       |      "destinationType": "${MovementScenario.UkTaxWarehouse.GB}"
       |    }
       |  },
       |  "lastUpdated": "2020-01-01T00:00:00Z"
       |}""".stripMargin

  def model(consigneeErn: Option[String], consigneeVat: Option[String]): Template =
    createTemplate(
      ern = testErn,
      templateId = "1",
      templateName = "my name",
      movementScenario = MovementScenario.UkTaxWarehouse.GB,
      consigneeErn = consigneeErn,
      consigneeVat = consigneeVat
    )

  "reads" must {
    "read JSON to a model" when {
      "consignee ERN is present" in {
        Json.parse(downstreamJsonWithExciseRegistrationNumber).as[Template] mustBe model(Some("ern123"), None)
      }
      "consignee export VAT is present" in {
        Json.parse(downstreamJsonWithConsigneeExportVat).as[Template] mustBe model(None, Some("vat123"))
      }
      "consignee ERN or export VAT is not present" in {
        val parsed = Json.parse(downstreamJsonWithNoErn).as[Template]
        parsed.ern mustBe testErn
        parsed.templateId mustBe "1"
        parsed.templateName mustBe "my name"
        parsed.data mustBe Json.obj(
          "info" -> Json.obj(
            "destinationType" -> MovementScenario.UkTaxWarehouse.GB.toString
          ),
          "consignee" -> Json.obj()
        )
        parsed.destinationType mustBe MovementScenario.UkTaxWarehouse.GB
        parsed.consigneeIdentifier mustBe None
      }
      "consignee is not present" in {
        val parsed = Json.parse(downstreamJsonWithNoConsignee).as[Template]
        parsed.ern mustBe testErn
        parsed.templateId mustBe "1"
        parsed.templateName mustBe "my name"
        parsed.data mustBe Json.obj(
          "info" -> Json.obj(
            "destinationType" -> MovementScenario.UkTaxWarehouse.GB.toString
          )
        )
        parsed.destinationType mustBe MovementScenario.UkTaxWarehouse.GB
        parsed.consigneeIdentifier mustBe None
      }
    }
  }

  "writes" must {

    "convert a model to JSON with a consignee ERN" in {
      Json.toJson(model(Some("ern123"), None)) mustBe Json.obj(
        "ern" -> testErn,
        "templateId" -> "1",
        "templateName" -> "my name",
        "data" -> Json.obj(
          "info" -> Json.obj(
            "destinationType" -> MovementScenario.UkTaxWarehouse.GB.toString
          ),
          "consignee" -> Json.obj("exciseRegistrationNumber" -> "ern123")
        ),
        "lastUpdated" -> "2020-01-01T00:00:00Z"
      )
    }

    "convert a model to JSON with a consignee VAT" in {
      Json.toJson(model(None, Some("vat123"))) mustBe Json.obj(
        "ern" -> testErn,
        "templateId" -> "1",
        "templateName" -> "my name",
        "data" -> Json.obj(
          "info" -> Json.obj(
            "destinationType" -> MovementScenario.UkTaxWarehouse.GB.toString
          ),
          "consignee" -> Json.obj("consigneeExportVat" -> "vat123")
        ),
        "lastUpdated" -> "2020-01-01T00:00:00Z"
      )
    }
  }
}
