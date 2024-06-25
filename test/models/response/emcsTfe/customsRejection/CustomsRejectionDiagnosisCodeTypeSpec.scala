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

package models.response.emcsTfe.customsRejection

import base.SpecBase


class CustomsRejectionDiagnosisCodeTypeSpec extends SpecBase {

  "CustomsRejectionDiagnosisCodeType" should {

    "have the correct codes" in {
      CustomsRejectionDiagnosisCodeType.UnknownArc.toString mustBe "1"
      CustomsRejectionDiagnosisCodeType.BodyRecordUniqueReferenceDoesNotExist.toString mustBe "2"
      CustomsRejectionDiagnosisCodeType.NoGoodsItemInDeclaration.toString mustBe "3"
      CustomsRejectionDiagnosisCodeType.WeightMismatch.toString mustBe "4"
      CustomsRejectionDiagnosisCodeType.DestinationTypeIsNotExport.toString mustBe "5"
      CustomsRejectionDiagnosisCodeType.CommodityCodesDoNotMatch.toString mustBe "6"
    }
  }
}