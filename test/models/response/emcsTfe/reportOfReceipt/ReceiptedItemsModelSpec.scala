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

package models.response.emcsTfe.reportOfReceipt

import base.SpecBase
import fixtures.ReceiptedItemsModelFixtures
import play.api.libs.json.Json

class ReceiptedItemsModelSpec extends SpecBase with ReceiptedItemsModelFixtures {

  "ReceiptedItemsModel" must {

    "for a shortage with all other reasons" must {

      "be possible to serialise and de-serialise to/from JSON" in {
        Json.toJson(shortageReceiptedItemsModel).as[ReceiptedItemsModel] mustBe shortageReceiptedItemsModel
      }
    }

    "for a excess with no other reasons" must {

      "be possible to serialise and de-serialise to/from JSON" in {
        Json.toJson(excessReceiptedItemsModel).as[ReceiptedItemsModel] mustBe excessReceiptedItemsModel
      }
    }

    "for the minimum amount of info" must {

      "be possible to serialise and de-serialise to/from JSON" in {
        Json.toJson(minReceiptedItemsModel).as[ReceiptedItemsModel] mustBe minReceiptedItemsModel
      }

    }
  }
}
