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

import models.Index
import models.requests.UserAnswersRequest
import pages.prevalidateTrader.PrevalidateEPCPage
import play.api.i18n.Messages
import queries.PrevalidateTraderEPCCount
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.checkAnswers.prevalidate.PrevalidateExciseProductCodeSummary
import viewmodels.govuk.summarylist._

object PrevalidateAddToListHelper {

  def addedEpcs()(implicit request: UserAnswersRequest[_], messages: Messages): SummaryList = {
    SummaryListViewModel(request.userAnswers.get(PrevalidateTraderEPCCount) match {
      case Some(value) => (0 until value).flatMap { int =>
        val idx = Index(int)
        request.userAnswers.get(PrevalidateEPCPage(idx)).map(PrevalidateExciseProductCodeSummary.row(idx, _))
      }
      case None => Nil
    })
  }
}
