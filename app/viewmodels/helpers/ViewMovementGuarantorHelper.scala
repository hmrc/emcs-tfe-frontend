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

import models.common.GuarantorType._
import models.common.{GuarantorType, TraderModel}
import models.response.emcsTfe.GetMovementResponse
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.helpers.SummaryListHelper._
import views.html.components.{h2, p}
import views.html.viewMovement.partials.overview_partial

import javax.inject.{Inject, Singleton}

@Singleton
class ViewMovementGuarantorHelper @Inject()(h2: h2,
                                            p: p,
                                            overviewPartial: overview_partial) {

  private[helpers] def constructMovementGuarantor(
                                                   movement: GetMovementResponse,
                                                   showNoGuarantorContentIfApplicable: Boolean = true,
                                                   isSummaryCard: Boolean = true,
                                                   headingMessageClass: Option[String] = Some("govuk-heading-l"))(implicit messages: Messages): Html = {
    movement.movementGuarantee.guarantorTypeCode match {
      case GuarantorNotRequired | NoGuarantor =>
        buildNoGuarantorCard(showNoGuarantorContentIfApplicable)
      case guarantorType if GuarantorType.singleGuarantorTypes.contains(guarantorType) =>
        buildSingleGuarantorCard(movement, headingMessageClass, isSummaryCard)
      case _ =>
        buildJointGuarantorCards(movement, headingMessageClass, isSummaryCard)
    }
  }

  private def buildNoGuarantorCard(showNoGuarantorContentIfApplicable: Boolean)(implicit messages: Messages): Html = {
    if (showNoGuarantorContentIfApplicable) {

      HtmlFormat.fill(
        Seq(
          h2(messages("viewMovement.guarantor.title"), classes = "govuk-heading-l"),
          p()(Html(messages("viewMovement.guarantor.summary.noGuarantor")))
        )
      )
    } else {
      Html("")
    }

  }

  // scalastyle:off line.size.limit
  private def buildSingleGuarantorCard(movement: GetMovementResponse, headingMessageClass: Option[String], isSummaryCard: Boolean)(implicit messages: Messages): Html = {
    val guarantor: Option[TraderModel] = movement.movementGuarantee.guarantorTypeCode match {
      case GuarantorType.Consignor => Some(movement.consignorTrader)
      case GuarantorType.Consignee => movement.consigneeTrader
      case _ => movement.movementGuarantee.guarantorTrader.flatMap(_.headOption)
    }
    guarantor.map { singleGuarantor =>

      overviewPartial(
        headingId = Some("guarantor-information-heading"),
        headingMessageKey = Some(messages("viewMovement.guarantor.title")),
        headingMessageClass = headingMessageClass.getOrElse(""),
        cardTitleMessageKey = if(isSummaryCard) Some(messages("viewMovement.guarantor.summary.subheading")) else None,
        cardTitleHeadingLevel = Some(3),
        summaryListRows = Seq(
          guarantorTypeSummaryListRows(movement),
          guarantorSummaryListRows(singleGuarantor)
        ).flatten,
        summaryListAttributes = Map("id" -> "guarantor-information-summary")
      )

    }.getOrElse(Html(""))
  }
  // scalastyle:on line.size.limit

  // scalastyle:off line.size.limit
  private def buildJointGuarantorCards(movement: GetMovementResponse, headingMessageClass: Option[String], isSummaryCard: Boolean)(implicit messages: Messages): Html = {
    val guarantors: Seq[TraderModel] = {
      val consignor: Seq[TraderModel] = if (movement.movementGuarantee.guarantorTypeCode.toString.contains(GuarantorType.Consignor.toString)) {
        Seq(movement.consignorTrader)
      } else {
        Seq.empty
      }
      val consignee: Seq[TraderModel] = if (movement.movementGuarantee.guarantorTypeCode.toString.contains(GuarantorType.Consignee.toString)) {
        Seq(movement.consigneeTrader).flatten
      } else {
        Seq.empty
      }
      val other: Seq[TraderModel] = movement.movementGuarantee.guarantorTrader.getOrElse(Seq.empty)
      consignor ++ consignee ++ other
    }

    lazy val summaryCard: Seq[Html] = Seq(
      overviewPartial(
        headingId = Some("guarantor-information-heading"),
        headingMessageKey = if(isSummaryCard) None else Some("viewMovement.guarantor.summary.subheading"),
        headingMessageClass = "govuk-heading-m",
        headingLevel = 3,
        cardTitleMessageKey = if(isSummaryCard) Some("viewMovement.guarantor.summary.subheading") else None,
        cardTitleHeadingLevel = Some(3),
        summaryListRows = Seq(guarantorTypeSummaryListRows(movement)).flatten,
        summaryListAttributes = Map("id" -> s"guarantor-summary")
      )
    )
    val guarantorCards = guarantors.zipWithIndex.map {
      case (guarantor, index) =>
        overviewPartial(
          headingMessageKey = if(isSummaryCard) None else Some(messages("viewMovement.guarantor.summary.joint.subheading", index + 1)),
          headingLevel = 3,
          headingMessageClass = "govuk-heading-m",
          cardTitleMessageKey = if(isSummaryCard) Some(messages("viewMovement.guarantor.summary.joint.subheading", index + 1)) else None,
          cardTitleHeadingLevel = Some(3),
          summaryListRows = Seq(guarantorSummaryListRows(guarantor)).flatten,
          summaryListAttributes = Map("id" -> "guarantor-information-summary")
        )
    }

    if (guarantorCards.isEmpty) {
      Html("")
    } else {
      HtmlFormat.fill(
        Seq(h2(messages("viewMovement.guarantor.title"), classes = headingMessageClass.getOrElse(""), id = Some("guarantor-information-heading"))) ++
        summaryCard ++ guarantorCards
      )
    }
  }
  // scalastyle:on line.size.limit

  private def guarantorSummaryListRows(guarantor: TraderModel)(implicit messages: Messages): Seq[SummaryListRow] = {
    val name = guarantor.traderName.map(summaryListRowBuilder("viewMovement.guarantor.summary.name", _))
    val ern = guarantor.traderExciseNumber.map(summaryListRowBuilder("viewMovement.guarantor.summary.ern", _))
    val address = guarantor.address.map(address => summaryListRowBuilder("viewMovement.guarantor.summary.address", renderAddress(address)))
    val vat = guarantor.vatNumber.map(summaryListRowBuilder("viewMovement.guarantor.summary.vat", _))

    Seq(name, ern, address, vat).flatten
  }

  private def guarantorTypeSummaryListRows(movement: GetMovementResponse)(implicit messages: Messages): Seq[SummaryListRow] = {
    Seq(summaryListRowBuilder("viewMovement.guarantor.summary.type", s"viewMovement.guarantor.summary.type.${movement.movementGuarantee.guarantorTypeCode}"))
  }

}
