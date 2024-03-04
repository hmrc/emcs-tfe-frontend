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

import connectors.emcsTfe.DraftMovementConnector
import models.response.emcsTfe.messages.submissionFailure.GetSubmissionFailureMessageResponse
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DraftMovementService @Inject()(draftMovementConnector: DraftMovementConnector) extends Logging {

  def putErrorMessagesAndMarkMovementAsDraft(ern: String, getSubmissionFailureMessageResponse: GetSubmissionFailureMessageResponse)
                                            (implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Option[String]] = {

    getSubmissionFailureMessageResponse.ie704.header.correlationIdentifier.map { correlationId =>
      val errorMessages = getSubmissionFailureMessageResponse.ie704.body.functionalError
      //TODO: delete message (ETFE-2855)
      draftMovementConnector.putErrorMessagesAndReturnDraftId(ern, correlationId, errorMessages).flatMap {
        case Right(draftId) =>
          draftMovementConnector.markMovementAsDraft(ern, draftId.value).map {
            case Right(_) => Some(draftId.value)
            case Left(error) =>
              logger.warn(s"[putErrorMessagesAndMarkMovementAsDraft] - Failed to mark movement as draft for ERN: $ern and draft ID: $draftId with error: ${error.message}")
              None
          }
        case Left(error) =>
          logger.warn(s"[putErrorMessagesAndMarkMovementAsDraft] - Failed to insert error messages for for ERN: $ern and correlation ID: $correlationId with error: ${error.message}")
          Future(None)
      }
    }
  }.getOrElse(Future(None))

}
