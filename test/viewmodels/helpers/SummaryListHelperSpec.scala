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

package viewmodels.helpers

import base.SpecBase
import models.common.AddressModel
import models.requests.DataRequest
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, SummaryListRow, Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

class SummaryListHelperSpec extends SpecBase {

  implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"))
  implicit lazy val messages: Messages = messages(request)

  "summaryListRowBuilder" should {
    "construct a key value summary list row (when value is a string)" in {
      val result = SummaryListHelper.summaryListRowBuilder("random.key", "random.value")
      result mustBe SummaryListRow(
        key = Key(Text(value = "random.key")),
        value = Value(Text(value = "random.value")),
        classes = "govuk-summary-list__row"
      )
    }

    "construct a key value summary list row (when value is HTML)" in {
      val result = SummaryListHelper.summaryListRowBuilder("random.key", Html("some html value"))
      result mustBe SummaryListRow(
        key = Key(Text(value = "random.key")),
        value = Value(HtmlContent(Html("some html value"))),
        classes = "govuk-summary-list__row"
      )
    }
  }

  ".renderAddress" should {

    "render street number and street when provided" in {
      val addressModel: AddressModel = AddressModel(
        Some("1"), Some("Street Street"), Some("POST CODE"), Some("City City")
      )
      SummaryListHelper.renderAddress(addressModel) mustBe Html("1 Street Street <br>City City <br>POST CODE")
    }

    "render just street when no street number provided" in {
      val addressModel: AddressModel = AddressModel(
        None, Some("Street Street"), Some("POST CODE"), Some("City City")
      )
      SummaryListHelper.renderAddress(addressModel) mustBe Html("Street Street <br>City City <br>POST CODE")
    }

    "render nothing when all fields are undefined" in {
      val addressModel: AddressModel = AddressModel(
        None, None, None, None
      )
      SummaryListHelper.renderAddress(addressModel) mustBe Html("")
    }

    "render all rows when all fields are defined" in {
      val addressModel: AddressModel = AddressModel(
        Some("1"), Some("Street Street"), Some("POST CODE"), Some("City City")
      )
      SummaryListHelper.renderAddress(addressModel) mustBe Html("1 Street Street <br>City City <br>POST CODE")
    }
  }

  ".summaryListRowBuilder" should {
    "construct a key value summary list row (when value is a string)" in {
      val result = SummaryListHelper.summaryListRowBuilder("random.key", "random.value")
      result mustBe SummaryListRow(
        key = Key(Text(value = "random.key")),
        value = Value(Text(value = "random.value")),
        classes = "govuk-summary-list__row"
      )
    }

    "construct a key value summary list row (when value is HTML)" in {
      val result = SummaryListHelper.summaryListRowBuilder("random.key", Html("some html value"))
      result mustBe SummaryListRow(
        key = Key(Text(value = "random.key")),
        value = Value(HtmlContent(Html("some html value"))),
        classes = "govuk-summary-list__row"
      )
    }
  }
}
