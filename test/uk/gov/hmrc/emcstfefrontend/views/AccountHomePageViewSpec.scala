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

package uk.gov.hmrc.emcstfefrontend.views

import org.jsoup.Jsoup
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.emcstfefrontend.support.UnitSpec
import uk.gov.hmrc.emcstfefrontend.views.html.AccountHomePage
import play.api.test.Helpers._
import uk.gov.hmrc.emcstfefrontend.models.common.RoleType._
import uk.gov.hmrc.emcstfefrontend.models.response.emcsTfe.GetMessageStatisticsResponse

class AccountHomePageViewSpec extends UnitSpec {
  lazy val page: AccountHomePage = app.injector.instanceOf[AccountHomePage]
  implicit lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()
  implicit lazy val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(request)
  val testErn = "testErn"
  val testBusinessName = "testBusinessName"
  val testMessageStatistics: GetMessageStatisticsResponse = GetMessageStatisticsResponse(
    "testDateTime",
    testErn,
    1,
    1
  )
  val testEuropaCheckLink = "testEuropaCheckLink"
  val testCreateAMovementLink = "testCreateAMovementLink"

  "The account home page" should {
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
        lazy val doc = Jsoup.parse(page(testErn, roleType, testBusinessName, testMessageStatistics, testEuropaCheckLink, testCreateAMovementLink).toString())

        s"have the correct navigation links for $roleType" in {
          val navigationLinks = doc.getElementsByTag("ul").get(0).children()
          navigationLinks.get(0).text shouldBe "Home"
          navigationLinks.get(1).text shouldBe s"Messages ${testMessageStatistics.countOfNewMessages}"
          navigationLinks.get(2).text shouldBe "Drafts"
          navigationLinks.get(3).text shouldBe "Movements"
        }
        s"have the correct content for $roleType" in {
          doc.getElementsByTag("h2").get(0).text shouldBe "This section is Account home"
          doc.getElementsByTag("h1").text shouldBe testBusinessName
          doc.getElementsByTag("p").get(1).text shouldBe roleTypeDescription
          doc.getElementsByTag("p").get(2).text shouldBe s"Excise registration number (ERN): $testErn"

          doc.getElementsByTag("h2").get(1).text shouldBe "Your messages"
          val messagesLinks = doc.getElementsByTag("ul").get(1).children
          messagesLinks.get(0).text shouldBe "All messages"
          //TODO link location when built

          doc.getElementsByTag("h2").get(2).text shouldBe "Your movements"
          val movementsLinks = doc.getElementsByTag("ul").get(2).children
          movementsLinks.get(0).text shouldBe "All movements"
          //TODO link location when built
          movementsLinks.get(1).text shouldBe "Undischarged movements"
          //TODO link location when built
          movementsLinks.get(2).text shouldBe "Draft movements"
          //TODO link location when built

          if (roleType.isConsignor) {
            doc.getElementsByTag("p").get(3).text shouldBe "Create a new movement"
            doc.getElementsByTag("p").get(3).getElementsByTag("a").get(0).attr("href") shouldBe testCreateAMovementLink
          } else {
            doc.getElementsByTag("p").text shouldNot contain("Create a new movement")
          }


          if (roleType.isNorthernIsland) {
            doc.getElementsByTag("h2").get(3).text shouldBe "Prevalidate"
            val prevalidateLinks = doc.getElementsByTag("ul").get(3).children
            prevalidateLinks.get(0).text shouldBe "Check Europa to find out if a trader can receive excise goods"
            prevalidateLinks.get(0).getElementsByTag("a").get(0).attr("href") shouldBe testEuropaCheckLink
          } else {
            doc.getElementsByTag("h2").text shouldNot contain("Prevalidate")
          }
        }
    }
  }
}
