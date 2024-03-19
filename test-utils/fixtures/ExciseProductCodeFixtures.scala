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

import play.api.libs.json.Json
import models.ExciseProductCode

trait ExciseProductCodeFixtures {

  val testEpcWine = "W200"
  val testEpcBeer = "B000"
  val testEpcTobacco: String = "T200"

  val tobaccoExciseProductCode = ExciseProductCode(
    code = testEpcTobacco,
    description = "Tobacco",
    category = "T",
    categoryDescription = "Tobacco"
  )

  val beerExciseProductCode = ExciseProductCode(
    code = testEpcBeer,
    description = "Beer",
    category = "B",
    categoryDescription = "Beer"
  )

  val wineExciseProductCode = ExciseProductCode(
    code = testEpcWine,
    description = "Still wine and still fermented beverages other than wine and beer",
    category = "W",
    categoryDescription = "Wine and fermented beverages other than wine and beer"
  )

  val beerExciseProductCodeJson = Json.obj(
    "code" -> testEpcBeer,
    "description" -> "Beer",
    "category" -> "B",
    "categoryDescription" -> "Beer"
  )

  val wineExciseProductCodeJson = Json.obj(
    "code" -> testEpcWine,
    "description" -> "Still wine and still fermented beverages other than wine and beer",
    "category" -> "W",
    "categoryDescription" -> "Wine and fermented beverages other than wine and beer"
  )
}
