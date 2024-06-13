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

import models.common.DestinationType._
import models.common.GuarantorType._
import models.common.{AcceptMovement, DestinationType, WrongWithMovement}
import models.requests.DataRequest
import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import models.response.emcsTfe.reportOfReceipt.{IE818ItemModelWithCnCodeInformation, UnsatisfactoryModel}
import models.response.emcsTfe.{GetMovementResponse, MovementItem}
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.Details
import uk.gov.hmrc.govukfrontend.views.html.components.GovukDetails
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.govuk.all.{ActionItemViewModel, FluentActionItem}
import viewmodels.helpers.SummaryListHelper._
import viewmodels.helpers.{ItemDetailsCardHelper, ItemPackagingCardHelper, ViewMovementTransportHelper}
import views.ViewUtils.LocalDateExtensions
import views.html.components.{h2, list}
import views.html.viewMovement.partials.overview_partial

import javax.inject.{Inject, Singleton}

@Singleton
class MovementEventHelper @Inject()(
                                     h2: h2,
                                     list: list,
                                     overview_partial: overview_partial,
                                     govukDetails: GovukDetails,
                                     transportCardHelper: ViewMovementTransportHelper,
                                     itemDetailsCardHelper: ItemDetailsCardHelper,
                                     packagingCardHelper: ItemPackagingCardHelper
                                   ) {

  private def buildOverviewPartial(
                                    headingId: Option[String] = None,
                                    headingLevel: Int = 2,
                                    headingTitle: Option[String] = None,
                                    headingMessageClass: String = "govuk-heading-m govuk-!-margin-top-9",
                                    cardTitleMessageKey: Option[String] = None,
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
      cardAction = cardAction,
      cardTitleHeadingLevel = cardTitleHeadingLevel,
      cardFooterHtml = cardFooterHtml,
      summaryListRows = summaryListRows.flatten,
      summaryListAttributes = summaryListAttributes
    )
  }

  def movementInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    val upstreamArc = movement.eadEsad.upstreamArc.map(summaryListRowBuilder("movementCreatedView.section.movement.replacedArc", _))
    val localReferenceNumber = summaryListRowBuilder("movementCreatedView.section.movement.lrn", movement.localReferenceNumber)
    val originType = summaryListRowBuilder("movementCreatedView.section.movement.originType", messages(s"movementCreatedView.section.movement.originType.${movement.eadEsad.originTypeCode}"))
    val destinationType = summaryListRowBuilder("movementCreatedView.section.movement.destinationType", messages(s"movementCreatedView.section.movement.destinationType.${movement.headerEadEsad.destinationType}"))
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

  def importInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    movement.dispatchImportOfficeReferenceNumber.map { officeCode =>

      buildOverviewPartial(
        headingTitle = Some("movementCreatedView.section.import"),
        headingId = Some("import-information-heading"),
        summaryListRows = Seq(
          Some(summaryListRowBuilder("movementCreatedView.section.import.customsOfficeCode", officeCode))
        ),
        summaryListAttributes = Map("id" -> "import-information-summary")
      )
    }.getOrElse(Html(""))
  }

  def consigneeInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    movement.consigneeTrader.map { trader =>
      val name = trader.traderName.map(summaryListRowBuilder("movementCreatedView.section.consignee.name", _))
      val ernLabel = if (movement.destinationType == TemporaryRegisteredConsignee) "movementCreatedView.section.consignee.identifier" else "movementCreatedView.section.consignee.ern"
      val ern = trader.traderExciseNumber.map(summaryListRowBuilder(ernLabel, _))
      val vatNumber = trader.vatNumber.map(summaryListRowBuilder("movementCreatedView.section.consignee.vatNumber", _))
      val address = trader.address.map(address => summaryListRowBuilder("movementCreatedView.section.consignee.address", renderAddress(address)))
      val eoriNumber = trader.eoriNumber.map(summaryListRowBuilder("movementCreatedView.section.consignee.eoriNumber", _))

      buildOverviewPartial(
        headingTitle = Some("movementCreatedView.section.consignee"),
        headingId = Some("consignee-information-heading"),
        summaryListRows = Seq(name, ern, vatNumber, address, eoriNumber),
        summaryListAttributes = Map("id" -> "consignee-information-summary")
      )
    }.getOrElse(Html(""))
  }

  def exemptedOrganisationInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    val memberState = movement.memberStateCode.map(summaryListRowBuilder("movementCreatedView.section.exemptedConsignee.memberState", _))
    val serialNumber = movement.serialNumberOfCertificateOfExemption.map(summaryListRowBuilder("movementCreatedView.section.exemptedConsignee.serialNumber", _))

    if (memberState.isDefined || serialNumber.isDefined) {
      buildOverviewPartial(
        headingTitle = Some("movementCreatedView.section.exemptedConsignee"),
        headingId = Some("exempted-organisation-information-heading"),
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

  def exportInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    movement.deliveryPlaceCustomsOfficeReferenceNumber.map { officeCode =>
      buildOverviewPartial(
        headingTitle = Some("movementCreatedView.section.export"),
        headingId = Some("export-information-heading"),
        summaryListRows = Seq(
          Some(summaryListRowBuilder("movementCreatedView.section.export.customsOfficeCode", officeCode))
        ),
        summaryListAttributes = Map("id" -> "export-information-summary")
      )
    }.getOrElse(Html(""))
  }

  def guarantorInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    val guarantor = movement.movementGuarantee
    lazy val optFirstGuarantor = guarantor.guarantorTrader.flatMap(_.headOption)

    val guarantorType = summaryListRowBuilder("movementCreatedView.section.guarantor.type", s"viewMovement.guarantor.summary.type.${guarantor.guarantorTypeCode}")

    val guarantorName = guarantor.guarantorTypeCode match {
      case Owner | Transporter =>
        optFirstGuarantor.flatMap(firstGuarantor => firstGuarantor.traderName.map(summaryListRowBuilder("movementCreatedView.section.guarantor.name", _)))
      case _ => None
    }

    val guarantorErn = (guarantor.guarantorTypeCode, movement.destinationType) match {
      case (Consignor, _) =>
        movement.consignorTrader.traderExciseNumber.map(summaryListRowBuilder("movementCreatedView.section.guarantor.ern", _))
      case (Consignee, TaxWarehouse | DirectDelivery | RegisteredConsignee) =>
        movement.consigneeTrader.flatMap(trader => trader.traderExciseNumber.map(summaryListRowBuilder("movementCreatedView.section.guarantor.ern", _)))
      case _ => None
    }

    val temporaryReference = (guarantor.guarantorTypeCode, movement.destinationType) match {
      case (Consignee, TemporaryRegisteredConsignee) =>
        movement.consigneeTrader.flatMap(trader => trader.traderExciseNumber.map(summaryListRowBuilder("movementCreatedView.section.guarantor.temporaryReference", _)))
      case _ => None
    }

    val identificationNumber = (guarantor.guarantorTypeCode, movement.destinationType) match {
      case (Consignee, Export) =>
        movement.consigneeTrader.flatMap(_.traderExciseNumber.map(summaryListRowBuilder("movementCreatedView.section.guarantor.identificationNumber", _)))
      case _ => None
    }

    val guarantorVatNumber = guarantor.guarantorTypeCode match {
      case Owner | Transporter =>
        optFirstGuarantor.flatMap(_.vatNumber.map(summaryListRowBuilder("movementCreatedView.section.guarantor.vat", _)))
      case _ => None
    }

    val guarantorAddress = guarantor.guarantorTypeCode match {
      case Owner | Transporter =>
        optFirstGuarantor.flatMap(firstGuarantor => firstGuarantor.address.map(address => summaryListRowBuilder("movementCreatedView.section.guarantor.address", renderAddress(address))))
      case _ => None
    }

    buildOverviewPartial(
      headingTitle = Some("movementCreatedView.section.guarantor"),
      headingId = Some("guarantor-information-heading"),
      summaryListRows = Seq(
        Some(guarantorType),
        guarantorName,
        guarantorErn,
        temporaryReference,
        identificationNumber,
        guarantorVatNumber,
        guarantorAddress
      ),
      summaryListAttributes = Map("id" -> "guarantor-information-summary")
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

  def sadInformationCard()(implicit movement: GetMovementResponse, messages: Messages): Html = {
    movement.eadEsad.importSadNumber.map { sadNumbers =>

      val sadCards = sadNumbers.zipWithIndex.map {
        case (sad, index) =>
          buildOverviewPartial(
            cardTitleMessageKey = Some(messages("movementCreatedView.section.sad.heading", index + 1)),
            summaryListRows = Seq(
              Some(summaryListRowBuilder("movementCreatedView.section.sad.importNumber", sad))
            ),
            summaryListAttributes = Map("id" -> s"sad-information-summary-${index + 1}")
          )
      }

      HtmlFormat.fill(
        Seq(
          h2(messages("movementCreatedView.section.sads.heading"), classes = "govuk-heading-m govuk-!-margin-top-9", id = Some("sad-information-heading"))
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

  def rorDetailsCard(event: MovementHistoryEvent)(implicit movement: GetMovementResponse, messages: Messages): Html = {
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

        buildOverviewPartial(
          headingId = None,
          headingTitle = Some(headingTitle),
          cardTitleMessageKey = None,
          cardFooterHtml = None,
          summaryListRows = Seq(
            Some(summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.rorDetails.dateOfArrival", reportOfReceipt.dateOfArrival.formatDateForUIOutput())),
            Some(summaryListRowBuilder(statusKey, messages(s"movementHistoryEvent.${event.eventType}.rorDetails.status.value.${reportOfReceipt.acceptMovement}"))),
            Some(summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.rorDetails.moreInformation", reportOfReceipt.otherInformation.getOrElse(s"movementHistoryEvent.${event.eventType}.rorDetails.moreInformation.notProvided"))),
          )
        )
      case None => Html("")
    }
  }

  def rorConsigneeCard(event: MovementHistoryEvent)(implicit movement: GetMovementResponse, messages: Messages): Html = {
    val optContent: Option[Html] = for {
      reportOfReceipt <- movement.reportOfReceipt
      consigneeTrader <- reportOfReceipt.consigneeTrader
    } yield {

      val ernRow: Option[SummaryListRow] =
        if (Seq(DestinationType.TaxWarehouse, DestinationType.DirectDelivery, DestinationType.RegisteredConsignee).contains(movement.destinationType)) {
          consigneeTrader.traderExciseNumber.map(summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.rorConsignee.ern", _))
        } else {
          None
        }

      val temporaryAuthorisationReferenceRow: Option[SummaryListRow] =
        if (movement.destinationType == DestinationType.TemporaryRegisteredConsignee) {
          consigneeTrader.traderExciseNumber.map(summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.rorConsignee.temporaryReference", _))
        } else {
          None
        }

      buildOverviewPartial(
        headingId = None,
        headingTitle = Some(s"movementHistoryEvent.${event.eventType}.rorConsignee.h2"),
        cardTitleMessageKey = None,
        cardFooterHtml = None,
        summaryListRows = Seq(
          consigneeTrader.traderName.map(summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.rorConsignee.name", _)),
          ernRow,
          temporaryAuthorisationReferenceRow,
          consigneeTrader.vatNumber.map(summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.rorConsignee.vatNumber", _)),
          consigneeTrader.address.map(address => summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.rorConsignee.address", renderAddress(address))),
          consigneeTrader.eoriNumber.map(summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.rorConsignee.eoriNumber", _))
        )
      )
    }

    optContent.getOrElse(Html(""))
  }

  def rorDestinationCard(event: MovementHistoryEvent)(implicit movement: GetMovementResponse, messages: Messages): Html = {
    val optContent: Option[Html] = for {
      reportOfReceipt <- movement.reportOfReceipt
      deliveryPlaceTrader <- reportOfReceipt.deliveryPlaceTrader
      if movement.destinationType != DestinationType.Export
    } yield {
      val ernRow: Option[SummaryListRow] =
        if (movement.destinationType == DestinationType.TaxWarehouse) {
          deliveryPlaceTrader.traderExciseNumber.map(summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.rorDeliveryPlaceTrader.ern", _))
        } else {
          None
        }

      buildOverviewPartial(
        headingId = None,
        headingTitle = Some(s"movementHistoryEvent.${event.eventType}.rorDeliveryPlaceTrader.h2"),
        cardTitleMessageKey = None,
        cardFooterHtml = None,
        summaryListRows = Seq(
          deliveryPlaceTrader.traderName.map(summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.rorDeliveryPlaceTrader.name", _)),
          ernRow,
          deliveryPlaceTrader.vatNumber.map(summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.rorDeliveryPlaceTrader.vatNumber", _)),
          deliveryPlaceTrader.address.map(address => summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.rorDeliveryPlaceTrader.address", renderAddress(address))),
        )
      )
    }

    optContent.getOrElse(Html(""))
  }

  def rorExportCard(event: MovementHistoryEvent)(implicit movement: GetMovementResponse, messages: Messages): Html = {
    val optContent: Option[Html] = for {
      reportOfReceipt <- movement.reportOfReceipt
      if movement.destinationType == DestinationType.Export
    } yield {
      buildOverviewPartial(
        headingId = None,
        headingTitle = Some(s"movementHistoryEvent.${event.eventType}.rorExport.h2"),
        cardTitleMessageKey = None,
        cardFooterHtml = None,
        summaryListRows = Seq(
          // TODO: check if destinationOffice is correct
          Some(summaryListRowBuilder(s"movementHistoryEvent.${event.eventType}.rorExport.customsOfficeCode", reportOfReceipt.destinationOffice))
        )
      )
    }

    optContent.getOrElse(Html(""))
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
                  rorItemRows(event, model),
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
      .sortBy {
        case UnsatisfactoryModel(reason, _) => UnsatisfactoryModel.sortingOrder(reason)
      }.flatMap {
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

}
