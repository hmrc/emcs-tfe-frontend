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

package uk.gov.hmrc.emcstfefrontend.connectors.referenceData

import fixtures.ExciseProductCodeFixtures
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.emcstfefrontend.base.SpecBase
import uk.gov.hmrc.emcstfefrontend.mocks.connectors.MockHttpClient
import uk.gov.hmrc.emcstfefrontend.models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.http.{HttpClient, HttpResponse}

class GetExciseProductCodesHttpParserSpec extends SpecBase
  with GetExciseProductCodesHttpParser with MockHttpClient with ExciseProductCodeFixtures {

  override def http: HttpClient = mockHttpClient

  "GetExciseProductCodesReads" - {

    "should return a Seq of Excise Product Codes" - {

      s"when an OK (${Status.OK}) response is retrieved" in {

        val expectedResult = Right(Seq(
          beerExciseProductCode,
          wineExciseProductCode
        ))

        val jsonResponse = Json.arr(
          beerExciseProductCodeJson,
          wineExciseProductCodeJson
        )

        val actualResult = new GetExciseProductCodesReads().read("", "", HttpResponse(Status.OK, jsonResponse.toString()))

        actualResult mustBe expectedResult
      }
    }

    "should return a JsonValidationError" - {

      s"when invalid json response is retrieved" in {

        val expectedResult = Left(JsonValidationError)

        val jsonResponse = Json.arr(
          Json.obj("something" -> "that"),
          Json.obj("is" -> "incorrect")
        )

        val actualResult = new GetExciseProductCodesReads().read("", "", HttpResponse(Status.OK, jsonResponse.toString()))

        actualResult mustBe expectedResult
      }
    }

    "should return UnexpectedDownstreamError" - {

      s"when status is anything else" in {

        val expectedResult = Left(UnexpectedDownstreamResponseError)

        val actualResult = new GetExciseProductCodesReads().read("", "", HttpResponse(Status.INTERNAL_SERVER_ERROR, ""))

        actualResult mustBe expectedResult
      }
    }
  }
}
