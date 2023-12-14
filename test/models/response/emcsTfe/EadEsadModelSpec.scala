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

package models.response.emcsTfe

import base.SpecBase
import fixtures.GetMovementResponseFixtures

import java.time.format.DateTimeParseException

class EadEsadModelSpec extends SpecBase with GetMovementResponseFixtures {

  "formattedInvoiceDate" should {
    "return None" when {
      "there is no invoice date defined" in {
        val model = eadEsadModel.copy(invoiceDate = None)
        model.formattedInvoiceDate mustBe None
      }
    }

    "return Some" when {
      "there is an invoice date and the correctly formatted date should be returned" in {
        val model = eadEsadModel.copy(invoiceDate = Some("2023-01-01"))
        model.formattedInvoiceDate mustBe Some("1 January 2023")
      }
    }

    "throw an exception" when {
      "the invoice date is in an invalid format" in {
        val model = eadEsadModel.copy(invoiceDate = Some("fake-date"))
        intercept[DateTimeParseException](model.formattedInvoiceDate)
      }
    }


  }

}
