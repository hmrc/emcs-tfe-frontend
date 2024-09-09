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
import models.movementScenario.MovementScenario
import play.api.libs.json.Json

class TemplateSpec extends SpecBase {
  val downstreamJsonWithExciseRegistrationNumber: String =
    s"""{
       |  "templateId": "1",
       |  "templateName": "my name",
       |  "data": {
       |    "info": {
       |      "destinationType": "${MovementScenario.UkTaxWarehouse.GB}"
       |    },
       |    "consignee": {
       |      "exciseRegistrationNumber": "ern123"
       |    }
       |  }
       |}""".stripMargin

  val downstreamJsonWithConsigneeExportVat: String =
    s"""{
       |  "templateId": "1",
       |  "templateName": "my name",
       |  "data": {
       |    "info": {
       |      "destinationType": "${MovementScenario.UkTaxWarehouse.GB}"
       |    },
       |    "consignee": {
       |      "consigneeExportVat": "vat123"
       |    }
       |  }
       |}""".stripMargin

  val downstreamJsonWithNoErn: String =
    s"""{
       |  "templateId": "1",
       |  "templateName": "my name",
       |  "data": {
       |    "info": {
       |      "destinationType": "${MovementScenario.UkTaxWarehouse.GB}"
       |    },
       |    "consignee": {
       |    }
       |  }
       |}""".stripMargin

  val downstreamJsonWithNoConsignee: String =
    s"""{
       |  "templateId": "1",
       |  "templateName": "my name",
       |  "data": {
       |    "info": {
       |      "destinationType": "${MovementScenario.UkTaxWarehouse.GB}"
       |    },
       |    "consignee": {
       |    }
       |  }
       |}""".stripMargin

  def model(consigneeErn: Option[String]): Template = Template(
    templateId = "1",
    templateName = "my name",
    destinationType = MovementScenario.UkTaxWarehouse.GB,
    consigneeErn = consigneeErn
  )

  "reads" must {
    "read JSON to a model" when {
      "consignee ERN is present" in {
        Json.parse(downstreamJsonWithExciseRegistrationNumber).as[Template] mustBe model(Some("ern123"))
      }
      "consignee export VAT is present" in {
        Json.parse(downstreamJsonWithConsigneeExportVat).as[Template] mustBe model(Some("vat123"))
      }
      "consignee ERN is not present" in {
        Json.parse(downstreamJsonWithNoErn).as[Template] mustBe model(None)
      }
      "consignee is not present" in {
        Json.parse(downstreamJsonWithNoConsignee).as[Template] mustBe model(None)
      }
    }
  }

  "writes" must {
    "convert a model to JSON" in {
      Json.toJson(model(Some("ern123"))) mustBe Json.obj(
        "templateId" -> "1",
        "templateName" -> "my name",
        "destinationType" -> MovementScenario.UkTaxWarehouse.GB.toString,
        "consigneeErn" -> "ern123"
      )
    }
  }
}
