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
import mocks.connectors.MockHttpClient
import models.common.UnitOfMeasure.Kilograms
import models.response.referenceData.{CnCodeInformation, CnCodeInformationResponse}
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import play.api.http.{HeaderNames, MimeTypes, Status}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HttpClient, HttpResponse}

class CnCodeInformationHttpParserSpec extends SpecBase
  with Status with MimeTypes with HeaderNames with MockHttpClient {

  lazy val httpParser = new CnCodeInformationHttpParser {
    override def http: HttpClient = mockHttpClient
  }

  "CnCodeInformationReads.read(method: String, url: String, response: HttpResponse)" should {

    "return a successful response" when {

      "valid JSON is returned that can be parsed to the model" in {

        val cnCodeInformation = Map("24029000" -> CnCodeInformation(
          cnCode = "T200",
          cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
          exciseProductCode = "24029000",
          exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
          unitOfMeasure = Kilograms
        ))

        val cnCodeInformationJson = Json.obj("24029000" -> Json.obj(
          "cnCode" -> "T200",
          "cnCodeDescription" -> "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
          "exciseProductCode" -> "24029000",
          "exciseProductCodeDescription" -> "Fine-cut tobacco for the rolling of cigarettes",
          "unitOfMeasureCode" -> 1
        ))

        val httpResponse = HttpResponse(Status.OK, cnCodeInformationJson, Map())

        httpParser.CnCodeInformationReads.read("POST", "/oracle/cn-code-information", httpResponse) mustBe Right(
          CnCodeInformationResponse(cnCodeInformation)
        )
      }
    }

    "return UnexpectedDownstreamError" when {

      s"status is not OK (${Status.OK})" in {

        val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, Json.obj(), Map())

        httpParser.CnCodeInformationReads.read("POST", "/oracle/cn-code-information", httpResponse) mustBe Left(UnexpectedDownstreamResponseError)
      }
    }

    "return JsonValidationError" when {

      s"response does not contain Json" in {

        val httpResponse = HttpResponse(Status.OK, "", Map())

        httpParser.CnCodeInformationReads.read("POST", "/oracle/cn-code-information", httpResponse) mustBe Left(JsonValidationError)
      }

      s"response contains JSON but can't be deserialized to model" in {

        val httpResponse = HttpResponse(Status.OK, Json.arr(), Map())

        httpParser.CnCodeInformationReads.read("POST", "/oracle/cn-code-information", httpResponse) mustBe Left(JsonValidationError)
      }
    }
  }
}
