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

import models.common.UnitOfMeasure.Kilograms
import models.response.emcsTfe.{MovementItem, Packaging}
import models.response.referenceData.ItemPackaging
import play.api.libs.json.{JsObject, Json}

trait ItemFixtures { _: BaseFixtures =>

  val testItemPackagingTypes: Seq[ItemPackaging] = Seq(
    ItemPackaging("AE", "Aerosol"),
    ItemPackaging("AM", "Ampoule, non protected"),
    ItemPackaging("BG", "Bag"),
    ItemPackaging("VA", "Vat")
  )

  val testItemPackagingTypesJson: JsObject = Json.obj(
    "AE" -> "Aerosol",
    "AM" -> "Ampoule, non protected",
    "BG" -> "Bag",
    "VA" -> "Vat"
  )

  val item1 = MovementItem(
    itemUniqueReference = 1,
    productCode = "W200",
    cnCode = "22041011",
    quantity = BigDecimal(500),
    commercialDescription = Some("description 1"),
    packaging = Seq(Packaging("AE", Some(1))),
    unitOfMeasure = None
  )

  val item1Json = Json.obj(
    "itemUniqueReference" -> 1,
    "productCode" -> "W200",
    "cnCode" -> "22041011",
    "quantity" -> BigDecimal(500),
    "commercialDescription" -> "description 1",
    "packaging" -> Json.arr(
      Json.obj(
        "typeOfPackage" -> "AE",
        "quantity" -> 1
      )
    ),
    "unitOfMeasure" -> None
  )

  val item1WithPackaging = item1.copy(
    packaging = Seq(Packaging("Aerosol", Some(1)))
  )

  val item1WithPackagingAndUnitOfMeasure = item1WithPackaging.copy(
    unitOfMeasure = Some(Kilograms)
  )

  val item2 = MovementItem(
    itemUniqueReference = 2,
    productCode = "W300",
    cnCode = "22041011",
    quantity = BigDecimal(550),
    commercialDescription = Some("description 2"),
    packaging = Seq(
      Packaging("AE", Some(1)),
      Packaging("BG", Some(1))
    ),
    unitOfMeasure = None
  )

  val item2Json = Json.obj(
    "itemUniqueReference" -> 2,
    "productCode" -> "W300",
    "cnCode" -> "22041011",
    "quantity" -> BigDecimal(550),
    "commercialDescription" -> "description 2",
    "packaging" -> Json.arr(
      Json.obj(
        "typeOfPackage" -> "AE",
        "quantity" -> 1
      ),
      Json.obj(
        "typeOfPackage" -> "BG",
        "quantity" -> 1
      )
    )
  )

  val item2WithPackaging = item2.copy(
    packaging = Seq(
      Packaging("Aerosol", Some(1)),
      Packaging("Bag", Some(1))
    )
  )

  val item2WithPackagingAndUnitOfMeasure = item2WithPackaging.copy(
    unitOfMeasure = Some(Kilograms)
  )
}
