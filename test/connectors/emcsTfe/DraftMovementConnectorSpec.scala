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
import models.response.JsonValidationError
import models.response.emcsTfe.draftMovement.DraftId
import models.response.emcsTfe.messages.submissionFailure.IE704FunctionalError
import play.api.http.{HeaderNames, MimeTypes, Status}
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}

import scala.concurrent.{ExecutionContext, Future}

class DraftMovementConnectorSpec extends SpecBase
  with Status with MimeTypes with HeaderNames with MockHttpClient with GetSubmissionFailureMessageFixtures {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val connector = new DraftMovementConnector(mockHttpClient, appConfig)

  ".markMovementAsDraft" should {

    "return a successful response" when {

      "downstream call is successful" in {

        MockHttpClient
          .putEmpty(url"${appConfig.emcsTfeBaseUrl}/user-answers/create-movement/$testErn/$testDraftId/mark-as-draft")
          .returns(Future.successful(Right(DraftId(testDraftId))))

        connector.markMovementAsDraft(testErn, testDraftId).futureValue mustBe Right(DraftId(testDraftId))
      }
    }

    "return an error response" when {

      "when downstream call fails" in {

        MockHttpClient
          .putEmpty(url"${appConfig.emcsTfeBaseUrl}/user-answers/create-movement/$testErn/$testDraftId/mark-as-draft")
          .returns(Future.successful(Left(JsonValidationError)))

        connector.markMovementAsDraft(testErn, testDraftId).futureValue mustBe Left(JsonValidationError)
      }
    }
  }

  ".putErrorMessagesAndReturnDraftId" should {

    val errorMessages: Seq[IE704FunctionalError] = Seq(IE704FunctionalErrorFixtures.ie704FunctionalErrorModel)

    "return a successful response" when {

      "downstream call is successful" in {

        MockHttpClient
          .put(url"${appConfig.emcsTfeBaseUrl}/user-answers/create-movement/$testErn/$testDraftId/error-messages", errorMessages)
          .returns(Future.successful(Right(DraftId(testDraftId))))

        connector.putErrorMessagesAndReturnDraftId(testErn, testDraftId, errorMessages).futureValue mustBe Right(DraftId(testDraftId))
      }
    }

    "return an error response" when {

      "when downstream call fails" in {

        MockHttpClient
          .put(url"${appConfig.emcsTfeBaseUrl}/user-answers/create-movement/$testErn/$testDraftId/error-messages", errorMessages)
          .returns(Future.successful(Left(JsonValidationError)))

        connector.putErrorMessagesAndReturnDraftId(testErn, testDraftId, errorMessages).futureValue mustBe Left(JsonValidationError)
      }
    }
  }
}
