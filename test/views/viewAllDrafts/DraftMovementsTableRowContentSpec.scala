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

package views.viewAllDrafts

import base.ViewSpecBase
import fixtures.DraftMovementsFixtures
import fixtures.messages.DraftMovementsMessages
import fixtures.messages.ViewAllDraftMovementsMessages.English
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import views.html.viewAllDrafts.DraftMovementsTableRowContent
import views.{BaseSelectors, ViewBehaviours}


class DraftMovementsTableRowContentSpec extends ViewSpecBase with ViewBehaviours with DraftMovementsFixtures {

  lazy val view: DraftMovementsTableRowContent = app.injector.instanceOf[DraftMovementsTableRowContent]

  object Selectors extends BaseSelectors {
    val h2 = "h2 a"
    val listElement: Int => String = index => s"ul li:nth-of-type($index)"
  }

  "DraftMovementsTableRowContent" when {

    implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))

    s"When rendered in language code of '${English.lang.code}'" must {

      "min data supplied for the Draft Data" must {

        "render the correct content" must {

          implicit val doc: Document = asDocument(view(testErn, draftMovementModelMin))

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.h2 -> draftMovementModelMax.data.lrn
          ))
        }
      }

      "max data supplied for the Draft Data" must {

        "render the correct content" must {

          implicit val doc: Document = asDocument(view(testErn, draftMovementModelMax))

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.h2 -> draftMovementModelMax.data.lrn,
            Selectors.listElement(1) -> English.destinationRowContent(draftMovementModelMax.data.movementScenario.get),
            Selectors.listElement(2) -> English.consigneeRowContent(draftMovementModelMax.data.consigneeReference.get),
            Selectors.listElement(3) -> English.dispatchDateRowContent(draftMovementModelMax.data.dispatchDate.get)
          ))
        }
      }
    }
  }
}
