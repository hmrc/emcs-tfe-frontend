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

package mocks.services

import models.response.emcsTfe.getMovementHistoryEvents.GetMovementHistoryEventsResponse
import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import services.GetMovementHistoryEventsService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

trait MockGetMovementHistoryEventsService extends MockFactory {

  lazy val mockGetMovementHistoryEventsService: GetMovementHistoryEventsService = mock[GetMovementHistoryEventsService]

  object MockGetMovementHistoryEventsService {
    def getMovementHistoryEvents(ern: String, arc: String): CallHandler3[String, String, HeaderCarrier, Future[GetMovementHistoryEventsResponse]] =
      (mockGetMovementHistoryEventsService.getMovementHistoryEvents(_: String, _: String)(_: HeaderCarrier))
        .expects(ern, arc, *)
  }
}
