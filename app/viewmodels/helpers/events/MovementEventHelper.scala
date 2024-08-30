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

package viewmodels.helpers.events

import models.DocumentType
import models.common.DestinationType._
import models.common.{AcceptMovement, DestinationType, OriginType, RoleType, TraderModel, WrongWithMovement}
import models.requests.DataRequest
import models.response.emcsTfe.customsRejection.NotificationOfCustomsRejectionModel
import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import models.response.emcsTfe.reportOfReceipt.{IE818ItemModelWithCnCodeInformation, UnsatisfactoryModel}
import models.response.emcsTfe._
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.Details
import uk.gov.hmrc.govukfrontend.views.html.components.GovukDetails
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Empty, HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import utils.DateUtils
import viewmodels.govuk.all.{ActionItemViewModel, FluentActionItem}
import viewmodels.helpers.SummaryListHelper._
import viewmodels.helpers._
import views.ViewUtils
import views.html.components.{bullets, h2, list, p}
import views.html.viewMovement.partials.overview_partial

import javax.inject.{Inject, Singleton}

//noinspection ScalaStyle TODO: Maybe consider splitting this out, although it's already split to be in this helper...
@Singleton
class MovementEventHelper @Inject()(
                                     h2: h2,
                                     list: list,
                                     overview_partial: overview_partial,
                                     govukDetails: GovukDetails,
                                     transportCardHelper: ViewMovementTransportHelper,
                                     guarantorCardHelper: ViewMovementGuarantorHelper,
                                     itemDetailsCardHelper: ItemDetailsCardHelper,
                                     packagingCardHelper: ItemPackagingCardHelper,
                                     bullets: bullets,
                                     p: p
                                   ) extends DateUtils {

  private def buildOverviewPartial(
                                    headingId: Option[String] = None,
                                    headingLevel: Int = 2,
                                    headingTitle: Option[String] = None,
                                    headingMessageClass: String = "govuk-heading-m govuk-!-margin-top-9",
                                    cardTitleMessageKey: Option[String] = None,
                                    cardTitleMessageArgs: Seq[String] = Seq.empty,
                                    cardAction: Option[ActionItem] = None,
                                    cardTitleHeadingLevel: Option[Int] = None,
                                    cardFooterHtml: Option[Html] = None,
                                    summaryListRows: Seq[Option[SummaryListRow]],
                                    summaryListAttributes: Map[String, String] = Map.empty)(implicit messages: Messages): Html = {
    overview_partial(
      headingId = headingId,
      headingLevel = headingLevel,
      headingMessageKey = headingTitle,
      headingMessageClass = headingMessageClass,
      cardTitleMessageKey = cardTitleMessageKey,
      cardTitleMessageArgs = cardTitleMessageArgs,
      cardAction = cardAction,
      cardTitleHeadingLevel = cardTitleHeadingLevel,
      cardFooterHtml = cardFooterHtml,
      summaryListRows = summaryListRows.flatten,
      summaryListAttributes = summaryListAttributes
    )
  }

  // TODO: test
  private[events] def getOriginTypeForMovementInformationCard()(implicit movement: GetMovementResponse, messages: Messages): String = {
    // TODO: check what to do about non-WK consignors
    def consignorIsGBWK = movement.consignorTrader.traderExciseNumber.exists(RoleType.fromExciseRegistrationNumber(_) == RoleType.GBWK)
    def consignorIsXIWK = movement.consignorTrader.traderExciseNumber.exists(RoleType.fromExciseRegistrationNumber(_) == RoleType.XIWK)

    def consignorIsWarehouseKeeperAndNotGBOrXI = movement.consignorTrader.traderExciseNumber.exists(RoleType.isWarehouseKeeper) && (!consignorIsGBWK && !consignorIsXIWK)

    // TODO: check if this is against the correct ERN
    def dispatchIsGB: Boolean = movement.placeOfDispatchTrader.flatMap(_.traderExciseNumber).exists(RoleType.isGB)
    def dispatchIsXI: Boolean = movement.placeOfDispatchTrader.flatMap(_.traderExciseNumber).exists(RoleType.isXI)

    movement.eadEsad.originTypeCode match {
      case OriginType.TaxWarehouse if consignorIsGBWK =>
        messages(s"movementCreatedView.section.movement.originType.${OriginType.TaxWarehouse}.inGB")
      case OriginType.TaxWarehouse if consignorIsXIWK && dispatchIsGB =>
        messages(s"movementCreatedView.section.movement.originType.${OriginType.TaxWarehouse}.inGB")
      case OriginType.TaxWarehouse if consignorIsXIWK && dispatchIsXI =>
        messages(s"movementCreatedView.section.movement.originType.${OriginType.TaxWarehouse}.inXI")
      case OriginType.TaxWarehouse if consignorIsWarehouseKeeperAndNotGBOrXI =>
        messages(s"movementCreatedView.section.movement.originType.${OriginType.TaxWarehouse}.inEU")
      case _ =>
        messages(s"movementCreatedView.section.movement.originType.${movement.eadEsad.originTypeCode}")
    }
  }

  // TODO: test
  private[events] def getDestinationTypeForMovementInformationCard()(implicit movement: GetMovementResponse, messages: Messages): String = {
    def consigneeIsGB = movement.consigneeTrader.exists(_.traderExciseNumber.exists(RoleType.isGB))
    def consigneeIsXI = movement.consigneeTrader.exists(_.traderExciseNumber.exists(RoleType.isXI))
    def consigneeIsNotGBOrXI = !consigneeIsGB && !consigneeIsXI

    movement.headerEadEsad.destinationType match {
      case DestinationType.TaxWarehouse if consigneeIsGB =>
        messages(s"movementCreatedView.section.movement.destinationType.${DestinationType.TaxWarehouse}.inGB")
      case DestinationType.TaxWarehouse if consigneeIsXI =>
        messages(s"movementCreatedView.section.movement.destinationType.${DestinationType.TaxWarehouse}.inXI")
      case DestinationType.TaxWarehouse if consigneeIsNotGBOrXI =>
        messages(s"movementCreatedView.section.movement.destinationType.${DestinationType.TaxWarehouse}.inEU")
      case _ =>
        messages(s"movementCreatedView.section.movement.destinationType.${movement.headerEadEsad.destinationType}")
    }
  }

  def movementInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    val upstreamArc = movement.eadEsad.upstreamArc.map(summaryListRowBuilder("movementCreatedView.section.movement.replacedArc", _))
    val localReferenceNumber = summaryListRowBuilder("movementCreatedView.section.movement.lrn", movement.localReferenceNumber)
    val originType = summaryListRowBuilder("movementCreatedView.section.movement.originType", getOriginTypeForMovementInformationCard())
    val destinationType = summaryListRowBuilder("movementCreatedView.section.movement.destinationType", getDestinationTypeForMovementInformationCard())
    val dateOfDispatch = summaryListRowBuilder("movementCreatedView.section.movement.dateOfDispatch", movement.formattedDateOfDispatch)
    val timeOfDispatch = summaryListRowBuilder("movementCreatedView.section.movement.timeOfDispatch", movement.eadEsad.formattedTimeOfDispatch.getOrElse(""))
    val invoiceNumber = summaryListRowBuilder("movementCreatedView.section.movement.invoiceReference", movement.eadEsad.invoiceNumber)
    val invoiceDate = summaryListRowBuilder("movementCreatedView.section.movement.invoiceDate", movement.eadEsad.formattedInvoiceDate.getOrElse(""))

    buildOverviewPartial(
      headingTitle = Some("movementCreatedView.section.movement"),
      headingId = Some("movement-information-heading"),
      summaryListRows = Seq(upstreamArc, Some(localReferenceNumber), Some(originType), Some(destinationType), Some(dateOfDispatch), Some(timeOfDispatch), Some(invoiceNumber), Some(invoiceDate)),
      summaryListAttributes = Map("id" -> "movement-information-summary")

    )
  }

  def responseInformation()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    movement.manualClosureResponse.map { response =>
      val date = Seq(response.dateOfArrivalOfExciseProducts.map(date => summaryListRowBuilder("movementHistoryEvent.IE881.dateExciseProductsArrived", date.toLocalDate.formatDateForUIOutput())))
      val sequenceNumber = Seq(Some(summaryListRowBuilder("movementHistoryEvent.IE881.sequenceNumber", response.sequenceNumber.toString)))
      val globalConclusionOfReceipt = Seq(response.globalConclusionOfReceipt.map(reason => summaryListRowBuilder("movementHistoryEvent.IE881.conclusionOfReceipt", s"movementHistoryEvent.IE881.conclusionOfReceipt.${reason.toString}")))
      val moreReceiptInformation = Seq(response.complementaryInformation.map(info => summaryListRowBuilder("movementHistoryEvent.IE881.moreReceiptInformation", info)))
      val reasonCode = Seq(Some(summaryListRowBuilder("movementHistoryEvent.IE881.reasonCode", response.manualClosureRequestReason.toString)))
      val reasonCodeDescription = Seq(Some(summaryListRowBuilder("movementHistoryEvent.IE881.reasonCodeDescription", s"movementHistoryEvent.IE881.reasonCode.${response.manualClosureRequestReason.toString}")))
      val reasonCodeInformation = Seq(response.manualClosureRequestReasonComplement.map(info => summaryListRowBuilder("movementHistoryEvent.IE881.moreReasonInformation", info)))
      val responseStatus = Seq(Some(summaryListRowBuilder("movementHistoryEvent.IE881.responseStatus", s"movementHistoryEvent.IE881.responseStatus.${response.manualClosureRequestAccepted.toString}")))
      val manualClosureRejectionReason = Seq(response.manualClosureRejectionReason.map(reason => summaryListRowBuilder("movementHistoryEvent.IE881.rejectionReason", s"movementHistoryEvent.IE881.rejectionReason.${reason.toString}")))
      val manualClosureRejectionComplement = Seq(response.manualClosureRejectionComplement.map(complement => summaryListRowBuilder("movementHistoryEvent.IE881.moreRejectionInformation", complement)))


      buildOverviewPartial(
        summaryListRows = sequenceNumber ++ date ++ globalConclusionOfReceipt ++ moreReceiptInformation ++ reasonCode ++ reasonCodeDescription ++ reasonCodeInformation ++ responseStatus ++ manualClosureRejectionReason ++ manualClosureRejectionComplement,
        summaryListAttributes = Map("id" -> "manual-closure-response-information-summary")
      )
    }.getOrElse(Html(""))
  }

  def closureDocumentsInformationCard(documentTypes: Seq[DocumentType])(implicit movement: GetMovementResponse, messages: Messages): Html = {
    movement.manualClosureResponse.map(_.supportingDocuments.map { documents =>
      val documentCards = documents.zipWithIndex.map {
        case (document, index) =>
          val documentTypeDescription = document.supportingDocumentType.flatMap(documentType => documentTypes.find(_.code == documentType).map(_.description)).getOrElse("Not provided")
          val documentDescription = if (document.supportingDocumentDescription != None) Seq(Some(summaryListRowBuilder("movementHistoryEvent.IE881.document.description", document.supportingDocumentDescription.getOrElse("")))) else None
          buildOverviewPartial(
            cardTitleMessageKey = Some(messages("movementHistoryEvent.IE881.document", index + 1)),
            summaryListRows = Seq(
              Some(summaryListRowBuilder("movementHistoryEvent.IE881.document.type", documentTypeDescription)),
              Some(summaryListRowBuilder("movementHistoryEvent.IE881.document.reference", document.referenceOfSupportingDocument.getOrElse("")))
            ) ++ documentDescription,
            summaryListAttributes = Map("id" -> s"documents-information-summary-${index + 1}")
          )
      }

      HtmlFormat.fill(
        Seq(
          h2(messages("movementHistoryEvent.IE881.document.heading"), "govuk-heading-m govuk-!-margin-top-9", id = Some("documents-information-heading"))
        ) ++ documentCards
      )

    }.getOrElse(HtmlFormat.fill(
      Seq(
        h2(messages("movementHistoryEvent.IE881.document.heading"), "govuk-heading-m govuk-!-margin-top-9", id = Some("documents-information-heading")),
        p(classes = "govuk-body-m")(Html(
          messages("movementHistoryEvent.IE881.notProvided")
        )),
      )
    ))).getOrElse(Html(""))
  }

  def manualClosureItemsCard(event: MovementHistoryEvent, ie881ItemModelWithCnCodeInformation: Seq[IE881ItemModelWithCnCodeInformation])
                            (implicit request: DataRequest[_], movement: GetMovementResponse, messages: Messages): Html = {
    val items: Seq[Html] = ie881ItemModelWithCnCodeInformation.map {
      case IE881ItemModelWithCnCodeInformation(manualClosureItem, information) =>
        val exciseProductCode = Seq(manualClosureItem.productCode.map(epc => summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.item.epc", epc)))
        val bodyUniqueReference = Seq(Some(summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.item.bodyRecordUniqueReference", manualClosureItem.bodyRecordUniqueReference.toString)))
        val shortageOrExcess = Seq(manualClosureItem.indicatorOfShortageOrExcess.map(shortageOrExcess => summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.item.shortageOrExcess", s"movementHistoryEvent.${event.eventType}.item.shortageOrExcess.${shortageOrExcess.toLowerCase()}")))
        val shortageOrExcessQuantity = Seq(manualClosureItem.observedShortageOrExcess.map(shortageOrExcessQuantity => summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.item.shortageOrExcessQuantity", s"$shortageOrExcessQuantity ${information.unitOfMeasure.toShortFormatMessage()}")))
        val refusedQuantity = Seq(manualClosureItem.refusedQuantity.map(refusedQuantity => summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.item.refusedQuantity", s"$refusedQuantity ${information.unitOfMeasure.toShortFormatMessage()}")))
        val moreInformation = Seq(manualClosureItem.complementaryInformation.map(complementaryInformation => summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.item.moreShortageOrExcessInformation", complementaryInformation)))

        HtmlFormat.fill(
          Seq(
            buildOverviewPartial(
              cardTitleMessageKey = Some(messages(s"movementHistoryEvent.${event.eventType}.item.h3", manualClosureItem.bodyRecordUniqueReference)),
              cardAction = Some(ActionItemViewModel(
                content = Text(messages(s"movementHistoryEvent.${event.eventType}.item.link")),
                href = controllers.routes.ItemDetailsController.onPageLoad(request.ern, movement.arc, manualClosureItem.bodyRecordUniqueReference).url,
                id = s"viewItem-${manualClosureItem.bodyRecordUniqueReference}"
              ).withVisuallyHiddenText(messages(
                s"movementHistoryEvent.${event.eventType}.item.link.hidden",
                manualClosureItem.bodyRecordUniqueReference,
                information.cnCodeDescription
              ))),
              cardTitleHeadingLevel = Some(3),
              summaryListRows = exciseProductCode ++ bodyUniqueReference ++ shortageOrExcess ++ shortageOrExcessQuantity ++ refusedQuantity ++ moreInformation
            )
          )
        )
    }

    HtmlFormat.fill(
      if (items.isEmpty != true) {
        Seq(
          h2(messages(s"movementHistoryEvent.${event.eventType}.response.heading"), "govuk-heading-m govuk-!-margin-top-9")
        ) ++ items
      } else {
        Seq(
          h2(messages(s"movementHistoryEvent.${event.eventType}.response.heading"), "govuk-heading-m govuk-!-margin-top-9"),
          p(classes = "govuk-body-m")(Html(messages("movementHistoryEvent.IE881.notProvided"))),
        )
      }
    )
  }

  def consignorInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    val name = movement.consignorTrader.traderName.map(summaryListRowBuilder("movementCreatedView.section.consignor.name", _))
    val ern = movement.consignorTrader.traderExciseNumber.map(summaryListRowBuilder("movementCreatedView.section.consignor.ern", _))
    val address = movement.consignorTrader.address.map(address => summaryListRowBuilder("movementCreatedView.section.consignor.address", renderAddress(address)))

    buildOverviewPartial(
      headingTitle = Some("movementCreatedView.section.consignor"),
      headingId = Some("consignor-information-heading"),
      summaryListRows = Seq(name, ern, address),
      summaryListAttributes = Map("id" -> "consignor-information-summary")
    )
  }

  def placeOfDispatchInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    movement.placeOfDispatchTrader.map { trader =>
      val name = trader.traderName.map(summaryListRowBuilder("movementCreatedView.section.placeOfDispatch.name", _))
      val ern = trader.traderExciseNumber.map(summaryListRowBuilder("movementCreatedView.section.placeOfDispatch.ern", _))
      val address = trader.address.map(address => summaryListRowBuilder("movementCreatedView.section.placeOfDispatch.address", renderAddress(address)))

      buildOverviewPartial(
        headingTitle = Some("movementCreatedView.section.placeOfDispatch"),
        headingId = Some("place-of-dispatch-information-heading"),
        summaryListRows = Seq(name, ern, address),
        summaryListAttributes = Map("id" -> "place-of-dispatch-information-summary")
      )
    }.getOrElse(Html(""))
  }

  def importInformationCard(isLargeHeading: Boolean = false)(implicit movement: GetMovementResponse, messages: Messages): Html = {
    movement.dispatchImportOfficeReferenceNumber.map { officeCode =>

      buildOverviewPartial(
        headingTitle = Some("movementCreatedView.section.import"),
        headingId = Some("import-information-heading"),
        headingMessageClass = if(isLargeHeading) "govuk-heading-l govuk-!-margin-top-9" else "govuk-heading-m govuk-!-margin-top-9",
        summaryListRows = Seq(
          Some(summaryListRowBuilder("movementCreatedView.section.import.customsOfficeCode", officeCode))
        ),
        summaryListAttributes = Map("id" -> "import-information-summary")
      )
    }.getOrElse(Html(""))
  }

  def consigneeInformationCard(consigneeTrader: Option[TraderModel] = None)(implicit movement: GetMovementResponse, messages: Messages): Html = {
    consigneeTrader.orElse(movement.consigneeTrader).map { consignee =>
      val name = consignee.traderName.map(summaryListRowBuilder("movementCreatedView.section.consignee.name", _))
      val address = consignee.address.map(address => summaryListRowBuilder("movementCreatedView.section.consignee.address", renderAddress(address)))
      val eoriNumber = consignee.eoriNumber.map(summaryListRowBuilder("movementCreatedView.section.consignee.eoriNumber", _))

      val identifier = movement.destinationType match {
        case TaxWarehouse | DirectDelivery | RegisteredConsignee =>
          consignee.traderExciseNumber.map(summaryListRowBuilder("movementCreatedView.section.consignee.ern", _))
        case TemporaryRegisteredConsignee =>
          consignee.traderExciseNumber.map(summaryListRowBuilder("movementCreatedView.section.consignee.identifier.registered", _))
        case TemporaryCertifiedConsignee =>
          consignee.traderExciseNumber.map(summaryListRowBuilder("movementCreatedView.section.consignee.identifier.certified", _))
        case Export =>
          consignee.traderExciseNumber.map(summaryListRowBuilder("movementCreatedView.section.consignee.vatNumber", _))
        case _ => None
      }

      buildOverviewPartial(
        headingTitle = Some("movementCreatedView.section.consignee"),
        headingId = Some("consignee-information-heading"),
        summaryListRows = Seq(name, identifier, address, eoriNumber),
        summaryListAttributes = Map("id" -> "consignee-information-summary")
      )
    }.getOrElse(Html(""))
  }

  def exemptedOrganisationInformationCard(isLargeHeading: Boolean = false)(implicit movement: GetMovementResponse, messages: Messages): Html = {
    val memberState = movement.memberStateCode.map(summaryListRowBuilder("movementCreatedView.section.exemptedConsignee.memberState", _))
    val serialNumber = movement.serialNumberOfCertificateOfExemption.map(summaryListRowBuilder("movementCreatedView.section.exemptedConsignee.serialNumber", _))

    if (memberState.isDefined || serialNumber.isDefined) {
      buildOverviewPartial(
        headingTitle = Some("movementCreatedView.section.exemptedConsignee"),
        headingId = Some("exempted-organisation-information-heading"),
        headingMessageClass = if(isLargeHeading) "govuk-heading-l govuk-!-margin-top-9" else "govuk-heading-m govuk-!-margin-top-9",
        summaryListRows = Seq(memberState, serialNumber),
        summaryListAttributes = Map("id" -> "exempted-organisation-information-summary")
      )
    } else {
      Html("")
    }
  }

  def placeOfDestinationInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    movement.deliveryPlaceTrader.map { trader =>
      val name = trader.traderName.map(summaryListRowBuilder("movementCreatedView.section.placeOfDestination.name", _))
      val ern = trader.traderExciseNumber.map(summaryListRowBuilder("movementCreatedView.section.placeOfDestination.ern", _))
      val vat = trader.vatNumber.map(summaryListRowBuilder("movementCreatedView.section.placeOfDestination.vatNumber", _))
      val address = trader.address.map(address => summaryListRowBuilder("movementCreatedView.section.placeOfDestination.address", renderAddress(address)))

      buildOverviewPartial(
        headingTitle = Some("movementCreatedView.section.placeOfDestination"),
        headingId = Some("place-of-destination-information-heading"),
        summaryListRows = Seq(name, ern, vat, address),
        summaryListAttributes = Map("id" -> "place-of-destination-information-summary")
      )
    }.getOrElse(Html(""))
  }

  def exportInformationCard(isLargeHeading: Boolean = false)(implicit movement: GetMovementResponse, messages: Messages): Html = {
    movement.deliveryPlaceCustomsOfficeReferenceNumber.map { officeCode =>
      buildOverviewPartial(
        headingTitle = Some("movementCreatedView.section.export"),
        headingId = Some("export-information-heading"),
        headingMessageClass = if(isLargeHeading) "govuk-heading-l govuk-!-margin-top-9" else "govuk-heading-m govuk-!-margin-top-9",
        summaryListRows = Seq(
          Some(summaryListRowBuilder("movementCreatedView.section.export.customsOfficeCode", officeCode))
        ),
        summaryListAttributes = Map("id" -> "export-information-summary")
      )
    }.getOrElse(Html(""))
  }

  def guarantorInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    guarantorCardHelper.constructMovementGuarantor(
      movement = movement,
      showNoGuarantorContentIfApplicable = false,
      headingMessageClass = Some("govuk-heading-m govuk-!-margin-top-9")
    )
  }

  def journeyInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    val journeyTime = summaryListRowBuilder("movementCreatedView.section.journey.time", movement.journeyTime)
    val modeOfTransport = summaryListRowBuilder("movementCreatedView.section.journey.mode", movement.transportMode.transportModeCode.messageKey)
    val modeOfTransportInfo = movement.transportMode.complementaryInformation.map(info => summaryListRowBuilder("movementCreatedView.section.journey.info", info))

    buildOverviewPartial(
      headingTitle = Some("movementCreatedView.section.journey"),
      headingId = Some("journey-information-heading"),
      summaryListRows = Seq(
        Some(journeyTime),
        Some(modeOfTransport),
        modeOfTransportInfo
      ),
      summaryListAttributes = Map("id" -> "journey-information-summary")
    )
  }

  def transportArrangerInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    movement.transportArrangerTrader.map { trader =>
      val transportArranger = summaryListRowBuilder("movementCreatedView.section.transportArranger.arranger", movement.headerEadEsad.transportArrangement.messageKey)
      val transportArrangerName = trader.traderName.map(summaryListRowBuilder("movementCreatedView.section.transportArranger.name", _))
      val vatRegistrationNumber = trader.vatNumber.map(summaryListRowBuilder("movementCreatedView.section.transportArranger.vat", _))

      buildOverviewPartial(
        headingTitle = Some("movementCreatedView.section.transportArranger"),
        headingId = Some("transport-arranger-information-heading"),
        summaryListRows = Seq(
          Some(transportArranger),
          transportArrangerName,
          vatRegistrationNumber
        ),
        summaryListAttributes = Map("id" -> "transport-arranger-information-summary")
      )
    }.getOrElse(Html(""))
  }

  def firstTransporterInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    movement.firstTransporterTrader.map { trader =>
      val name = trader.traderName.map(summaryListRowBuilder("movementCreatedView.section.firstTransporter.name", _))
      val vat = trader.vatNumber.map(summaryListRowBuilder("movementCreatedView.section.firstTransporter.vat", _))
      val address = trader.address.map(address => summaryListRowBuilder("movementCreatedView.section.firstTransporter.address", renderAddress(address)))

      buildOverviewPartial(
        headingTitle = Some("movementCreatedView.section.firstTransporter"),
        headingId = Some("first-transporter-information-heading"),
        summaryListRows = Seq(
          name,
          vat,
          address
        ),
        summaryListAttributes = Map("id" -> "first-transporter-information-summary")
      )
    }.getOrElse(Html(""))
  }

  def transportUnitsInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    transportCardHelper.transportUnits(movement, firstHeadingLevelIsH2 = true)
  }

  def itemsInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    val items = movement.items.zipWithIndex.map {
      case (item, index) =>
        implicit val _item = item

        HtmlFormat.fill(
          Seq(
            buildOverviewPartial(
              cardTitleMessageKey = Some(messages("movementCreatedView.section.item.heading", index + 1)),
              cardTitleHeadingLevel = Some(3),
              summaryListRows = Seq(
                itemDetailsCardHelper.brandNameOfProductRow(),
                itemDetailsCardHelper.commercialDescriptionRow(),
                itemDetailsCardHelper.quantityRow(),
                itemDetailsCardHelper.allPackagingQuantitiesAndTypesRow()
              ),
              cardFooterHtml = Some(
                HtmlFormat.fill(
                  Seq(
                    allItemInformationCard(item, index),
                    allItemPackagingInformationCard(item, index)
                  )
                )
              )
            )
          )
        )
    }

    HtmlFormat.fill(
      Seq(
        h2(messages("movementCreatedView.section.items"), "govuk-heading-m govuk-!-margin-top-9")
      ) ++ items
    )

  }

  def sadInformationCard(isSummaryCard: Boolean = true, isLargeHeading: Boolean = false)(implicit movement: GetMovementResponse, messages: Messages): Html = {
    movement.eadEsad.importSadNumber.map { sadNumbers =>

      val sadCards = sadNumbers.zipWithIndex.map {
        case (sad, index) =>
          if(isSummaryCard) {
          buildOverviewPartial(
            cardTitleMessageKey = Some(messages("movementCreatedView.section.sad.heading", index + 1)),
            summaryListRows = Seq(
              Some(summaryListRowBuilder("movementCreatedView.section.sad.importNumber", sad))
            ),
            summaryListAttributes = Map("id" -> s"sad-information-summary-${index + 1}")
          )
          } else {
            buildOverviewPartial(
              headingTitle = Some(messages("movementCreatedView.section.sad.heading", index + 1)),
              headingLevel = 3,
              summaryListRows = Seq(
                Some(summaryListRowBuilder("movementCreatedView.section.sad.importNumber", sad))
              ),
              summaryListAttributes = Map("id" -> s"sad-information-summary-${index + 1}")
            )
          }
      }

      HtmlFormat.fill(
        Seq(
          h2(messages("movementCreatedView.section.sads.heading"), classes = if(isLargeHeading) "govuk-heading-l govuk-!-margin-top-9" else "govuk-heading-m govuk-!-margin-top-9", id = Some("sad-information-heading"))
        ) ++ sadCards
      )

    }.getOrElse(Html(""))
  }

  def documentsInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    movement.documentCertificate.map { documents =>

      val documentCards = documents.zipWithIndex.map {
        case (document, index) =>
          buildOverviewPartial(
            cardTitleMessageKey = Some(messages("movementCreatedView.section.document.heading", index + 1)),
            cardTitleHeadingLevel = Some(3),
            summaryListRows = Seq(
              Some(summaryListRowBuilder("movementCreatedView.section.document.type", document.documentType.getOrElse(""))),
              Some(summaryListRowBuilder("movementCreatedView.section.document.reference", document.documentReference.getOrElse("")))
            ),
            summaryListAttributes = Map("id" -> s"documents-information-summary-${index + 1}")
          )
      }

      HtmlFormat.fill(
        Seq(
          h2(messages("movementCreatedView.section.documents.heading"), "govuk-heading-m govuk-!-margin-top-9", id = Some("documents-information-heading"))
        ) ++ documentCards
      )

    }.getOrElse(Html(""))
  }

  private def allItemInformationCard(item: MovementItem, index: Int)(implicit messages: Messages): Html = {
    implicit val _item = item

    govukDetails(
      Details(
        summary = Text(messages("movementCreatedView.section.item.viewAllInformation", index + 1)),
        classes = "govuk-details govuk-!-margin-bottom-2",
        id = Some(s"item-information-details-${index + 1}"),
        content = HtmlContent(
          buildOverviewPartial(
            summaryListRows = Seq(
              itemDetailsCardHelper.exciseProductCodeRow(),
              itemDetailsCardHelper.commodityCodeRow(),
              itemDetailsCardHelper.netWeightRow(),
              itemDetailsCardHelper.grossWeightRow(),
              itemDetailsCardHelper.alcoholicStrengthRow(),
              itemDetailsCardHelper.degreePlatoRow(),
              itemDetailsCardHelper.maturationAgeRow(),
              itemDetailsCardHelper.densityRow(),
              itemDetailsCardHelper.fiscalMarksPresentRow(),
              itemDetailsCardHelper.fiscalMarkRow(),
              itemDetailsCardHelper.sizeOfProducerRow(),
              itemDetailsCardHelper.designationOfOriginRow(),
              itemDetailsCardHelper.independentSmallProducersDeclarationRow(),
              itemDetailsCardHelper.sizeOfProducerRow(),
              itemDetailsCardHelper.wineProductCategoryRow(),
              itemDetailsCardHelper.wineOperationsRow(),
              itemDetailsCardHelper.wineGrowingZoneCodeRow(),
              itemDetailsCardHelper.thirdCountryOfOriginRow(),
              itemDetailsCardHelper.wineOtherInformationRow()
            )
          )
        )
      )
    )
  }

  private def allItemPackagingInformationCard(item: MovementItem, index: Int)(implicit messages: Messages): Html = {
    govukDetails(
      Details(
        summary = Text(messages("movementCreatedView.section.item.viewAllPackagingInformation", index + 1)),
        classes = "govuk-details govuk-!-margin-top-2",
        id = Some(s"item-packaging-details-${index + 1}"),
        content =
          HtmlContent(
            HtmlFormat.fill(
              item.packaging.zipWithIndex.flatMap {
                case (packaging, index) =>
                  implicit val _packaging = packaging

                  Seq(
                    buildOverviewPartial(
                      headingTitle = Some(messages("itemDetails.packagingTypeCardTitle", index + 1)),
                      headingLevel = 4,
                      headingMessageClass = "govuk-heading-s govuk-!-margin-top-3",
                      summaryListRows = Seq(
                        packagingCardHelper.typeRow(),
                        packagingCardHelper.quantityRow(),
                        packagingCardHelper.identityOfCommercialSealRow(),
                        packagingCardHelper.sealInformationRow(),
                        packagingCardHelper.shippingMarksRow()
                      )
                    )
                  )
              }
            )
          )
      )
    )
  }

  def rorDetailsCard(event: MovementHistoryEvent, onlyArrivalDate: Boolean = false)(implicit movement: GetMovementResponse, messages: Messages): Html = {
    movement.reportOfReceipt match {
      case Some(reportOfReceipt) =>
        val headingTitle: String = if (movement.destinationType == DestinationType.Export) {
          messages(s"movementHistoryEvent.${event.eventType}.rorDetails.h2.export")
        } else {
          messages(s"movementHistoryEvent.${event.eventType}.rorDetails.h2")
        }

        val statusKey: String = if (movement.destinationType == DestinationType.Export) {
          s"movementHistoryEvent.${event.eventType}.rorDetails.status.export"
        } else {
          s"movementHistoryEvent.${event.eventType}.rorDetails.status"
        }

        val dateOfArrival =
          Some(summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.rorDetails.dateOfArrival", reportOfReceipt.dateOfArrival.formatDateForUIOutput()))

        val status =
          Some(summaryListRowBuilder(statusKey, messages(s"movementHistoryEvent.${event.eventType}.rorDetails.status.value.${reportOfReceipt.acceptMovement}")))

        val moreInfo =
          Some(summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.rorDetails.moreInformation", reportOfReceipt.otherInformation.getOrElse(s"movementHistoryEvent.${event.eventType}.rorDetails.moreInformation.notProvided")))

        buildOverviewPartial(
          headingId = None,
          headingTitle = Some(headingTitle),
          cardTitleMessageKey = None,
          cardFooterHtml = None,
          summaryListRows = if (onlyArrivalDate) Seq(dateOfArrival) else Seq(dateOfArrival, status, moreInfo)
        )
      case None => Html("")
    }
  }

  def rorItemsCard(event: MovementHistoryEvent, ie818ItemModelWithCnCodeInformation: Seq[IE818ItemModelWithCnCodeInformation])
                  (implicit request: DataRequest[_], movement: GetMovementResponse, messages: Messages): Html = {
    val optContent: Option[Html] = for {
      ror <- movement.reportOfReceipt
      if ror.acceptMovement != AcceptMovement.Satisfactory
    } yield {
      val items: Seq[Html] = ie818ItemModelWithCnCodeInformation.map {
        case model@IE818ItemModelWithCnCodeInformation(rorItem, information) =>
          lazy val whatWasWrongRow =
            Some(summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.rorItems.whatWasWrong", list(rorItem.unsatisfactoryReasons.map {
              reason => HtmlContent(messages(s"movementHistoryEvent.${event.eventType}.rorItems.whatWasWrong.${reason.reason}")).asHtml
            })))

          HtmlFormat.fill(
            Seq(
              buildOverviewPartial(
                cardTitleMessageKey = Some(messages(s"movementHistoryEvent.${event.eventType}.rorItems.h3", rorItem.eadBodyUniqueReference)),
                cardAction = Some(ActionItemViewModel(
                  content = Text(messages(s"movementHistoryEvent.${event.eventType}.rorItems.link")),
                  href = controllers.routes.ItemDetailsController.onPageLoad(request.ern, movement.arc, rorItem.eadBodyUniqueReference).url,
                  id = s"viewItem-${rorItem.eadBodyUniqueReference}"
                ).withVisuallyHiddenText(messages(
                  s"movementHistoryEvent.${event.eventType}.rorItems.link.hidden",
                  rorItem.eadBodyUniqueReference,
                  information.cnCodeDescription
                ))),
                cardTitleHeadingLevel = Some(3),
                summaryListRows = Seq(
                  Seq(whatWasWrongRow),
                  rorItemRows(event, model)
                ).flatten
              )
            )
          )
      }

      HtmlFormat.fill(
        Seq(
          h2(messages(s"movementHistoryEvent.${event.eventType}.rorItems.h2"), "govuk-heading-m govuk-!-margin-top-9")
        ) ++ items
      )
    }

    optContent.getOrElse(Html(""))
  }

  private def generateRoRItemAdditionalInfoKey(row: String)(implicit event: MovementHistoryEvent): String =
    s"movementHistoryEvent.${event.eventType}.rorItems.additionalInformation.$row"

  private[events] def generateRoRItemAdditionalInfoRow(key: String, additionalInformation: Option[String])
                                                      (implicit messages: Messages): Option[SummaryListRow] = additionalInformation.map {
    summaryListRowBuilder(
      key,
      _
    )
  }

  private[events] def generateShortageOrExcessRows(
                                                    isShortage: Boolean,
                                                    quantityOption: Option[BigDecimal],
                                                    additionalInformation: Option[String]
                                                  )(
                                                    implicit event: MovementHistoryEvent,
                                                    ie818ItemModelWithCnCodeInformation: IE818ItemModelWithCnCodeInformation,
                                                    messages: Messages
                                                  ): Seq[Option[SummaryListRow]] = {
    val quantityRow: Option[SummaryListRow] = quantityOption.map {
      quantity =>
        summaryListRowBuilder(
          s"movementHistoryEvent.${event.eventType}.rorItems.quantity.${if (isShortage) "shortage" else "excess"}",
          messages(
            s"movementHistoryEvent.${event.eventType}.rorItems.quantity.value",
            quantity,
            ie818ItemModelWithCnCodeInformation.information.unitOfMeasure.toShortFormatMessage()
          )
        )
    }

    val infoRow: Option[SummaryListRow] = additionalInformation.map {
      info =>
        summaryListRowBuilder(
          s"movementHistoryEvent.${event.eventType}.rorItems.additionalInformation.${if (isShortage) "shortage" else "excess"}",
          info
        )
    }

    Seq(quantityRow, infoRow)
  }

  private[events] def rorItemRows(event: MovementHistoryEvent, ie818ItemModelWithCnCodeInformation: IE818ItemModelWithCnCodeInformation)
                                 (implicit messages: Messages): Seq[Option[SummaryListRow]] = {
    implicit val _event: MovementHistoryEvent = event
    implicit val _ie818ItemModelWithCnCodeInformation: IE818ItemModelWithCnCodeInformation = ie818ItemModelWithCnCodeInformation

    ie818ItemModelWithCnCodeInformation.rorItem.unsatisfactoryReasons
      .sorted
      .flatMap {
        case UnsatisfactoryModel(WrongWithMovement.Shortage, additionalInformation) =>
          generateShortageOrExcessRows(isShortage = true, ie818ItemModelWithCnCodeInformation.rorItem.shortageAmount, additionalInformation)
        case UnsatisfactoryModel(WrongWithMovement.Excess, additionalInformation) =>
          generateShortageOrExcessRows(isShortage = false, ie818ItemModelWithCnCodeInformation.rorItem.excessAmount, additionalInformation)
        case UnsatisfactoryModel(WrongWithMovement.Damaged, additionalInformation) =>
          Seq(generateRoRItemAdditionalInfoRow(generateRoRItemAdditionalInfoKey("damage"), additionalInformation))
        case UnsatisfactoryModel(WrongWithMovement.BrokenSeals, additionalInformation) =>
          Seq(generateRoRItemAdditionalInfoRow(generateRoRItemAdditionalInfoKey("seals"), additionalInformation))
        case UnsatisfactoryModel(WrongWithMovement.Other, additionalInformation) =>
          Seq(generateRoRItemAdditionalInfoRow(generateRoRItemAdditionalInfoKey("other"), additionalInformation))
      }
  }


  def alertRejectInformationCard(event: NotificationOfAlertOrRejectionModel)(implicit messages: Messages): Html = {

    val date = Seq(Some(summaryListRowBuilder(
      messages(s"movementHistoryEvent.IE819.summary.${event.notificationType}.date"),
      event.notificationDateAndTime.toLocalDate.formatDateForUIOutput()
    )))

    val reasons = Seq(
      Some(summaryListRowBuilder(
        ViewUtils.pluralSingular(messages(s"movementHistoryEvent.IE819.summary.${event.notificationType}.reason"), event.alertRejectReason.size),
        bullets(event.alertRejectReason.sorted.map { reason =>
          Html(messages(s"movementHistoryEvent.IE819.reason.${reason.reason}"))
        }, if (event.alertRejectReason.size > 1) "govuk-list govuk-list--bullet" else "govuk-list")
      ))
    )

    val reasonInfo = event.alertRejectReason.sorted.map(reason => reason.additionalInformation.map { info =>
      summaryListRowBuilder(
        messages(s"movementHistoryEvent.IE819.reason.${reason.reason}.info"),
        info
      )
    })

    buildOverviewPartial(
      headingTitle = Some(s"movementHistoryEvent.IE819.summary.${event.notificationType}.h2"),
      headingId = Some("alert-reject-information-heading"),
      summaryListRows = date ++ reasons ++ reasonInfo,
      summaryListAttributes = Map("id" -> "alert-rejection-information-summary")
    )
  }

  def customsRejectionInformationCard(event: NotificationOfCustomsRejectionModel)(implicit messages: Messages): Html = {
    val rows = Seq(
      Some(summaryListRowBuilder(
        messages("movementHistoryEvent.IE839.summary.rejection.date"),
        event.rejectionDateAndTime.toLocalDate.formatDateForUIOutput()
      )),
      Some(summaryListRowBuilder(
        messages("movementHistoryEvent.IE839.summary.rejection.code"),
        messages(s"movementHistoryEvent.IE839.summary.rejection.code.${event.rejectionReasonCode}")
      )),
      event.localReferenceNumber.fold[Option[SummaryListRow]](None)(
        lrn => Some(summaryListRowBuilder(messages("movementHistoryEvent.IE839.summary.rejection.lrn"), lrn))
      ),
      event.documentReferenceNumber.fold[Option[SummaryListRow]](None)(
        docRef => Some(summaryListRowBuilder(messages("movementHistoryEvent.IE839.summary.rejection.documentReferenceNumber"), docRef))
      )
    )

    buildOverviewPartial(
      headingTitle = None,
      headingId = None,
      summaryListRows = rows,
      summaryListAttributes = Map("id" -> "customs-rejection-information-summary")
    )
  }

  def customsRejectionDiagnosisCards(event: NotificationOfCustomsRejectionModel)(implicit messages: Messages): Html =
    event.diagnoses match {
      case Nil => HtmlFormat.empty
      case diagnosis :: Nil =>
        buildOverviewPartial(
          headingTitle = Some("movementHistoryEvent.IE839.summary.diagnosis.h2"),
          headingId = Some("customs-reject-diagnosis"),
          summaryListRows = Seq(
            Some(summaryListRowBuilder(
              messages("movementHistoryEvent.IE839.summary.diagnosis.bodyRecordUniqueReference"),
              diagnosis.bodyRecordUniqueReference
            )),
            Some(summaryListRowBuilder(
              messages("movementHistoryEvent.IE839.summary.diagnosis.diagnosisCode"),
              messages(s"movementHistoryEvent.IE839.summary.diagnosis.diagnosisCode.${diagnosis.diagnosisCode}")
            ))
          ),
          summaryListAttributes = Map("id" -> "customs-rejection-diagnosis-summary")
        )
      case diagnoses =>
        HtmlFormat.fill(Seq(h2(messages("movementHistoryEvent.IE839.summary.diagnosis.h2"))) ++
          diagnoses.zipWithIndex.map { case (diagnosis, idx) =>
            buildOverviewPartial(
              cardTitleMessageKey = Some("movementHistoryEvent.IE839.summary.diagnosis.h3.item"),
              cardTitleMessageArgs = Seq(s"${idx + 1}"),
              cardTitleHeadingLevel = Some(3),
              headingId = Some("customs-reject-diagnosis"),
              summaryListRows = Seq(
                Some(summaryListRowBuilder(
                  messages("movementHistoryEvent.IE839.summary.diagnosis.bodyRecordUniqueReference"),
                  diagnosis.bodyRecordUniqueReference
                )),
                Some(summaryListRowBuilder(
                  messages("movementHistoryEvent.IE839.summary.diagnosis.diagnosisCode"),
                  messages(s"movementHistoryEvent.IE839.summary.diagnosis.diagnosisCode.${diagnosis.diagnosisCode}")
                ))
              ),
              summaryListAttributes = Map("id" -> "customs-rejection-diagnosis-summary")
            )
          }
        )
    }

  def ie829AcceptedExportDetails(notificationOfAcceptedExport: NotificationOfAcceptedExportModel)(implicit movement: GetMovementResponse, messages: Messages): Html = {
    def acceptedExportSummary: Html = {
      val acceptedDate = summaryListRowBuilder("movementCreatedView.section.ie829.acceptedDate", notificationOfAcceptedExport.dateOfAcceptance.formatDateForUIOutput())
      val senderCustomsOfficeReference = summaryListRowBuilder("movementCreatedView.section.ie829.senderCustomsOfficeReference", notificationOfAcceptedExport.referenceNumberOfSenderCustomsOffice)
      val senderCustomsOfficer = summaryListRowBuilder("movementCreatedView.section.ie829.senderCustomsOfficer", notificationOfAcceptedExport.identificationOfSenderCustomsOfficer)
      val documentReferenceNumber = summaryListRowBuilder("movementCreatedView.section.ie829.documentReferenceNumber", notificationOfAcceptedExport.documentReferenceNumber)

      buildOverviewPartial(
        summaryListAttributes = Map("id" -> "accepted-export-summary"),
        summaryListRows = Seq(
          Some(acceptedDate),
          Some(senderCustomsOfficeReference),
          Some(senderCustomsOfficer),
          Some(documentReferenceNumber)
        )
      )
    }

    HtmlFormat.fill(
      Seq(
        acceptedExportSummary,
        consigneeInformationCard(Some(notificationOfAcceptedExport.consigneeTrader))
      )
    )
  }

  def delayInformationCard(event: NotificationOfDelayModel, messageRole: Int)(implicit messages: Messages): Html = {

    val submittedBy =
      Some(summaryListRowBuilder(
        messages("movementHistoryEvent.IE837.summary.submittedBy"),
        messages(s"movementHistoryEvent.IE837.summary.submittedBy.${event.submitterType}")
      ))

    val submitterId =
      Some(summaryListRowBuilder(messages(s"movementHistoryEvent.IE837.summary.submitterId.${event.submitterType}"), event.submitterIdentification))

    val delayType =
      Some(summaryListRowBuilder(
        messages("movementHistoryEvent.IE837.summary.delayType"),
        messages(s"movementHistoryEvent.IE837.summary.delayType.$messageRole")
      ))

    val reason =
      Some(summaryListRowBuilder(
        messages("movementHistoryEvent.IE837.summary.reason"),
        messages(s"movementHistoryEvent.IE837.summary.reason.${event.explanationCode}")
      ))

    val info =
      event.complementaryInformation.map(summaryListRowBuilder(messages("movementHistoryEvent.IE837.summary.info"), _))

    buildOverviewPartial(
      summaryListRows = Seq(submittedBy, submitterId, delayType, reason, info),
      summaryListAttributes = Map("id" -> "delay-information-summary")
    )
  }

  def ie871IndividualItemDetails(event: NotificationOfShortageOrExcessModel, movement: GetMovementResponse)
                                (implicit messages: Messages, request: DataRequest[_]): Html =
    event.individualItemReasons.map { items =>
      HtmlFormat.fill(
        Seq(h2(messages("movementHistoryEvent.IE871.summary.h2"), classes = "govuk-heading-m govuk-!-margin-top-9")) ++
          items.map { item =>

            val epc =
              Some(summaryListRowBuilder(messages("movementHistoryEvent.IE871.summary.epc"), item.exciseProductCode))

            val itemReference =
              Some(summaryListRowBuilder(messages("movementHistoryEvent.IE871.summary.itemRef"), item.bodyRecordUniqueReference.toString))

            val amount =
              item.actualQuantity.map { actualQuantity =>
                summaryListRowBuilder(messages("movementHistoryEvent.IE871.summary.amount"), actualQuantity.toString)
              }

            val explanation =
              Some(summaryListRowBuilder(messages("movementHistoryEvent.IE871.summary.explanation"), item.explanation))

            buildOverviewPartial(
              cardTitleMessageKey = Some(messages("movementHistoryEvent.IE871.summary.cardTitle", item.bodyRecordUniqueReference)),
              cardTitleHeadingLevel = Some(3),
              cardAction = Some(ActionItemViewModel(
                content = Text(messages("movementHistoryEvent.IE871.summary.viewItem")),
                href = controllers.routes.ItemDetailsController.onPageLoad(request.ern, movement.arc, item.bodyRecordUniqueReference).url,
                id = s"viewItem-${item.bodyRecordUniqueReference}"
              )),
              headingId = Some(s"item-information-${item.bodyRecordUniqueReference}"),
              summaryListRows = Seq(epc, itemReference, amount, explanation),
              summaryListAttributes = Map("id" -> s"item-information-summary-${item.bodyRecordUniqueReference}")
            )
          }
      )
    }.getOrElse(Empty.asHtml)

  def ie871GlobalDetails(event: NotificationOfShortageOrExcessModel)(implicit messages: Messages): Html = {

    event.globalDateOfAnalysis.flatMap { date =>
      event.globalExplanation.map { explanation =>

        val dateRow =
          Some(summaryListRowBuilder(messages("movementHistoryEvent.IE871.summary.global.date"), date.formatDateForUIOutput()))

        val globalExplanationRow =
          Some(summaryListRowBuilder(messages("movementHistoryEvent.IE871.summary.global.explanation"), explanation))

        buildOverviewPartial(
          headingTitle = Some(messages("movementHistoryEvent.IE871.summary.global.h2")),
          headingId = Some("shortage-or-excess-information-heading"),
          summaryListRows = Seq(dateRow, globalExplanationRow),
          summaryListAttributes = Map("id" -> "shortage-or-excess-information-summary")
        )
      }
    }.getOrElse(Empty.asHtml)
  }
}
