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

package models.response.emcsTfe

import models.common.{UnitOfMeasure, WineProduct}
import play.api.libs.json.{Json, Reads}

case class MovementItem(itemUniqueReference: Int,
                        productCode: String,
                        cnCode: String,
                        quantity: BigDecimal,
                        grossMass: BigDecimal,
                        netMass: BigDecimal,
                        alcoholicStrength: Option[BigDecimal],
                        degreePlato: Option[BigDecimal],
                        fiscalMark: Option[String],
                        fiscalMarkUsedFlag: Option[Boolean],
                        designationOfOrigin: Option[String],
                        sizeOfProducer: Option[String],
                        density: Option[BigDecimal],
                        commercialDescription: Option[String],
                        brandNameOfProduct: Option[String],
                        maturationAge: Option[String],
                        packaging: Seq[Packaging],
                        wineProduct: Option[WineProduct],
                        unitOfMeasure: Option[UnitOfMeasure],
                        productCodeDescription: Option[String]) {

  /**
   * Product code S500 has no corresponding CN Codes in reference data.
   * Reference: https://www.revenue.ie/en/online-services/support/documents/emcs/technical-information/emcs-trader-guide.pdf
   * BR018 - It is obligatory that the cn code of each body ead that is
   * included in the draft message exists in the same
   * correspondence cn code-excise product of the reference data
   * in seed, with the excise product code of the same body ead
   * that is included in the draft message, if the excise product
   * code is anything but S500.
   *
   * BR29 - Exceptionally, when the excise product is S500 (in the
   * IE815,IE801 and IE825 messages), the <(BODY) E-AD.CN
   * Code> may not be in the business code list <CN CODES>.
   *
   * @return true if the MovementItem productCode is not in the list of Product Codes without a corresponding CN Code (i.e. productCode != S500)
   */
  def hasProductCodeWithValidCnCode: Boolean = {
    val productCodesWithoutAnyCorrespondingCnCodes = Seq("S500")
    !productCodesWithoutAnyCorrespondingCnCodes.contains(productCode)
  }
}

object MovementItem {
  implicit val reads: Reads[MovementItem] = Json.reads
}
