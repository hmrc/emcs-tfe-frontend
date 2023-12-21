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
import models.response.emcsTfe.{MovementItem, Packaging}
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, HtmlContent}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryListRow, Value}
import views.html.components.{link, list}

import javax.inject.Inject

class ItemDetailsCardHelper @Inject()(list: list, link: link, appConfig: AppConfig) {

  def constructItemDetailsCard(item: MovementItem)(implicit messages: Messages): Seq[SummaryListRow] = {

    implicit val _item = item

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


  private def summaryListRowBuilder(key: Content, value: Content) = SummaryListRow(
    Key(key),
    Value(value),
    classes = "govuk-summary-list__row--no-border"
  )

  private def wineOtherInformationRow()(implicit item: MovementItem, messages: Messages) =
    item.wineProduct.flatMap(_.otherInformation.map { otherInformation =>
      summaryListRowBuilder(
        Text(messages("itemDetails.key.wineOtherInformation")),
        Text(otherInformation)
      )
    })

  private def thirdCountryOfOriginRow()(implicit item: MovementItem, messages: Messages) =
    item.wineProduct.flatMap(_.thirdCountryOfOrigin.map { country =>
      summaryListRowBuilder(
        Text(messages("itemDetails.key.thirdCountryOfOrigin")),
        Text(country)
      )
    })

  private def wineGrowingZoneCodeRow()(implicit item: MovementItem, messages: Messages) =
    item.wineProduct.flatMap(_.wineGrowingZoneCode.map { zoneCode =>
      summaryListRowBuilder(
        Text(messages("itemDetails.key.wineGrowingZoneCode")),
        Text(zoneCode)
      )
    })

  private def wineOperationsRow()(implicit item: MovementItem, messages: Messages) =
    item.wineProduct.map(_.wineOperations match {
      case Some(values) if values.nonEmpty =>
        summaryListRowBuilder(
          Text(messages("itemDetails.key.wineOperations")),
          HtmlContent(list(values.map(Html(_))))
        )
      case _ =>
        summaryListRowBuilder(
          Text(messages("itemDetails.key.wineOperations")),
          Text(messages("itemDetails.value.wineOperations.none"))
        )
    })

  private def wineProductCategoryRow()(implicit item: MovementItem, messages: Messages) =
    item.wineProduct.map { wineProduct =>
      summaryListRowBuilder(
        Text(messages("itemDetails.key.wineProductCategory")),
        Text(messages(s"wineProductCategory.${wineProduct.wineProductCategory}"))
      )
    }

  private def commercialDescriptionRow()(implicit item: MovementItem, messages: Messages) =
    item.commercialDescription.map { description =>
      summaryListRowBuilder(
        Text(messages("itemDetails.key.commercialDescription")),
        Text(description)
      )
    }

  private def brandNameOfProductRow()(implicit item: MovementItem, messages: Messages) =
    item.brandNameOfProduct.map { brandNameOfProduct =>
      summaryListRowBuilder(
        Text(messages("itemDetails.key.brandNameOfProduct")),
        Text(brandNameOfProduct)
      )
    }

  private def sizeOfProducerRow()(implicit item: MovementItem, messages: Messages) =
    item.sizeOfProducer.map { size =>
      summaryListRowBuilder(
        Text(messages("itemDetails.key.sizeOfProducer")),
        Text(messages("itemDetails.value.sizeOfProducer", size))
      )
    }

  private def designationOfOriginRow()(implicit item: MovementItem, messages: Messages) =
    item.designationOfOrigin.map {
      designation =>
        summaryListRowBuilder(
          Text(messages("itemDetails.key.designationOfOrigin")),
          Text(designation)
        )
    }

  private def fiscalMarkRow()(implicit item: MovementItem, messages: Messages) =
    item.fiscalMark.map { fiscalMark =>
      summaryListRowBuilder(
        Text(messages("itemDetails.key.fiscalMark")),
        Text(fiscalMark)
      )
    }

  private def degreePlatoRow()(implicit item: MovementItem, messages: Messages) =
    item.degreePlato.map { deg =>
      summaryListRowBuilder(
        Text(messages("itemDetails.key.degreePlato")),
        HtmlContent(messages("itemDetails.value.degreePlato", deg))
      )
    }

  private def maturationAgeRow()(implicit item: MovementItem, messages: Messages) =
    item.maturationAge.map { maturationAge =>
      summaryListRowBuilder(
        Text(messages("itemDetails.key.maturationAge")),
        Text(maturationAge)
      )
    }

  private def alcoholicStrengthRow()(implicit item: MovementItem, messages: Messages) =
    item.alcoholicStrength.map { strength =>
      summaryListRowBuilder(
        Text(messages("itemDetails.key.alcoholicStrength")),
        Text(messages("itemDetails.value.alcoholicStrength", strength))
      )
    }

  private def densityRow()(implicit item: MovementItem, messages: Messages) =
    item.density.flatMap { density =>
      item.unitOfMeasure.map { unitOfMeasure =>
        summaryListRowBuilder(
          Text(messages("itemDetails.key.density")),
          HtmlContent(messages("itemDetails.value.density", density.toString(), messages(s"itemDetails.value.density.$unitOfMeasure")))
        )
      }
    }

  private def netWeightRow()(implicit item: MovementItem, messages: Messages) =
    Some(summaryListRowBuilder(
      Text(messages("itemDetails.key.netWeight")),
      Text(messages(
        "itemDetails.value.netWeight",
        item.netMass.toString(),
        messages(s"unitOfMeasure.$Kilograms.short")
      ))
    ))

  private def grossWeightRow()(implicit item: MovementItem, messages: Messages) =
    Some(summaryListRowBuilder(
      Text(messages("itemDetails.key.grossWeight")),
      Text(messages(
        "itemDetails.value.grossWeight",
        item.grossMass.toString(),
        messages(s"unitOfMeasure.$Kilograms.short")
      ))
    ))

  private def quantityRow()(implicit item: MovementItem, messages: Messages) =
    item.unitOfMeasure.map { unitOfMeasure =>
      summaryListRowBuilder(
        Text(messages("itemDetails.key.quantity")),
        Text(messages(
          "itemDetails.value.quantity",
          item.quantity.toString(),
          unitOfMeasure.toLongFormatMessage()
        ))
      )
    }

  private[viewmodels] def commodityCodeRow()(implicit item: MovementItem, messages: Messages) =
    Some(summaryListRowBuilder(
      Text(messages("itemDetails.key.commodityCode")),
      if (item.hasProductCodeWithValidCnCode) {
        HtmlContent(link(link = appConfig.getUrlForCommodityCode(item.cnCode), messageKey = item.cnCode, isExternal = true, id = Some("commodity-code")))
      } else {
        Text(item.cnCode)
      }
    ))


  def constructPackagingTypeCard(packaging: Packaging)(implicit messages: Messages): Seq[SummaryListRow] = {

    val typeRow: Option[SummaryListRow] =
      Some(summaryListRowBuilder(
        Text(messages("itemDetails.packaging.key.type")),
        Text(packaging.typeOfPackage)
      ))

    val quantityRow: Option[SummaryListRow] =
      packaging.quantity.map { value =>
        summaryListRowBuilder(
          Text(messages("itemDetails.packaging.key.quantity")),
          Text(value.toString())
        )
      }

    val identityOfCommercialSealRow: Option[SummaryListRow] =
      packaging.identityOfCommercialSeal.map { value =>
        summaryListRowBuilder(
          Text(messages("itemDetails.packaging.key.identityOfCommercialSeal")),
          Text(value)
        )
      }

    val sealInformationRow: Option[SummaryListRow] =
      packaging.sealInformation.map { value =>
        summaryListRowBuilder(
          Text(messages("itemDetails.packaging.key.sealInformation")),
          Text(value)
        )
      }

    val shippingMarksRow: Option[SummaryListRow] =
      packaging.shippingMarks.map { value =>
        summaryListRowBuilder(
          Text(messages("itemDetails.packaging.key.shippingMarks")),
          Text(value)
        )
      }

    Seq(
      typeRow,
      quantityRow,
      identityOfCommercialSealRow,
      sealInformationRow,
      shippingMarksRow
    ).flatten
  }
}
