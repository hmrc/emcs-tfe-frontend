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

import models.common.WrongWithMovement._
import models.response.emcsTfe.reportOfReceipt.ReceiptedItemsModel

trait ReceiptedItemsModelFixtures extends BaseFixtures with UnsatisfactoryModelFixtures with ExciseProductCodeFixtures {

  val excessReceiptedItemsModel = ReceiptedItemsModel(
    eadBodyUniqueReference = 1,
    productCode = testEpcWine,
    excessAmount = Some(12.145),
    shortageAmount = None,
    refusedAmount = Some(10),
    unsatisfactoryReasons = Seq(maxUnsatisfactoryModel(Excess))
  )

  val shortageReceiptedItemsModel = ReceiptedItemsModel(
    eadBodyUniqueReference = 1,
    productCode = testEpcWine,
    excessAmount = None,
    shortageAmount = Some(12.145),
    refusedAmount = Some(10),
    unsatisfactoryReasons = Seq(
      maxUnsatisfactoryModel(Excess),
      maxUnsatisfactoryModel(BrokenSeals),
      maxUnsatisfactoryModel(Other),
      maxUnsatisfactoryModel(Damaged)
    )
  )

  val minReceiptedItemsModel = ReceiptedItemsModel(
    eadBodyUniqueReference = 1,
    productCode = testEpcWine,
    excessAmount = None,
    shortageAmount = None,
    refusedAmount = None,
    unsatisfactoryReasons = Seq(
      minUnsatisfactoryModel(Damaged)
    )
  )

}
