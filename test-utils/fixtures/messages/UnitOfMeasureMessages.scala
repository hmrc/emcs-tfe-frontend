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

object UnitOfMeasureMessages {

  sealed trait ViewMessages { _: i18n =>
    val kilogramsShort: String = "kg"
    val kilogramsLong: String = "kilograms"
    val litres20Short: String = "litres"
    val litres20Long: String = "litres (temperature of 20°C)"
    val litres15Short: String = "litres"
    val litres15Long: String = "litres (temperature of 15°C)"
    val thousandsShort: String = "x1000"
    val thousandsLong: String = "x1000 items"
  }

  object English extends ViewMessages with BaseEnglish
}
