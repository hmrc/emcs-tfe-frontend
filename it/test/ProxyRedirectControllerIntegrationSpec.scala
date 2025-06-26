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

  "Calling /emcs/trader/GBWK123456789" should {

    "redirect to /emcs/account/GBWK123456789/account" in {

      val response: WSResponse = await(client.url(s"http://localhost:$port/emcs/trader/GBWK123456789").withFollowRedirects(false).get())

      response.status mustBe Status.SEE_OTHER
      response.header(HeaderNames.LOCATION) mustBe Some(controllers.routes.IndexController.exciseNumber().url)
    }
  }

  "Calling /emcs/trader/GBWK123456789/message/415797604?messagetype=IE801&date=23062023&movement=11GB00111000074521730&localReferenceNumber=8100001617&readStatus=false&version=1&tradersubmission=true" should {

    "redirect to /emcs/account/GBWK123456789/account" in {

      val response: WSResponse = await(client.url(s"http://localhost:$port/emcs/trader/GBWK123456789/message/415797604?messagetype=IE801&date=23062023&movement=11GB00111000074521730&localReferenceNumber=8100001617&readStatus=false&version=1&tradersubmission=true").withFollowRedirects(false).get())

      response.status mustBe Status.SEE_OTHER
      response.header(HeaderNames.LOCATION) mustBe Some(controllers.routes.IndexController.exciseNumber().url)
    }
  }

  "Calling /emcs/trader/GBWK123456789/movements" should {

    "redirect to /emcs/account/GBWK123456789/account" in {

      val response: WSResponse = await(client.url(s"http://localhost:$port/emcs/trader/GBWK123456789/movments").withFollowRedirects(false).get())

      response.status mustBe Status.SEE_OTHER
      response.header(HeaderNames.LOCATION) mustBe Some(controllers.routes.IndexController.exciseNumber().url)
    }
  }
}
