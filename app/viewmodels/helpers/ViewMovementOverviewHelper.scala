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
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow}
import viewmodels.helpers.SummaryListHelper._
import views.html.components.list
import views.html.viewMovement.partials.overview_partial

import javax.inject.{Inject, Singleton}

@Singleton
class ViewMovementOverviewHelper @Inject()(list: list,
                                           overviewPartial: overview_partial) {

  def constructMovementOverview(movementResponse: GetMovementResponse)
                                                (implicit messages: Messages): Html = {

    val localReferenceNumber = summaryListRowBuilder("viewMovement.overview.lrn", movementResponse.localReferenceNumber)

    val eadStatus = summaryListRowBuilder("viewMovement.overview.eadStatus", movementResponse.eadStatus.toString)

    val dateOfDispatch = summaryListRowBuilder("viewMovement.overview.dateOfDispatch", movementResponse.formattedDateOfDispatch)

    val expectedDate = summaryListRowBuilder("viewMovement.overview.journeyTime", movementResponse.formattedExpectedDateOfArrival)

    val consignor = movementResponse.consignorTrader.traderExciseNumber.map(ern => summaryListRowBuilder("viewMovement.overview.consignor", ern))

    val itemCount = summaryListRowBuilder("viewMovement.overview.numberOfItems", movementResponse.numberOfItems.toString)

    val transportingVehicles = {
      SummaryListRow(
        Key(Text(messages("viewMovement.overview.transporting"))),
        Value(
          HtmlContent(
            list(
              movementResponse.transportDetails
                .filter(_.identityOfTransportUnits.isDefined)
                .map(transport => Html(transport.identityOfTransportUnits))
            )
          )
        )
      )
    }

    overviewPartial(
      headingMessageKey = Some("viewMovement.overview.title"),
      cardTitleMessageKey = "viewMovement.overview.title",
      summaryListRows = Seq(
        Some(localReferenceNumber),
        Some(eadStatus),
        Some(dateOfDispatch),
        Some(expectedDate),
        consignor,
        Some(itemCount),
        Some(transportingVehicles)
      ).flatten
    )

  }
}
