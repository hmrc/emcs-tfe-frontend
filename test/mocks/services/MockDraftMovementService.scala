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

package mocks.services

import models.response.emcsTfe.messages.submissionFailure.GetSubmissionFailureMessageResponse
import org.scalamock.handlers.CallHandler4
import org.scalamock.scalatest.MockFactory
import services.DraftMovementService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockDraftMovementService extends MockFactory {

  lazy val mockDraftMovementService: DraftMovementService = mock[DraftMovementService]

  object MockDraftMovementService {

    def checkDraftMovementExists(ern: String, getSubmissionFailureMessageResponse: GetSubmissionFailureMessageResponse): CallHandler4[String, GetSubmissionFailureMessageResponse, ExecutionContext, HeaderCarrier, Future[Option[Boolean]]] =
      (mockDraftMovementService.checkDraftMovementExists(_: String, _: GetSubmissionFailureMessageResponse)(_: ExecutionContext, _: HeaderCarrier))
        .expects(ern, getSubmissionFailureMessageResponse, *, *)

    def putErrorMessagesAndMarkMovementAsDraft(ern: String, getSubmissionFailureMessageResponse: GetSubmissionFailureMessageResponse): CallHandler4[String, GetSubmissionFailureMessageResponse, ExecutionContext, HeaderCarrier, Future[Option[String]]] =
      (mockDraftMovementService.putErrorMessagesAndMarkMovementAsDraft(_: String, _: GetSubmissionFailureMessageResponse)(_: ExecutionContext, _: HeaderCarrier))
        .expects(ern, getSubmissionFailureMessageResponse, *, *)

  }

}
