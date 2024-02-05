/*
 * Copyright 2023 HM Revenue & Customs
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

import connectors.emcsTfe.GetMovementHistoryEventsConnector
import models.response.ErrorResponse
import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import org.scalamock.handlers.CallHandler4
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockGetMovementHistoryEventsConnector extends MockFactory {

  lazy val mockGetMovementHistoryEventsConnector: GetMovementHistoryEventsConnector = mock[GetMovementHistoryEventsConnector]

  object MockGetMovementHistoryEventsConnector {
    def getMovementHistoryEvents(ern: String, arc: String): CallHandler4[String, String, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, Seq[MovementHistoryEvent]]]]  =
      (mockGetMovementHistoryEventsConnector.getMovementHistoryEvents(_: String, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, arc, *, *)
  }
}
