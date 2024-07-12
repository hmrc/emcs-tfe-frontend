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

package object forms {
  private[forms] val XSS_REGEX = "^(?s)(?!.*javascript)(?!.*[<>;:]).{1,}$"
  private[forms] val ALPHANUMERIC_REGEX = "^(?s)(?=.*[A-Za-z0-9]).{1,}$"
  private[forms] val EXCISE_NUMBER_REGEX = "[A-Z]{2}[a-zA-Z0-9]{11}"

  /**
   * When form values will be used as query parameters, we will need to silently guard against users entering `&` or `/` for example,
   * this function will replace any non-alphanumeric with a blank value e.g. `value&unexpected/ parameter` -> `valueunexpected parameter`
   */
  private[forms] def removeAnyQueryParamCharacters(rawString: String): String = rawString.replaceAll("[/&?=]", "")

  /**
   * A more strict variant of `removeAnyQueryParamCharacters` that will remove all non-alphanumeric characters
   */
  private[forms] def removeAnyNonAlphanumerics(rawString: String): String = rawString.replaceAll("[^A-Za-z0-9]", "")
}
