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
import fixtures.MemberStatesFixtures
import mocks.connectors.MockHttpClient
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HttpClient, HttpResponse}

class GetMemberStatesHttpParserSpec extends SpecBase with GetMemberStatesHttpParser with MockHttpClient with MemberStatesFixtures {

  override def http: HttpClient = mockHttpClient

  "GetMemberStatesReads" must {

    "must return a Seq[MemberState]" when {

      s"when an OK (${Status.OK}) response is retrieved" in {

        val expectedResult = Right(Seq(
          memberStateAT,
          memberStateBE
        ))

        val jsonResponse = Json.arr(
          memberStateJsonBT,
          memberStateJsonBE
        )

        val actualResult = new GetMemberStatesReads().read("", "", HttpResponse(Status.OK, jsonResponse.toString()))

        actualResult mustBe expectedResult
      }
    }

    "must return a JsonValidationError" when {

      s"when invalid json response is retrieved" in {

        val expectedResult = Left(JsonValidationError)

        val jsonResponse = Json.arr(
          Json.obj("something" -> "that"),
          Json.obj("is" -> "incorrect")
        )

        val actualResult = new GetMemberStatesReads().read("", "", HttpResponse(Status.OK, jsonResponse.toString()))

        actualResult mustBe expectedResult
      }
    }

    "must return UnexpectedDownstreamError" when {

      s"when status is anything else" in {

        val expectedResult = Left(UnexpectedDownstreamResponseError)

        val actualResult = new GetMemberStatesReads().read("", "", HttpResponse(Status.INTERNAL_SERVER_ERROR, ""))

        actualResult mustBe expectedResult
      }
    }
  }
}
