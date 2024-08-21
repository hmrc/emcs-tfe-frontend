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

package controllers.auth

import base.SpecBase
import config.AppConfig
import play.api.test.FakeRequest
import play.api.test.Helpers._

import java.net.URLEncoder

class AuthControllerSpec extends SpecBase {

  "signOut" must {

    "redirect to sign out, specifying the exit survey as the continue URL" in {

      val appConfig = app.injector.instanceOf[AppConfig]
      val request   = FakeRequest(GET, routes.AuthController.signOut().url)

      val result = route(app, request).value

      val encodedContinueUrl  = URLEncoder.encode(appConfig.feedbackFrontendSurveyUrl, "UTF-8")
      val expectedRedirectUrl = s"${appConfig.signOutUrl}?continue=$encodedContinueUrl"

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual expectedRedirectUrl
    }
  }
}
