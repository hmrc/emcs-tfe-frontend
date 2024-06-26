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
import config.AppConfig
import featureswitch.core.config.{EnableXIPCInCaM, FeatureSwitching, MessageStatisticsNotification}
import fixtures.messages.NavigationBarMessages
import models.PageSection.Movements
import models.common.RoleType
import models.{NavigationBannerInfo, PageSection}
import org.jsoup.Jsoup
import play.api.i18n.Messages
import views.html.components.navigation_bar

class NavigationBarSpec extends SpecBase with FeatureSwitching {

  override implicit lazy val config: AppConfig = app.injector.instanceOf[AppConfig]
  def navigationBar: navigation_bar = app.injector.instanceOf[navigation_bar]

  class Test(messageStatisticsNotificationEnabled: Boolean) {
    if (messageStatisticsNotificationEnabled) enable(MessageStatisticsNotification) else disable(MessageStatisticsNotification)
  }

  "navigation_bar" when {

    Seq(true, false).foreach { messageStatisticsNotificationEnabled =>

      s"MessageStatisticsNotification enabled is '$messageStatisticsNotificationEnabled'" when {

        val newMessageCount = Option.when(messageStatisticsNotificationEnabled)(testMessageStatistics.countOfNewMessages)

        Seq(NavigationBarMessages.English).foreach { messagesForLanguage =>

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

          RoleType.values.filterNot(_ == RoleType.XIPC).foreach {
            roleType =>
              val ern = {
                val prefix = roleType.descriptionKey.split('.').last // accountHome.roleType.GBWK -> GBWK etc

                prefix + "123"
              }

              val html = navigationBar(NavigationBannerInfo(ern, newMessageCount, Movements))
              val doc = Jsoup.parse(html.toString())

              s"Role Type is [${msgs(roleType.descriptionKey)}]" must {
                "render the Home link" in new Test(messageStatisticsNotificationEnabled) {
                  doc.select("#navigation-home-link").text() mustBe messagesForLanguage.home
                }
                "render the Messages link" in new Test(messageStatisticsNotificationEnabled) {
                  doc.select("#navigation-messages-link").text() mustBe messagesForLanguage.messages(newMessageCount)
                }
                if (roleType.canCreateNewMovement(appConfig)) {
                  "render the Drafts link" in new Test(messageStatisticsNotificationEnabled) {
                    doc.select("#navigation-drafts-link").text() mustBe messagesForLanguage.drafts
                  }
                } else {
                  "not render the Drafts link" in new Test(messageStatisticsNotificationEnabled) {
                    doc.select("#navigation-drafts-link").size() mustBe 0
                  }
                }

                "render the Movements link" in new Test(messageStatisticsNotificationEnabled) {
                  doc.select("#navigation-movements-link").text() mustBe messagesForLanguage.movements
                }

                "show the user which page they are currently on" in new Test(messageStatisticsNotificationEnabled) {
                  doc.select("#navigation-movements-link").attr("aria-current") mustBe "page"
                }
              }
          }


          s"Role Type is [${msgs(RoleType.XIPC.descriptionKey)}]" must {
            val ern = "XIPC123"
            def html = navigationBar(NavigationBannerInfo(ern, newMessageCount, Movements))
            def doc = Jsoup.parse(html.toString())
            "render the Home link" in new Test(messageStatisticsNotificationEnabled) {
              doc.select("#navigation-home-link").text() mustBe messagesForLanguage.home
            }
            "render the Messages link" in new Test(messageStatisticsNotificationEnabled) {
              doc.select("#navigation-messages-link").text() mustBe messagesForLanguage.messages(newMessageCount)
            }
            "when enableXIPCInCaM is true" must {
              "render the Drafts link" in new Test(messageStatisticsNotificationEnabled) {
                enable(EnableXIPCInCaM)
                doc.select("#navigation-drafts-link").text() mustBe messagesForLanguage.drafts
              }
            }

            "when enableXIPCInCaM is false" must {
              "not render the Drafts link" in new Test(messageStatisticsNotificationEnabled) {
                disable(EnableXIPCInCaM)
                doc.select("#navigation-drafts-link").size() mustBe 0
              }
            }

            "render the Movements link" in new Test(messageStatisticsNotificationEnabled) {
              doc.select("#navigation-movements-link").text() mustBe messagesForLanguage.movements
            }

            "show the user which page they are currently on" in new Test(messageStatisticsNotificationEnabled) {
              doc.select("#navigation-movements-link").attr("aria-current") mustBe "page"
            }
          }

          PageSection.values.foreach {
            pageSection =>
              s"When page section is [$pageSection]" must {
                "only have one link with an aria-current attribute, where the value is 'page'" in new Test(messageStatisticsNotificationEnabled) {
                  val html = navigationBar(NavigationBannerInfo(testErn, Some(testMessageStatistics.countOfNewMessages), pageSection))
                  val doc = Jsoup.parse(html.toString())

                  doc.select("a[aria-current]").size() mustBe 1
                  doc.select("a[aria-current]").get(0).attr("aria-current") mustBe "page"
                }
              }
          }
        }
      }
    }
  }
}
