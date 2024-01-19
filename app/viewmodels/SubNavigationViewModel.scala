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

package viewmodels

sealed abstract class SubNavigationTab {

  val name = s"${getClass.getSimpleName.stripSuffix("$")}"

  def linkId = s"${name.toLowerCase}-tab"

  def href = s"${name.toLowerCase}"
}

case object Overview extends SubNavigationTab
case object Movement extends SubNavigationTab
case object Delivery extends SubNavigationTab
case object Guarantor extends SubNavigationTab
case object Transport extends SubNavigationTab
case object Items extends SubNavigationTab
case object Documents extends SubNavigationTab

object SubNavigationTab {
  val values: Seq[SubNavigationTab] = Seq(
    Overview,
    Movement,
    Delivery,
    Guarantor,
    Transport,
    Items,
    Documents
  )
}
