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

class MovementFilterDirectionOptionSpec extends SpecBase {

  "apply" when {
    MovementFilterDirectionOption.values.foreach {
      value =>
        val in = value.code
        val out = value
        s"provided $in" must {
          s"return $out" in {
            MovementFilterDirectionOption.apply(in) mustBe out
          }
        }
    }

    "provided an invalid value" must {
      "throw an IllegalArgumentException" in {
        val result = intercept[IllegalArgumentException](MovementFilterDirectionOption.apply("beans"))

        result.getMessage mustBe "Invalid argument of 'beans' received which can not be mapped to a MovementFilterDirectionOption"
      }
    }
  }

  "toOptions" when {
    Seq(MovementFilterDirectionOption.GoodsIn, MovementFilterDirectionOption.GoodsOut).foreach {
      value =>
        val out = Set(value)
        s"provided $value" must {
          s"return $out" in {
            MovementFilterDirectionOption.toOptions(value) mustBe out
          }
        }
    }

    s"provided ${MovementFilterDirectionOption.All}" must {
      val out = Set(MovementFilterDirectionOption.GoodsIn, MovementFilterDirectionOption.GoodsOut)
      s"return $out" in {
        MovementFilterDirectionOption.toOptions(MovementFilterDirectionOption.All) mustBe out
      }
    }
  }

  "getOptionalValueFromCheckboxes" when {
    "only GoodsIn" must {
      "return Some(GoodsIn)" in {
        MovementFilterDirectionOption.getOptionalValueFromCheckboxes(Set(MovementFilterDirectionOption.GoodsIn)) mustBe
          Some(MovementFilterDirectionOption.GoodsIn)
      }
    }
    "only GoodsOut" must {
      "return Some(GoodsOut)" in {
        MovementFilterDirectionOption.getOptionalValueFromCheckboxes(Set(MovementFilterDirectionOption.GoodsOut)) mustBe
          Some(MovementFilterDirectionOption.GoodsOut)
      }
    }
    "both GoodsIn and GoodsOut" must {
      "return Some(All)" in {
        MovementFilterDirectionOption.getOptionalValueFromCheckboxes(Set(MovementFilterDirectionOption.GoodsIn, MovementFilterDirectionOption.GoodsOut)) mustBe
          Some(MovementFilterDirectionOption.All)
      }
    }
    "neither GoodsIn nor GoodsOut" must {
      "return None" in {
        MovementFilterDirectionOption.getOptionalValueFromCheckboxes(Set(MovementFilterDirectionOption.All)) mustBe None
        MovementFilterDirectionOption.getOptionalValueFromCheckboxes(Set()) mustBe None
      }
    }
  }

}
