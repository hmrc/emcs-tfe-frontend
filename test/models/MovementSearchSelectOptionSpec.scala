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

package models

import base.SpecBase
import fixtures.messages.ViewAllMovementsMessages.English
import forms.ViewAllMovementsFormProvider
import models.MovementSearchSelectOption._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem

class MovementSearchSelectOptionSpec extends SpecBase {

  implicit val msgs: Messages = messages(Seq(English.lang))

  "MovementSearchSelectOption" when {

    ".apply" must {

      "be constructed from all valid codes" in {

        MovementSearchSelectOption.apply("chooseRefType") mustBe ChooseSearch
        MovementSearchSelectOption.apply("arc") mustBe ARC
        MovementSearchSelectOption.apply("lrn") mustBe LRN
        MovementSearchSelectOption.apply("otherTraderId") mustBe ERN
        MovementSearchSelectOption.apply("transporterTraderName") mustBe Transporter
      }

      "throw illegal argument error when the code can't be mapped to a search type" in {
        intercept[IllegalArgumentException](MovementSearchSelectOption.apply("OtherSearch")).getMessage mustBe
          s"Invalid argument of 'OtherSearch' received which can not be mapped to a MovementSearchSelectOption"
      }
    }

    s"being rendered in lang code of '${English.lang.code}'" must {

      "output the correct messages for ChooseSearch" in {

        msgs(ChooseSearch.displayName) mustBe English.searchSelectChooseSearch
      }

      "output the correct messages for ARC" in {

        msgs(ARC.displayName) mustBe English.searchSelectARC
      }

      "output the correct messages for LRN" in {

        msgs(LRN.displayName) mustBe English.searchSelectLRN
      }

      "output the correct messages for ERN" in {

        msgs(ERN.displayName) mustBe English.searchSelectERN
      }

      "output the correct messages for Transporter" in {

        msgs(Transporter.displayName) mustBe English.searchSelectTransporter
      }
    }

    ".filterNotChooseStatus" when {
      MovementSearchSelectOption.values.filterNot(_ == MovementSearchSelectOption.ChooseSearch).foreach {
        value =>
          val out = Some(value)
          s"provided $value" must {
            s"return $out" in {
              MovementSearchSelectOption.filterNotChooseSearch(Some(value.code)) mustBe out
            }
          }
      }

      s"provided ${MovementSearchSelectOption.ChooseSearch}" must {
        val out = None
        s"return $out" in {
          MovementSearchSelectOption.filterNotChooseSearch(Some(MovementSearchSelectOption.ChooseSearch.code)) mustBe out
        }
      }

      s"provided None" must {
        val out = None
        s"return $out" in {
          MovementSearchSelectOption.filterNotChooseSearch(None) mustBe out
        }
      }
    }

    ".constructSelectItems" must {

      val formProvider = new ViewAllMovementsFormProvider()

      "return a Seq of SelectItem" when {

        "no previous selected option and no searchValue" in {

          val form = formProvider().bind(Map[String, String]())

          MovementSearchSelectOption.constructSelectItems(form) mustBe Seq(
            SelectItem(
              text = English.searchSelectChooseSearch,
              value = None,
              selected = true,
              disabled = true
            ),
            SelectItem(
              text = English.searchSelectARC,
              value = Some(ARC.code),
              selected = false
            ),
            SelectItem(
              text = English.searchSelectLRN,
              value = Some(LRN.code),
              selected = false
            ),
            SelectItem(
              text = English.searchSelectERN,
              value = Some(ERN.code),
              selected = false
            ),
            SelectItem(
              text = English.searchSelectTransporter,
              value = Some(Transporter.code),
              selected = false
            )
          )
        }

        "no previous selected option and searchValue" in {

          val options = MovementListSearchOptions(None, Some("123456789012345678901"))

          val form = formProvider().bind(Map(ViewAllMovementsFormProvider.searchValue -> options.searchValue.get))

          MovementSearchSelectOption.constructSelectItems(form) mustBe Seq(
            SelectItem(
              text = English.searchSelectChooseSearch,
              value = None,
              selected = true,
              disabled = true
            ),
            SelectItem(
              text = English.searchSelectARC,
              value = Some(ARC.code),
              selected = false
            ),
            SelectItem(
              text = English.searchSelectLRN,
              value = Some(LRN.code),
              selected = false
            ),
            SelectItem(
              text = English.searchSelectERN,
              value = Some(ERN.code),
              selected = false
            ),
            SelectItem(
              text = English.searchSelectTransporter,
              value = Some(Transporter.code),
              selected = false
            )
          )
        }

        "there is a previously selected option but no searchValue" in {

          val options = MovementListSearchOptions(Some(ARC), None)

          val form = formProvider().bind(Map(
            ViewAllMovementsFormProvider.searchKey -> options.searchKey.get.code,
          ))

          MovementSearchSelectOption.constructSelectItems(form) mustBe Seq(
            SelectItem(
              text = English.searchSelectChooseSearch,
              value = None,
              selected = true,
              disabled = true
            ),
            SelectItem(
              text = English.searchSelectARC,
              value = Some(ARC.code),
              selected = false
            ),
            SelectItem(
              text = English.searchSelectLRN,
              value = Some(LRN.code),
              selected = false
            ),
            SelectItem(
              text = English.searchSelectERN,
              value = Some(ERN.code),
              selected = false
            ),
            SelectItem(
              text = English.searchSelectTransporter,
              value = Some(Transporter.code),
              selected = false
            )
          )
        }

        "there is a previously selected option and a searchValue" in {

          val options = MovementListSearchOptions(Some(ARC), Some("beans"))

          val form = formProvider().bind(Map(
            ViewAllMovementsFormProvider.searchKey -> options.searchKey.get.code,
            ViewAllMovementsFormProvider.searchValue -> options.searchValue.get
          ))

          MovementSearchSelectOption.constructSelectItems(form) mustBe Seq(
            SelectItem(
              text = English.searchSelectChooseSearch,
              value = None,
              selected = false,
              disabled = true
            ),
            SelectItem(
              text = English.searchSelectARC,
              value = Some(ARC.code),
              selected = true
            ),
            SelectItem(
              text = English.searchSelectLRN,
              value = Some(LRN.code),
              selected = false
            ),
            SelectItem(
              text = English.searchSelectERN,
              value = Some(ERN.code),
              selected = false
            ),
            SelectItem(
              text = English.searchSelectTransporter,
              value = Some(Transporter.code),
              selected = false
            )
          )
        }
      }
    }
  }
}
