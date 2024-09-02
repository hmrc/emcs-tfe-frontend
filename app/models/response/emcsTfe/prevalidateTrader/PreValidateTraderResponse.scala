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

package models.response.emcsTfe.prevalidateTrader

import play.api.libs.json.{Format, Json}

case class ProductError(
                         exciseProductCode: String,
                         errorCode: Int,
                         errorText: String)

case class ValidateProductAuthorisationResponse(productError: Option[Seq[ProductError]] = None)

case class FailDetails(
                        validTrader: Boolean,
                        errorCode: Option[Int] = None,
                        errorText: Option[String] = None,
                        validateProductAuthorisationResponse: Option[ValidateProductAuthorisationResponse] = None)

case class PreValidateTraderResponse(
                                            processingDateTime: String,
                                            exciseId: String,
                                            validationResult: String,
                                            failDetails: Option[FailDetails] = None)


object ProductError {
  implicit val format: Format[ProductError] = Json.format
}

object ValidateProductAuthorisationResponse {
  implicit val format: Format[ValidateProductAuthorisationResponse] = Json.format
}

object FailDetails {
  implicit val format: Format[FailDetails] = Json.format
}

object PreValidateTraderResponse {
  implicit val format: Format[PreValidateTraderResponse] = Json.format
}