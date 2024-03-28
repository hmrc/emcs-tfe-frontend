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

package fixtures

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.emcstfe.models.response.prevalidate.{ExciseTraderResponse, ExciseTraderValidationResponse, PreValidateTraderApiResponse, ValidateProductAuthorisationResponse}

trait PrevalidateTraderFixtures extends BaseFixtures {

  val preValidateApiResponseAsJson: JsValue = Json.parse(
    s"""
       | {
       |   "exciseTraderValidationResponse": {
       |     "validationTimestamp": "2023-12-15T10:55:17.443Z",
       |     "exciseTraderResponse": [
       |       {
       |         "exciseRegistrationNumber": "GBWK002281023",
       |         "entityGroup": "UK Record",
       |         "validTrader": true,
       |         "traderType": "1",
       |         "validateProductAuthorisationResponse": {
       |           "valid": true
       |         }
       |       }
       |     ]
       |   }
       | }
       |""".stripMargin)

  val preValidateApiResponseModel: PreValidateTraderApiResponse = PreValidateTraderApiResponse(
    exciseTraderValidationResponse = ExciseTraderValidationResponse(
      validationTimestamp = "2023-12-15T10:55:17.443Z",
      exciseTraderResponse = Seq(
        ExciseTraderResponse(
          exciseRegistrationNumber = "GBWK002281023",
          entityGroup = "UK Record",
          validTrader = true,
          traderType = Some("1"),
          validateProductAuthorisationResponse = Some(ValidateProductAuthorisationResponse(true))
        )
      )
    )
  )
}
