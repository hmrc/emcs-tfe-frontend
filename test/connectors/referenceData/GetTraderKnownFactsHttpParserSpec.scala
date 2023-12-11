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

package connectors.referenceData

import base.SpecBase
import fixtures.BaseFixtures
import mocks.config.MockAppConfig
import mocks.connectors.MockHttpClient
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HttpClient, HttpResponse}

class GetTraderKnownFactsHttpParserSpec extends SpecBase with BaseFixtures with MockAppConfig with GetTraderKnownFactsHttpParser with MockHttpClient {

  override def http: HttpClient = mockHttpClient

  "GetTraderKnownFactsReads.read(method: String, url: String, response: HttpResponse)" must {

    "return 'Some(traderKnownFacts)'" when {
      s"an OK (${Status.OK}) response is retrieved" in {
        GetTraderKnownFactsReads(testErn).read("", "", HttpResponse(Status.OK, Json.obj("traderName" -> "testTraderName").toString())) mustBe Right(Some(testMinTraderKnownFacts))
      }
    }

    "return JsonValidationError" when {
      s"an OK (${Status.OK}) response is retrieved but JSON is invalid" in {
        GetTraderKnownFactsReads(testErn).read("", "", HttpResponse(Status.OK, "bad json")) mustBe Left(JsonValidationError)
      }
    }

    "must return 'None'" when {
      s"when a NOT_FOUND (${Status.NO_CONTENT}) response is retrieved" in {
        GetTraderKnownFactsReads(testErn).read("", "", HttpResponse(Status.NO_CONTENT, "")) mustBe Right(None)
      }
    }

    "must return UnexpectedDownstreamError" when {
      s"when status is anything else" in {
        GetTraderKnownFactsReads(testErn).read("", "", HttpResponse(Status.INTERNAL_SERVER_ERROR, "")) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}
