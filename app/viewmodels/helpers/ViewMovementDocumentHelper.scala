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

import models.response.emcsTfe.GetMovementResponse
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import services.GetDocumentTypesService
import uk.gov.hmrc.govukfrontend.views.Aliases._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.helpers.SummaryListHelper.summaryListRowBuilder
import views.html.components.{h2, p, summaryCard}
import views.html.viewMovement.partials.overview_partial

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ViewMovementDocumentHelper @Inject()(h2: h2,
                                           summaryCard: summaryCard,
                                           overviewPartial: overview_partial,
                                           p: p,
                                           getDocumentTypesService: GetDocumentTypesService
                                          ) {


  def constructMovementDocument(movement: GetMovementResponse, isSummaryCard: Boolean)
                               (implicit messages: Messages, hc: HeaderCarrier, ec: ExecutionContext): Future[Html] = {

    val notProvidedMessage = messages("viewMovement.document.notProvided")
    getDocumentTypesService.getDocumentTypes().map {
      sequenceOfDocuments =>
        HtmlFormat.fill(Seq(h2(messages("viewMovement.document.details"), "govuk-heading-l")) ++
          (movement.documentCertificate match {
            case Some(value) => value.zipWithIndex.map {
              case (document, index) =>
                val documentTypeDescription =
                  document.documentType
                    .flatMap(documentType => sequenceOfDocuments.find(_.code == documentType).map(_.description))
                    .getOrElse(notProvidedMessage)

                val summaryRows = Seq(
                  summaryListRowBuilder(
                    "viewMovement.document.documentType",
                    documentTypeDescription
                  ),
                  summaryListRowBuilder(
                    "viewMovement.document.documentReference",
                    document.documentReference.getOrElse(notProvidedMessage)
                  ),
                  summaryListRowBuilder(
                    "viewMovement.document.documentDescription",
                    document.documentDescription.getOrElse(notProvidedMessage)
                  )
                )

                if (isSummaryCard) {
                  summaryCard(
                    card = Some(Card(
                      Some(CardTitle(Text(messages("viewMovement.document.heading", index + 1)), headingLevel = Some(3)))
                    )),
                    summaryListRows = summaryRows
                  )
                } else {
                  overviewPartial(
                    headingId = Some(s"document-information-heading-$index"),
                    headingMessageKey = Some(messages("viewMovement.document.heading", index + 1)),
                    headingLevel = 3,
                    headingMessageClass = "govuk-heading-m",
                    cardTitleMessageKey = None,
                    summaryListRows = summaryRows
                  )
                }
            }
            case _ => Seq(p()(Html(notProvidedMessage)))
          }
            ))
    }

  }

}
