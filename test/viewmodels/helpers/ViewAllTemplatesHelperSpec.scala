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

import base.SpecBase
import fixtures.DraftTemplatesFixtures
import fixtures.messages.ViewAllTemplatesMessages.English
import models.draftTemplates.Template
import models.movementScenario.MovementScenario
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, TableRow}
import viewmodels.helpers.events.MovementEventHelper
import views.html.components._

class ViewAllTemplatesHelperSpec extends SpecBase with DraftTemplatesFixtures {

  val templates: Seq[Template] = Seq(
    createTemplate(testErn, "Template1ID", "Template 1", MovementScenario.UkTaxWarehouse.GB, Some("GB001234567890")),
    createTemplate(testErn, "Template2ID", "Template 2", MovementScenario.UkTaxWarehouse.GB, Some("GB001234567890"))
  )

  lazy val list: list = app.injector.instanceOf[list]
  lazy val link: link = app.injector.instanceOf[link]
  lazy val h3: h3 = app.injector.instanceOf[h3]
  lazy val p: p = app.injector.instanceOf[p]
  lazy val movementEventHelper: MovementEventHelper = app.injector.instanceOf[MovementEventHelper]

  val helper: ViewAllTemplatesHelper = new ViewAllTemplatesHelper(list, link, h3, p)

  def createLink(ern: String, templateId: String): String = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url

  def renameLink(ern: String, templateId: String): String = controllers.draftTemplates.routes.RenameTemplateController.onPageLoad(ern, templateId).url

  def deleteLink(ern: String, templateId: String): String = testOnly.controllers.routes.UnderConstructionController.onPageLoad().url

  "ViewAllTemplatesHelper" when {
    Seq(English) foreach { messagesForLanguage =>

      implicit val msgs = messages(Seq(messagesForLanguage.lang))

      s"being rendered in lang code of '${messagesForLanguage.lang.code}'" when {

        ".constructTable" must {
          "have the correct head cell content" in {
            val result = helper.constructTable(testErn, templates)

            result.head.get mustBe Seq(
              HeadCell(HtmlContent(messagesForLanguage.tableHeadingDetails)),
              HeadCell(HtmlContent(messagesForLanguage.tableHeadingActions))
            )
          }

          "construct rows" in {
            val result = helper.constructTable(testErn, templates)

            result.rows.length mustBe templates.length
          }
        }

        ".dataRows" must {
          "construct the correct rows" in {
            val result = helper.dataRows(testErn, templates)

            result mustBe templates.zipWithIndex.map {
              case (template, idx) =>
                Seq(
                  TableRow(
                    content = HtmlContent(HtmlFormat.fill(Seq(
                      h3(template.templateName),
                      p()(Html(messagesForLanguage.destination("Tax warehouse in Great Britain (England, Scotland or Wales)"))),
                      p()(Html(messagesForLanguage.consignee(template.consignee.getOrElse(""))))
                    )))
                  ),
                  TableRow(
                    content = HtmlContent(list(
                      Seq(
                        p()(link(createLink(testErn, template.templateId), messagesForLanguage.actionCreate, Some(s"start-draft-${idx + 1}"))),
                        p()(link(renameLink(testErn, template.templateId), messagesForLanguage.actionRename, Some(s"rename-${idx + 1}"))),
                        p()(link(deleteLink(testErn, template.templateId), messagesForLanguage.actionDelete, Some(s"delete-${idx + 1}")))
                      )
                    ))
                  )
                )
            }
          }
        }
      }
    }
  }
}
