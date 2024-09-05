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
import config.AppConfig
import controllers.routes
import featureswitch.core.config.{AccountHomeBanner, FeatureSwitching}
import models.MovementFilterUndischargedOption.Undischarged
import models.MovementListSearchOptions
import models.common.RoleType._
import models.draftMovements.GetDraftMovementsSearchOptions
import models.messages.MessagesSearchOptions
import models.requests.DataRequest
import org.jsoup.Jsoup
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import views.html.AccountHomeView

class AccountHomeViewSpec extends SpecBase with FeatureSwitching {
  override lazy val config: AppConfig = app.injector.instanceOf[AppConfig]
  lazy val page: AccountHomeView = app.injector.instanceOf[AccountHomeView]
  implicit lazy val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest())
  implicit lazy val messages: Messages = messagesApi.preferred(request)

  "The account home page" must {
    Seq(
      GBWK -> "Excise warehousekeeper located in Great Britain",
      XIWK -> "Excise warehousekeeper located in Northern Ireland",
      GBRC -> "Registered consignor located in Great Britain",
      XIRC -> "Registered consignor located in Northern Ireland",
      XI00 -> "Registered consignee located in Northern Ireland",
      GB00 -> "Tax warehouse or premises located in Great Britain",
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

        def doc = Jsoup.parse(page(ern, roleType).toString())

        if (roleType == XIPC) {
          s"have the correct navigation links for $roleType" in {
            val navigationLinks = doc.getElementsByClass("hmrc-account-menu__link")
            navigationLinks.get(0).text mustBe "Home"
            navigationLinks.get(1).text mustBe s"Messages ${testMessageStatistics.countOfNewMessages}"
            navigationLinks.get(2).text mustBe "Drafts"
            navigationLinks.get(3).text mustBe "Movements"
            navigationLinks.get(4).text mustBe "Templates"
            navigationLinks.get(5).text mustBe "Menu"
            navigationLinks.get(5).hasClass("hidden") mustBe true
            navigationLinks.get(6).text mustBe "Business tax account"
          }
        } else {
          s"have the correct navigation links for $roleType" in {
            val navigationLinks = doc.getElementsByClass("hmrc-account-menu__link")
            navigationLinks.get(0).text mustBe "Home"
            navigationLinks.get(1).text mustBe s"Messages ${testMessageStatistics.countOfNewMessages}"
            if (roleType.canCreateNewMovement) {
              navigationLinks.get(2).text mustBe "Drafts"
              navigationLinks.get(3).text mustBe "Movements"
              navigationLinks.get(4).text mustBe "Templates"
              navigationLinks.get(5).text mustBe "Menu"
              navigationLinks.get(5).hasClass("hidden") mustBe true
              navigationLinks.get(6).text mustBe "Business tax account"
            } else {
              navigationLinks.get(2).text mustBe "Movements"
              navigationLinks.get(3).text mustBe "Menu"
              navigationLinks.get(3).hasClass("hidden") mustBe true
              navigationLinks.get(4).text mustBe "Business tax account"
            }
          }
        }
        s"have the correct content for $roleType" in {
          disable(AccountHomeBanner)
          doc.getElementsByTag("h2").get(0).text mustBe "This section is Account home"
          doc.getElementsByTag("h1").text mustBe testTraderName
          doc.getElementsByTag("p").get(1).text mustBe roleTypeDescription
          doc.getElementsByTag("p").get(2).text mustBe s"Excise registration number (ERN): $ern"

          doc.getElementsByTag("h2").get(1).text mustBe "Your messages"

          val messagesLinks = doc.getElementsByTag("ul").get(1).children

          messagesLinks.get(0).text mustBe "All messages"
          messagesLinks.get(0).getElementsByTag("a").attr("href") mustBe controllers.messages.routes.ViewAllMessagesController.onPageLoad(ern, MessagesSearchOptions()).url

          doc.getElementsByTag("h2").get(2).text mustBe "Your movements"

          def movementsLinks = doc.getElementsByTag("ul").get(2).children

          movementsLinks.get(0).text mustBe "All movements"

          movementsLinks.get(0).getElementsByTag("a").attr("href") mustBe controllers.routes.ViewAllMovementsController.onPageLoad(ern, MovementListSearchOptions()).url

          movementsLinks.get(1).text mustBe "Undischarged movements"
          movementsLinks.get(1).getElementsByTag("a").attr("href") mustBe
            routes.ViewAllMovementsController.onPageLoad(ern, MovementListSearchOptions(undischargedMovements = Some(Undischarged))).url

          if (roleType == XIPC) {
            movementsLinks.get(2).text mustBe "Draft movements"
            movementsLinks.get(2).getElementsByTag("a").attr("href") mustBe controllers.drafts.routes.ViewAllDraftMovementsController.onPageLoad(ern, GetDraftMovementsSearchOptions()).url
          } else {
            if (roleType.canCreateNewMovement) {
              movementsLinks.get(2).text mustBe "Draft movements"
              movementsLinks.get(2).getElementsByTag("a").attr("href") mustBe controllers.drafts.routes.ViewAllDraftMovementsController.onPageLoad(ern, GetDraftMovementsSearchOptions()).url
            } else {
              movementsLinks.text mustNot contain("Draft movements")
            }
          }

          if (roleType.canCreateNewMovement) {
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
            prevalidateLinks.get(0).getElementsByTag("a").get(0).attr("href") mustBe controllers.prevalidateTrader.routes.PrevalidateTraderStartController.onPageLoad(ern).url
            prevalidateLinks.get(1).text mustBe "Check Europa to find out if a trader can receive excise goods"
            prevalidateLinks.get(1).getElementsByTag("a").get(0).attr("href") mustBe appConfig.europaCheckLink
          }

          doc.getElementsByTag("h2").get(4).text mustBe "Business tax account"
          val btaLink = doc.getElementById("bta-link")
          btaLink.text mustBe "Go to your business tax account"
          btaLink.attr("href") mustBe appConfig.businessTaxAccountUrl
        }
    }

    "when the trader name is not in known facts" must {

      "use the ERN as the H1 and not output the ERN paragraph" in {

        def doc = Jsoup.parse(page(testErn, GBWK)(
          dataRequest(FakeRequest(), knownFacts = Some(testMinTraderKnownFacts.copy(traderName = ""))), messages
        ).toString())

        doc.getElementsByTag("h1").text mustBe testErn
        doc.getElementsByTag("p").get(1).text mustBe "Excise warehousekeeper located in Great Britain"
        doc.getElementsByTag("p").get(2).text mustNot be(s"Excise registration number (ERN): $testErn")
      }
    }

    "show the banner and its content when enabled" in {
      enable(AccountHomeBanner)

      def doc = Jsoup.parse(page(testErn, GBWK)(
        dataRequest(FakeRequest(), knownFacts = Some(testMinTraderKnownFacts.copy(traderName = ""))), messages
      ).toString())

      val bannerElements = doc.getElementById("banner")
      bannerElements.getElementsByClass("govuk-notification-banner__title").get(0).text mustBe "Important"
      bannerElements.getElementsByTag("p").get(0).text mustBe "EMCS will unavailable from midday on Friday 16 August 2024 to 10am on Monday 19 August 2024, due to scheduled updates."
      bannerElements.getElementsByTag("p").get(1).text mustBe "Movements must follow fallback procedures during this time."
      bannerElements.getElementsByTag("p").get(2).text mustBe "Updates are part of a wider plan to improve EMCS and bring it in line with other HMRC services."
    }
  }
}
