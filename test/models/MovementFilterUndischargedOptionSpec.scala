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

class MovementFilterUndischargedOptionSpec extends SpecBase {

  "apply" when {
    MovementFilterUndischargedOption.values.foreach {
      value =>
        val in = value.code
        val out = value
        s"provided $in" must {
          s"return $out" in {
            MovementFilterUndischargedOption.apply(in) mustBe out
          }
        }
    }

    "provided an invalid value" must {
      "throw an IllegalArgumentException" in {
        val result = intercept[IllegalArgumentException](MovementFilterUndischargedOption.apply("beans"))

        result.getMessage mustBe "Invalid argument of 'beans' received which can not be mapped to a MovementFilterUndischargedOption"
      }
    }
  }
}
