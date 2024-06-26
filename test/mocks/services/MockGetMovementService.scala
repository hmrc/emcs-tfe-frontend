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

import models.response.emcsTfe.GetMovementResponse
import models.response.emcsTfe.getMovementHistoryEvents.MovementHistoryEvent
import org.scalamock.handlers.{CallHandler3, CallHandler4, CallHandler5}
import org.scalamock.scalatest.MockFactory
import services.GetMovementService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

trait MockGetMovementService extends MockFactory {

  lazy val mockGetMovementService: GetMovementService = mock[GetMovementService]

  object MockGetMovementService {
    def getMovement(ern: String, arc: String, sequenceNumber: Option[Int] = None, historyEvents: Option[Seq[MovementHistoryEvent]] = None): CallHandler5[String, String, Option[Int], Option[Seq[MovementHistoryEvent]], HeaderCarrier, Future[GetMovementResponse]] =
      (mockGetMovementService.getMovement(_: String, _: String, _: Option[Int], _: Option[Seq[MovementHistoryEvent]])(_: HeaderCarrier))
        .expects(ern, arc, sequenceNumber, historyEvents, *)

    def getRawMovement(ern: String, arc: String, sequenceNumber: Option[Int] = None): CallHandler4[String, String, Option[Int], HeaderCarrier, Future[GetMovementResponse]] =
      (mockGetMovementService.getRawMovement(_: String, _: String, _: Option[Int])(_: HeaderCarrier))
        .expects(ern, arc, sequenceNumber, *)

    def getLatestMovementForLoggedInUser(ern: String, arc: String): CallHandler3[String, String, HeaderCarrier, Future[GetMovementResponse]] =
      (mockGetMovementService.getLatestMovementForLoggedInUser(_: String, _: String)(_: HeaderCarrier))
        .expects(ern, arc, *)
  }
}
