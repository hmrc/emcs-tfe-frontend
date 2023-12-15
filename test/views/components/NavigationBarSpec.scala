/*
 * Copyright 2023 HM Revenue & Customs
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

package views.components

import base.SpecBase
import fixtures.messages.NavigationBarMessages
import models.NavigationBannerInfo
import models.common.RoleType
import models.response.emcsTfe.GetMessageStatisticsResponse
import org.jsoup.Jsoup
import play.api.i18n.Messages
import views.html.components.navigation_bar

class NavigationBarSpec extends SpecBase {

  override val testMessageStatistics: GetMessageStatisticsResponse = GetMessageStatisticsResponse(
    dateTime = "testDateTime",
    exciseRegistrationNumber = testErn,
    countOfAllMessages = 10,
    countOfNewMessages = 5
  )

  val navigation_bar: navigation_bar = app.injector.instanceOf[navigation_bar]

  "navigation_bar" when {

    Seq(NavigationBarMessages.English).foreach { messagesForLanguage =>

      implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      RoleType.values.foreach {
        roleType =>
          val ern = {
            val prefix = roleType.descriptionKey.split('.').last // accountHome.roleType.GBWK -> GBWK etc

            prefix + "123"
          }
          val html = navigation_bar(NavigationBannerInfo(ern, testMessageStatistics.countOfNewMessages))
          val doc = Jsoup.parse(html.toString())

          s"Role Type is [${msgs(roleType.descriptionKey)}]" must {
            "render the Home link" in {
              doc.select("#navigation-home-link").text() mustBe messagesForLanguage.home
            }
            "render the Messages link" in {
              doc.select("#navigation-messages-link").text() mustBe messagesForLanguage.messages(testMessageStatistics.countOfNewMessages)
            }
            if (roleType.isConsignor) {
              "render the Drafts link" in {
                doc.select("#navigation-drafts-link").text() mustBe messagesForLanguage.drafts
              }
            } else {
              "not render the Drafts link" in {
                doc.select("#navigation-drafts-link").size() mustBe 0
              }
            }
            "render the Movements link" in {
              doc.select("#navigation-movements-link").text() mustBe messagesForLanguage.movements
            }
          }
      }

    }
  }
}
