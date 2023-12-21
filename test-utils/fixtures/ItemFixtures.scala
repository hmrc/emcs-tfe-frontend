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
import models.common.WineProduct
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

  val aerosolPackage = Packaging(
    typeOfPackage = "AE",
    quantity = Some(1),
    shippingMarks = Some("MARKS"),
    identityOfCommercialSeal = Some("SEAL456789321"),
    sealInformation = Some("Red Strip")
  )

  val bagPackage = Packaging(
    typeOfPackage = "BG",
    quantity = Some(1),
    shippingMarks = None,
    identityOfCommercialSeal = Some("SEAL77"),
    sealInformation = Some("Cork")
  )

  val wineProduct = WineProduct(
    wineProductCategory = "1",
    wineGrowingZoneCode = Some("2"),
    thirdCountryOfOrigin = Some("FJ"),
    otherInformation = Some("Not available"),
    wineOperations = Some(Seq("4", "5"))
  )

  val item1 = MovementItem(
    itemUniqueReference = 1,
    productCode = "W200",
    cnCode = "22041011",
    quantity = BigDecimal(500),
    grossMass = BigDecimal(900),
    netMass = BigDecimal(375),
    alcoholicStrength = Some(1.5),
    degreePlato = Some(1.2),
    fiscalMark = Some("FM564789 Fiscal Mark"),
    fiscalMarkUsedFlag = Some(true),
    designationOfOrigin = Some("Designation of Origin"),
    sizeOfProducer = Some("20000"),
    density = Some(880),
    commercialDescription = Some("description 1"),
    brandNameOfProduct = Some("MALAMATINA"),
    maturationAge = Some("Maturation Period"),
    packaging = Seq(aerosolPackage),
    wineProduct = Some(wineProduct),
    unitOfMeasure = None,
    productCodeDescription = None
  )

  val item1Json = Json.obj(fields =
    "itemUniqueReference" -> 1,
    "productCode" -> "W200",
    "cnCode" -> "22041011",
    "quantity" -> 500,
    "grossMass" -> 900,
    "netMass" -> 375,
    "alcoholicStrength" -> 1.5,
    "degreePlato" -> 1.2,
    "fiscalMark" -> "FM564789 Fiscal Mark",
    "fiscalMarkUsedFlag" -> true,
    "designationOfOrigin" -> "Designation of Origin",
    "sizeOfProducer" -> "20000",
    "density" -> 880,
    "commercialDescription" -> "description 1",
    "brandNameOfProduct" -> "MALAMATINA",
    "maturationAge" -> "Maturation Period",
    "packaging" -> Json.arr(
      Json.obj(fields =
        "typeOfPackage" -> "AE",
        "quantity" -> 1,
        "shippingMarks" -> "MARKS",
        "identityOfCommercialSeal" -> "SEAL456789321",
        "sealInformation" -> "Red Strip"
      )
    ),
    "wineProduct" -> Json.obj(
      "wineProductCategory" -> "1",
      "wineGrowingZoneCode" -> "2",
      "thirdCountryOfOrigin" -> "FJ",
      "otherInformation" -> "Not available",
      "wineOperations" -> Json.arr("4", "5")
    )
  )

  val item1WithWineOperations = item1.copy(
    wineProduct = Some(wineProduct.copy(
      wineOperations = Some(Seq("Reason 4", "Reason 5"))
    ))
  )

  val item1WithWineAndPackaging = item1WithWineOperations.copy(
    packaging = Seq(Packaging(
      typeOfPackage = "Aerosol",
      quantity = Some(1),
      shippingMarks = Some("MARKS"),
      identityOfCommercialSeal = Some("SEAL456789321"),
      sealInformation = Some("Red Strip")))
  )

  val item1WithWineAndPackagingAndCnCodeInfo = item1WithWineAndPackaging.copy(
    unitOfMeasure = Some(Kilograms),
    productCodeDescription = Some("Fine-cut tobacco for the rolling of cigarettes")
  )

  val wineProduct2 = WineProduct(
    wineProductCategory = "3",
    wineGrowingZoneCode = None,
    thirdCountryOfOrigin = Some("FJ"),
    otherInformation = Some("Not available"),
    wineOperations = Some(Seq("0", "1"))
  )

  val item2 = MovementItem(
    itemUniqueReference = 2,
    productCode = "W300",
    cnCode = "22041011",
    quantity = BigDecimal(550),
    grossMass = BigDecimal(901),
    netMass = BigDecimal(475),
    alcoholicStrength = Some(BigDecimal(12.7)),
    degreePlato = None,
    fiscalMark = Some("FM564790 Fiscal Mark"),
    fiscalMarkUsedFlag = Some(true),
    designationOfOrigin = Some("Designation of Origin"),
    sizeOfProducer = Some("20000"),
    density = None,
    commercialDescription = Some("description 2"),
    brandNameOfProduct = Some("BrandName"),
    maturationAge = None,
    packaging = Seq(aerosolPackage.copy(shippingMarks = None), bagPackage),
    wineProduct = Some(wineProduct2),
    unitOfMeasure = None,
    productCodeDescription = None
  )

  val item2Json = Json.obj(fields =
    "itemUniqueReference" -> 2,
    "productCode" -> "W300",
    "cnCode" -> "22041011",
    "quantity" -> 550,
    "grossMass" -> 901,
    "netMass" -> 475,
    "alcoholicStrength" -> 12.7,
    "fiscalMark" -> "FM564790 Fiscal Mark",
    "fiscalMarkUsedFlag" -> true,
    "designationOfOrigin" -> "Designation of Origin",
    "sizeOfProducer" -> "20000",
    "commercialDescription" -> "description 2",
    "brandNameOfProduct" -> "BrandName",
    "packaging" -> Json.arr(
      Json.obj(fields =
        "typeOfPackage" -> "AE",
        "quantity" -> 1,
        "identityOfCommercialSeal" -> "SEAL456789321",
        "sealInformation" -> "Red Strip"
      ),
      Json.obj(fields =
        "typeOfPackage" -> "BG",
        "quantity" -> 1,
        "identityOfCommercialSeal" -> "SEAL77",
        "sealInformation" -> "Cork"
      )
    ),
    "wineProduct" -> Json.obj(
      "wineProductCategory" -> "3",
      "thirdCountryOfOrigin" -> "FJ",
      "otherInformation" -> "Not available",
      "wineOperations" -> Json.arr("0", "1")
    )
  )

  val item2WithWineOperations = item2.copy(
    wineProduct = Some(wineProduct.copy(
      wineOperations = Some(Seq("Reason 0", "Reason 1"))
    ))
  )

  val item2WithWineAndPackaging = item2WithWineOperations.copy(
    packaging = Seq(
      Packaging(
        typeOfPackage = "Aerosol",
        quantity = Some(1),
        shippingMarks = None,
        identityOfCommercialSeal = Some("SEAL456789321"),
        sealInformation = Some("Red Strip")
      ),
      Packaging(
        typeOfPackage = "Bag",
        quantity = Some(1),
        shippingMarks = None,
        identityOfCommercialSeal = Some("SEAL77"),
        sealInformation = Some("Cork")
      )
    )
  )

  val item2WithWineAndPackagingAndCnCodeInfo = item2WithWineAndPackaging.copy(
    unitOfMeasure = Some(Kilograms),
    productCodeDescription = Some("Fine-cut tobacco for the rolling of cigarettes")
  )

  val testCnCodeResponse = Json.obj(
    "22041011" -> Json.obj(
      "cnCode" -> "22041011",
        "cnCodeDescription" -> "Wine sparkling",
        "exciseProductCode" -> "W300",
        "exciseProductCodeDescription" -> "Wine",
        "unitOfMeasureCode" -> 3
    ),
    "22041011" -> Json.obj(
      "cnCode" -> "22041011",
      "cnCodeDescription" -> "Wine sparkling",
      "exciseProductCode" -> "W200",
      "exciseProductCodeDescription" -> "Wine",
      "unitOfMeasureCode" -> 3
    )
  )
}
