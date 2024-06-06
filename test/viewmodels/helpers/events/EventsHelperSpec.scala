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

package viewmodels.helpers.events

import base.SpecBase
import fixtures.events.MovementEventMessages
import fixtures.{GetMovementHistoryEventsResponseFixtures, GetMovementResponseFixtures}
import models.requests.DataRequest
import org.jsoup.Jsoup
import play.api.i18n.Messages
import play.api.test.FakeRequest
import views.BaseSelectors

class EventsHelperSpec extends SpecBase
  with GetMovementHistoryEventsResponseFixtures
  with GetMovementResponseFixtures {

  implicit val request: DataRequest[_] = dataRequest(FakeRequest("GET", "/"))

  val helper: EventsHelper = app.injector.instanceOf[EventsHelper]

  implicit lazy val messages: Messages = messages(request)

  object Selectors extends BaseSelectors {
    override val p: Int => String = i => s"p:nth-of-type($i)"
  }

  Seq(MovementEventMessages.English).foreach { messagesForLanguage =>

    s"when language code of '${messagesForLanguage.lang.code}'" should {

      ".constructEventInformation" when {

        "being called with event type IE802 and message role 1 (change destination reminder)" must {

          "render the correct HTML" in {

            val result = helper.constructEventInformation(ie802ChangeDestinationEvent, getMovementResponseModel)
            val body = Jsoup.parse(result.toString())

            body.select(Selectors.p(1)).text() mustBe messagesForLanguage.ie802ChangeDestinationP1
            body.select(Selectors.p(2)).text() mustBe messagesForLanguage.printScreenContent
          }
        }

        "being called with event type IE802 and message role 2 (report of receipt reminder)" must {

          "render the correct HTML" in {

            val result = helper.constructEventInformation(ie802EventReportOfReceipt, getMovementResponseModel)
            val body = Jsoup.parse(result.toString())

            body.select(Selectors.p(1)).text() mustBe messagesForLanguage.ie802ReportReceiptP1
            body.select(Selectors.p(2)).text() mustBe messagesForLanguage.printScreenContent
          }
        }

        "being called with event type IE802 and message role 3 (movement destination reminder)" must {

          "render the correct HTML" in {

            val result = helper.constructEventInformation(ie802MovementDestinationEvent, getMovementResponseModel)
            val body = Jsoup.parse(result.toString())

            body.select(Selectors.p(1)).text() mustBe messagesForLanguage.ie802MovementDestinationP1
            body.select(Selectors.p(2)).text() mustBe messagesForLanguage.printScreenContent
          }
        }
      }
    }
  }
}
