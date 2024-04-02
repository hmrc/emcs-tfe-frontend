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

package views.prevalidateTrader

import base.ViewSpecBase
import config.AppConfig
import fixtures.messages.prevalidateTrader.PrevalidateTraderResultsMessages.English
import fixtures.{ExciseProductCodeFixtures, ItemFixtures}
import mocks.services.MockGetCnCodeInformationService
import models.ExciseProductCode
import models.requests.UserAnswersRequest
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier
import views.html.prevalidateTrader.PrevalidateTraderResultsView
import views.{BaseSelectors, ViewBehaviours}

import scala.concurrent.ExecutionContext

class PrevalidateTraderResultsViewSpec extends ViewSpecBase
  with ViewBehaviours
  with ItemFixtures
  with ExciseProductCodeFixtures
  with MockGetCnCodeInformationService {

  lazy val view = app.injector.instanceOf[PrevalidateTraderResultsView]

  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit val config: AppConfig = appConfig

  object Selectors extends BaseSelectors {
    val noErnLink = "#no-ern-link"
    val addCodeLink = "#add-code-link"
    val differentTraderLInk = "#different-trader-link"
    val returnToAccountLink = "#return-to-account-link"
    val feedbackLink = "#feedback-link"
  }

  "PrevalidateTraderResultsView" when {

    s"being rendered in language code of '${English.lang.code}'" when {

      implicit val msgs: Messages = messages(Seq(English.lang))

      "rendered for when there the ERN is invalid" when {

        implicit val request: UserAnswersRequest[AnyContentAsEmpty.type] = userAnswersRequest(FakeRequest(), emptyUserAnswers)

        implicit val doc: Document = Jsoup.parse(view(
          ernOpt = None,
          addCodeCall = testOnwardRoute,
          approved = Seq.empty,
          notApproved = Seq.empty
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> English.titleInvalidErn,
          Selectors.subHeadingCaptionSelector -> English.prevalidateTraderCaption,
          Selectors.h1 -> English.headingInvalidErn,
          Selectors.p(1) -> English.invalidErnLink,
          Selectors.p(2) -> English.linkReturnToAccount,
          Selectors.p(3) -> English.linkFeedback,
          Selectors.feedbackLink -> English.linkFeedback
        ))

        Seq(
          Selectors.noErnLink -> controllers.prevalidateTrader.routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(testErn).url,
          Selectors.returnToAccountLink -> controllers.routes.AccountHomeController.viewAccountHome(testErn).url,
          Selectors.feedbackLink -> appConfig.feedbackFrontendSurveyUrl
        ) foreach { case (selector, route) =>

          s"for link $selector have the correct route" in {
            doc.select(selector).attr("href") mustBe route
          }
        }
      }

      "rendered for when there is a valid ERN and codes that are approved" when {

        val exciseProductCodes = Seq(tobaccoExciseProductCode, beerExciseProductCode, wineExciseProductCode)

        implicit val request: UserAnswersRequest[AnyContentAsEmpty.type] = userAnswersRequest(FakeRequest(), emptyUserAnswers)

        implicit val doc: Document = Jsoup.parse(view(
          ernOpt = Some(testErn),
          addCodeCall = testOnwardRoute,
          approved = exciseProductCodes,
          notApproved = Seq.empty
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> English.title,
          Selectors.subHeadingCaptionSelector -> English.prevalidateTraderCaption,
          Selectors.h1 -> English.heading,
          Selectors.p(1) -> English.p1(testErn),
          Selectors.h2(2) -> English.h2Approved,
          Selectors.bullet(1) -> English.bullet(tobaccoExciseProductCode),
          Selectors.bullet(2) -> English.bullet(beerExciseProductCode),
          Selectors.bullet(3) -> English.bullet(wineExciseProductCode),
          Selectors.p(2) -> English.linkAddCode,
          Selectors.p(3) -> English.linkDifferentTrader,
          Selectors.p(4) -> English.linkReturnToAccount,
          Selectors.p(5) -> English.feedback,
          Selectors.feedbackLink -> English.linkFeedback
        ))

        Seq(
          Selectors.addCodeLink -> testOnwardRoute.url,
          Selectors.differentTraderLInk -> controllers.prevalidateTrader.routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(testErn).url,
          Selectors.returnToAccountLink -> controllers.routes.AccountHomeController.viewAccountHome(testErn).url,
          Selectors.feedbackLink -> appConfig.feedbackFrontendSurveyUrl
        ) foreach { case (selector, route) =>

          s"for link $selector have the correct route" in {
            doc.select(selector).attr("href") mustBe route
          }
        }
      }

      "rendered for when there is a valid ERN and codes that are not approved" when {

        val exciseProductCodes = Seq(beerExciseProductCode, wineExciseProductCode, tobaccoExciseProductCode)

        implicit val request: UserAnswersRequest[AnyContentAsEmpty.type] = userAnswersRequest(FakeRequest(), emptyUserAnswers)

        implicit val doc: Document = Jsoup.parse(view(
          ernOpt = Some(testErn),
          addCodeCall = testOnwardRoute,
          approved = Seq.empty,
          notApproved = exciseProductCodes
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> English.title,
          Selectors.subHeadingCaptionSelector -> English.prevalidateTraderCaption,
          Selectors.h1 -> English.heading,
          Selectors.p(1) -> English.p1(testErn),
          Selectors.h2(2) -> English.h2NotApproved,
          Selectors.bullet(1) -> English.bullet(beerExciseProductCode),
          Selectors.bullet(2) -> English.bullet(wineExciseProductCode),
          Selectors.bullet(3) -> English.bullet(tobaccoExciseProductCode),
          Selectors.p(2) -> English.linkAddCode,
          Selectors.p(3) -> English.linkDifferentTrader,
          Selectors.p(4) -> English.linkReturnToAccount,
          Selectors.p(5) -> English.feedback,
          Selectors.feedbackLink -> English.linkFeedback
        ))

        Seq(
          Selectors.addCodeLink -> testOnwardRoute.url,
          Selectors.differentTraderLInk -> controllers.prevalidateTrader.routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(testErn).url,
          Selectors.returnToAccountLink -> controllers.routes.AccountHomeController.viewAccountHome(testErn).url,
          Selectors.feedbackLink -> appConfig.feedbackFrontendSurveyUrl
        ) foreach { case (selector, route) =>

          s"for link $selector have the correct route" in {
            doc.select(selector).attr("href") mustBe route
          }
        }
      }

      "rendered for when there is a valid ERN and codes that are approved and not approved" when {

        val approved: Seq[ExciseProductCode] = Seq(wineExciseProductCode)
        val notApproved: Seq[ExciseProductCode] = Seq(beerExciseProductCode, tobaccoExciseProductCode)

        implicit val request: UserAnswersRequest[AnyContentAsEmpty.type] = userAnswersRequest(FakeRequest(), emptyUserAnswers)

        implicit val doc: Document = Jsoup.parse(view(
          ernOpt = Some(testErn),
          addCodeCall = testOnwardRoute,
          approved = approved,
          notApproved = notApproved
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> English.title,
          Selectors.subHeadingCaptionSelector -> English.prevalidateTraderCaption,
          Selectors.h1 -> English.heading,
          Selectors.p(1) -> English.p1(testErn),
          Selectors.h2(2) -> English.h2Approved,
          Selectors.bullet(1) -> English.bullet(wineExciseProductCode),
          Selectors.h2(3) -> English.h2NotApproved,
          Selectors.bullet(1, 2) -> English.bullet(beerExciseProductCode),
          Selectors.bullet(2, 2) -> English.bullet(tobaccoExciseProductCode),
          Selectors.p(2) -> English.linkAddCode,
          Selectors.p(3) -> English.linkDifferentTrader,
          Selectors.p(4) -> English.linkReturnToAccount,
          Selectors.p(5) -> English.feedback,
          Selectors.feedbackLink -> English.linkFeedback
        ))

        Seq(
          Selectors.addCodeLink -> testOnwardRoute.url,
          Selectors.differentTraderLInk -> controllers.prevalidateTrader.routes.PrevalidateConsigneeTraderIdentificationController.onPageLoad(testErn).url,
          Selectors.returnToAccountLink -> controllers.routes.AccountHomeController.viewAccountHome(testErn).url,
          Selectors.feedbackLink -> appConfig.feedbackFrontendSurveyUrl
        ) foreach { case (selector, route) =>

          s"for link $selector have the correct route" in {
            doc.select(selector).attr("href") mustBe route
          }
        }
      }
    }
  }
}

