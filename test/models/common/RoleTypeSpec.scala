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
import RoleType._

class RoleTypeSpec extends SpecBase {
  "RoleType.fromExciseRegistrationNumber" when {
    "the ERN begins with GBWK" should {
      s"return $GBWK" in {
        fromExciseRegistrationNumber("GBWKTestErn") mustBe GBWK
      }
    }
    "the ERN begins with XIWK" should {
      s"return $XIWK" in {
        fromExciseRegistrationNumber("XIWKTestErn") mustBe XIWK
      }
    }
    "the ERN begins with GBRC" should {
      s"return $GBRC" in {
        fromExciseRegistrationNumber("GBRCTestErn") mustBe GBRC
      }
    }
    "the ERN begins with XIRC" should {
      s"return $XIRC" in {
        fromExciseRegistrationNumber("XIRCTestErn") mustBe XIRC
      }
    }
    "the ERN begins with XI00" should {
      s"return $XI00" in {
        fromExciseRegistrationNumber("XI00TestErn") mustBe XI00
      }
    }
    "the ERN begins with XITC" should {
      s"return $XITC" in {
        fromExciseRegistrationNumber("XITCTestErn") mustBe XITC
      }
    }
    "the ERN begins with XIPA" should {
      s"return $XIPA" in {
        fromExciseRegistrationNumber("XIPATestErn") mustBe XIPA
      }
    }
    "the ERN begins with XIPB" should {
      s"return $XIPB" in {
        fromExciseRegistrationNumber("XIPBTestErn") mustBe XIPB
      }
    }
    "the ERN begins with XIPC" should {
      s"return $XIPC" in {
        fromExciseRegistrationNumber("XIPCTestErn") mustBe XIPC
      }
    }
    "the ERN begins with XIPD" should {
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
}
