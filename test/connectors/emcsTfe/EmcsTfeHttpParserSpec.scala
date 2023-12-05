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

package connectors.emcsTfe

import base.SpecBase
import fixtures.MovementListFixtures
import mocks.connectors.MockHttpClient
import models.response.emcsTfe.GetMovementListResponse
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.Status
import play.api.libs.json.{Json, Reads}
import uk.gov.hmrc.http.{HttpClient, HttpResponse}

class EmcsTfeHttpParserSpec
  extends SpecBase
    with MovementListFixtures
    with MockHttpClient {

  lazy val httpParser = new EmcsTfeHttpParser[GetMovementListResponse] {
    override implicit val reads: Reads[GetMovementListResponse] = GetMovementListResponse.reads
    override def http: HttpClient = mockHttpClient
  }

  "EmcsTfeReads.read(method: String, url: String, response: HttpResponse)" must {

    "return a successful response" when {

      "valid JSON is returned that can be parsed to the model" in {

        val httpResponse = HttpResponse(Status.OK, getMovementListJson, Map())

        httpParser.EmcsTfeReads.read("POST", "/movement/ern/arc", httpResponse) mustBe Right(getMovementListResponse)
      }
    }

    "return UnexpectedDownstreamError" when {

      s"status is not OK (${Status.OK})" in {

        val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, Json.obj(), Map())

        httpParser.EmcsTfeReads.read("POST", "/movement/ern/arc", httpResponse) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }

    "return JsonValidationError" when {

      s"response does not contain Json" in {

        val httpResponse = HttpResponse(Status.OK, "", Map())

        httpParser.EmcsTfeReads.read("POST", "/movement/ern/arc", httpResponse) mustBe Left(JsonValidationError)
      }

      s"response contains JSON but can't be deserialized to model" in {

        val httpResponse = HttpResponse(Status.OK, Json.obj(), Map())

        httpParser.EmcsTfeReads.read("POST", "/movement/ern/arc", httpResponse) mustBe Left(JsonValidationError)
      }
    }
  }
}
