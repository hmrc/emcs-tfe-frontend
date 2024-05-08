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

package fixtures.messages

object ItemDetailsMessages {

  sealed trait ViewMessages { _: i18n =>
    val itemDetailsCardHeading: String
    val packagingCardHeading: Int => String
    val wineWithoutPDOPGI: String
    val commodityCodeKey: String
    val descriptionKey: String
    val quantityKey: String
    val quantityValue: BigDecimal => String
    val grossWeightKey: String
    val grossWeightValue: BigDecimal => String
    val netWeightKey: String
    val netWeightValue: BigDecimal => String
    val densityKey: String
    val densityValue: BigDecimal => String
    val alcoholicStrengthKey: String
    val alcoholicStrengthValue: BigDecimal => String
    val maturationAgeKey: String
    val degreePlatoKey: String
    val degreePlatoValue: BigDecimal => String
    val fiscalMarkKey: String
    val designationOfOriginKey: String
    val independentSmallProducersDeclarationKey: String
    val sizeOfProducerKey: String
    val sizeOfProducerValue: String => String
    val commercialDescriptionKey: String
    val brandNameOfProductKey: String
    val wineProductCategoryKey: String
    val wineGrowingZoneCodeKey: String
    val thirdCountryOfOriginKey: String
    val wineOperationsKey: String
    val wineOperationsValueNone: String
    val wineOtherInformationKey: String
    val packagingTypeKey: String
    val packagingQuantityKey: String
    val packagingIdentityOfCommercialSealKey: String
    val packagingSealInformationKey: String
    val packagingShippingMarksKey: String
  }

  object English extends ViewMessages with BaseEnglish {
    override val itemDetailsCardHeading = "Item details"
    override val packagingCardHeading: Int => String = i => s"Packaging type $i"
    override val wineWithoutPDOPGI = "Wine without PDO/PGI"
    override val commodityCodeKey = "Commodity code"
    override val descriptionKey = "Description"
    override val quantityKey = "Quantity"
    override val quantityValue: BigDecimal => String = value => s"$value litres (temperature of 15°C)"
    override val grossWeightKey = "Gross weight"
    override val grossWeightValue: BigDecimal => String = value => s"$value kg"
    override val netWeightKey = "Net weight"
    override val netWeightValue: BigDecimal => String = value => s"$value kg"
    override val densityKey = "Density"
    override val densityValue: BigDecimal => String = value => s"${value}kg/m<sup>3</sup> (temperature of 15&deg;C)"
    override val alcoholicStrengthKey = "Alcoholic strength by volume"
    override val alcoholicStrengthValue: BigDecimal => String = value => s"$value%"
    override val maturationAgeKey = "Maturation age"
    override val degreePlatoKey = "Degree Plato"
    override val degreePlatoValue: BigDecimal => String = value => s"$value&deg;P"
    override val fiscalMarkKey = "Fiscal marks"
    override val designationOfOriginKey = "Designation of origin"
    override val independentSmallProducersDeclarationKey = "Independent small producer’s declaration"
    override val sizeOfProducerKey = "Size of producer"
    override val sizeOfProducerValue: String => String = value => s"$value hectolitres"
    override val commercialDescriptionKey = "Commercial description"
    override val brandNameOfProductKey = "Brand name"
    override val wineProductCategoryKey = "Category of wine product"
    override val wineGrowingZoneCodeKey = "Wine growing zone code"
    override val thirdCountryOfOriginKey = "Third country of origin"
    override val wineOperationsKey = "Wine operations"
    override val wineOperationsValueNone = "The product has not undergone any operations"
    override val wineOtherInformationKey = "More information about the wine"
    override val packagingTypeKey = "Type"
    override val packagingQuantityKey = "Quantity"
    override val packagingIdentityOfCommercialSealKey = "Identity of commercial seal"
    override val packagingSealInformationKey = "Seal information"
    override val packagingShippingMarksKey = "Shipping marks"
  }
}
