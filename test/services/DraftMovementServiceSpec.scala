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
import mocks.connectors.MockDraftMovementConnector
import models.response.JsonValidationError
import models.response.emcsTfe.draftMovement.DraftId
import models.response.emcsTfe.messages.submissionFailure.IE704FunctionalError
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class DraftMovementServiceSpec extends SpecBase with MockDraftMovementConnector with GetSubmissionFailureMessageFixtures {

  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val ec: ExecutionContext = ExecutionContext.global

  lazy val testService = new DraftMovementService(mockDraftMovementConnector)

  ".putErrorMessagesAndMarkMovementAsDraft" should {

    val errors: Seq[IE704FunctionalError] = GetSubmissionFailureMessageResponseFixtures.getSubmissionFailureMessageResponseModel.ie704.body.functionalError

    "return Some(_)" when {

      "the error messages are inserted and the movement is marked as a draft successfully" in {

        MockDraftMovementConnector.markMovementAsDraft(testErn, testDraftId).returns(Future.successful(Right(DraftId(testDraftId))))

        MockDraftMovementConnector
          .putErrorMessagesAndReturnDraftId(testErn, testLrn, errors)
          .returns(Future.successful(Right(DraftId(testDraftId))))

        val result = testService.putErrorMessagesAndMarkMovementAsDraft(testErn, GetSubmissionFailureMessageResponseFixtures.getSubmissionFailureMessageResponseModel).futureValue

        result mustBe Some(testDraftId)
      }
    }

    "return None" when {

      "inserting the error messages fail" in {

        MockDraftMovementConnector
          .putErrorMessagesAndReturnDraftId(testErn, testLrn, errors)
          .returns(Future.successful(Left(JsonValidationError)))

        val result = testService.putErrorMessagesAndMarkMovementAsDraft(testErn, GetSubmissionFailureMessageResponseFixtures.getSubmissionFailureMessageResponseModel).futureValue

        result mustBe None
      }

      "inserting the error messages fail is successful but marking the movement as a draft fails" in {

        MockDraftMovementConnector
          .putErrorMessagesAndReturnDraftId(testErn, testLrn, errors)
          .returns(Future.successful(Right(DraftId(testDraftId))))

        MockDraftMovementConnector.markMovementAsDraft(testErn, testDraftId).returns(Future.successful(Left(JsonValidationError)))

        val result = testService.putErrorMessagesAndMarkMovementAsDraft(testErn, GetSubmissionFailureMessageResponseFixtures.getSubmissionFailureMessageResponseModel).futureValue

        result mustBe None
      }
    }
  }
}
