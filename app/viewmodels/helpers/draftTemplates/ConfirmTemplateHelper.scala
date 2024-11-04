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

package viewmodels.helpers.draftTemplates

import models.common.GuarantorType.Owner
import models.common.{RoleType, TransportArrangement}
import models.draftTemplates.{Template, TemplateItem}
import models.movementScenario.MovementScenario._
import models.requests.DataRequest
import models.response.referenceData.CnCodeInformation
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.helpers.MovementTypeHelper
import viewmodels.helpers.SummaryListHelper.summaryListRowBuilder
import views.html.components.list
import views.html.viewMovement.partials.overview_partial

import javax.inject.Inject

class ConfirmTemplateHelper @Inject()(
                                       movementTypeHelper: MovementTypeHelper,
                                       overviewPartial: overview_partial,
                                       list: list
                                     ) {

  def constructTable(
                      template: Template,
                      itemsWithCnCodeInfo: Seq[(TemplateItem, CnCodeInformation)]
                    )(implicit request: DataRequest[_], messages: Messages): Html = {

    implicit val _template: Template = template

    overviewPartial(
      headingMessageKey = None,
      cardTitleMessageKey = None,
      summaryListRows = Seq(
        Some(movementType),
        consignee,
        consigneeERN,
        exportOffice,
        importOffice,
        exemptedOrganisationOffice,
        guarantor,
        journeyType,
        transportArranger,
        firstTransporter,
        items(itemsWithCnCodeInfo)
      ).flatten
    )
  }


  private[draftTemplates] def movementType()(implicit template: Template, request: DataRequest[_], messages: Messages): SummaryListRow = {
      summaryListRowBuilder("confirmTemplate.movement.summary.type", movementTypeHelper.getMovementType(
        request.userTypeFromErn,
        template.destinationType,
        template.placeOfDispatch,
        isBeingViewedByConsignor = true,
        isBeingViewedByConsignee = false
      ))
  }

  private[draftTemplates] def consignee()(implicit template: Template, messages: Messages): Option[SummaryListRow] =
    template.consigneeBusinessName
      .filter(_ => template.destinationType != UnknownDestination)
      .map(summaryListRowBuilder("confirmTemplate.movement.summary.consigneeBusinessName", _))


  private[draftTemplates] def consigneeERN()(implicit template: Template, messages: Messages): Option[SummaryListRow] =
    template.consigneeERN
      .filter(_ => !Seq(UnknownDestination, ExemptedOrganisation).contains(template.destinationType))
      .map(summaryListRowBuilder("confirmTemplate.movement.summary.consigneeERN", _))


  private[draftTemplates] def exportOffice()(implicit template: Template, messages: Messages): Option[SummaryListRow] =
    template.exportCustomsOfficeCode
      .filter(_ => Seq(ExportWithCustomsDeclarationLodgedInTheUk, ExportWithCustomsDeclarationLodgedInTheEu).contains(template.destinationType))
      .map(summaryListRowBuilder("confirmTemplate.movement.summary.exportOffice", _))


  private[draftTemplates] def importOffice()(implicit template: Template, request: DataRequest[_], messages: Messages): Option[SummaryListRow] =
    template.importCustomsOfficeCode
      .filter(_ => Seq(RoleType.GBRC, RoleType.XIRC).contains(request.userTypeFromErn))
      .map(summaryListRowBuilder("confirmTemplate.movement.summary.importOffice", _))


  private[draftTemplates] def exemptedOrganisationOffice()(implicit template: Template, messages: Messages): Option[SummaryListRow] =
    template.memberState
      .filter(_ => template.destinationType == ExemptedOrganisation)
      .map(summaryListRowBuilder("confirmTemplate.movement.summary.exemptedOrganisationOffice", _))


  private[draftTemplates] def guarantor()(implicit template: Template, messages: Messages): Option[SummaryListRow] =
    template.guarantorArranger.flatMap {
      case guarantorArranger if guarantorArranger != Owner =>
        Some(summaryListRowBuilder("confirmTemplate.movement.summary.guarantor", guarantorArranger.messageKey))
      case guarantorArranger =>
        template.guarantorBusinessName.map { guarantorBusinessName =>
          summaryListRowBuilder("confirmTemplate.movement.summary.guarantor", Html(s"${messages(guarantorArranger.messageKey)}<br>$guarantorBusinessName"))
        }
    }


  private[draftTemplates] def journeyType()(implicit template: Template, messages: Messages): Option[SummaryListRow] =
    template.journeyType.map { journeyType =>
      summaryListRowBuilder("confirmTemplate.movement.summary.journeyType", s"${journeyType.messageKey}")
    }


  private[draftTemplates] def transportArranger()(implicit template: Template, messages: Messages): Option[SummaryListRow] =
    template.transportArranger match {
      case Some(transporterArranger) if Seq(TransportArrangement.Consignor, TransportArrangement.Consignee).contains(transporterArranger) =>
        Some(summaryListRowBuilder("confirmTemplate.movement.summary.transportArranger", transporterArranger.messageKey))
      case Some(transporterArranger) if Seq(TransportArrangement.Other, TransportArrangement.OwnerOfGoods).contains(transporterArranger) =>
        template.transportArrangerBusinessName.map { transportArrangerBusinessName =>
          summaryListRowBuilder("confirmTemplate.movement.summary.transportArranger", Html(s"${messages(transporterArranger.messageKey)}<br>$transportArrangerBusinessName"))
        }
      case _ => None
    }


  private[draftTemplates] def firstTransporter()(implicit template: Template, messages: Messages): Option[SummaryListRow] =
    template.firstTransporterBusinessName.map { firstTransporterBusinessName =>
      summaryListRowBuilder("confirmTemplate.movement.summary.firstTransporter", firstTransporterBusinessName)
    }


  private[draftTemplates] def items(itemsWithCnCodeInfo: Seq[(TemplateItem, CnCodeInformation)])(implicit messages: Messages): Option[SummaryListRow] = {
    itemsWithCnCodeInfo.headOption.map { _ =>
      val itemsList = itemsWithCnCodeInfo.zipWithIndex.map { case ((item, cnCodeInfo), index) =>

        Html(
          messages(
            "confirmTemplate.movement.summary.item.description",
            index + 1,
            item.itemQuantity,
            messages(s"unitOfMeasure.${cnCodeInfo.unitOfMeasure}.short"),
            cnCodeInfo.exciseProductCodeDescription
          )
        )
      }
      summaryListRowBuilder("confirmTemplate.movement.summary.items", list(itemsList, extraClasses = Some("govuk-list--bullet")))
    }
  }

}
