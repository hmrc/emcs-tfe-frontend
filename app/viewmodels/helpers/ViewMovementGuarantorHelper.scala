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
                                                   showNoGuarantorContentIfApplicable: Boolean = true)(implicit messages: Messages): Html = {

    movement.movementGuarantee.guarantorTypeCode match {
      case GuarantorNotRequired | NoGuarantor =>
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
      case Consignor | Transporter | Owner | Consignee =>
        movement.movementGuarantee.guarantorTrader.flatMap(_.headOption).map { singleGuarantor =>
          buildGuarantorCard(singleGuarantor, movement.movementGuarantee.guarantorTypeCode, Some(messages("viewMovement.guarantor.title")), Some(messages("viewMovement.guarantor.summary.subheading")))
        }.getOrElse(Html(""))
      case guarantorType if GuarantorType.jointGuarantorTypes.contains(guarantorType) =>
        movement.movementGuarantee.guarantorTrader.map { guarantors =>
          buildJointGuarantorCards(guarantors, movement.movementGuarantee.guarantorTypeCode)
        }.getOrElse(Html(""))
    }
  }

  private def buildGuarantorCard(
                                  guarantor: TraderModel,
                                  guarantorTypeCode: GuarantorType,
                                  headingMessageKey: Option[String] = None,
                                  cardTitleMessageKey: Option[String] = None,
                                  excludeGuarantorType: Boolean = false
                                )(implicit messages: Messages): Html = {

    val guarantorType = if (excludeGuarantorType) None else Some(summaryListRowBuilder("viewMovement.guarantor.summary.type", s"viewMovement.guarantor.summary.type.${guarantorTypeCode}"))
    val name = guarantor.traderName.map(summaryListRowBuilder("viewMovement.guarantor.summary.name", _))
    val ern = guarantor.traderExciseNumber.map(summaryListRowBuilder("viewMovement.guarantor.summary.ern", _))
    val address = guarantor.address.map(address => summaryListRowBuilder("viewMovement.guarantor.summary.address", renderAddress(address)))
    val vat = guarantor.vatNumber.map(summaryListRowBuilder("viewMovement.guarantor.summary.vat", _))

    overviewPartial(
      headingMessageKey = headingMessageKey,
      cardTitleMessageKey = cardTitleMessageKey,
      summaryListRows = Seq(guarantorType, name, ern, address, vat).flatten,
      summaryListAttributes = Map("id" -> s"guarantor-1")
    )
  }

  private def buildJointGuarantorCards(guarantors: Seq[TraderModel], guarantorTypeCode: GuarantorType)(implicit messages: Messages): Html = {
    val guarantorType = Some(summaryListRowBuilder("viewMovement.guarantor.summary.type", s"viewMovement.guarantor.summary.type.${guarantorTypeCode}"))

    val summaryCard: Seq[Html] = Seq(
      overviewPartial(
        headingMessageKey = Some("viewMovement.guarantor.title"),
        headingLevel = 2,
        cardTitleMessageKey = Some("viewMovement.guarantor.summary.subheading"),
        summaryListRows = Seq(guarantorType).flatten,
        summaryListAttributes = Map("id" -> s"guarantor-summary")
      )
    )

    val cards = guarantors.zipWithIndex.map {
      case (guarantor, index) => buildGuarantorCard(guarantor, guarantorTypeCode, None, Some(messages("viewMovement.guarantor.summary.joint.subheading", index + 1)), excludeGuarantorType = true)
    }

    HtmlFormat.fill(summaryCard ++ cards)
  }


}
