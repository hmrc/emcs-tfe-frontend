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

import models.TransportUnitType
import models.common.Enumerable
import models.response.emcsTfe.GetMovementResponse
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import utils.ExpectedDateOfArrival
import viewmodels.govuk.TagFluency
import viewmodels.helpers.SummaryListHelper._
import views.html.components.{h2, h3, summaryCard}

import javax.inject.{Inject, Singleton}

@Singleton
class ViewMovementTransportHelper @Inject()(
                                             h2: h2,
                                             h3: h3,
                                             summaryCard: summaryCard
                                           ) extends ExpectedDateOfArrival with TagFluency {

  def constructMovementTransport(movement: GetMovementResponse)(implicit messages: Messages): Html = {
    val notProvidedMessage = messages("viewMovement.transport.transportUnit.notProvided")

    HtmlFormat.fill(Seq(
      h2(messages("viewMovement.transport.h2"), "govuk-heading-l"),
      summaryCard(
        None,
        Seq(
          summaryListRowBuilder(
            "viewMovement.transport.summary.transportArranger",
            movement.headerEadEsad.transportArrangement.messageKey
          ),
          summaryListRowBuilder(
            "viewMovement.transport.summary.modeOfTransport",
            movement.transportMode.transportModeCode.messageKey
          ),
          summaryListRowBuilder("viewMovement.transport.summary.journeyTime", movement.journeyTime)
        )
      ),
      h3("viewMovement.transport.firstTransporter"),
      summaryCard(
        None,
        Seq(
          summaryListRowBuilder(
            "viewMovement.transport.firstTransporter.name",
            movement.firstTransporterTrader.flatMap(_.traderName).getOrElse(notProvidedMessage)
          ),
          summaryListRowBuilder(
            "viewMovement.transport.firstTransporter.address",
            movement.firstTransporterTrader.flatMap(_.address).map(renderAddress).getOrElse(Html(notProvidedMessage))
          ),
          summaryListRowBuilder(
            "viewMovement.transport.firstTransporter.vrn",
            movement.firstTransporterTrader.flatMap(_.vatNumber).getOrElse(notProvidedMessage)
          )
        )
      ),
      h3("viewMovement.transport.transportUnits")
    ) ++
      movement.transportDetails.zipWithIndex.map{
        case(transport, index) =>
          summaryCard(
            card = Some(Card(
              Some(CardTitle(
                Text(messages("viewMovement.transport.transportUnit.heading", index + 1)),
                headingLevel = Some(4)
              ))
            )),
            summaryListRows = Seq(
              summaryListRowBuilder(
                "viewMovement.transport.transportUnit.unitType",
                implicitly[Enumerable[TransportUnitType]].withName(transport.transportUnitCode) match {
                  case Some(transportItem) => messages(transportItem.messageKey)
                  case None => messages("viewMovement.transport.transportUnit.notProvided") //TODO confirm text here
                }
              ),
              summaryListRowBuilder(
                "viewMovement.transport.transportUnit.identity",
                transport.identityOfTransportUnits.getOrElse(messages("viewMovement.transport.transportUnit.notProvided"))
              ),
              summaryListRowBuilder(
                "viewMovement.transport.transportUnit.commercialSeal",
                transport.commercialSealIdentification.getOrElse(messages("viewMovement.transport.transportUnit.notProvided"))
              ),
              summaryListRowBuilder(
                "viewMovement.transport.transportUnit.complementaryInformation",
                transport.complementaryInformation.getOrElse(messages("viewMovement.transport.transportUnit.notProvided"))
              ),
              summaryListRowBuilder(
                "viewMovement.transport.transportUnit.sealInformation",
                transport.sealInformation.getOrElse(messages("viewMovement.transport.transportUnit.notProvided"))
              )
            )
          )
      }
    )
  }

}
