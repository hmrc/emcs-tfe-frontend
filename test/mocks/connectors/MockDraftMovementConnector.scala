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

package mocks.connectors

import connectors.emcsTfe.DraftMovementConnector
import models.response.ErrorResponse
import models.response.emcsTfe.draftMovement.DraftId
import models.response.emcsTfe.messages.submissionFailure.IE704FunctionalError
import org.scalamock.handlers.{CallHandler4, CallHandler5}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockDraftMovementConnector extends MockFactory {

  lazy val mockDraftMovementConnector: DraftMovementConnector = mock[DraftMovementConnector]

  object MockDraftMovementConnector {
    def markMovementAsDraft(ern: String,
                            draftId: String): CallHandler4[String, String, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, DraftId]]] =
      (mockDraftMovementConnector.markMovementAsDraft(_: String, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, draftId, *, *)

    def putErrorMessagesAndReturnDraftId(ern: String,
                                         lrn: String,
                                         errorMessages: Seq[IE704FunctionalError]): CallHandler5[String, String, Seq[IE704FunctionalError], HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, DraftId]]] =
      (mockDraftMovementConnector.putErrorMessagesAndReturnDraftId(_: String, _: String, _: Seq[IE704FunctionalError])(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, lrn, errorMessages, *, *)
  }

}
