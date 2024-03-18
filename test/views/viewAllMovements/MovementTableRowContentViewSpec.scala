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

package views.viewAllMovements

import base.ViewSpecBase
import fixtures.MovementListFixtures
import fixtures.messages.ViewAllMovementsMessages.English
import models.MovementFilterDirectionOption._
import org.jsoup.nodes.Document
import play.api.i18n.{Messages, MessagesApi}
import views.html.viewAllMovements.MovementTableRowContent
import views.{BaseSelectors, ViewBehaviours}

class MovementTableRowContentViewSpec extends ViewSpecBase with ViewBehaviours with MovementListFixtures {

  lazy val view: MovementTableRowContent = app.injector.instanceOf[MovementTableRowContent]

  object Selectors extends BaseSelectors {
    val h2 = "h2 a"

    val listElement: Int => String = index => s"ul li:nth-of-type($index)"
  }

  "MovementTableRowContent" when {

    implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(English.lang))

    Seq(
      All -> English.movementOtherTraderId,
      GoodsIn -> English.movementConsignor,
      GoodsOut -> English.movementConsignee
    ).foreach { directionAndExpectedMessage =>

      s"render the correct content for ${directionAndExpectedMessage._1}" should {

        implicit val doc: Document = asDocument(view(testErn, movement1, directionAndExpectedMessage._1))

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.h2 -> movement1.arc,
          Selectors.listElement(1) -> directionAndExpectedMessage._2(movement1.otherTraderID),
          Selectors.listElement(2) -> English.dateOfDispatch(movement1.formattedDateOfDispatch)
        ))
      }
    }
  }
}
