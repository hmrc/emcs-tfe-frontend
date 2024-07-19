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
import fixtures.GetMovementResponseFixtures
import fixtures.messages.ViewMovementMessages
import mocks.services.MockGetDocumentTypesService
import models.DocumentType
import models.response.emcsTfe.GetMovementResponse
import org.jsoup.Jsoup
import play.api.i18n.Messages
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.govuk.TagFluency
import views.html.components.{h2, p, summaryCard}
import views.html.viewMovement.partials.overview_partial

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ViewMovementDocumentHelperSpec extends SpecBase with GetMovementResponseFixtures with TagFluency with MockGetDocumentTypesService {
  lazy val h2: h2 = app.injector.instanceOf[h2]
  lazy val summaryCard: summaryCard = app.injector.instanceOf[summaryCard]
  lazy val p: p = app.injector.instanceOf[p]
  lazy val overviewPartial: overview_partial = app.injector.instanceOf[overview_partial]
  lazy val helper: ViewMovementDocumentHelper = new ViewMovementDocumentHelper(h2, summaryCard, overviewPartial, p, mockGetDocumentTypesService)

  val movementResponseWithReferenceData: GetMovementResponse = getMovementResponseModel
  implicit val hc: HeaderCarrier = HeaderCarrier()

  ".constructMovementDocument" must {
    Seq(ViewMovementMessages.English).foreach {
      messagesForLang =>
        s"when rendering in language code of '${messagesForLang.lang.code}'" must {

          implicit lazy val msgs: Messages = messages(Seq(messagesForLang.lang))

          "return a summary card from the movement formatted with the correct wording" in {
            MockGetDocumentTypesService.getDocumentTypes().returns(Future.successful(Seq(DocumentType("1","Document type description"), DocumentType("2", "Document type description 2"))))
            val result = Jsoup.parse(await(helper.constructMovementDocument(movementResponseWithReferenceData, true)).toString())

            result.getElementsByTag("h2").get(0).text mustBe messagesForLang.documentDetailsHeading
            val summaryCards = result.getElementsByClass("govuk-summary-card")

            val documentSummaryCard = summaryCards.get(0)
            documentSummaryCard.getElementsByTag("h3").text mustBe messagesForLang.documentSummaryHeading(1)
            val documentSummaryCardRows = documentSummaryCard.getElementsByClass("govuk-summary-list__row")
            documentSummaryCardRows.get(0).getElementsByTag("dt").text() mustBe messagesForLang.documentType
            documentSummaryCardRows.get(0).getElementsByTag("dd").text() mustBe "Document type description"
            documentSummaryCardRows.get(1).getElementsByTag("dt").text() mustBe messagesForLang.documentReference
            documentSummaryCardRows.get(1).getElementsByTag("dd").text() mustBe "Document reference"
            documentSummaryCardRows.get(2).getElementsByTag("dt").text() mustBe messagesForLang.documentDescription
            documentSummaryCardRows.get(2).getElementsByTag("dd").text() mustBe "Document description"

            val documentSummaryCard2 = summaryCards.get(1)
            documentSummaryCard2.getElementsByTag("h3").text mustBe messagesForLang.documentSummaryHeading(2)
            val documentSummaryCardRows2 = documentSummaryCard2.getElementsByClass("govuk-summary-list__row")
            documentSummaryCardRows2.get(0).getElementsByTag("dt").text() mustBe messagesForLang.documentType
            documentSummaryCardRows2.get(0).getElementsByTag("dd").text() mustBe "Document type description 2"
            documentSummaryCardRows2.get(1).getElementsByTag("dt").text() mustBe messagesForLang.documentReference
            documentSummaryCardRows2.get(1).getElementsByTag("dd").text() mustBe "Document reference 2"
            documentSummaryCardRows2.get(2).getElementsByTag("dt").text() mustBe messagesForLang.documentDescription
            documentSummaryCardRows2.get(2).getElementsByTag("dd").text() mustBe "Document description 2"

          }
        }
    }
  }
}
