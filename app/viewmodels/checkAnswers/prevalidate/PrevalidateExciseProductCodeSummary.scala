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

package viewmodels.checkAnswers.prevalidate

import controllers.prevalidateTrader.routes
import models.requests.UserAnswersRequest
import models.{CheckMode, ExciseProductCode, Index}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._

object PrevalidateExciseProductCodeSummary {

  def row(idx: Index, exciseProductCode: ExciseProductCode)(implicit request: UserAnswersRequest[_], messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = KeyViewModel(Text(exciseProductCode.code)).withCssClass("govuk-!-width-10"),
      value = ValueViewModel(Text(exciseProductCode.description)).withCssClass("govuk-!-width-one-quarter"),
      actions = Seq(
        ActionItemViewModel(
          href = routes.PrevalidateExciseProductCodeController.onPageLoad(request.ern, idx, CheckMode).url,
          content = Text(messages("site.change")),
          id = s"change-epc-${idx.displayIndex}"
        ).withVisuallyHiddenText(messages(s"prevalidateTrader.addToList.change.hidden", idx.displayIndex)),
        ActionItemViewModel(
          href = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
          content = Text(messages("site.remove")),
          id = s"remove-epc-${idx.displayIndex}"
        ).withVisuallyHiddenText(messages(s"prevalidateTrader.addToList.change.hidden", idx.displayIndex))
      )
    )

}
