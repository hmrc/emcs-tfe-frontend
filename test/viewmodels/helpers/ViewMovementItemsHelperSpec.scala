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
import fixtures.GetMovementResponseFixtures
import fixtures.messages.{UnitOfMeasureMessages, ViewMovementMessages}
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.{HeadCell, HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.html.components.GovukTable
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{Table, TableRow}
import viewmodels.govuk.TagFluency
import views.html.components.{h2, link, list}

class ViewMovementItemsHelperSpec extends SpecBase with GetMovementResponseFixtures with TagFluency {

  lazy val helper = app.injector.instanceOf[ViewMovementItemsHelper]
  lazy val govukTable = app.injector.instanceOf[GovukTable]
  lazy val link = app.injector.instanceOf[link]
  lazy val list = app.injector.instanceOf[list]
  lazy val h2 = app.injector.instanceOf[h2]

  val movementResponseWithReferenceData = getMovementResponseModel.copy(items = Seq(
    item1WithPackagingAndUnitOfMeasure,
    item2WithPackagingAndUnitOfMeasure
  ))

  ".constructSelectItems" must {

    Seq(ViewMovementMessages.English -> UnitOfMeasureMessages.English).foreach {
      case (messagesForLang, unitOfMeasureMessages) =>

      s"when rendering in language code of '${messagesForLang.lang.code}'" must {

        implicit lazy val msgs = messages(Seq(messagesForLang.lang))

        "return a table of all items from the movement formatted with the correct wording" in {

          val result = helper.constructMovementItems(movementResponseWithReferenceData)

          result mustBe HtmlFormat.fill(Seq(
            h2(messagesForLang.itemsH2),
            govukTable(Table(
              firstCellIsHeader = true,
              head = Some(Seq(
                HeadCell(Text(messagesForLang.itemsTableItemHeading)),
                HeadCell(Text(messagesForLang.itemsTableCommercialDescriptionHeading)),
                HeadCell(Text(messagesForLang.itemsTableQuantityHeading)),
                HeadCell(Text(messagesForLang.itemsTablePackagingHeading))
//              TODO: Add in as part of ETFE-2882
//              HeadCell(Text(messagesForLang.itemsTableReceiptHeading))
              )),
              rows = Seq(
                Seq(
                  TableRow(
                    content = HtmlContent(link(
                      link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                      messageKey = messagesForLang.itemsTableItemRow(1)
                    )),
                    classes = "white-space-nowrap"
                  ),
                  TableRow(
                    content = Text(item1WithPackagingAndUnitOfMeasure.commercialDescription.get),
                    classes = "govuk-!-width-one-third"
                  ),
                  TableRow(
                    content = Text(s"${item1WithPackagingAndUnitOfMeasure.quantity} ${unitOfMeasureMessages.kilogramsShort}")
                  ),
                  TableRow(
                    content = HtmlContent(list(item1WithPackagingAndUnitOfMeasure.packaging.map(pckg =>
                      Html(pckg.typeOfPackage)
                    )))
                  )
//                TODO: Add in as part of ETFE-2882
//                TableRow(
//                  content = HtmlContent(govukTag(TagViewModel(Text(messagesForLang.itemsReceiptStatusNotReceipted)).grey())),
//                  classes = "white-space-nowrap"
//                )
                ),
                Seq(
                  TableRow(
                    content = HtmlContent(link(
                      link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                      messageKey = messagesForLang.itemsTableItemRow(2)
                    )),
                    classes = "white-space-nowrap"
                  ),
                  TableRow(
                    content = Text(item2WithPackagingAndUnitOfMeasure.commercialDescription.get),
                    classes = "govuk-!-width-one-third"
                  ),
                  TableRow(
                    content = Text(s"${item2WithPackagingAndUnitOfMeasure.quantity} ${unitOfMeasureMessages.kilogramsShort}")
                  ),
                  TableRow(
                    content = HtmlContent(list(item2WithPackagingAndUnitOfMeasure.packaging.map(pckg =>
                      Html(pckg.typeOfPackage)
                    )))
                  )
//                TODO: Add in as part of ETFE-2882
//                TableRow(
//                  content = HtmlContent(govukTag(TagViewModel(Text(messagesForLang.itemsReceiptStatusNotReceipted)).grey())),
//                  classes = "white-space-nowrap"
//                )
                )
              )
            ))
          ))
        }
      }
    }
  }
}
