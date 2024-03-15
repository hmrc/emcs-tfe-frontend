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

package views

import base.SpecBase
import controllers.routes
import models.MovementFilterUndischargedOption.Undischarged
import models.MovementListSearchOptions
import models.common.RoleType._
import models.messages.MessagesSearchOptions
import models.requests.DataRequest
import org.jsoup.Jsoup
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.AccountHomePage

class AccountHomePageViewSpec extends SpecBase {
  lazy val page: AccountHomePage = app.injector.instanceOf[AccountHomePage]
  implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())
  implicit lazy val messages: Messages = messagesApi.preferred(request)

  "The account home page" must {
    Seq(
      GBWK -> "Excise warehousekeeper located in Great Britain",
      XIWK -> "Excise warehousekeeper located in Northern Ireland",
      GBRC -> "Registered consignor located in Great Britain",
      XIRC -> "Registered consignor located in Northern Ireland",
      XI00 -> "Registered consignee located in Northern Ireland",
      XITC -> "Temporary registered consignee located in Northern Ireland",
      XIPA -> "Certified consignor located in Northern Ireland",
      XIPB -> "Certified consignee located in Northern Ireland",
      XIPC -> "Temporary certified consignor located in Northern Ireland",
      XIPD -> "Temporary certified consignee located in Northern Ireland"
    ) foreach {
      case (roleType, roleTypeDescription) =>
        val ern = {
          val prefix = roleType.descriptionKey.split('.').last // accountHome.roleType.GBWK -> GBWK etc

          prefix + "123"
        }

        lazy val doc = Jsoup.parse(page(ern, roleType).toString())

        s"have the correct navigation links for $roleType" in {
          val navigationLinks = doc.getElementsByTag("ul").get(0).children()
          navigationLinks.get(0).text mustBe "Home"
          navigationLinks.get(1).text mustBe s"Messages ${testMessageStatistics.countOfNewMessages}"
          if (roleType.isConsignor) {
            navigationLinks.get(2).text mustBe "Drafts"
            navigationLinks.get(3).text mustBe "Movements"
          } else {
            navigationLinks.get(2).text mustBe "Movements"
          }
        }
        s"have the correct content for $roleType" in {
          doc.getElementsByTag("h2").get(0).text mustBe "This section is Account home"
          doc.getElementsByTag("h1").text mustBe testTraderName
          doc.getElementsByTag("p").get(1).text mustBe roleTypeDescription
          doc.getElementsByTag("p").get(2).text mustBe s"Excise registration number (ERN): $ern"

          doc.getElementsByTag("h2").get(1).text mustBe "Your messages"

          val messagesLinks = doc.getElementsByTag("ul").get(1).children

          messagesLinks.get(0).text mustBe "All messages"
          messagesLinks.get(0).getElementsByTag("a").attr("href") mustBe controllers.messages.routes.ViewAllMessagesController.onPageLoad(ern, MessagesSearchOptions()).url

          doc.getElementsByTag("h2").get(2).text mustBe "Your movements"

          val movementsLinks = doc.getElementsByTag("ul").get(2).children

          movementsLinks.get(0).text mustBe "All movements"

          movementsLinks.get(0).getElementsByTag("a").attr("href") mustBe controllers.routes.ViewAllMovementsController.onPageLoad(ern, MovementListSearchOptions()).url

          movementsLinks.get(1).text mustBe "Undischarged movements"
          movementsLinks.get(1).getElementsByTag("a").attr("href") mustBe
            routes.ViewAllMovementsController.onPageLoad(ern, MovementListSearchOptions( undischargedMovements = Some(Undischarged) )).url

          if (roleType.isConsignor) {
            movementsLinks.get(2).text mustBe "Draft movements"
            //TODO update link location when built
            movementsLinks.get(2).getElementsByTag("a").attr("href") mustBe testOnly.controllers.routes.UnderConstructionController.onPageLoad().url
          } else {
            movementsLinks.text mustNot contain("Draft movements")
          }

          if (roleType.isConsignor) {
            doc.getElementsByTag("p").get(3).text mustBe "Create a new movement"
            doc.getElementsByTag("p").get(3).getElementsByTag("a").get(0).attr("href") mustBe appConfig.emcsTfeCreateMovementUrl(ern)
          } else {
            doc.getElementsByTag("p").text mustNot contain("Create a new movement")
          }

          doc.getElementsByTag("h2").get(3).text mustBe "Prevalidate"
          if (roleType.isDutyPaid) {
            val prevalidateLinks = doc.getElementsByTag("ul").get(3).children
            prevalidateLinks.get(0).text mustBe "Check Europa to find out if a trader can receive excise goods"
            prevalidateLinks.get(0).getElementsByTag("a").get(0).attr("href") mustBe appConfig.europaCheckLink
          } else {
            val prevalidateLinks = doc.getElementsByTag("ul").get(3).children
            prevalidateLinks.get(0).text mustBe "Check if a trader can receive excise goods"
            prevalidateLinks.get(0).getElementsByTag("a").get(0).attr("href") mustBe controllers.prevalidateTrader.routes.StartPrevalidateTraderController.onPageLoad(ern).url
            prevalidateLinks.get(1).text mustBe "Check Europa to find out if a trader can receive excise goods"
            prevalidateLinks.get(1).getElementsByTag("a").get(0).attr("href") mustBe appConfig.europaCheckLink
          }
        }
    }
  }
}
