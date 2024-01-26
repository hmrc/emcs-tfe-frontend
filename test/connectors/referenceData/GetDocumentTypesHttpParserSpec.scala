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

package connectors.referenceData

import base.SpecBase
import fixtures.messages.DocumentTypeFixtures
import mocks.connectors.MockHttpClient
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HttpClient, HttpResponse}

class GetDocumentTypesHttpParserSpec extends SpecBase with GetDocumentTypesHttpParser with MockHttpClient with DocumentTypeFixtures {

  override def http: HttpClient = mockHttpClient

  "GetDocumentTypesReads" should {

    "return a Seq[DocumentTypes]" when {

      s"an OK (${Status.OK}) response is retrieved" in {

        val expectedResult = Right(Seq(
          documentTypeModel,
          documentTypeOtherModel
        ))

        val jsonResponse = Json.arr(
          documentTypeJson,
          documentTypeOtherJson
        )

        val actualResult = new GetDocumentTypesReads().read("", "", HttpResponse(Status.OK, jsonResponse.toString()))

        actualResult mustBe expectedResult
      }
    }

    "return a JsonValidationError" when {

      s"invalid json response is retrieved" in {

        val expectedResult = Left(JsonValidationError)

        val jsonResponse = Json.arr(
          Json.obj("something" -> "that"),
          Json.obj("is" -> "incorrect")
        )

        val actualResult = new GetDocumentTypesReads().read("", "", HttpResponse(Status.OK, jsonResponse.toString()))

        actualResult mustBe expectedResult
      }
    }

    "return UnexpectedDownstreamError" when {

      s"status is anything else" in {

        val expectedResult = Left(UnexpectedDownstreamResponseError)

        val actualResult = new GetDocumentTypesReads().read("", "", HttpResponse(Status.INTERNAL_SERVER_ERROR, ""))

        actualResult mustBe expectedResult
      }
    }
  }
}
