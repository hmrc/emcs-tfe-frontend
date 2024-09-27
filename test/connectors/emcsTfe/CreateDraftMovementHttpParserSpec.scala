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
import models.response.CreateDraftMovementException
import models.response.emcsTfe.draftTemplate.DraftMovementCreatedResponse
import play.api.http.Status.{CREATED, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.{JsObject, Json, Reads}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.http.client.HttpClientV2

class CreateDraftMovementHttpParserSpec
  extends SpecBase
  with MockHttpClient {

  lazy val httpParser = new CreateDraftMovementHttpParser[DraftMovementCreatedResponse] {
    override implicit val reads: Reads[DraftMovementCreatedResponse] = DraftMovementCreatedResponse.fmt
    override def http: HttpClientV2 = mockHttpClient
  }

  "CreateDraftMovementHttpParser.createDraftMovement" must {
    "return Right(DraftMovementCreatedResponse) when the server responds with CREATED" in {
      val httpResponse = HttpResponse(CREATED, Json.obj("createdDraftId" -> testDraftId), Map())
      httpParser.CreateDraftMovementReads.read("GET", "url", httpResponse) mustBe Right(DraftMovementCreatedResponse(createdDraftId = testDraftId))
    }

    "return Left(CreateDraftMovementException) when the server responds with any other status" in {
      val httpResponse = HttpResponse(OK, JsObject.empty, Map())
      httpParser.CreateDraftMovementReads.read("GET", "url", httpResponse) mustBe Left(CreateDraftMovementException(s"Unexpected status from emcs-tfe: $OK"))
    }

    "return Left(UnexpectedDownstreamResponseError) when the connection fails" in {
      val httpResponse = HttpResponse(INTERNAL_SERVER_ERROR, JsObject.empty, Map())
      httpParser.CreateDraftMovementReads.read("GET", "url", httpResponse) mustBe Left(CreateDraftMovementException(s"Unexpected status from emcs-tfe: $INTERNAL_SERVER_ERROR"))
    }
  }

}
