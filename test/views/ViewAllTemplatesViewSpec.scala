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

package views

import base.ViewSpecBase
import config.AppConfig
import fixtures.messages.ViewAllTemplatesMessages.English
import models.common.DestinationType
import models.draftTemplates.Template
import models.requests.DataRequest
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import utils.PaginationUtil
import views.html.ViewAllTemplatesView

class ViewAllTemplatesViewSpec extends ViewSpecBase with ViewBehaviours {
  lazy val view: ViewAllTemplatesView = app.injector.instanceOf[ViewAllTemplatesView]
  implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())

  object Selectors extends BaseSelectors {
    val tableSelector = "#main-content table"
    val pagination = "#main-content .govuk-pagination"
  }

  "view" must {

    Seq(English) foreach { messagesForLanguage =>

      implicit val msgs = messages(Seq(messagesForLanguage.lang))
      implicit val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

      s"render the view when being rendered in lang code of '${messagesForLanguage.lang.code}'" when {
        "list of templates is empty" when {
          implicit val doc = asDocument(view(Seq.empty, None))

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.p(1) -> messagesForLanguage.noTemplatesP1,
            Selectors.p(2) -> messagesForLanguage.noTemplatesP2,
            Selectors.button -> messagesForLanguage.createNewMovementButton
          ))

          "not display a table" in {
            doc.select(Selectors.tableSelector).size() mustBe 0
          }

          "not display pagination" in {
            doc.select(Selectors.pagination).size() mustBe 0
          }
        }

        "list of templates is not empty" when {
          val templates: Seq[Template] = Seq(
            Template("1", "Template 1", DestinationType.TaxWarehouse, Some("GB001234567890")),
            Template("2", "Template 2", DestinationType.TaxWarehouse, Some("GB001234567890")),
            Template("3", "Template 3", DestinationType.TaxWarehouse, Some("XI001234567890")),
            Template("4", "Template 4", DestinationType.TaxWarehouse, Some("IE001234567890")),
            Template("5", "Template 5", DestinationType.TaxWarehouse, None),
            Template("6", "Template 6", DestinationType.Export, Some("GB001234567890")),
            Template("7", "Template 7", DestinationType.Export, Some("GB001234567890")),
            Template("8", "Template 8", DestinationType.Export, Some("XI001234567890")),
            Template("9", "Template 9", DestinationType.Export, Some("IE001234567890")),
            Template("10", "Template 10", DestinationType.Export, None)
          )

          val paginationHelper = new PaginationUtil {
            override val link: Int => String =
              (index: Int) => controllers.draftTemplates.routes.ViewAllTemplatesController.onPageLoad(testErn, Some(index)).url

            override val currentPage: Int = 1
            override val pages: Int = 5
          }

          implicit val doc = asDocument(view(templates, paginationHelper.constructPagination()))

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.p(1) -> messagesForLanguage.p1,
          ))

          "display a table" in {
            doc.select(Selectors.tableSelector).size() mustBe 1
          }

          "display pagination" in {
            doc.select(Selectors.pagination).size() mustBe 1
          }
        }
      }
    }
  }
}
