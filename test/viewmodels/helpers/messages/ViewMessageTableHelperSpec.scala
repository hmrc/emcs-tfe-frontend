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
import fixtures.messages.ViewMessageMessages
import models.requests.DataRequest
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.html.components.GovukTable
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{Table, TableRow}
import views.html.components.{link, list}

class ViewMessageTableHelperSpec extends SpecBase with MessagesFixtures {

  implicit lazy val msgs: Messages = messages(Seq(ViewMessageMessages.English.lang))
  implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  lazy val helper: ViewMessageHelper = app.injector.instanceOf[ViewMessageHelper]

  lazy val govukTable: GovukTable = app.injector.instanceOf[GovukTable]
  lazy val link: link = app.injector.instanceOf[link]
  lazy val list: list = app.injector.instanceOf[list]

  ".constructTable" in {
    val result: Html = helper.constructTable(message1)

    result mustBe
      HtmlFormat.fill(
        Seq(
          govukTable(
            Table(
              firstCellIsHeader = true,
              head = None,
              rows = Seq(
                Seq(
                  TableRow(content = Text(ViewMessageMessages.English.labelMessageType)),
                  TableRow(content = Text(ViewMessageMessages.English.messageTypeDescriptionForIE819))
                ),
                Seq(
                  TableRow(content = Text(ViewMessageMessages.English.labelArc)),
                  TableRow(content = Text(message1.arc.getOrElse("")))
                ),
                Seq(
                  TableRow(content = Text(ViewMessageMessages.English.labelLrn)),
                  TableRow(content = Text(message1.lrn.getOrElse("")))
                )
              )
            )
          )
        )
      )
  }

  ".constructActions" must {

    "return the correct action links" when {

      "the message is an IE819" in {
        val testMessage = createMessage("IE819")
        val result: Html = helper.constructActions(testMessage)

        result mustBe
          HtmlFormat.fill(
            Seq(
              list(
                Seq(
                  link(
                    link = controllers.routes.ViewMovementController.viewMovementOverview(request.ern, testMessage.arc.getOrElse("")).url,
                    messageKey = "View movement",
                    id = Some("view-movement"),
                    classes = "govuk-link"
                  ),
                  link(
                    link = "#print-dialogue",
                    messageKey = "Print message",
                    id = Some("print-link"),
                    classes = "govuk-link"
                  ),
                  link(
                    link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                    messageKey = "Delete message",
                    id = Some("delete-message"),
                    classes = "govuk-link"
                  )
                ),
                extraClasses = Some("govuk-!-display-none-print")
              )
            )
          )
      }
    }
  }

}
