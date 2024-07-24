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

import play.api.http.{HeaderNames, Status}
import play.api.libs.ws.WSResponse
import support.IntegrationBaseSpec

class ProxyRedirectControllerIntegrationSpec extends IntegrationBaseSpec {

  "Calling /emcs" should {

    "redirect to /emcs/account" in {

      val response: WSResponse = await(client.url(s"http://localhost:$port/emcs").withFollowRedirects(false).get())

      response.status mustBe Status.SEE_OTHER
      response.header(HeaderNames.LOCATION) mustBe Some(controllers.routes.IndexController.exciseNumber().url)
    }
  }
}
