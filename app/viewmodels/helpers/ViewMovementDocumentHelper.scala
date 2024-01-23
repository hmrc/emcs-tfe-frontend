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

import models.response.emcsTfe.GetMovementResponse
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.ExpectedDateOfArrival
import viewmodels.govuk.TagFluency
import views.html.components.{h2, summaryCard}

import javax.inject.Inject

class ViewMovementDocumentHelper @Inject()(h2: h2,
                                           summaryCard: summaryCard,
                                          ) extends ExpectedDateOfArrival with TagFluency {


  def constructMovementDocument(movement: GetMovementResponse)(implicit messages: Messages): Html = {
    val notProvidedMessage = messages("viewMovement.transport.transportUnit.notProvided")
    HtmlFormat.fill(
      movement.documentCertificate match {
        case Some(value) => value.zipWithIndex.map {
            case (document, index) =>
              summaryCard(
                card = Card(
                  Some(CardTitle(Text(messages("viewMovement.document.heading", index + 1)))),
                ),
                summaryListRows = Seq(
                  summaryListRowBuilder(
                    "viewMovement.document.documentReference",
                    document.documentReference.getOrElse(notProvidedMessage)
                  ),
                  summaryListRowBuilder(
                    "viewMovement.document.documentType",
                    document.documentType.getOrElse(notProvidedMessage)
                  ),
                  summaryListRowBuilder(
                    "viewMovement.document.documentDescription",
                    document.documentDescription.getOrElse(notProvidedMessage)
                  ),
                )
              )
        }
        case _ => Seq.empty
      }
    )
  }

  private def summaryListRowBuilder(key: String, value: String)(implicit messages: Messages) = SummaryListRow(
    key = Key(Text(value = messages(key))),
    value = Value(Text(value = messages(value))),
    classes = "govuk-summary-list__row"
  )

}
