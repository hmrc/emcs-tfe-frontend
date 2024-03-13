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

import connectors.emcsTfe.CheckDraftMovementConnector
import models.response.ErrorResponse
import models.response.emcsTfe.draftMovement.DraftExists
import org.scalamock.handlers.CallHandler4
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockCheckDraftMovementConnector extends MockFactory {

  lazy val mockCheckDraftMovementConnector: CheckDraftMovementConnector = mock[CheckDraftMovementConnector]

  object MockCheckDraftMovementConnector {
    def checkDraftMovementExists(ern: String,
                                 draftId: String): CallHandler4[String, String, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, DraftExists]]] =
      (mockCheckDraftMovementConnector.checkDraftMovementExists(_: String, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, draftId, *, *)
  }

}
