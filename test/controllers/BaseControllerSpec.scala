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
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import pages.QuestionPage
import play.api.data.Form
import play.api.data.Forms.nonEmptyText
import play.api.libs.json.{JsPath, __}
import play.api.mvc.MessagesControllerComponents
import play.api.test.FakeRequest

class BaseControllerSpec extends SpecBase with GuiceOneAppPerSuite {

  val page = new QuestionPage[String] {
    override val path: JsPath = __ \ "page1"
  }

  val form = Form("value" -> nonEmptyText)

  lazy val testController = new BaseController {
    override protected def controllerComponents: MessagesControllerComponents = messagesControllerComponents
  }

  "fillForm" when {
    "when no answer exists for the page" must {
      "return the form unfilled" in {

        implicit val request = userAnswersRequest(FakeRequest())
        testController.fillForm(page, form).value mustBe None
      }
    }

    "when an answer DOES exist for the page" must {
      "return the form with answer filled in" in {

        implicit val request = userAnswersRequest(FakeRequest(), emptyUserAnswers.set(page, "foo"))
        testController.fillForm(page, form).value mustBe Some("foo")
      }
    }
  }
}
