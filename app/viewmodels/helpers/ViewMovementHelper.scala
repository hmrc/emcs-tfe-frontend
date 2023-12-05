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

import models.requests.DataRequest
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.emcstfefrontend.models.response.emcsTfe.GetMovementResponse
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow}
import utils.ExpectedDateOfArrival
import viewmodels.{Overview, SubNavigationTab}
import views.html.components.list
import views.html.viewMovement.partials.overview_partial

import javax.inject.Inject

class ViewMovementHelper @Inject()(
                                    list: list,
                                    overviewPartial: overview_partial
                                  ) extends ExpectedDateOfArrival {

  def movementCard(subNavigationTab: SubNavigationTab, movementResponse: GetMovementResponse)
                  (implicit request: DataRequest[_], messages: Messages): HtmlContent =

    subNavigationTab match {
      case Overview => constructMovementOverview(movementResponse)
      case _ => HtmlContent("")
    }


  private def constructMovementOverview(movementResponse: GetMovementResponse)
                                           (implicit request: DataRequest[_], messages: Messages): HtmlContent = {

    def summaryListRowBuilder(key: String, value: String) = SummaryListRow(
      Key(Text(messages(key))),
      Value(Text(messages(value))),
      classes = "govuk-summary-list__row"
    )

    def localReferenceNumber() =
      summaryListRowBuilder("viewMovement.overview.lrn", movementResponse.localReferenceNumber)

    def eadStatus() =
      summaryListRowBuilder("viewMovement.overview.eadStatus", movementResponse.eadStatus)

    def dateOfDispatch() =
      summaryListRowBuilder("viewMovement.overview.dateOfDispatch", movementResponse.formattedDateOfDispatch)

    def expectedDate() =
      summaryListRowBuilder("viewMovement.overview.journeyTime", movementResponse.formattedExpectedDateOfArrival)

    def consignor() =
      summaryListRowBuilder("viewMovement.overview.consignor", movementResponse.consignorTrader.traderExciseNumber)

    def itemCount() =
      summaryListRowBuilder("viewMovement.overview.numberOfItems", movementResponse.numberOfItems.toString)

    def transportingVehicles() =
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

    HtmlContent(
      overviewPartial(
        Seq(
          localReferenceNumber(),
          eadStatus(),
          dateOfDispatch(),
          expectedDate(),
          consignor(),
          itemCount(),
          transportingVehicles()
        )
      )
    )

  }


}
