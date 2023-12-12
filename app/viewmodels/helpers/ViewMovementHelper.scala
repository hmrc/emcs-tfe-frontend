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

import models.common.RoleType
import models.common.RoleType.{GBRC, GBWK, XIRC, XIWK}
import models.movementScenario.MovementScenario
import models.movementScenario.MovementScenario._
import models.common.AddressModel
import models.requests.DataRequest
import models.response.emcsTfe.GetMovementResponse
import models.response.{InvalidUserTypeException, MissingDispatchPlaceTraderException}
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Key, SummaryListRow}
import utils.ExpectedDateOfArrival
import viewmodels._
import views.html.components.{list, p}
import viewmodels.{Items, Overview, SubNavigationTab}
import views.html.components.list
import views.html.viewMovement.partials.overview_partial

import javax.inject.Inject

class ViewMovementHelper @Inject()(
                                    list: list,
                                    p: p,
                                    overviewPartial: overview_partial,
                                    viewMovementItemsHelper: ViewMovementItemsHelper
                                  ) extends ExpectedDateOfArrival {

  def movementCard(subNavigationTab: SubNavigationTab, movementResponse: GetMovementResponse)
                  (implicit request: DataRequest[_], messages: Messages): Html =

    subNavigationTab match {
      case Overview => constructMovementOverview(movementResponse)
      case Movement => constructMovementView(movementResponse)
      case Delivery => constructMovementDelivery(movementResponse)
      case Items    => viewMovementItemsHelper.constructMovementItems(movementResponse)
      case _ => Html("")
    }

  private[helpers] def summaryListRowBuilder(key: String, value: String)(implicit messages: Messages) = SummaryListRow(
    key = Key(Text(value = messages(key))),
    value = Value(Text(value = messages(value))),
    classes = "govuk-summary-list__row"
  )

  private[helpers] def summaryListRowBuilder(key: String, value: HtmlContent)(implicit messages: Messages) = SummaryListRow(
    key = Key(Text(value = messages(key))),
    value = Value(value),
    classes = "govuk-summary-list__row"
  )

  private[helpers] def constructMovementOverview(movementResponse: GetMovementResponse)
                                                (implicit messages: Messages): HtmlContent = {

    val localReferenceNumber = summaryListRowBuilder("viewMovement.overview.lrn", movementResponse.localReferenceNumber)

    val eadStatus = summaryListRowBuilder("viewMovement.overview.eadStatus", movementResponse.eadStatus)

    val dateOfDispatch = summaryListRowBuilder("viewMovement.overview.dateOfDispatch", movementResponse.formattedDateOfDispatch)

    val expectedDate = summaryListRowBuilder("viewMovement.overview.journeyTime", movementResponse.formattedExpectedDateOfArrival)

    val consignor = summaryListRowBuilder("viewMovement.overview.consignor", movementResponse.consignorTrader.traderExciseNumber)

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
          localReferenceNumber,
          eadStatus,
          dateOfDispatch,
          expectedDate,
          consignor,
          itemCount,
          transportingVehicles
        )
      )

  }

  private[helpers] def constructMovementView(movementResponse: GetMovementResponse)
                                            (implicit request: DataRequest[_], messages: Messages): HtmlContent = {


    val userRole = RoleType.fromExciseRegistrationNumber(request.ern)

    val movementTypeValue = getMovementTypeForMovementView(movementResponse)

    lazy val eadStatusExplanation = messages(s"viewMovement.movement.summary.eADStatus.explanation.${movementResponse.eadStatus.toLowerCase}")

    val optReceiptStatusMessage = movementResponse.reportOfReceipt.map(ror => messages(s"viewMovement.movement.summary.receiptStatus.${ror.acceptMovement}"))

    //Summary section - start
    val localReferenceNumber = summaryListRowBuilder("viewMovement.movement.summary.lrn", movementResponse.localReferenceNumber)
    val eadStatus = if (movementResponse.eadStatus.equalsIgnoreCase("None")) None else Some(summaryListRowBuilder("viewMovement.movement.summary.eADStatus", HtmlContent(
      HtmlFormat.fill(Seq(
        p()(Text(movementResponse.eadStatus).asHtml),
        p(classes = "govuk-hint govuk-!-margin-top-0")(Text(eadStatusExplanation).asHtml)
      )
    ))))
    val receiptStatus = optReceiptStatusMessage.map(statusMessage => summaryListRowBuilder("viewMovement.movement.summary.receiptStatus", statusMessage))
    val movementType = summaryListRowBuilder("viewMovement.movement.summary.type", movementTypeValue)
    val movementDirection = summaryListRowBuilder("viewMovement.movement.summary.direction", if(userRole.isConsignor) "viewMovement.movement.summary.direction.out" else "viewMovement.movement.summary.direction.in")
    //Summary section - end

    //Time and data section - start
    val dateOfDispatch = summaryListRowBuilder("viewMovement.movement.timeAndDate.dateOfDispatch", movementResponse.formattedDateOfDispatch)
    val timeOfDispatch = movementResponse.eadEsad.formattedTimeOfDispatch.map(summaryListRowBuilder("viewMovement.movement.timeAndDate.timeOfDispatch", _))
    val dateOfArrival = getDateOfArrivalRow(movementResponse)
    //Time and data section - end

    //Invoice section - start
    val invoiceNumber = summaryListRowBuilder("viewMovement.movement.invoice.reference", movementResponse.eadEsad.invoiceNumber)
    val invoiceDateOfIssue = movementResponse.eadEsad.formattedInvoiceDate.map(summaryListRowBuilder("viewMovement.movement.invoice.dateOfIssue", _))
    //Invoice section - end


    HtmlFormat.fill(
        Seq(
          overviewPartial(
            headingMessageKey = Some("viewMovement.movement.title"),
            cardTitleMessageKey = "viewMovement.movement.summary",
            summaryListRows = Seq(
              Some(localReferenceNumber),
              eadStatus,
              receiptStatus,
              Some(movementType),
              Some(movementDirection)
            ).flatten
          ),
          overviewPartial(
            headingMessageKey = None,
            cardTitleMessageKey = "viewMovement.movement.timeAndDate",
            summaryListRows = Seq(
              Some(dateOfDispatch),
              timeOfDispatch,
              Some(dateOfArrival)
            ).flatten
          ),
          overviewPartial(
            headingMessageKey = None,
            cardTitleMessageKey = "viewMovement.movement.invoice",
            summaryListRows = Seq(
              Some(invoiceNumber),
              invoiceDateOfIssue
            ).flatten
          )
        )
      )
  }

  private[helpers] def getDateOfArrivalRow(movementResponse: GetMovementResponse)(implicit messages: Messages) = {
    movementResponse.formattedDateOfArrival.map {
      dateOfArrival => summaryListRowBuilder("viewMovement.movement.timeAndDate.dateOfArrival", dateOfArrival)
    }.getOrElse(summaryListRowBuilder("viewMovement.movement.timeAndDate.predictedArrival", movementResponse.formattedExpectedDateOfArrival))
  }

  private[helpers] def getMovementTypeForMovementView(movementResponse: GetMovementResponse)(implicit request: DataRequest[_], messages: Messages): String = {
    (request.userTypeFromErn, MovementScenario.getMovementScenarioFromMovement(movementResponse)) match {
      case (GBWK, GbTaxWarehouse) =>
        messages("viewMovement.movement.summary.type.gbTaxWarehouseTo", messages(s"viewMovement.movement.summary.type.1.$GbTaxWarehouse"))

      case (XIWK, destinationType@(GbTaxWarehouse | EuTaxWarehouse | DirectDelivery | RegisteredConsignee | TemporaryRegisteredConsignee | ExemptedOrganisation | UnknownDestination)) =>
        movementResponse.placeOfDispatchTrader.map(placeOfDispatchTrader => RoleType.fromExciseRegistrationNumber(placeOfDispatchTrader.traderExciseNumber)) match {
          case Some(value) =>
            val countryCodePrefix = value.countryCode
            messages("viewMovement.movement.summary.type.dispatchPlaceTo", messages(s"viewMovement.movement.summary.type.dispatchPlace.$countryCodePrefix"), messages(s"viewMovement.movement.summary.type.2.$destinationType"))
          case None =>
            logger.error(s"[constructMovementView] Missing place of dispatch trader for $XIWK")
            throw MissingDispatchPlaceTraderException(s"[constructMovementView][getMovementTypeForMovementView] Missing place of dispatch trader for $XIWK")
        }

      case (GBRC | XIRC, destinationType) =>
        messages("viewMovement.movement.summary.type.importFor", messages(s"viewMovement.movement.summary.type.2.$destinationType"))

      case (GBWK | XIWK, destinationType@(ExportWithCustomsDeclarationLodgedInTheUk | ExportWithCustomsDeclarationLodgedInTheEu)) =>
        messages(s"viewMovement.movement.summary.type.2.$destinationType")

      case (userType, destinationType) =>
        logger.error(s"[constructMovementView] invalid UserType and movement scenario combination for MOV journey: $userType | $destinationType")
        throw InvalidUserTypeException(s"[ViewMovementHelper][constructMovementView][getMovementTypeForMovementView] invalid UserType and movement scenario combination for MOV journey: $userType | $destinationType")
    }
  }

private[helpers] def constructMovementDelivery(movementResponse: GetMovementResponse)(implicit messages: Messages): HtmlContent = {
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

private[helpers] def renderAddress(address: AddressModel): Html = {
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
