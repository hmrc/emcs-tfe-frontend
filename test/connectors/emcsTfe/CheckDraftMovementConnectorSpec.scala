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
import fixtures.GetSubmissionFailureMessageFixtures
import mocks.connectors.MockHttpClient
import models.response.UnexpectedDownstreamResponseError
import models.response.emcsTfe.draftMovement.DraftExists
import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class CheckDraftMovementConnectorSpec extends SpecBase
  with Status with MimeTypes with HeaderNames with MockHttpClient with GetSubmissionFailureMessageFixtures {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val connector = new CheckDraftMovementConnector(mockHttpClient, appConfig)

  ".checkDraftMovementExists" should {

    "return a successful response" when {

      "downstream call is successful" in {

        MockHttpClient
          .get(s"${appConfig.emcsTfeBaseUrl}/user-answers/create-movement/draft/$testErn/$testDraftId")
          .returns(Future.successful(Right(DraftExists(true))))

        connector.checkDraftMovementExists(testErn, testDraftId).futureValue mustBe Right(DraftExists(true))
      }
    }

    "return an error response" when {

      "when downstream call fails" in {

        MockHttpClient
          .get(s"${appConfig.emcsTfeBaseUrl}/user-answers/create-movement/draft/$testErn/$testDraftId")
          .returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        connector.checkDraftMovementExists(testErn, testDraftId).futureValue mustBe Left(UnexpectedDownstreamResponseError)
      }
    }
  }
}
