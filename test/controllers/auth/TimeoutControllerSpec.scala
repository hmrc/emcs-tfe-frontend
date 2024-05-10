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

package controllers.auth

import base.SpecBase
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.auth.TimeoutView

class TimeoutControllerSpec extends SpecBase {

  val controller: TimeoutController = app.injector.instanceOf[TimeoutController]

  val view: TimeoutView = app.injector.instanceOf[TimeoutView]

  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  implicit val msgs: Messages = messages(request)

  "Timeout Controller" when {

    "calling .onPageLoad()" should {

      "render the correct view" in {

        val result = controller.onPageLoad()(request.withSession("foo" -> "bar"))

        status(result) mustBe OK
        contentAsString(result) mustBe view().toString()
        session(result).data mustBe empty
      }
    }
  }
}
