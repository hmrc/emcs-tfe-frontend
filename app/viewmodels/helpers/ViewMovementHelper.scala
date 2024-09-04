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

import config.AppConfig
import models.common.RoleType._
import models.movementScenario.MovementScenario
import models.movementScenario.MovementScenario._
import models.requests.DataRequest
import models.response.emcsTfe.GetMovementResponse
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.http.HeaderCarrier
import utils.ExpectedDateOfArrival
import viewmodels._
import viewmodels.helpers.SummaryListHelper._
import viewmodels.helpers.events.MovementEventHelper
import views.html.components.{h2, p}
import views.html.viewMovement.partials.overview_partial

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ViewMovementHelper @Inject()(
                                    p: p,
                                    h2: h2,
                                    overviewPartial: overview_partial,
                                    viewMovementItemsHelper: ViewMovementItemsHelper,
                                    viewMovementTransportHelper: ViewMovementTransportHelper,
                                    viewMovementGuarantorHelper: ViewMovementGuarantorHelper,
                                    viewMovementOverviewHelper: ViewMovementOverviewHelper,
                                    viewMovementDeliveryHelper: ViewMovementDeliveryHelper,
                                    viewMovementDocumentHelper: ViewMovementDocumentHelper,
                                    itemDetailsCardHelper: ItemDetailsCardHelper,
                                    itemPackagingCardHelper: ItemPackagingCardHelper,
                                    movementEventHelper: MovementEventHelper,
                                    appConfig: AppConfig) extends ExpectedDateOfArrival {

  def movementCard(subNavigationTab: Option[SubNavigationTab], movementResponse: GetMovementResponse)
                  (implicit request: DataRequest[_], messages: Messages, hc: HeaderCarrier, ec: ExecutionContext): Future[Html] =

    subNavigationTab match {
      case Some(Overview) => Future(viewMovementOverviewHelper.constructMovementOverview(movementResponse))
      case Some(Movement) => Future(constructMovementView(movementResponse))
      case Some(Delivery) => Future(viewMovementDeliveryHelper.constructMovementDelivery(movementResponse))
      case Some(Transport) => Future(viewMovementTransportHelper.constructMovementTransport(movementResponse, transportUnitsAreSummaryCards = true))
      case Some(Items) => Future(viewMovementItemsHelper.constructMovementItems(movementResponse))
      case Some(Guarantor) => Future(viewMovementGuarantorHelper.constructMovementGuarantor(movementResponse))
      case Some(Documents) => viewMovementDocumentHelper.constructMovementDocument(movementResponse, isSummaryCard = true)
      case _ => for {
        movement <- Future.successful(constructMovementView(movementResponse))
        delivery <- Future.successful(viewMovementDeliveryHelper.constructMovementDelivery(movementResponse))
        transport <- Future.successful(viewMovementTransportHelper.constructMovementTransport(movementResponse, transportUnitsAreSummaryCards = false))
        guarantor <- Future.successful(viewMovementGuarantorHelper.constructMovementGuarantor(movementResponse, isSummaryCard = false, headingMessageClass = Some("govuk-heading-l")))
        exemptedOrganisation <- Future.successful(movementEventHelper.exemptedOrganisationInformationCard(isLargeHeading = true)(movementResponse, messages))
        export <- Future.successful(movementEventHelper.exportInformationCard(isLargeHeading = true)(movementResponse, messages))
        importInfo <- Future.successful(movementEventHelper.importInformationCard(isLargeHeading = true)(movementResponse, messages))
        sad <- Future.successful(movementEventHelper.sadInformationCard(isSummaryCard = false, isLargeHeading = true)(movementResponse, messages))
        documents <- viewMovementDocumentHelper.constructMovementDocument(movementResponse, isSummaryCard = false)
        items <- Future.successful(constructDetailedItems(movementResponse))
      } yield {
        HtmlFormat.fill(Seq(
          movement,
          delivery,
          transport,
          guarantor,
          exemptedOrganisation,
          export,
          importInfo,
          sad,
          documents,
          items
        ))
      }
    }

  //scalastyle:off method.length
  private[helpers] def constructMovementView(movementResponse: GetMovementResponse)
                                            (implicit request: DataRequest[_], messages: Messages): Html = {

    val movementTypeValue = getMovementTypeForMovementView(movementResponse)

    lazy val eadStatusExplanation = messages(s"viewMovement.movement.summary.eADStatus.explanation.${movementResponse.eadStatus.toString.toLowerCase}")

    val optReceiptStatusMessage = movementResponse.reportOfReceipt.map(ror => messages(s"viewMovement.movement.summary.receiptStatus.${ror.acceptMovement}"))

    //Summary section - start
    val localReferenceNumber = summaryListRowBuilder("viewMovement.movement.summary.lrn", movementResponse.localReferenceNumber)
    val eadStatus = {
      if (movementResponse.eadStatus.toString.equalsIgnoreCase("None")) {
        None
      } else {
        Some(summaryListRowBuilder("viewMovement.movement.summary.eADStatus",
          HtmlFormat.fill(Seq(
            p()(Text(messages(s"viewAllMovements.filters.status.${movementResponse.eadStatus.toString.toLowerCase}")).asHtml),
            p(classes = "govuk-hint govuk-!-margin-top-0")(Text(eadStatusExplanation).asHtml)
          )
          )))
      }
    }
    val receiptStatus: Option[SummaryListRow] = optReceiptStatusMessage.map(statusMessage => summaryListRowBuilder("viewMovement.movement.summary.receiptStatus", statusMessage))
    val movementType: SummaryListRow = summaryListRowBuilder("viewMovement.movement.summary.type", movementTypeValue)
    val movementDirection: SummaryListRow = summaryListRowBuilder(
      "viewMovement.movement.summary.direction",
      s"viewMovement.movement.summary.direction.${if (movementResponse.consignorTrader.traderExciseNumber.contains(request.ern)) "out" else "in"}"
    )
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
          cardTitleMessageKey = None,
          summaryListRows = Seq(
            Some(localReferenceNumber),
            eadStatus,
            receiptStatus,
            Some(movementType),
            Some(movementDirection)
          ).flatten
        ),
        overviewPartial(
          headingMessageKey = Some("viewMovement.movement.timeAndDate"),
          headingLevel = 3,
          headingMessageClass = "govuk-heading-m",
          cardTitleMessageKey = None,
          summaryListRows = Seq(
            Some(dateOfDispatch),
            timeOfDispatch,
            Some(dateOfArrival)
          ).flatten
        ),
        overviewPartial(
          headingMessageKey = Some("viewMovement.movement.invoice"),
          headingLevel = 3,
          headingMessageClass = "govuk-heading-m",
          cardTitleMessageKey = None,
          summaryListRows = Seq(
            Some(invoiceNumber),
            invoiceDateOfIssue
          ).flatten
        )
      )
    )
  }
  //scalastyle:on

  private[helpers] def getDateOfArrivalRow(movementResponse: GetMovementResponse)(implicit messages: Messages) = {
    movementResponse.formattedDateOfArrival.map {
      dateOfArrival => summaryListRowBuilder("viewMovement.movement.timeAndDate.dateOfArrival", dateOfArrival)
    }.getOrElse(summaryListRowBuilder("viewMovement.movement.timeAndDate.predictedArrival", movementResponse.formattedExpectedDateOfArrival))
  }

  //scalastyle:off cyclomatic.complexity line.size.limit
  private[helpers] def getMovementTypeForMovementView(movementResponse: GetMovementResponse)(implicit request: DataRequest[_], messages: Messages): String = {
    (request.userTypeFromErn, MovementScenario.getMovementScenarioFromMovement(movementResponse)) match {
      case (_, ReturnToThePlaceOfDispatchOfTheConsignor) =>
        messages("viewMovement.movement.summary.type.returnToThePlaceOfDispatchOfTheConsignor")

      case (XIWK, destinationType@(UkTaxWarehouse.GB | UkTaxWarehouse.NI | EuTaxWarehouse | DirectDelivery | RegisteredConsignee | TemporaryRegisteredConsignee | ExemptedOrganisation | UnknownDestination)) =>
        movementResponse.placeOfDispatchTrader match {
          case Some(placeOfDispatch) => placeOfDispatch.traderExciseNumber match {
            case Some(dispatchErn) =>
              if (isGB(dispatchErn)) {
                messages("viewMovement.movement.summary.type.gbTaxWarehouseTo", messages(s"viewMovement.movement.summary.type.2.$destinationType"))
              } else if (isXI(dispatchErn)) {
                messages("viewMovement.movement.summary.type.niTaxWarehouseTo", messages(s"viewMovement.movement.summary.type.2.$destinationType"))
              } else {
                messages("viewMovement.movement.summary.type.nonUkMovementTo", messages(s"viewMovement.movement.summary.type.2.$destinationType"))
              }
            case None =>
              logger.info(s"[constructMovementView] Missing place of dispatch ERN for $XIWK")
              messages("viewMovement.movement.summary.type.movementTo", messages(s"viewMovement.movement.summary.type.2.$destinationType"))
          }
          case None =>
            logger.info(s"[constructMovementView] Missing place of dispatch trader for $XIWK")
            messages("viewMovement.movement.summary.type.movementTo", messages(s"viewMovement.movement.summary.type.2.$destinationType"))
        }

      case (GBRC | XIRC, destinationType) =>
        messages("viewMovement.movement.summary.type.importFor", messages(s"viewMovement.movement.summary.type.2.$destinationType"))

      case (GBWK | XIWK, destinationType@(ExportWithCustomsDeclarationLodgedInTheUk | ExportWithCustomsDeclarationLodgedInTheEu)) =>
        messages(s"viewMovement.movement.summary.type.$destinationType")

      case (GBWK, destinationType) if movementResponse.isBeingViewedByConsignor =>
        messages("viewMovement.movement.summary.type.gbTaxWarehouseTo", messages(s"viewMovement.movement.summary.type.2.$destinationType"))

      case (GBWK, _) if movementResponse.isBeingViewedByConsignee =>
        messages("viewMovement.movement.summary.type.movementToGbTaxWarehouse")

      case (XIPA, destinationType@(CertifiedConsignee | TemporaryCertifiedConsignee | ReturnToThePlaceOfDispatchOfTheConsignor)) =>
        messages(s"viewMovement.movement.summary.type.XIPA", messages(s"viewMovement.movement.summary.type.2.$destinationType"))

      case (XIPC, destinationType@(CertifiedConsignee | TemporaryCertifiedConsignee | ReturnToThePlaceOfDispatchOfTheConsignor)) =>
        messages(s"viewMovement.movement.summary.type.XIPC", messages(s"viewMovement.movement.summary.type.2.$destinationType"))

      case (XIPB, (CertifiedConsignee | TemporaryCertifiedConsignee | ReturnToThePlaceOfDispatchOfTheConsignor)) =>
        messages(s"viewMovement.movement.summary.type.XIPB")

      case (XIPD, (CertifiedConsignee | TemporaryCertifiedConsignee | ReturnToThePlaceOfDispatchOfTheConsignor)) =>
        messages(s"viewMovement.movement.summary.type.XIPD")

      case (XITC, _) =>
        messages("viewMovement.movement.summary.type.XITC")

      case (userType, destinationType) =>
        logger.warn(s"[constructMovementView] catch-all UserType and movement scenario combination for MOV journey: $userType | $destinationType")
        messages("viewMovement.movement.summary.type.movementTo", messages(s"viewMovement.movement.summary.type.2.$destinationType"))
    }
  }
  //scalastyle:on

  private[helpers] def constructDetailedItems(movement: GetMovementResponse)(implicit messages: Messages): Html = {
    HtmlFormat.fill(Seq(
      Seq(h2("viewMovement.movement.items", classes = "govuk-heading-l")),
      movement.items.zipWithIndex.flatMap { case (item, i) =>
        val packagingHtml: Seq[Html] = item.packaging.zipWithIndex.map { case (packaging, i) =>
          HtmlFormat.fill(Seq(
            overviewPartial(
              headingMessageKey = if (i == 0) {
                Some(messages("viewMovement.movement.items.packagingTitleSingle"))
              }
              else {
                Some(messages("viewMovement.movement.items.packagingTitlePlural", i + 1))
              },
              headingLevel = 4,
              headingMessageClass = "govuk-heading-s",
              cardTitleMessageKey = None,
              summaryListRows = itemPackagingCardHelper.constructPackagingTypeCard(packaging)
            )
          ))
        }
        Seq(overviewPartial(
          headingMessageKey = Some(messages("viewMovement.movement.items.itemTitle", i + 1)),
          headingLevel = 3,
          headingMessageClass = "govuk-heading-m",
          cardTitleMessageKey = None,
          summaryListRows = itemDetailsCardHelper.constructItemDetailsCard(item, cnCodeHasLink = false),
        )) ++ packagingHtml
      },
    ).flatten
    )
  }

}
