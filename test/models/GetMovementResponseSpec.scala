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

/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import base.SpecBase
import fixtures.GetMovementResponseFixtures
import models.response.emcsTfe.GetMovementResponse
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, convertToStringShouldWrapper}
import play.api.libs.json.Json

class GetMovementResponseSpec extends SpecBase with GetMovementResponseFixtures {

  "GetMovementResponse" should {

    "read from json" in {
      val result = Json.fromJson[GetMovementResponse](getMovementResponseInputJson)

      result.isSuccess shouldBe true
      result.get shouldBe getMovementResponseModel
    }

    ".formattedDateOfDispatch" in {
      getMovementResponseModel.formattedDateOfDispatch shouldBe "20 November 2008"
    }

    ".formattedExpectedDateOfArrival" when {

      "a timeOfDispatch is present on the movement" in {
        getMovementResponseModel.formattedExpectedDateOfArrival shouldBe "10 December 2008"
      }

      "a timeOfDispatch is absent from the movement" in {
        val model = getMovementResponseModel.eadEsad.copy(timeOfDispatch = None)

        getMovementResponseModel
          .copy(eadEsad = model)
          .formattedExpectedDateOfArrival shouldBe "10 December 2008"
      }

    }

  }

}