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

package viewmodels.helpers.messages

import base.SpecBase
import fixtures.MessagesFixtures
import fixtures.messages.ViewAllMessagesMessages
import models.requests.DataRequest
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.html.components.{GovukTable, GovukTag}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table, TableRow}
import uk.gov.hmrc.govukfrontend.views.viewmodels.tag.Tag
import viewmodels.govuk.TagFluency
import views.html.components.link

class ViewAllMessagesTableHelperSpec extends SpecBase with MessagesFixtures with TagFluency {

  implicit lazy val msgs: Messages = messages(Seq(ViewAllMessagesMessages.English.lang))

  implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  lazy val helper: ViewAllMessagesTableHelper = app.injector.instanceOf[ViewAllMessagesTableHelper]
  lazy val govukTable: GovukTable = app.injector.instanceOf[GovukTable]
  lazy val link: link = app.injector.instanceOf[link]

  ".constructTable" when {

    "building the messages HTML table" in {

      val result: Html = helper.constructTable(getMessageResponse.messages)

      result mustBe
        HtmlFormat.fill(
          Seq(
            govukTable(
              Table(
                firstCellIsHeader = true,
                head = Some(Seq(
                  HeadCell(Text(ViewAllMessagesMessages.English.tableMessageHeading)),
                  HeadCell(Text(ViewAllMessagesMessages.English.tableMessageStatus)),
                  HeadCell(Text(ViewAllMessagesMessages.English.tableMessageDate)),
                  HeadCell(Text(ViewAllMessagesMessages.English.tableMessageAction))
                )),
                rows = Seq(
                  Seq(
                    TableRow(
                      content = HtmlContent(link(
                        link = controllers.messages.routes.ViewMessageController.onPageLoad(testErn, message1.uniqueMessageIdentifier).url,
                        messageKey = "Alert or rejection received",
                        hintKey = Some("ARC1001")
                      ))
                    ),
                    TableRow(
                      content = HtmlContent(
                        new GovukTag().apply(
                          Tag(
                            content = Text("UNREAD"),
                            classes = " govuk-tag--blue"
                          )
                        )
                      )
                    ),
                    TableRow(
                      content = Text("5 January 2024")
                    ),
                    TableRow(
                      content = HtmlContent(
                        link(
                          link = controllers.messages.routes.DeleteMessageController.onPageLoad(testErn, message1.uniqueMessageIdentifier).url,
                          messageKey = "Delete"
                        )
                      )
                    )
                  ),
                  Seq(
                    TableRow(
                      content = HtmlContent(link(
                        link = controllers.messages.routes.ViewMessageController.onPageLoad(testErn, message2.uniqueMessageIdentifier).url,
                        messageKey = "Error with report of receipt",
                        hintKey = Some("LRN1001")
                      ))
                    ),
                    TableRow(
                      content = HtmlContent(
                        new GovukTag().apply(
                          Tag(
                            content = Text("READ"),
                            classes = " govuk-tag--grey"
                          )
                        )
                      )
                    ),
                    TableRow(
                      content = Text("6 January 2024")
                    ),
                    TableRow(
                      content = HtmlContent(
                        link(
                          link = controllers.messages.routes.DeleteMessageController.onPageLoad(testErn, message2.uniqueMessageIdentifier).url,
                          messageKey = "Delete"
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        )
    }

  }


}
