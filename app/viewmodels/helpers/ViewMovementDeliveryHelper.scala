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
import viewmodels.helpers.SummaryListHelper._
import views.html.viewMovement.partials.overview_partial

import javax.inject.{Inject, Singleton}

@Singleton
class ViewMovementDeliveryHelper @Inject()(
                                            overviewPartial: overview_partial
                                          ) {

  def constructMovementDelivery(movementResponse: GetMovementResponse)(implicit messages: Messages): Html = {
    val consignorSummaryCards = Seq(
      summaryListRowBuilder("viewMovement.delivery.consignor.name", movementResponse.consignorTrader.traderName),
      summaryListRowBuilder("viewMovement.delivery.consignor.ern", movementResponse.consignorTrader.traderExciseNumber),
      summaryListRowBuilder("viewMovement.delivery.consignor.address", renderAddress(movementResponse.consignorTrader.address))
    )

    val optPlaceOfDispatchSummaryCards = movementResponse.placeOfDispatchTrader.map { placeOfDispatch =>
      Seq(
        summaryListRowBuilder("viewMovement.delivery.placeOfDispatch.name", placeOfDispatch.traderName),
        summaryListRowBuilder("viewMovement.delivery.placeOfDispatch.ern", placeOfDispatch.traderExciseNumber),
        summaryListRowBuilder("viewMovement.delivery.placeOfDispatch.address", renderAddress(placeOfDispatch.address))
      )
    }

    val optConsigneeSummaryCards = movementResponse.consigneeTrader.map { consigneeTrader =>
      Seq(
        summaryListRowBuilder("viewMovement.delivery.consignee.name", consigneeTrader.traderName),
        summaryListRowBuilder("viewMovement.delivery.consignee.ern", consigneeTrader.traderExciseNumber),
        summaryListRowBuilder("viewMovement.delivery.consignee.address", renderAddress(consigneeTrader.address))
      )
    }

    val optPlaceOfDestinationCards = movementResponse.deliveryPlaceTrader.map { deliverPlaceTrader =>
      Seq(
        summaryListRowBuilder("viewMovement.delivery.placeOfDestination.name", deliverPlaceTrader.traderName),
        summaryListRowBuilder("viewMovement.delivery.placeOfDestination.ern", deliverPlaceTrader.traderExciseNumber),
        summaryListRowBuilder("viewMovement.delivery.placeOfDestination.address", renderAddress(deliverPlaceTrader.address))
      )
    }

    HtmlFormat.fill(Seq(
      overviewPartial(
        headingMessageKey = Some("viewMovement.delivery.title"),
        cardTitleMessageKey = "viewMovement.delivery.consignor",
        consignorSummaryCards
      ),
      optPlaceOfDispatchSummaryCards.map { placeOfDispatchSummaryCards =>
        overviewPartial(
          headingMessageKey = None,
          cardTitleMessageKey = "viewMovement.delivery.placeOfDispatch",
          placeOfDispatchSummaryCards
        )
      }.getOrElse(Html("")),
      optConsigneeSummaryCards.map { consigneeSummaryCards =>
        overviewPartial(
          headingMessageKey = None,
          cardTitleMessageKey = "viewMovement.delivery.consignee",
          consigneeSummaryCards
        )
      }.getOrElse(Html("")),
      optPlaceOfDestinationCards.map { placeOfDestinationCards =>
        overviewPartial(
          headingMessageKey = None,
          cardTitleMessageKey = "viewMovement.delivery.placeOfDestination",
          placeOfDestinationCards
        )
      }.getOrElse(Html(""))
    ))
  }
}
