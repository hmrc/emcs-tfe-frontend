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

case class WineOperationsResponse(data: Map[String, String])

object WineOperationsResponse {
  implicit val reads: Reads[WineOperationsResponse] = {
    case JsObject(underlying) => JsSuccess(WineOperationsResponse(underlying.map {
      case (key, value) => (key, value.as[String])
    }.toMap))
    case other =>
      JsError("Unable to read WineOperationsResponse as a JSON object: " + other.toString())
  }
}
