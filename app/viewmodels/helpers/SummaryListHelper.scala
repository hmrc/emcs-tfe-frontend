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

import models.common.AddressModel
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow}

object SummaryListHelper {

  def summaryListRowBuilder(key: String, value: String)(implicit messages: Messages): SummaryListRow = SummaryListRow(
    key = Key(Text(value = messages(key))),
    value = Value(Text(value = messages(value))),
    classes = "govuk-summary-list__row"
  )

  def summaryListRowBuilder(key: String, value: Html)(implicit messages: Messages): SummaryListRow = SummaryListRow(
    key = Key(Text(value = messages(key))),
    value = Value(HtmlContent(value)),
    classes = "govuk-summary-list__row"
  )

  def renderAddress(address: AddressModel): Html = {
    val firstLineOfAddress = (address.streetNumber, address.street) match {
      case (Some(propertyNumber), Some(street)) => Html(s"$propertyNumber $street <br>")
      case (Some(number), None) => Html(s"$number <br>")
      case (None, Some(street)) => Html(s"$street <br>")
      case _ => Html("")
    }
    val city = address.city.fold(Html(""))(city => Html(s"$city <br>"))
    val postCode = address.postcode.fold(Html(""))(postcode => Html(s"$postcode"))

    HtmlFormat.fill(Seq(
      firstLineOfAddress,
      city,
      postCode
    ))
  }
}
