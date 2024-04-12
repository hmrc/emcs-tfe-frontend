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

import models.response.emcsTfe.Packaging
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.helpers.SummaryListHelper.summaryListRowBuilder

import javax.inject.Inject

class ItemPackagingCardHelper @Inject()() {

  def constructPackagingTypeCard(packaging: Packaging)(implicit messages: Messages): Seq[SummaryListRow] = {
    implicit val _packaging = packaging

    Seq(
      typeRow,
      quantityRow,
      identityOfCommercialSealRow,
      sealInformationRow,
      shippingMarksRow
    ).flatten
  }

  private[viewmodels] def typeRow()(implicit packaging: Packaging, messages: Messages): Option[SummaryListRow] =
    Some(summaryListRowBuilder(
      messages("itemDetails.packaging.key.type"),
      packaging.typeOfPackage
    ))

  private[viewmodels] def quantityRow()(implicit packaging: Packaging, messages: Messages): Option[SummaryListRow] =
    packaging.quantity.map { value =>
      summaryListRowBuilder(
        messages("itemDetails.packaging.key.quantity"),
        value.toString()
      )
    }

  private[viewmodels] def identityOfCommercialSealRow()(implicit packaging: Packaging, messages: Messages): Option[SummaryListRow] =
    packaging.identityOfCommercialSeal.map { value =>
      summaryListRowBuilder(
        messages("itemDetails.packaging.key.identityOfCommercialSeal"),
        value
      )
    }

  private[viewmodels] def sealInformationRow()(implicit packaging: Packaging, messages: Messages): Option[SummaryListRow] =
    packaging.sealInformation.map { value =>
      summaryListRowBuilder(
        messages("itemDetails.packaging.key.sealInformation"),
        value
      )
    }

  private[viewmodels] def shippingMarksRow()(implicit packaging: Packaging, messages: Messages): Option[SummaryListRow] =
    packaging.shippingMarks.map { value =>
      summaryListRowBuilder(
        messages("itemDetails.packaging.key.shippingMarks"),
        value
      )
    }
}
