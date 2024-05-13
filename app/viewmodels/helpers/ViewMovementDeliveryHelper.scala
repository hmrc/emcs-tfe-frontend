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
import views.html.components.h2
import views.html.viewMovement.partials.overview_partial

import javax.inject.{Inject, Singleton}

@Singleton
class ViewMovementDeliveryHelper @Inject()(
                                            overviewPartial: overview_partial,
                                            h2: h2
                                          ) {

  def constructMovementDelivery(movementResponse: GetMovementResponse)(implicit messages: Messages): Html = {
    val consignorSummaryCards = Seq(
      movementResponse.consignorTrader.traderName.map(name => summaryListRowBuilder("viewMovement.delivery.consignor.name", name)),
      movementResponse.consignorTrader.traderExciseNumber.map(ern => summaryListRowBuilder("viewMovement.delivery.consignor.ern", ern)),
      movementResponse.consignorTrader.address.map(address => summaryListRowBuilder("viewMovement.delivery.consignor.address", renderAddress(address)))
    )

    val optPlaceOfDispatchSummaryCards = movementResponse.placeOfDispatchTrader.map { placeOfDispatch =>
      Seq(
        placeOfDispatch.traderName.map(name => summaryListRowBuilder("viewMovement.delivery.placeOfDispatch.name", name)),
        placeOfDispatch.traderExciseNumber.map(ern => summaryListRowBuilder("viewMovement.delivery.placeOfDispatch.ern", ern)),
        placeOfDispatch.address.map(address => summaryListRowBuilder("viewMovement.delivery.placeOfDispatch.address", renderAddress(address)))
      )
    }

    val optConsigneeSummaryCards = movementResponse.consigneeTrader.map { consigneeTrader =>
      Seq(
        consigneeTrader.traderName.map(name => summaryListRowBuilder("viewMovement.delivery.consignee.name", name)),
        consigneeTrader.traderExciseNumber.map(ern => summaryListRowBuilder("viewMovement.delivery.consignee.ern", ern)),
        consigneeTrader.address.map(address => summaryListRowBuilder("viewMovement.delivery.consignee.address", renderAddress(address)))
      )
    }

    val optPlaceOfDestinationCards = movementResponse.deliveryPlaceTrader.map { deliverPlaceTrader =>
      Seq(
        deliverPlaceTrader.traderName.map( name => summaryListRowBuilder("viewMovement.delivery.placeOfDestination.name", name)),
        deliverPlaceTrader.traderExciseNumber.map(ern => summaryListRowBuilder("viewMovement.delivery.placeOfDestination.ern", ern)),
        deliverPlaceTrader.address.map(address => summaryListRowBuilder("viewMovement.delivery.placeOfDestination.address", renderAddress(address)))
      )
    }

    HtmlFormat.fill(Seq(
      h2("viewMovement.delivery.title", "govuk-heading-l"),
      overviewPartial(
        headingMessageKey = Some("viewMovement.delivery.consignor"),
        headingMessageClass = "govuk-heading-m",
        cardTitleMessageKey = None,
        summaryListRows = consignorSummaryCards.flatten
      ),
      optPlaceOfDispatchSummaryCards.map { placeOfDispatchSummaryCards =>
        overviewPartial(
          headingMessageKey = Some("viewMovement.delivery.placeOfDispatch"),
          headingMessageClass = "govuk-heading-m",
          cardTitleMessageKey = None,
          summaryListRows = placeOfDispatchSummaryCards.flatten
        )
      }.getOrElse(Html("")),
      optConsigneeSummaryCards.map { consigneeSummaryCards =>
        overviewPartial(
          headingMessageKey = Some("viewMovement.delivery.consignee"),
          headingMessageClass = "govuk-heading-m",
          cardTitleMessageKey = None,
          summaryListRows = consigneeSummaryCards.flatten
        )
      }.getOrElse(Html("")),
      optPlaceOfDestinationCards.map { placeOfDestinationCards =>
        overviewPartial(
          headingMessageKey = Some("viewMovement.delivery.placeOfDestination"),
          headingMessageClass = "govuk-heading-m",
          cardTitleMessageKey = None,
          summaryListRows = placeOfDestinationCards.flatten
        )
      }.getOrElse(Html(""))
    ))
  }
}
