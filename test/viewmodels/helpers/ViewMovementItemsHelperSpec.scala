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
import play.api.test.FakeRequest
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

  implicit val request = dataRequest(FakeRequest())

  val movementResponseWithReferenceData = getMovementResponseModel.copy(items = Seq(
    item1WithWineAndPackagingAndCnCodeInfo,
    item2WithWineAndPackagingAndCnCodeInfo
  ))

  ".constructSelectItems" when {

    Seq(ViewMovementMessages.English -> UnitOfMeasureMessages.English).foreach {
      case (messagesForLang, unitOfMeasureMessages) =>

        s"rendering in language code of '${messagesForLang.lang.code}'" when {

          implicit lazy val msgs = messages(Seq(messagesForLang.lang))

          "report of receipt has been submitted for the movement" when {

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
                    HeadCell(Text(messagesForLang.itemsTablePackagingHeading)),
                    HeadCell(Text(messagesForLang.itemsTableReceiptHeading))
                  )),
                  rows = Seq(
                    Seq(
                      TableRow(
                        content = HtmlContent(link(
                          link = controllers.routes.ItemDetailsController.onPageLoad(testErn, testArc, 1).url,
                          messageKey = messagesForLang.itemsTableItemRow(1)
                        )),
                        classes = "white-space-nowrap"
                      ),
                      TableRow(
                        content = Text(item1WithWineAndPackagingAndCnCodeInfo.commercialDescription.get),
                        classes = "govuk-!-width-one-third"
                      ),
                      TableRow(
                        content = Text(s"${item1WithWineAndPackagingAndCnCodeInfo.quantity} ${unitOfMeasureMessages.kilogramsShort}")
                      ),
                      TableRow(
                        content = HtmlContent(list(item1WithWineAndPackagingAndCnCodeInfo.packaging.map(pckg =>
                          Html(pckg.typeOfPackage)
                        )))
                      ),
                      TableRow(
                        content = HtmlContent(list(Seq(
                          Html(messagesForLang.itemsReceiptStatusExcess),
                          Html(messagesForLang.itemsReceiptStatusShortage),
                          Html(messagesForLang.itemsReceiptStatusDamaged),
                          Html(messagesForLang.itemsReceiptStatusBrokenSeals),
                          Html(messagesForLang.itemsReceiptStatusOther)
                        ))),
                        classes = "white-space-nowrap"
                      )
                    ),
                    Seq(
                      TableRow(
                        content = HtmlContent(link(
                          link = controllers.routes.ItemDetailsController.onPageLoad(testErn, testArc, 2).url,
                          messageKey = messagesForLang.itemsTableItemRow(2)
                        )),
                        classes = "white-space-nowrap"
                      ),
                      TableRow(
                        content = Text(item2WithWineAndPackagingAndCnCodeInfo.commercialDescription.get),
                        classes = "govuk-!-width-one-third"
                      ),
                      TableRow(
                        content = Text(s"${item2WithWineAndPackagingAndCnCodeInfo.quantity} ${unitOfMeasureMessages.kilogramsShort}")
                      ),
                      TableRow(
                        content = HtmlContent(list(item2WithWineAndPackagingAndCnCodeInfo.packaging.map(pckg =>
                          Html(pckg.typeOfPackage)
                        )))
                      ),
                      TableRow(
                        content = Text(messagesForLang.itemsReceiptStatusSatisfactory),
                        classes = "white-space-nowrap"
                      )
                    )
                  )
                ))
              ))
            }
          }

          "report of receipt has NOT been submitted for the movement" must {

            "return a table of all items from the movement formatted with the correct wording" in {

              val result = helper.constructMovementItems(movementResponseWithReferenceData.copy(
                reportOfReceipt = None
              ))

              result mustBe HtmlFormat.fill(Seq(
                h2(messagesForLang.itemsH2),
                govukTable(Table(
                  firstCellIsHeader = true,
                  head = Some(Seq(
                    HeadCell(Text(messagesForLang.itemsTableItemHeading)),
                    HeadCell(Text(messagesForLang.itemsTableCommercialDescriptionHeading)),
                    HeadCell(Text(messagesForLang.itemsTableQuantityHeading)),
                    HeadCell(Text(messagesForLang.itemsTablePackagingHeading)),
                    HeadCell(Text(messagesForLang.itemsTableReceiptHeading))
                  )),
                  rows = Seq(
                    Seq(
                      TableRow(
                        content = HtmlContent(link(
                          link = controllers.routes.ItemDetailsController.onPageLoad(testErn, testArc, 1).url,
                          messageKey = messagesForLang.itemsTableItemRow(1)
                        )),
                        classes = "white-space-nowrap"
                      ),
                      TableRow(
                        content = Text(item1WithWineAndPackagingAndCnCodeInfo.commercialDescription.get),
                        classes = "govuk-!-width-one-third"
                      ),
                      TableRow(
                        content = Text(s"${item1WithWineAndPackagingAndCnCodeInfo.quantity} ${unitOfMeasureMessages.kilogramsShort}")
                      ),
                      TableRow(
                        content = HtmlContent(list(item1WithWineAndPackagingAndCnCodeInfo.packaging.map(pckg =>
                          Html(pckg.typeOfPackage)
                        )))
                      ),
                      TableRow(
                        content = Text(messagesForLang.itemsReceiptStatusNotReceipted),
                        classes = "white-space-nowrap"
                      )
                    ),
                    Seq(
                      TableRow(
                        content = HtmlContent(link(
                          link = controllers.routes.ItemDetailsController.onPageLoad(testErn, testArc, 2).url,
                          messageKey = messagesForLang.itemsTableItemRow(2)
                        )),
                        classes = "white-space-nowrap"
                      ),
                      TableRow(
                        content = Text(item2WithWineAndPackagingAndCnCodeInfo.commercialDescription.get),
                        classes = "govuk-!-width-one-third"
                      ),
                      TableRow(
                        content = Text(s"${item2WithWineAndPackagingAndCnCodeInfo.quantity} ${unitOfMeasureMessages.kilogramsShort}")
                      ),
                      TableRow(
                        content = HtmlContent(list(item2WithWineAndPackagingAndCnCodeInfo.packaging.map(pckg =>
                          Html(pckg.typeOfPackage)
                        )))
                      ),
                      TableRow(
                        content = Text(messagesForLang.itemsReceiptStatusNotReceipted),
                        classes = "white-space-nowrap"
                      )
                    )
                  )
                ))
              ))
            }
          }
        }
    }
  }
}
