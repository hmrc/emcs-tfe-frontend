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

package viewmodels.helpers

import base.SpecBase
import fixtures.MemberStatesFixtures
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.SelectItem

class SelectItemHelperSpec extends SpecBase
  with MemberStatesFixtures {
  implicit lazy val msgs: Messages = messages(FakeRequest())

  ".constructSelectItems" must {

    "must return a list of select items (with a default option)" in {
      val result = SelectItemHelper.constructSelectItems(
        selectOptions = Seq(memberStateAT, memberStateBE),
        defaultTextMessageKey = Some("default"),
        existingAnswer = None)
      result mustBe Seq(
        SelectItem(value = Some(""), selected = true, disabled = true, text = "default"),
        SelectItem(value = Some("AT"), text = "Austria (AT)", selected = false),
        SelectItem(value = Some("BE"), text = "Belgium (BE)", selected = false)
      )
    }

    "must return a list of select items (without a default option)" in {
      val result = SelectItemHelper.constructSelectItems(
        selectOptions = Seq(memberStateAT, memberStateBE),
        defaultTextMessageKey = None,
        existingAnswer = None)
      result mustBe Seq(
        SelectItem(value = Some("AT"), text = "Austria (AT)", selected = false),
        SelectItem(value = Some("BE"), text = "Belgium (BE)", selected = false)
      )
    }

    "must return a list of select items (pre-selected when there is an existing answer)" in {
      val result = SelectItemHelper.constructSelectItems(
        selectOptions = Seq(memberStateAT, memberStateBE),
        defaultTextMessageKey = Some("default"),
        existingAnswer = Some("BE"))
      result mustBe Seq(
        SelectItem(value = Some(""), selected = false, disabled = true, text = "default"),
        SelectItem(value = Some("AT"), text = "Austria (AT)", selected = false),
        SelectItem(value = Some("BE"), text = "Belgium (BE)", selected = true)
      )
    }

  }

}
