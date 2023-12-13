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

package services

import base.SpecBase
import fixtures.GetMovementHistoryEventsResponseFixtures
import mocks.connectors.MockGetMovementHistoryEventsConnector
import models.response.{MovementHistoryEventsException, UnexpectedDownstreamResponseError}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class GetMovementHistoryEventsServiceSpec extends SpecBase with MockGetMovementHistoryEventsConnector with GetMovementHistoryEventsResponseFixtures {

  implicit val hc = HeaderCarrier()
  implicit val ec = ExecutionContext.global

  lazy val testService = new GetMovementHistoryEventsService(mockGetMovementHistoryEventsConnector)

  ".getMovementHistoryEvents" must {

    "must return GetMovementHistoryEventsResponse" when {

      "when Connector returns success from downstream" in {

        MockGetMovementHistoryEventsConnector.getMovementHistoryEvents(testErn, testArc).returns(Future(Right(getMovementHistoryEventsResponseModel)))

        val actualResults = testService.getMovementHistoryEvents(testErn, testArc).futureValue

        actualResults mustBe getMovementHistoryEventsResponseModel
      }
    }

    "must throw MovementHistoryEventsException" when {

      "when Connector returns failure from downstream" in {

        val expectedResult = "Failed to retrieve movement history events from emcs-tfe: UnexpectedDownstreamResponseError"

        MockGetMovementHistoryEventsConnector.getMovementHistoryEvents(testErn, testArc).returns(Future(Left(UnexpectedDownstreamResponseError)))

        val actualResult = intercept[MovementHistoryEventsException](await(testService.getMovementHistoryEvents(testErn, testArc))).getMessage

        actualResult mustBe expectedResult
      }
    }
  }
}
