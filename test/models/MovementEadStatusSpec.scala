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

import base.SpecBase
import models.MovementEadStatus._
import play.api.libs.json.Json

class MovementEadStatusSpec extends SpecBase {

  "MovementEadStatus" should {

    "have correct statuses for cancelMovementValidStatuses" in {
      val expectedStatuses = Seq(Accepted, Exporting, Rejected)
      cancelMovementValidStatuses mustBe expectedStatuses
    }

    "have correct statuses for changeDestinationValidStatuses" in {
      val expectedStatuses = Seq(Accepted, Exporting, PartiallyRefused, Refused, DeemedExported, Rejected)
      changeDestinationValidStatuses mustBe expectedStatuses
    }

    "have correct statuses for alertOrRejectValidStatuses" in {
      val expectedStatuses = Seq(Accepted)
      alertOrRejectValidStatuses mustBe expectedStatuses
    }

    "have correct statuses for reportOfReceiptValidStatuses" in {
      val expectedStatuses = Seq(Accepted)
      reportOfReceiptValidStatuses mustBe expectedStatuses
    }

    "have correct statuses for shortageOrExcessValidStatuses" in {
      val expectedStatuses = Seq(Delivered, Diverted, ManuallyClosed, Refused, PartiallyRefused, Exporting, Stopped, DeemedExported)
      shortageOrExcessValidStatuses mustBe expectedStatuses
    }

    "have correct statuses for shortageOrExcessExportValidStatuses" in {
      val expectedStatuses = Seq(Delivered, Diverted, ManuallyClosed, Refused, PartiallyRefused, Exporting, Stopped, Accepted, Rejected)
      shortageOrExcessExportValidStatuses mustBe expectedStatuses
    }

    "contain all expected statuses" in {
      val expectedStatuses = Seq(Accepted, Cancelled, DeemedExported, Delivered, Diverted, Exporting, ManuallyClosed, NoneStatus, PartiallyRefused, Refused, Replaced, Rejected, Stopped)
      values mustBe expectedStatuses
    }

    "destinationType" should {

      "return the correct MovementEadStatus" in {
        MovementEadStatus.destinationType("Accepted") mustBe Accepted
      }

      "throw an exception when an invalid MovementEadStatus is provided" in {
        val result = intercept[IllegalArgumentException](MovementEadStatus.destinationType("beans"))

        result.getMessage mustBe "MovementEadStatus code of 'beans' could not be mapped to a valid MovementEadStatus Type"
      }
    }

    "enumerable" should {

      values.filterNot(_ == ManuallyClosed).foreach { status =>

        s"for $status" must {
          "be readable from JSON" in {

            status.schemaValues.foreach { schemaValue =>
              val json = Json.toJson(schemaValue)
              json.as[MovementEadStatus] mustBe status
            }
          }
        }
      }

      s"for $ManuallyClosed" must {

        "be readable from JSON for a range of values" in {

          ManuallyClosed.schemaValues.foreach { schemaValue =>
            val json = Json.toJson(schemaValue)
            json.as[MovementEadStatus] mustBe ManuallyClosed
          }
        }
      }
    }
  }
}
