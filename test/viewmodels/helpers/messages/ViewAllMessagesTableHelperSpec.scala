package viewmodels.helpers.messages

import base.SpecBase
import fixtures.MessagesFixtures
import fixtures.messages.ViewAllMessagesMessages
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.html.components.{GovukTable, GovukTag}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table, TableRow}
import uk.gov.hmrc.govukfrontend.views.viewmodels.tag.Tag
import viewmodels.govuk.TagFluency
import views.html.components.link

class ViewAllMessagesTableHelperSpec extends SpecBase with MessagesFixtures with TagFluency {

  implicit lazy val msgs = messages(Seq(ViewAllMessagesMessages.English.lang))

  implicit val request = dataRequest(FakeRequest())

  lazy val helper = app.injector.instanceOf[ViewAllMessagesTableHelper]
  lazy val govukTable = app.injector.instanceOf[GovukTable]
  lazy val link = app.injector.instanceOf[link]

  ".constructTable" when {

    "building the messages HTML table" in {

      val result: Html = helper.constructTable(getMessageResponse.messagesData.messages)

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
                        link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                        messageKey = "Report of receipt successful submission",
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
                          link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                          messageKey = "Delete"
                        )
                      )
                    )
                  ),
                  Seq(
                    TableRow(
                      content = HtmlContent(link(
                        link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
                        messageKey = "Report of receipt",
                        hintKey = Some("LRN1002")
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
                          link = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url,
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
