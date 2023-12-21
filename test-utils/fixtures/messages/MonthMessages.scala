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

object MonthMessages {

  sealed trait ViewMessages { _: i18n =>
    val jan: String = "January"
    val feb: String = "February"
    val mar: String = "March"
    val apr: String = "April"
    val may: String = "May"
    val jun: String = "June"
    val jul: String = "July"
    val aug: String = "August"
    val sep: String = "September"
    val oct: String = "October"
    val nov: String = "November"
    val dec: String = "December"
    //noinspection ScalaStyle
    def month(i: Int): String = i match {
      case 1 => jan
      case 2 => feb
      case 3 => mar
      case 4 => apr
      case 5 => may
      case 6 => jun
      case 7 => jul
      case 8 => aug
      case 9 => sep
      case 10 => oct
      case 11 => nov
      case 12 => dec
    }
  }

  object English extends ViewMessages with BaseEnglish
}
