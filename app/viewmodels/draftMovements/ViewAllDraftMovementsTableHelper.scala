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

package viewmodels.draftMovements

import models.response.emcsTfe.draftMovement.DraftMovement
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow, Value}
import utils.DateUtils
import viewmodels.govuk.TagFluency
import views.html.viewAllDrafts.{DraftMovementsSummaryListKeyContent, DraftMovementsSummaryListValueContent}

import javax.inject.Inject

class ViewAllDraftMovementsTableHelper @Inject()(keyContent: DraftMovementsSummaryListKeyContent,
                                                 valueContent: DraftMovementsSummaryListValueContent) extends DateUtils with TagFluency {

  def constructSummaryListRows(ern: String, draftMovements: Seq[DraftMovement])
                              (implicit messages: Messages): Seq[SummaryListRow] =
    draftMovements.map { draftMovement =>
      SummaryListRow(
        Key(
          HtmlContent(keyContent(ern, draftMovement)),
          classes = "width-60pc"
        ),
        Value(
          HtmlContent(valueContent(ern, draftMovement)),
          classes = "govuk-!-text-align-right"
        )
      )
    }

}
