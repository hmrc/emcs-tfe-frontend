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

import base.SpecBase
import fixtures.ItemFixtures
import fixtures.messages.ItemDetailsMessages
import models.common.UnitOfMeasure.Litres15
import models.common.WineProduct
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, HtmlContent}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import views.html.components.{link, list}

class ItemDetailsCardHelperSpec extends SpecBase with ItemFixtures {

  lazy val link = app.injector.instanceOf[link]
  lazy val list = app.injector.instanceOf[list]

  lazy val helper = new ItemDetailsCardHelper(list, link, appConfig)

  private def summaryListRowBuilder(key: Content, value: Content) = SummaryListRow(
    Key(key),
    Value(value),
    classes = "govuk-summary-list__row--no-border"
  )

  "ItemDetailsCardHelper" when {

    Seq(ItemDetailsMessages.English).foreach { langMessages =>

      s"rendered for language code '${langMessages.lang.code}'" should {

        implicit lazy val msgs = messages(Seq(langMessages.lang))

        val item = item1WithWineAndPackaging.copy(unitOfMeasure = Some(Litres15))

        "should show the link for CN Code" when {
          "the CN Code is not S500" in {
            helper.commodityCodeRow()(item, msgs) mustBe Some(summaryListRowBuilder(
              Text(langMessages.commodityCodeKey),
              HtmlContent(link(link = appConfig.getUrlForCommodityCode(item.cnCode), messageKey = item.cnCode, isExternal = true, id = Some("commodity-code")))
            ))
          }
        }

        "should NOT show the link for CN Code" when {
          "the CN Code is S500" in {
            helper.commodityCodeRow()(item.copy(productCode = "S500"), msgs) mustBe Some(summaryListRowBuilder(
              Text(langMessages.commodityCodeKey),
              Text(item.cnCode)
            ))
          }
        }

        "should render the ItemDetails card" when {

          //noinspection ScalaStyle
          def card(wineProduct: Option[WineProduct]): Seq[SummaryListRow] = {
            Seq(
              Seq(summaryListRowBuilder(
                Text(langMessages.commodityCodeKey),
                HtmlContent(link(link = appConfig.getUrlForCommodityCode(item.cnCode), messageKey = item.cnCode, isExternal = true, id = Some("commodity-code")))
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.quantityKey),
                Text(langMessages.quantityValue(item.quantity))
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.grossWeightKey),
                Text(langMessages.grossWeightValue(item.grossMass))
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.netWeightKey),
                Text(langMessages.netWeightValue(item.netMass))
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.densityKey),
                HtmlContent(langMessages.densityValue(item.density.get))
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.alcoholicStrengthKey),
                Text(langMessages.alcoholicStrengthValue(item.alcoholicStrength.get))
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.maturationAgeKey),
                Text(item.maturationAge.get)
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.degreePlatoKey),
                HtmlContent(langMessages.degreePlatoValue(item.degreePlato.get))
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.fiscalMarkKey),
                Text(item.fiscalMark.get)
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.designationOfOriginKey),
                Text(item.designationOfOrigin.get)
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.independentSmallProducersDeclarationKey),
                Text(item.independentSmallProducersDeclaration.get)
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.sizeOfProducerKey),
                Text(langMessages.sizeOfProducerValue(item.sizeOfProducer.get))
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.brandNameOfProductKey),
                Text(item.brandNameOfProduct.get)
              )),
              Seq(summaryListRowBuilder(
                Text(langMessages.commercialDescriptionKey),
                Text(item.commercialDescription.get)
              )),
              wineProduct match {
                case Some(_) => Seq(summaryListRowBuilder(
                  Text(langMessages.wineProductCategoryKey),
                  Text(langMessages.wineWithoutPDOPGI)
                ))
                case None => Seq()
              },
              wineProduct match {
                case Some(value) => Seq(summaryListRowBuilder(
                  Text(langMessages.wineOperationsKey),
                  value.wineOperations match {
                    case Some(values) if values.nonEmpty => HtmlContent(list(values.map(v => Html(v))))
                    case _ => Text(langMessages.wineOperationsValueNone)
                  }
                ))
                case None => Seq()
              },
              wineProduct match {
                case Some(value) => Seq(summaryListRowBuilder(
                  Text(langMessages.wineGrowingZoneCodeKey),
                  Text(value.wineGrowingZoneCode.get)
                ))
                case None => Seq()
              },
              wineProduct match {
                case Some(value) => Seq(summaryListRowBuilder(
                  Text(langMessages.thirdCountryOfOriginKey),
                  Text(value.thirdCountryOfOrigin.get)
                ))
                case None => Seq()
              },
              wineProduct match {
                case Some(value) => Seq(summaryListRowBuilder(
                  Text(langMessages.wineOtherInformationKey),
                  Text(value.otherInformation.get)
                ))
                case None => Seq()
              }
            ).flatten
          }

          "when wineOperations is not empty" in {
            helper.constructItemDetailsCard(item) mustBe card(item.wineProduct)
          }

          "when wineOperations is empty" in {
            val itemWithEmptyWineOperations = item.copy(wineProduct = Some(wineProduct.copy(wineOperations = Some(Seq()))))
            helper.constructItemDetailsCard(itemWithEmptyWineOperations) mustBe card(itemWithEmptyWineOperations.wineProduct)
          }

          "when wineProduct is empty" in {
            val itemWithEmptyWineOperations = item.copy(wineProduct = None)
            helper.constructItemDetailsCard(itemWithEmptyWineOperations) mustBe card(itemWithEmptyWineOperations.wineProduct)
          }
        }

        "should render the PackagingType card" in {

          helper.constructPackagingTypeCard(aerosolPackage) mustBe Seq(
            summaryListRowBuilder(
              Text(langMessages.packagingTypeKey),
              Text(aerosolPackage.typeOfPackage)
            ),
            summaryListRowBuilder(
              Text(langMessages.packagingQuantityKey),
              Text(aerosolPackage.quantity.get.toString())
            ),
            summaryListRowBuilder(
              Text(langMessages.packagingIdentityOfCommercialSealKey),
              Text(aerosolPackage.identityOfCommercialSeal.get)
            ),
            summaryListRowBuilder(
              Text(langMessages.packagingSealInformationKey),
              Text(aerosolPackage.sealInformation.get)
            ),
            summaryListRowBuilder(
              Text(langMessages.packagingShippingMarksKey),
              Text(aerosolPackage.shippingMarks.get)
            )
          )
        }
      }
    }
  }
}
