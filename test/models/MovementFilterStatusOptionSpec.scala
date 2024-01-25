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

package models

import base.SpecBase

class MovementFilterStatusOptionSpec extends SpecBase {


  "apply" when {
    MovementFilterStatusOption.values.foreach {
      value =>
        val in = value.code
        val out = value
        s"provided $in" must {
          s"return $out" in {
            MovementFilterStatusOption.apply(in) mustBe out
          }
        }
    }

    "provided an invalid value" must {
      "throw an IllegalArgumentException" in {
        val result = intercept[IllegalArgumentException](MovementFilterStatusOption.apply("beans"))

        result.getMessage mustBe "Invalid argument of 'beans' received which can not be mapped to a MovementFilterStatusOption"
      }
    }
  }

  "toOption" when {
    MovementFilterStatusOption.values.filterNot(_ == MovementFilterStatusOption.ChooseStatus).foreach {
      value =>
        val out = Some(value)
        s"provided $value" must {
          s"return $out" in {
            MovementFilterStatusOption.toOption(value) mustBe out
          }
        }
    }

    s"provided ${MovementFilterStatusOption.ChooseStatus}" must {
      val out = None
      s"return $out" in {
        MovementFilterStatusOption.toOption(MovementFilterStatusOption.ChooseStatus) mustBe out
      }
    }
  }
}
