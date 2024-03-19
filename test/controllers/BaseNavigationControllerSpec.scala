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

package controllers

import base.SpecBase
import mocks.services.MockUserAnswersService
import models.auth.UserRequest
import models.{Index, NormalMode, UserAnswers}
import navigation.BaseNavigator
import navigation.FakeNavigators.FakeNavigator
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.QuestionPage
import play.api.libs.json.{JsObject, JsPath, __}
import play.api.mvc.{AnyContentAsEmpty, MessagesControllerComponents, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers.{GET, redirectLocation}
import queries.Derivable
import services.BaseUserAnswersService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class BaseNavigationControllerSpec extends SpecBase with GuiceOneAppPerSuite with MockUserAnswersService {
  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()

    implicit val request: UserRequest[AnyContentAsEmpty.type] = userRequest(FakeRequest(GET, "/foo/bar"))

    val page = new QuestionPage[String] {
      override val path: JsPath = __ \ "page1"
    }
    val page2 = new QuestionPage[String] {
      override val path: JsPath = __ \ "page2"
    }
    val value = "foo"

    case class TestIndexPage(index: Index) extends QuestionPage[String] {
      override val path: JsPath = TestDerivable.path \ index.position \ "test"
    }

    object TestDerivable extends Derivable[Seq[JsObject], Int] {
      override val derive: Seq[JsObject] => Int = _.size

      override val path: JsPath = JsPath \ "something"
    }

    val testNavigator = new FakeNavigator(testOnwardRoute)

    lazy val testController = new BaseNavigationController with BaseController {
      override val userAnswersService: BaseUserAnswersService = mockUserAnswersService
      override val navigator: BaseNavigator = testNavigator

      override protected def controllerComponents: MessagesControllerComponents = messagesControllerComponents
    }
  }

  "saveAndRedirect" when {
    "with currentAnswers" must {
      "save the answer and redirect" when {
        "current UserAnswers doesn't contain the input answer" in new Test {
          val newUserAnswers: UserAnswers = emptyUserAnswers.set(page, value)

          MockUserAnswersService.set().returns(Future.successful(newUserAnswers))

          val answer: Future[Result] =
            testController.saveAndRedirect(page, value, emptyUserAnswers, NormalMode)

          redirectLocation(answer) mustBe Some(testOnwardRoute.url)
        }
      }

      "only redirect" when {
        "current UserAnswers contains the input answer" in new Test {
          val newUserAnswers: UserAnswers = emptyUserAnswers.set(page, value)

          MockUserAnswersService.set().never()

          val answer: Future[Result] =
            testController.saveAndRedirect(page, value, newUserAnswers, NormalMode)

          redirectLocation(answer) mustBe Some(testOnwardRoute.url)
        }
      }
    }

    "without currentAnswers" must {
      "save the answer and redirect" in new Test {
        val newUserAnswers: UserAnswers = emptyUserAnswers.set(page, value)

        MockUserAnswersService.set().returns(Future.successful(newUserAnswers))

        val answer: Future[Result] =
          testController.saveAndRedirect(page, value, NormalMode)(userAnswersRequest(FakeRequest()), implicitly)

        redirectLocation(answer) mustBe Some(testOnwardRoute.url)
      }
    }
  }

  "validateIndex" should {

    "must run the onSuccess function" should {
      "when index is for a new page where index exists in previous items" in new Test {
        val result: Boolean =
          testController.validateIndex(
            TestDerivable, Index(2)
          )(
            true, false
          )(
            userAnswersRequest(FakeRequest(), emptyUserAnswers
              .set(TestIndexPage(Index(0)), "answer")
              .set(TestIndexPage(Index(1)), "answer")
              .set(TestIndexPage(Index(2)), "answer")
              .set(TestIndexPage(Index(3)), "answer")
            ), implicitly
          )

        result mustBe true
      }
    }
    "must run the onFailure function" should {
      "when no user answers present and value is Index(0)" in new Test {
        val result: Boolean =
          testController.validateIndex(TestDerivable, Index(0))(true, false)(userAnswersRequest(FakeRequest(), emptyUserAnswers), implicitly)

        result mustBe false
      }

      "when index is greater than last index in user answers" in new Test {
        val result: Boolean =
          testController.validateIndex(
            TestDerivable, Index(2)
          )(
            true, false
          )(
            userAnswersRequest(FakeRequest(), emptyUserAnswers
              .set(TestIndexPage(Index(0)), "answer")
              .set(TestIndexPage(Index(1)), "answer")
            ), implicitly
          )

        result mustBe false
      }
    }
  }
}
