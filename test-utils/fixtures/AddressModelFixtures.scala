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

import models.common.AddressModel
import play.api.libs.json.{JsObject, Json}

trait AddressModelFixtures extends BaseFixtures {

  val maxAddressModel: AddressModel = AddressModel(
    streetNumber = Some("number"),
    street = Some("street"),
    postcode = Some("postcode"),
    city = Some("city")
  )


  val maxAddressModelJson: JsObject = Json.obj(
    "streetNumber" -> "number",
    "street" -> "street",
    "postcode" -> "postcode",
    "city" -> "city"
  )

  val minAddressModel: AddressModel = AddressModel(
    streetNumber = None,
    street = None,
    postcode = None,
    city = None
  )


}
