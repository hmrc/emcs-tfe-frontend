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

package models.common

import base.SpecBase
import models.common.GuarantorType._

class GuarantorTypeSpec extends SpecBase {
  
  "GuarantorType" should {

    "have the correct codes" in {
      GuarantorNotRequired.toString mustBe "0"
      Consignor.toString mustBe "1"
      ConsignorTransporter.toString mustBe "12"
      ConsignorTransporterOwner.toString mustBe "123"
      ConsignorTransporterOwnerConsignee.toString mustBe "1234"
      ConsignorTransporterConsignee.toString mustBe "124"
      ConsignorOwner.toString mustBe "13"
      ConsignorOwnerConsignee.toString mustBe "134"
      JointConsignorConsignee.toString mustBe "14"
      Transporter.toString mustBe "2"
      TransporterOwner.toString mustBe "23"
      TransporterOwnerConsignee.toString mustBe "234"
      TransporterConsignee.toString mustBe "24"
      Owner.toString mustBe "3"
      OwnerConsignee.toString mustBe "34"
      Consignee.toString mustBe "4"
      NoGuarantor.toString mustBe "5"
    }
  }
}
