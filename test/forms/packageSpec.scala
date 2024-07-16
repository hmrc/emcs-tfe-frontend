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

package forms

import base.SpecBase

class packageSpec extends SpecBase {

  "removeAnyQueryParamCharacters" must {
    "remove any query param characters" in {
      removeAnyQueryParamCharacters("value&unexpected/ parameter2") mustBe "valueunexpected parameter2"
    }
    "trim leading and trailing whitespace" in {
      removeAnyQueryParamCharacters(" value&unexpected/ parameter2 ") mustBe "valueunexpected parameter2"
    }
    "not remove alphanumeric characters" in {
      removeAnyQueryParamCharacters("valueunexpected123 parameter") mustBe "valueunexpected123 parameter"
    }
  }

  "removeAnyNonAlphanumerics" must {
    "remove any non-alphanumeric characters" in {
      removeAnyNonAlphanumerics("value&unexpected/ parameter2") mustBe "valueunexpectedparameter2"
    }
    "trim leading and trailing whitespace" in {
      removeAnyNonAlphanumerics(" value&unexpected/ parameter2 ") mustBe "valueunexpectedparameter2"
    }
    "not remove alphanumeric characters" in {
      removeAnyNonAlphanumerics("valueunexpected123 parameter") mustBe "valueunexpected123parameter"
    }
  }

}
