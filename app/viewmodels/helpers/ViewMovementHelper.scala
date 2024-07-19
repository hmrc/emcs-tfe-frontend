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
import models.common.RoleType
import models.common.RoleType._
import models.movementScenario.MovementScenario
import models.movementScenario.MovementScenario._
import models.requests.DataRequest
import models.response.InvalidUserTypeException
import models.response.emcsTfe.GetMovementResponse
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.http.HeaderCarrier
import utils.ExpectedDateOfArrival
import viewmodels._
import viewmodels.helpers.SummaryListHelper._
import views.html.components.p
import views.html.viewMovement.partials.overview_partial

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class ViewMovementHelper @Inject()(
                                    p: p,
                                    overviewPartial: overview_partial,
                                    viewMovementItemsHelper: ViewMovementItemsHelper,
                                    viewMovementTransportHelper: ViewMovementTransportHelper,
                                    viewMovementGuarantorHelper: ViewMovementGuarantorHelper,
                                    viewMovementOverviewHelper: ViewMovementOverviewHelper,
                                    viewMovementDeliveryHelper: ViewMovementDeliveryHelper,
                                    viewMovementDocumentHelper: ViewMovementDocumentHelper,
                                    appConfig: AppConfig) extends ExpectedDateOfArrival {

  def movementCard(subNavigationTab: SubNavigationTab, movementResponse: GetMovementResponse)
                  (implicit request: DataRequest[_], messages: Messages, hc: HeaderCarrier, ec: ExecutionContext): Future[Html] =

    subNavigationTab match {
      case Overview => Future(viewMovementOverviewHelper.constructMovementOverview(movementResponse))
      case Movement => Future(constructMovementView(movementResponse))
      case Delivery => Future(viewMovementDeliveryHelper.constructMovementDelivery(movementResponse))
      case Transport => Future(viewMovementTransportHelper.constructMovementTransport(movementResponse))
      case Items => Future(viewMovementItemsHelper.constructMovementItems(movementResponse))
      case Guarantor => Future(viewMovementGuarantorHelper.constructMovementGuarantor(movementResponse))
      case Documents => viewMovementDocumentHelper.constructMovementDocument(movementResponse)
      case _ => Future(Html(""))
    }

  //scalastyle:off method.length
  private[helpers] def constructMovementView(movementResponse: GetMovementResponse)
                                            (implicit request: DataRequest[_], messages: Messages): Html = {

    val userRole = RoleType.fromExciseRegistrationNumber(request.ern)

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
            p()(Text(movementResponse.eadStatus.toString).asHtml),
            p(classes = "govuk-hint govuk-!-margin-top-0")(Text(eadStatusExplanation).asHtml)
          )
          )))
      }
    }
    val receiptStatus = optReceiptStatusMessage.map(statusMessage => summaryListRowBuilder("viewMovement.movement.summary.receiptStatus", statusMessage))
    val movementType = movementTypeValue.map(mov => summaryListRowBuilder("viewMovement.movement.summary.type", mov))
    val movementDirection = summaryListRowBuilder(
      "viewMovement.movement.summary.direction",
      s"viewMovement.movement.summary.direction.${if (userRole.isConsignor(appConfig)) "out" else "in"}"
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
            movementType,
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
  private[helpers] def getMovementTypeForMovementView(movementResponse: GetMovementResponse)(implicit request: DataRequest[_], messages: Messages): Option[String] = {
    Try((request.userTypeFromErn, MovementScenario.getMovementScenarioFromMovement(movementResponse)) match {
      case (GBWK, taxWarehouse@(UkTaxWarehouse.GB | UkTaxWarehouse.NI)) =>
        messages("viewMovement.movement.summary.type.gbTaxWarehouseTo", messages(s"viewMovement.movement.summary.type.1.$taxWarehouse"))

      case (XIWK, destinationType@(UkTaxWarehouse.GB | UkTaxWarehouse.NI | EuTaxWarehouse | DirectDelivery | RegisteredConsignee | TemporaryRegisteredConsignee | ExemptedOrganisation | UnknownDestination)) =>
        movementResponse.placeOfDispatchTrader match {
          case Some(placeOfDispatch) => placeOfDispatch.traderExciseNumber match {
            case Some(dispatchErn) =>
              if(isGB(dispatchErn)) {
                messages("viewMovement.movement.summary.type.gbTaxWarehouseTo", messages(s"viewMovement.movement.summary.type.2.$destinationType"))
              } else if(isXI(dispatchErn)) {
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
        messages(s"viewMovement.movement.summary.type.2.$destinationType")

      case (XIPA | XIPC, destinationType@(CertifiedConsignee | TemporaryCertifiedConsignee | ReturnToThePlaceOfDispatchOfTheConsignor)) =>
        messages(s"viewMovement.movement.summary.type.dutyPaid.$destinationType")

      case (userType, destinationType) =>
        logger.error(s"[constructMovementView][${PagerDutyTrigger.invalidUserType}] invalid UserType and movement scenario combination for MOV journey: $userType | $destinationType")
        throw InvalidUserTypeException(s"[ViewMovementHelper][constructMovementView][getMovementTypeForMovementView] invalid UserType and movement scenario combination for MOV journey: $userType | $destinationType")
    }).toOption
  }
  //scalastyle:on

}
