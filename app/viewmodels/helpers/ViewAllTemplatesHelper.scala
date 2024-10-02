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

import com.google.inject.Inject
import models.Index
import models.draftTemplates.Template
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.table.{HeadCell, Table, TableRow}
import views.html.components._

class ViewAllTemplatesHelper @Inject()(list: list, link: link, h3: h3, p: p) {

  private[viewmodels] def dataRows(ern: String, listOfTemplates: Seq[Template])
                                  (implicit messages: Messages): Seq[Seq[TableRow]] =
    listOfTemplates.zipWithIndex.map {
      case (template, idx) =>
        val index = Index(idx)
        Seq(
          // Content
          TableRow(
            content = HtmlContent(HtmlFormat.fill(Seq(
              h3(template.templateName),
              list(
                Seq(
                  Some(Html(messages("viewAllTemplates.table.destination", messages(s"viewAllTemplates.table.destination.${template.destinationType}")))),
                  template.consigneeBusinessName.map(name =>
                    Html(messages("viewAllTemplates.table.businessName", name))
                  ),
                  template.consigneeERN.map(ern =>
                    Html(messages("viewAllTemplates.table.consignee", ern))
                  )).flatten
              )
            )))
          ),
          // Action links
          TableRow(
            content = HtmlContent(list(
              Seq(
                p()(link(
                  link = controllers.draftTemplates.routes.ConfirmTemplateController.onPageLoad(ern, template.templateId).url,
                  messageKey = "viewAllTemplates.table.startDraft",
                  id = Some(s"start-draft-${index.displayIndex}")
                )),
                p()(link(
                  link = controllers.draftTemplates.routes.RenameTemplateController.onSubmit(ern,template.templateId).url,
                  messageKey = "viewAllTemplates.table.rename",
                  id = Some(s"rename-${index.displayIndex}")
                )),
                p()(link(
                  link = controllers.draftTemplates.routes.DeleteTemplateController.onPageLoad(ern, template.templateId).url,
                  messageKey = "viewAllTemplates.table.delete",
                  id = Some(s"delete-${index.displayIndex}")
                ))
              )
            )),
          )
        )
    }

  def constructTable(ern: String, listOfTemplates: Seq[Template])(implicit messages: Messages): Table = {
    Table(
      rows = dataRows(ern, listOfTemplates)(messages),
      head = Some(Seq(
        HeadCell(
          content = HtmlContent(messages("viewAllTemplates.table.templateDetails"))
        ),
        HeadCell(
          content = HtmlContent(messages("viewAllTemplates.table.actions"))
        )
      ))
    )
  }
}
