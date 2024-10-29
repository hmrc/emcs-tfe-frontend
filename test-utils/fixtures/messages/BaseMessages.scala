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

package fixtures.messages

import play.api.i18n.Lang


trait BaseMessages { _: i18n =>
  def titleHelper(heading: String, tab: Option[String] = None) =
    s"${tab.fold("")(_ + " - ")}$heading - Create and manage excise goods movements with EMCS - GOV.UK"
  val opensInNewTab: String
  val lang: Lang

  val prevalidateTraderCaption: String = "Prevalidate a trader"

  val continue = "Continue"
  val saveAndContinue = "Save and continue"
  val clearSelectedCode = "Clear selected code"
  val change = "Change"
  val remove = "Remove"
  val yes = "Yes"
  val no = "No"
  val signIn = "Sign back in to EMCS"
  val subNavAriaLabel = "Electronic Administrative Document navigation"

  def errorHelper(error: String) = s"Error: $error"
}

trait BaseEnglish extends BaseMessages with EN {
  override val opensInNewTab: String = "(opens in new tab)"
}
object BaseEnglish extends BaseEnglish
