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

  private[helpers] def constructMovementGuarantor(movementResponse: GetMovementResponse)(implicit messages: Messages): Html = {
    val guarantor = movementResponse.movementGuarantee
    lazy val optFirstGuarantor = guarantor.guarantorTrader.flatMap(_.headOption)
    lazy val guarantorArranger = summaryListRowBuilder("viewMovement.guarantor.summary.type", messages(s"viewMovement.guarantor.summary.type.${guarantor.guarantorTypeCode}"))
    lazy val guarantorBusinessName = optFirstGuarantor.flatMap(firstGuarantor => firstGuarantor.traderName.map(name => summaryListRowBuilder("viewMovement.guarantor.summary.businessName", name)))
    lazy val guarantorErn = optFirstGuarantor.flatMap(firstGuarantor => firstGuarantor.traderExciseNumber.map(ern => summaryListRowBuilder("viewMovement.guarantor.summary.ern", ern)))
    lazy val guarantorAddress = optFirstGuarantor.flatMap(firstGuarantor => firstGuarantor.address.map(address => summaryListRowBuilder("viewMovement.guarantor.summary.address", renderAddress(address))))
    lazy val guarantorVatNumber = optFirstGuarantor.flatMap(_.vatNumber.map(firstGuarantorVatNumber => summaryListRowBuilder("viewMovement.guarantor.summary.vatRegistrationNumber", firstGuarantorVatNumber)))

    guarantor.guarantorTypeCode match {
      // Guarantor Type = 0 or 5 then no guarantor needed
      case GuarantorNotRequired | NoGuarantor => HtmlFormat.fill(Seq(
        h2(messages("viewMovement.guarantor.title")),
        p()(Html(messages("viewMovement.guarantor.summary.noGuarantor")))
      ))
      // Guarantor Type is single digit (not 0 or 5), then only one guarantor
      case Consignor | Transporter | Owner | Consignee => HtmlFormat.fill(Seq(
        overviewPartial(
          headingMessageKey = Some("viewMovement.guarantor.title"),
          cardTitleMessageKey = "viewMovement.guarantor.summary",
          summaryListRows = Seq(
            Some(guarantorArranger),
            guarantorBusinessName,
            guarantorErn,
            guarantorAddress,
            guarantorVatNumber
          ).flatten
        )
      ))
      // Guarantor Type is > 1 digit, then there is 2 guarantors
      //TODO: alignment needed to show 2 guarantors when the guarantorType is > 1 digit
      case _ => HtmlFormat.fill(Seq())
    }
  }

}
