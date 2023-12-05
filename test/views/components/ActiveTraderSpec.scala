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

import org.jsoup.Jsoup
import play.api.i18n.MessagesApi
import config.AppConfig
import fixtures.messages.ActiveTraderMessages
import support.UnitSpec
import viewmodels.TraderInfo

class ActiveTraderSpec extends UnitSpec {

  "ActiveTrader" should {

    Seq(ActiveTraderMessages.English).foreach { messagesForLanguage =>

      implicit val messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

      val activeTrader = app.injector.instanceOf[views.html.components.activeTrader]

      lazy val appConfig = app.injector.instanceOf[AppConfig]

      val divSelector = "div.active-trader-info"
      val titleSelector = "div.active-trader-info__title"
      val linkSelector = "a.active-trader-info__link"

      "Should not render the active trader component when no trader info supplied" in {
        val html = activeTrader(None, appConfig)
        val doc = Jsoup.parse(html.toString())

        doc.select(divSelector).size shouldEqual 0
      }

      "Should render the active trader component when trader info supplied" when {

        val traderName = "Greggs"
        val ern = "GB123K0950459403"
        val traderInfo = TraderInfo(traderName, ern)

        "trader info div must exist" in {

          val html = activeTrader(Some(traderInfo), appConfig)
          val doc = Jsoup.parse(html.toString())

          doc.select(divSelector).size shouldEqual 1
        }

        "title must exist" in {
          val html = activeTrader(Some(traderInfo), appConfig)
          val doc = Jsoup.parse(html.toString())

          doc.select(titleSelector).text shouldEqual s"$traderName ($ern)"
        }

        "link must exist" in {
          val html = activeTrader(Some(traderInfo), appConfig)
          val doc = Jsoup.parse(html.toString())

          val link =  doc.select(linkSelector).first

          (link.attr("href"), link.text) shouldEqual ("http://localhost:8310/emcs-tfe", messagesForLanguage.changeTraderType)
        }

      }

    }
  }
}
