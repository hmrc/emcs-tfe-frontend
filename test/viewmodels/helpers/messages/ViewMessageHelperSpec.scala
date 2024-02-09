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
import uk.gov.hmrc.govukfrontend.views.Aliases.{Key, SummaryListRow, Text, Value}
import uk.gov.hmrc.govukfrontend.views.html.components._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import views.html.components.{link, list}

class ViewMessageHelperSpec extends SpecBase with MessagesFixtures {

  implicit lazy val msgs: Messages = messages(Seq(ViewMessageMessages.English.lang))
  implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  lazy val helper: ViewMessageHelper = app.injector.instanceOf[ViewMessageHelper]

  lazy val govukTable: GovukTable = app.injector.instanceOf[GovukTable]
  lazy val link: link = app.injector.instanceOf[link]
  lazy val list: list = app.injector.instanceOf[list]
  lazy val govukSummaryList: GovukSummaryList = app.injector.instanceOf[GovukSummaryList]

  ".constructMovementInformation" in {
    val result: Html = helper.constructMovementInformation(message1)
    result.toString().replaceAll("\n", "") mustBe govukSummaryList(SummaryList(Seq(
      SummaryListRow(
        key = Key(Text(value = ViewMessageMessages.English.labelMessageType)),
        value = Value(Text(value = ViewMessageMessages.English.messageTypeDescriptionForIE819))
      ),
      SummaryListRow(
        key = Key(Text(value = ViewMessageMessages.English.labelArc)),
        value = Value(Text(value = message1.arc.get))
      ),
      SummaryListRow(
        key = Key(Text(value = ViewMessageMessages.English.labelLrn)),
        value = Value(Text(value = message1.lrn.get))
      )
    ))).toString().replaceAll("\n", "")

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
