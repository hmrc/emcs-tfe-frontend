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

/*
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import pages.QuestionPage
import play.api.libs.json._
import support.UnitSpec


class UserAnswersSpec extends UnitSpec {

  val userAnswers = UserAnswers("someId")
  object TestPage extends QuestionPage[String] {
    override def path: JsPath = JsPath \ toString
    override def toString: String = "TestPage"
    override def cleanup(value: Option[String], userAnswers: UserAnswers): UserAnswers = {
      userAnswers.remove(TestPage2)
    }
  }

  object TestPage2 extends QuestionPage[String] {
    override def path: JsPath = JsPath \ toString
    override def toString: String = "TestPage2"
  }

  "UserAnswers" when {

    "calling .set(page)" must {

      "no data exists for that page" must {

        "set the answer for the first time" in {
          userAnswers.set(TestPage, "foo") shouldBe userAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
        }
      }

      "data exists for that page" must {

        "change the answer" in {
          val withData = userAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
          withData.set(TestPage, "bar") shouldBe userAnswers.copy(data = Json.obj(
            "TestPage" -> "bar"
          ))
        }
      }

      "data exists for that page and TestPage2 exists" must {

        "change the answer AND remove TestPage2 as part of the cleanup rules" in {
          val withData = userAnswers.copy(data = Json.obj(
            "TestPage" -> "foo",
            "TestPage2" -> "bar",
          ))
          withData.set(TestPage, "bar") shouldBe userAnswers.copy(data = Json.obj(
            "TestPage" -> "bar"
          ))
        }
      }
    }

    "calling .get(page)" when {

      "no data exists for that page" must {

        "return None" in {
          userAnswers.get(TestPage) shouldBe None
        }
      }

      "data exists for that page" must {

        "Some(data)" in {
          val withData = userAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
          withData.get(TestPage) shouldBe Some("foo")
        }
      }
    }

    "calling .remove(page)" must {

      "no data exists for that page" must {

        "return the userAnswers unchanged" in {
          val withData = userAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
          withData.remove(TestPage2) shouldBe withData
        }
      }

      "data exists for that page" must {

        "remove the answer" in {
          val withData = userAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
          withData.remove(TestPage) shouldBe userAnswers
        }
      }

      "data exists for that page and TestPage2 exists" must {

        "remove the answer AND remove TestPage2 as part of the cleanup rules" in {
          val withData = userAnswers.copy(data = Json.obj(
            "TestPage" -> "foo",
            "TestPage2" -> "bar",
          ))
          withData.remove(TestPage) shouldBe userAnswers
        }
      }
    }

    "calling .cleanUp(page)" when {

      "failed to update the UserAnswers" must {

        "throw the exception" in {
          intercept[JsResultException](userAnswers.cleanUp(TestPage2)(JsError("OhNo")))
        }
      }

      "updated UserAnswers successfully" must {

        "return the user answers" in {
          userAnswers.cleanUp(TestPage2)(JsSuccess(userAnswers.data)) shouldBe userAnswers
        }
      }
    }
  }
}