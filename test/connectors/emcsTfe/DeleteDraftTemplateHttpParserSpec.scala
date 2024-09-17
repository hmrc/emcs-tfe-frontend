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

package connectors.emcsTfe

import base.SpecBase
import mocks.connectors.MockHttpClient
import models.response.UnexpectedDownstreamResponseError
import play.api.http.Status
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.client.HttpClientV2

class DeleteDraftTemplateHttpParserSpec
  extends SpecBase
  with MockHttpClient {

  lazy val httpParser = new DeleteDraftTemplateHttpParser {
    override def http: HttpClientV2 = mockHttpClient
  }

  "DeleteDraftTemplateHttpParser.delete" must {
    "return Right(true) when the server responds with NO_CONTENT" in {
      val httpResponse = HttpResponse(Status.NO_CONTENT, JsObject.empty, Map())
      httpParser.DeleteTemplateReads.read("DELETE", "url", httpResponse) mustBe Right(true)
    }

    "return Left(UnexpectedDownstreamResponseError) when the server responds with any other status" in {
      val httpResponse = HttpResponse(Status.OK, JsObject.empty, Map())
      httpParser.DeleteTemplateReads.read("DELETE", "url", httpResponse) mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
