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
import fixtures.GetMovementResponseFixtures
import mocks.connectors.MockHttpClient
import models.response.referenceData.WineOperationsResponse
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.client.HttpClientV2

class WineOperationsHttpParserSpec extends SpecBase
  with Status with MimeTypes with HeaderNames with MockHttpClient with GetMovementResponseFixtures {

  lazy val httpParser = new WineOperationsHttpParser {
    override def http: HttpClientV2 = mockHttpClient
  }

  "WineOperationsReads.read(method: String, url: String, response: HttpResponse)" should {

    "return a successful response" when {

      "valid JSON is returned that can be parsed to the model" in {

        val packagingTypes = Map(
          "4" -> "The product has been sweetened",
          "11" -> "The product has been partially dealcoholised",
          "9" -> "The product has been made using oak chips"
        )

        val packagingTypesJson = Json.toJson(packagingTypes)

        val httpResponse = HttpResponse(Status.OK, packagingTypesJson, Map())

        httpParser.WineOperationsReads.read("POST", "/oracle/wine-operations", httpResponse) mustBe Right(
          WineOperationsResponse(packagingTypes)
        )
      }
    }

    "return UnexpectedDownstreamError" when {

      s"status is not OK (${Status.OK})" in {

        val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, Json.obj(), Map())

        httpParser.WineOperationsReads.read("POST", "/oracle/wine-operations", httpResponse) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }

    "return JsonValidationError" when {

      s"response does not contain Json" in {

        val httpResponse = HttpResponse(Status.OK, "", Map())

        httpParser.WineOperationsReads.read("POST", "/oracle/wine-operations", httpResponse) mustBe Left(JsonValidationError)
      }

      s"response contains JSON but can't be deserialized to model" in {

        val httpResponse = HttpResponse(Status.OK, Json.arr(), Map())

        httpParser.WineOperationsReads.read("POST", "/oracle/wine-operations", httpResponse) mustBe Left(JsonValidationError)
      }
    }
  }
}
