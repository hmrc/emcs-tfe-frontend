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

package models.common

import base.SpecBase
import models.common.RoleType._

class RoleTypeSpec extends SpecBase {
  "RoleType" should {

    ".fromExciseRegistrationNumber" when {
      "the ERN begins with GBWK" must {
        s"return $GBWK" in {
          fromExciseRegistrationNumber("GBWKTestErn") mustBe GBWK
        }
      }
      "the ERN begins with XIWK" must {
        s"return $XIWK" in {
          fromExciseRegistrationNumber("XIWKTestErn") mustBe XIWK
        }
      }
      "the ERN begins with GBRC" must {
        s"return $GBRC" in {
          fromExciseRegistrationNumber("GBRCTestErn") mustBe GBRC
        }
      }
      "the ERN begins with XIRC" must {
        s"return $XIRC" in {
          fromExciseRegistrationNumber("XIRCTestErn") mustBe XIRC
        }
      }
      "the ERN begins with XI00" must {
        s"return $XI00" in {
          fromExciseRegistrationNumber("XI00TestErn") mustBe XI00
        }
      }
      "the ERN begins with GB00" must {
        s"return $GB00" in {
          fromExciseRegistrationNumber("GB00TestErn") mustBe GB00
        }
      }
      "the ERN begins with XITC" must {
        s"return $XITC" in {
          fromExciseRegistrationNumber("XITCTestErn") mustBe XITC
        }
      }
      "the ERN begins with XIPA" must {
        s"return $XIPA" in {
          fromExciseRegistrationNumber("XIPATestErn") mustBe XIPA
        }
      }
      "the ERN begins with XIPB" must {
        s"return $XIPB" in {
          fromExciseRegistrationNumber("XIPBTestErn") mustBe XIPB
        }
      }
      "the ERN begins with XIPC" must {
        s"return $XIPC" in {
          fromExciseRegistrationNumber("XIPCTestErn") mustBe XIPC
        }
      }
      "the ERN begins with XIPD" must {
        s"return $XIPD" in {
          fromExciseRegistrationNumber("XIPDTestErn") mustBe XIPD
        }
      }
      "the ERN begins with an unexpected prefix" should {
        "throw an exception" in {
          intercept[IllegalArgumentException](fromExciseRegistrationNumber("InvalidTestErn"))
        }
      }
    }

    ".isGB" should {
      "return true when the ERN starts with GB" in {
        RoleType.isGB("GB0011111111") mustBe true
      }

      "return false when the ERN DOES NOT start with GB" in {
        RoleType.isGB("XI0011111111") mustBe false
      }
    }

    ".isXI" should {
      "return true when the ERN starts with XI" in {
        RoleType.isXI("XI0011111111") mustBe true
      }

      "return false when the ERN DOES NOT start with XI" in {
        RoleType.isXI("GB0011111111") mustBe false
      }
    }
  }
}
