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
import UnitOfMeasure._
import fixtures.messages.UnitOfMeasureMessages
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.Messages

class UnitOfMeasureSpec extends SpecBase with GuiceOneAppPerSuite {

  "UnitOfMeasure" must {

    "be constructed from UnitOfMeasureCode for all valid codes" in {

      UnitOfMeasure.apply(1) mustBe Kilograms
      UnitOfMeasure.apply(2) mustBe Litres15
      UnitOfMeasure.apply(3) mustBe Litres20
      UnitOfMeasure.apply(4) mustBe Thousands
    }

    "throws illegal argument error when EPC can't be mapped to GoodsType" in {
      intercept[IllegalArgumentException](UnitOfMeasure.apply(5)).getMessage mustBe
        s"Invalid argument of '5' received which can not be mapped to a UnitOfMeasure"
    }

    Seq(UnitOfMeasureMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}'" must {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        "output the correct messages" in {

          Kilograms.toShortFormatMessage() mustBe messagesForLanguage.kilogramsShort
          Kilograms.toLongFormatMessage() mustBe messagesForLanguage.kilogramsLong
          Litres15.toShortFormatMessage() mustBe messagesForLanguage.litres15Short
          Litres15.toLongFormatMessage() mustBe messagesForLanguage.litres15Long
          Litres20.toShortFormatMessage() mustBe messagesForLanguage.litres20Short
          Litres20.toLongFormatMessage() mustBe messagesForLanguage.litres20Long
          Thousands.toShortFormatMessage() mustBe messagesForLanguage.thousandsShort
          Thousands.toLongFormatMessage() mustBe messagesForLanguage.thousandsLong
        }
      }
    }
  }
}
