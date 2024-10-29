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

package views

import models.requests.DataRequest
import play.api.data.Form
import play.api.i18n.Messages
import utils.DateUtils
import viewmodels.TraderInfo

object ViewUtils extends DateUtils {

  def title(form: Form[_], title: String, section: Option[String] = None, tab: Option[String] = None)(implicit messages: Messages): String =
    titleNoForm(
      title   = s"${errorPrefix(form)} ${messages(title)}",
      section = section,
      tab = tab
    )

  def titleNoForm(title: String, section: Option[String] = None, tab: Option[String] = None)(implicit messages: Messages): String =
    s"${tab.fold("")(messages(_) + " - ")}${messages(title)} - ${section.fold("")(messages(_) + " - ")}${messages("service.name")} - ${messages("site.govuk")}"

  def errorPrefix(form: Form[_])(implicit messages: Messages): String = {
    if (form.hasErrors || form.hasGlobalErrors) messages("error.browser.title.prefix") else ""
  }

  def maybeShowActiveTrader(request: DataRequest[_]): Option[TraderInfo] =
    Option.when(request.request.hasMultipleErns) {
      TraderInfo(request.traderKnownFacts.map(_.traderName).getOrElse(""), request.ern)
    }

  def pluralSingular(msg: String, count: Int, additionalArgs: String*)(implicit messages: Messages): String = {
    val msgArgs = Seq(count.toString) ++ additionalArgs
    messages(msg + (if(count!=1) ".plural" else ".singular"), msgArgs:_*)
  }
}
