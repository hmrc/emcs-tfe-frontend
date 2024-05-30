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
import models.common.UnitOfMeasure.Kilograms
import models.response.emcsTfe.MovementItem
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.helpers.SummaryListHelper.summaryListRowBuilder
import views.html.components.{link, list}

import javax.inject.Inject

class ItemDetailsCardHelper @Inject()(list: list, link: link, appConfig: AppConfig) {

  def constructItemDetailsCard(item: MovementItem)(implicit messages: Messages): Seq[SummaryListRow] = {

    implicit val _item: MovementItem = item

    Seq(
      commodityCodeRow(),
      quantityRow(),
      grossWeightRow(),
      netWeightRow(),
      densityRow(),
      alcoholicStrengthRow(),
      maturationAgeRow(),
      degreePlatoRow(),
      fiscalMarkRow(),
      designationOfOriginRow(),
      independentSmallProducersDeclarationRow(),
      sizeOfProducerRow(),
      brandNameOfProductRow(),
      commercialDescriptionRow(),
      wineProductCategoryRow(),
      wineOperationsRow(),
      wineGrowingZoneCodeRow(),
      thirdCountryOfOriginRow(),
      wineOtherInformationRow()
    ).flatten
  }

  private[viewmodels] def wineOtherInformationRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    item.wineProduct.flatMap(_.otherInformation.map { otherInformation =>
      summaryListRowBuilder(
        messages("itemDetails.key.wineOtherInformation"),
        otherInformation
      )
    })

  private[viewmodels] def thirdCountryOfOriginRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    item.wineProduct.flatMap(_.thirdCountryOfOrigin.map { country =>
      summaryListRowBuilder(
        messages("itemDetails.key.thirdCountryOfOrigin"),
        country
      )
    })

  private[viewmodels] def wineGrowingZoneCodeRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    item.wineProduct.flatMap(_.wineGrowingZoneCode.map { zoneCode =>
      summaryListRowBuilder(
        messages("itemDetails.key.wineGrowingZoneCode"),
        zoneCode
      )
    })

  private[viewmodels] def wineOperationsRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    item.wineProduct.map(_.wineOperations match {
      case Some(values) if values.nonEmpty =>
        summaryListRowBuilder(
          messages("itemDetails.key.wineOperations"),
          list(values.map(Html(_)))
        )
      case _ =>
        summaryListRowBuilder(
          messages("itemDetails.key.wineOperations"),
          messages("itemDetails.value.wineOperations.none")
        )
    })

  private[viewmodels] def wineProductCategoryRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    item.wineProduct.map { wineProduct =>
      summaryListRowBuilder(
        messages("itemDetails.key.wineProductCategory"),
        messages(s"wineProductCategory.${wineProduct.wineProductCategory}")
      )
    }

  private[viewmodels] def commercialDescriptionRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    item.commercialDescription.map { description =>
      summaryListRowBuilder(
        messages("itemDetails.key.commercialDescription"),
        description
      )
    }

  private[viewmodels] def brandNameOfProductRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    item.brandNameOfProduct.map { brandNameOfProduct =>
      summaryListRowBuilder(
        messages("itemDetails.key.brandNameOfProduct"),
        brandNameOfProduct
      )
    }

  private[viewmodels] def sizeOfProducerRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    item.sizeOfProducer.map { size =>
      summaryListRowBuilder(
        messages("itemDetails.key.sizeOfProducer"),
        messages("itemDetails.value.sizeOfProducer", size)
      )
    }

  private[viewmodels] def designationOfOriginRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] = {
    item.designationOfOrigin.map {
      designation =>
        summaryListRowBuilder(
          messages("itemDetails.key.designationOfOrigin"),
          designation
        )
    }
  }

  private[viewmodels] def independentSmallProducersDeclarationRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    item.independentSmallProducersDeclaration.map {
      independentSmallProducersDeclaration =>
        summaryListRowBuilder(
          messages("itemDetails.key.independentSmallProducerDeclaration"),
          independentSmallProducersDeclaration
        )
    }

  private[viewmodels] def fiscalMarksPresentRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] = {
    Some(
      summaryListRowBuilder(
        messages("itemDetails.key.fiscalMarksPresent"),
        messages(s"itemDetails.key.fiscalMarksPresent.${item.fiscalMark.isDefined.toString}")
      )
    )
  }

  private[viewmodels] def fiscalMarkRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    item.fiscalMark.map { fiscalMark =>
      summaryListRowBuilder(
        messages("itemDetails.key.fiscalMark"),
        fiscalMark
      )
    }

  private[viewmodels] def degreePlatoRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    item.degreePlato.map { deg =>
      summaryListRowBuilder(
        messages("itemDetails.key.degreePlato"),
        Html(messages("itemDetails.value.degreePlato", deg))
      )
    }

  private[viewmodels] def maturationAgeRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    item.maturationAge.map { maturationAge =>
      summaryListRowBuilder(
        messages("itemDetails.key.maturationAge"),
        maturationAge
      )
    }

  private[viewmodels] def alcoholicStrengthRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    item.alcoholicStrength.map { strength =>
      summaryListRowBuilder(
        messages("itemDetails.key.alcoholicStrength"),
        messages("itemDetails.value.alcoholicStrength", strength)
      )
    }

  private[viewmodels] def densityRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    item.density.flatMap { density =>
      item.unitOfMeasure.map { unitOfMeasure =>
        summaryListRowBuilder(
          messages("itemDetails.key.density"),
          Html(messages("itemDetails.value.density", density.toString(), messages(s"itemDetails.value.density.$unitOfMeasure")))
        )
      }
    }

  private[viewmodels] def netWeightRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    Some(summaryListRowBuilder(
      messages("itemDetails.key.netWeight"),
      messages(
        "itemDetails.value.netWeight",
        item.netMass.toString(),
        messages(s"unitOfMeasure.$Kilograms.short")
      )
    ))

  private[viewmodels] def grossWeightRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    Some(summaryListRowBuilder(
      messages("itemDetails.key.grossWeight"),
      messages(
        "itemDetails.value.grossWeight",
        item.grossMass.toString(),
        messages(s"unitOfMeasure.$Kilograms.short")
      )
    ))

  private[viewmodels] def quantityRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    item.unitOfMeasure.map { unitOfMeasure =>
      summaryListRowBuilder(
        messages("itemDetails.key.quantity"),
        messages(
          "itemDetails.value.quantity",
          item.quantity.toString(),
          unitOfMeasure.toLongFormatMessage()
        )
      )
    }

  private[viewmodels] def commodityCodeRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    Some(summaryListRowBuilder(
      messages("itemDetails.key.commodityCode"),
      if (item.hasProductCodeWithValidCnCode) {
        link(link = appConfig.getUrlForCommodityCode(item.cnCode), messageKey = item.cnCode, isExternal = true, id = Some("commodity-code"))
      } else {
        Html(item.cnCode)
      }
    ))

  private[viewmodels] def exciseProductCodeRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] =
    Some(summaryListRowBuilder(
      messages("itemDetails.key.exciseProductCode"),
      item.productCode
    ))

  private[viewmodels] def allPackagingQuantitiesAndTypesRow()(implicit item: MovementItem, messages: Messages): Option[SummaryListRow] = {
    Some(
      summaryListRowBuilder(
        messages("itemDetails.key.packaging"),
        list(
          item.packaging.map { packaging =>
            Html(s"${packaging.quantity.get} x ${packaging.typeOfPackage}")
          }
        )
      )
    )
  }

}
