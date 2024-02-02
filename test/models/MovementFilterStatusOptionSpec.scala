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
import fixtures.messages.ViewAllMovementsMessages.English
import play.api.i18n.{Messages, MessagesApi}
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

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

  "filterNotChooseStatus" when {
    MovementFilterStatusOption.values.filterNot(_ == MovementFilterStatusOption.ChooseStatus).foreach {
      value =>
        val out = Some(value)
        s"provided $value" must {
          s"return $out" in {
            MovementFilterStatusOption.filterNotChooseStatus(Some(value)) mustBe out
          }
        }
    }

    s"provided ${MovementFilterStatusOption.ChooseStatus}" must {
      val out = None
      s"return $out" in {
        MovementFilterStatusOption.filterNotChooseStatus(Some(MovementFilterStatusOption.ChooseStatus)) mustBe out
      }
    }

    s"provided None" must {
      val out = None
      s"return $out" in {
        MovementFilterStatusOption.filterNotChooseStatus(None) mustBe out
      }
    }
  }

  "selectItems" must {
    "return a Seq of SelectItem" in {
      implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))

      def constructSelectItem(value: MovementFilterStatusOption, displayName: String, isSelected: Boolean = false): SelectItem =
        SelectItem(
          text   = displayName,
          value   = Some(value.toString),
          selected = isSelected
        )

      MovementFilterStatusOption.selectItems(existingAnswer = Some(MovementFilterStatusOption.DeemedExported)) mustBe Seq(
        constructSelectItem(MovementFilterStatusOption.ChooseStatus, English.filtersStatusChoose),
        constructSelectItem(MovementFilterStatusOption.Active, English.filtersStatusActive),
        constructSelectItem(MovementFilterStatusOption.Cancelled, English.filtersStatusCancelled),
        constructSelectItem(MovementFilterStatusOption.DeemedExported, English.filtersStatusDeemedExported, isSelected = true),
        constructSelectItem(MovementFilterStatusOption.Delivered, English.filtersStatusDelivered),
        constructSelectItem(MovementFilterStatusOption.Diverted, English.filtersStatusDiverted),
        constructSelectItem(MovementFilterStatusOption.Exporting, English.filtersStatusExporting),
        constructSelectItem(MovementFilterStatusOption.ManuallyClosed, English.filtersStatusManuallyClosed),
        constructSelectItem(MovementFilterStatusOption.PartiallyRefused, English.filtersStatusPartiallyRefused),
        constructSelectItem(MovementFilterStatusOption.Refused, English.filtersStatusRefused),
        constructSelectItem(MovementFilterStatusOption.Replaced, English.filtersStatusReplaced),
        constructSelectItem(MovementFilterStatusOption.Rejected, English.filtersStatusRejected),
        constructSelectItem(MovementFilterStatusOption.Stopped, English.filtersStatusStopped)
      )
    }
  }
}
