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

package models.response.emcsTfe.draftMovement

import base.SpecBase
import models.movementScenario.MovementScenario
import play.api.libs.json.Json

import java.time.LocalDate

class DataSpec extends SpecBase {

  "de-serialise from JSON as expected" when {

    "min data is supplied" in {
      Json.obj("info" -> Json.obj(fields =
        "localReferenceNumber" -> testLrn
      )).as[Data] mustBe Data(testLrn, None, None, None)
    }

    "max data is supplied (consignee has ERN, Dispatch Date in LocalDateFmt)" in {
      Json.obj(fields =
        "info" -> Json.obj(fields =
          "localReferenceNumber" -> testLrn,
          "destinationType" -> MovementScenario.EuTaxWarehouse.toString,
          "dispatchDetails" -> Json.obj(fields =
            "date" -> "2024-01-01"
          )
        ),
        "consignee" -> Json.obj(fields =
          "exciseRegistrationNumber" -> testErn
        )
      ).as[Data] mustBe Data(
        lrn = testLrn,
        movementScenario = Some(MovementScenario.EuTaxWarehouse),
        consigneeReference = Some(testErn),
        dispatchDate = Some(LocalDate.of(2024, 1, 1))
      )
    }

    "max data is supplied (consignee has VAT, Dispatch Date in MongoLocaltimeFormat)" in {
      Json.obj(fields =
        "info" -> Json.obj(fields =
          "localReferenceNumber" -> testLrn,
          "dispatchDetails" -> Json.obj(fields =
            "date" -> Json.obj(fields =
              "$date" -> Json.obj(fields =
                "$numberLong" -> "1704067200000"
              )
            )
          )
        ),
        "consignee" -> Json.obj(fields =
          "consigneeExportVat" -> "123456789"
        )
      ).as[Data] mustBe Data(
        lrn = testLrn,
        movementScenario = None,
        consigneeReference = Some("123456789"),
        dispatchDate = Some(LocalDate.of(2024, 1, 1))
      )
    }

    "consignee has EORI" in {
      Json.obj(fields =
        "info" -> Json.obj(fields =
          "localReferenceNumber" -> testLrn
        ),
        "consignee" -> Json.obj(fields =
          "consigneeExportEori" -> "ABC1234"
        )
      ).as[Data] mustBe Data(
        lrn = testLrn,
        movementScenario = None,
        consigneeReference = Some("ABC1234"),
        dispatchDate = None
      )
    }
  }
}
