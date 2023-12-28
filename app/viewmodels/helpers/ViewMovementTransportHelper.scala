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
import models.common.{AddressModel, Enumerable}
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

class ViewMovementTransportHelper @Inject()(h2: h2,
                                            summaryCard: summaryCard,
                                           ) extends ExpectedDateOfArrival with TagFluency {


  def constructMovementTransport(movement: GetMovementResponse)(implicit messages: Messages): Html = {
    val notProvidedMessage = messages("viewMovement.transport.transportUnit.notProvided")

    HtmlFormat.fill(Seq(
      h2(messages("viewMovement.transport.h2")),
      summaryCard(
        Card(
          Some(CardTitle(Text(messages("viewMovement.transport.summary.heading")))),
        ),
        Seq(
          summaryListRowBuilder(
            "viewMovement.transport.summary.transportArranger",
            movement.headerEadEsad.transportArrangement.messageKey
          ),
          summaryListRowBuilder(
            "viewMovement.transport.summary.modeOfTransport",
            movement.transportMode.transportModeCode.messageKey
          ),
          summaryListRowBuilder("viewMovement.transport.summary.journeyTime", movement.journeyTime),
        )
      ),
      summaryCard(
        Card(
          Some(CardTitle(Text(messages("viewMovement.transport.firstTransporter.heading")))),
        ),
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

          ),
        )
      )
    ) ++
      movement.transportDetails.zipWithIndex.map{
        case(transport, index) =>
          summaryCard(
            card = Card(
              Some(CardTitle(Text(messages("viewMovement.transport.transportUnit.heading", index + 1)))),
            ),
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
              ),
            )
          )
      }
    )
  }

  private def summaryListRowBuilder(key: String, value: String)(implicit messages: Messages) = SummaryListRow(
    key = Key(Text(value = messages(key))),
    value = Value(Text(value = messages(value))),
    classes = "govuk-summary-list__row"
  )

  private def summaryListRowBuilder(key: String, value: Html)(implicit messages: Messages) = SummaryListRow(
    key = Key(Text(value = messages(key))),
    value = Value(HtmlContent(value)),
    classes = "govuk-summary-list__row"
  )

  private def renderAddress(address: AddressModel): Html = {
    val firstLineOfAddress = (address.streetNumber, address.street) match {
      case (Some(propertyNumber), Some(street)) => Html(s"$propertyNumber $street <br>")
      case (Some(number), None) => Html(s"$number <br>")
      case (None, Some(street)) => Html(s"$street <br>")
      case _ => Html("")
    }
    val city = address.city.fold(Html(""))(city => Html(s"$city <br>"))
    val postCode = address.postcode.fold(Html(""))(postcode => Html(s"$postcode"))

    HtmlFormat.fill(Seq(
      firstLineOfAddress,
      city,
      postCode
    ))
  }
}
