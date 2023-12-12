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

package models.response.referenceData

import play.api.libs.json.{JsError, JsObject, JsSuccess, Reads}

case class CnCodeInformationResponse(data: Map[String, CnCodeInformation])

object CnCodeInformationResponse {
  implicit val reads: Reads[CnCodeInformationResponse] = {
    case JsObject(underlying) => JsSuccess(CnCodeInformationResponse(underlying.map {
      case (key, value) => (key, value.as[CnCodeInformation])
    }.toMap))
    case other =>
      JsError("Unable to read CnCodeInformationResponse as a JSON object: " + other.toString())
  }
}
