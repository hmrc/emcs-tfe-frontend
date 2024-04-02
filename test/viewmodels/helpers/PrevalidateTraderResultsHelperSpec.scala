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

package viewmodels.helpers

import base.SpecBase
import fixtures.ExciseProductCodeFixtures
import models.ExciseProductCode

class PrevalidateTraderResultsHelperSpec extends SpecBase with ExciseProductCodeFixtures {

  val sampleEPCs: Seq[ExciseProductCode] = Seq(wineExciseProductCode, beerExciseProductCode)

  ".parseExciseProductCodeFromStringToModel" should {

    "return a list of ExciseProductCode's from a list of strings" when {

      "the code of the EPC matches a value in the sequence" in {

        val result = PrevalidateTraderResultsHelper.parseExciseProductCodeFromStringToModel(
          Seq(wineExciseProductCode.code, beerExciseProductCode.code), sampleEPCs
        )

        result mustBe sampleEPCs
      }
    }

    "return only the EPCs that could be matched between both lists" in {

      val result = PrevalidateTraderResultsHelper.parseExciseProductCodeFromStringToModel(
        Seq(wineExciseProductCode.code, beerExciseProductCode.code, tobaccoExciseProductCode.code), sampleEPCs
      )

      result mustBe sampleEPCs
    }


    "return nothing when none of the EPCs match the model codes" in {

      val result = PrevalidateTraderResultsHelper.parseExciseProductCodeFromStringToModel(
        Seq(tobaccoExciseProductCode.code), sampleEPCs
      )

      result mustBe Seq.empty
    }
  }

}
