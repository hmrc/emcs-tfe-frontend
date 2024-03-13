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

package services

import base.SpecBase
import fixtures.GetSubmissionFailureMessageFixtures
import mocks.connectors.{MockCheckDraftMovementConnector, MockDraftMovementConnector}
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import models.response.emcsTfe.draftMovement.{DraftExists, DraftId}
import models.response.emcsTfe.messages.submissionFailure.{GetSubmissionFailureMessageResponse, IE704FunctionalError}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class DraftMovementServiceSpec extends SpecBase
  with MockDraftMovementConnector
  with MockCheckDraftMovementConnector
  with GetSubmissionFailureMessageFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new DraftMovementService(mockDraftMovementConnector, mockCheckDraftMovementConnector)

  ".checkDraftMovementExists" should {

    val errorResponse: GetSubmissionFailureMessageResponse =
      GetSubmissionFailureMessageResponseFixtures.getSubmissionFailureMessageResponseModel.copy(ie704 = IE704ModelFixtures.ie704ModelModel.copy(header = IE704HeaderFixtures.ie704HeaderModel.copy(correlationIdentifier = Some(testDraftId))))

    val errorResponseNoCorrelation: GetSubmissionFailureMessageResponse =
      GetSubmissionFailureMessageResponseFixtures.getSubmissionFailureMessageResponseModel.copy(ie704 = IE704ModelFixtures.ie704ModelModel.copy(header = IE704HeaderFixtures.ie704HeaderModel.copy(correlationIdentifier = None)))

    "return Some(true)" when {

      "the connector returns that a draft exists" in {

        MockCheckDraftMovementConnector.checkDraftMovementExists(testErn, testDraftId).returns(Future.successful(Right(DraftExists(true))))

        val result = testService.checkDraftMovementExists(testErn, errorResponse).futureValue
        result mustBe Some(true)
      }
    }

    "return Some(false)" when {

      "the connector returns that a draft DOES NOT exist" in {

        MockCheckDraftMovementConnector.checkDraftMovementExists(testErn, testDraftId).returns(Future.successful(Right(DraftExists(false))))

        val result = testService.checkDraftMovementExists(testErn, errorResponse).futureValue
        result mustBe Some(false)
      }
    }

    "return None" when {

      "retrieving the draft fails unexpectedly" in {

        MockCheckDraftMovementConnector.checkDraftMovementExists(testErn, testDraftId).returns(Future.successful(Left(UnexpectedDownstreamResponseError)))

        val result = testService.checkDraftMovementExists(testErn, errorResponse).futureValue
        result mustBe None
      }

      "no correlation ID exists in the 704 message" in {

        val result = testService.checkDraftMovementExists(testErn, errorResponseNoCorrelation).futureValue
        result mustBe None
      }
    }
  }

  ".putErrorMessagesAndMarkMovementAsDraft" should {

    val errorResponse: GetSubmissionFailureMessageResponse = GetSubmissionFailureMessageResponseFixtures.getSubmissionFailureMessageResponseModel.copy(ie704 = IE704ModelFixtures.ie704ModelModel.copy(header = IE704HeaderFixtures.ie704HeaderModel.copy(correlationIdentifier = Some(testDraftId))))

    val errors: Seq[IE704FunctionalError] = errorResponse.ie704.body.functionalError

    "return Some(_)" when {

      "the error messages are inserted and the movement is marked as a draft successfully" in {

        MockDraftMovementConnector.markMovementAsDraft(testErn, testDraftId).returns(Future.successful(Right(DraftId(testDraftId))))

        MockDraftMovementConnector
          .putErrorMessagesAndReturnDraftId(testErn, testDraftId, errors)
          .returns(Future.successful(Right(DraftId(testDraftId))))

        val result = testService.putErrorMessagesAndMarkMovementAsDraft(testErn, errorResponse).futureValue

        result mustBe Some(testDraftId)
      }
    }

    "return None" when {

      "inserting the error messages fail" in {

        MockDraftMovementConnector
          .putErrorMessagesAndReturnDraftId(testErn, testDraftId, errors)
          .returns(Future.successful(Left(JsonValidationError)))

        val result = testService.putErrorMessagesAndMarkMovementAsDraft(testErn, errorResponse).futureValue

        result mustBe None
      }

      "inserting the error messages fail is successful but marking the movement as a draft fails" in {

        MockDraftMovementConnector
          .putErrorMessagesAndReturnDraftId(testErn, testDraftId, errors)
          .returns(Future.successful(Right(DraftId(testDraftId))))

        MockDraftMovementConnector.markMovementAsDraft(testErn, testDraftId).returns(Future.successful(Left(JsonValidationError)))

        val result = testService.putErrorMessagesAndMarkMovementAsDraft(testErn, errorResponse).futureValue

        result mustBe None
      }
    }
  }
}
