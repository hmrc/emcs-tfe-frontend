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
import models.response.emcsTfe.draftTemplate.DraftMovementCreatedResponse
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}

class CreateDraftMovementConnectorSpec extends SpecBase
  with Status
  with MimeTypes
  with HeaderNames
  with MockHttpClient {

  trait Test {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

    val connector = new CreateDraftMovementConnector(mockHttpClient, appConfig)
  }

  "createDraftMovement" must {

      "return Right(DraftMovementCreatedResponse) when the response status is CREATED (201)" in new Test {

        MockHttpClient
          .get(url"${appConfig.emcsTfeBaseUrl}/template/$testErn/$testTemplateId/create-draft-from-template")
          .returns(Future.successful(Right(DraftMovementCreatedResponse(testDraftId))))

        val result = await(connector.createDraftMovement(testErn, testTemplateId))

        result shouldBe Right(DraftMovementCreatedResponse(testDraftId))

      }

      "return Left(UnexpectedDownstreamResponseError) when the response status is not CREATED (201)" in new Test {

        MockHttpClient
          .get(url"${appConfig.emcsTfeBaseUrl}/template/$testErn/$testTemplateId/create-draft-from-template")
          .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = await(connector.createDraftMovement(testErn, testTemplateId))

        result shouldBe Left(UnexpectedDownstreamResponseError)
      }
  }



}
