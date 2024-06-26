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

import connectors.emcsTfe.{GetDraftMovementsConnector, GetMovementConnector, GetMovementListConnector}
import models.MovementListSearchOptions
import models.draftMovements.GetDraftMovementsSearchOptions
import models.response.ErrorResponse
import models.response.emcsTfe.draftMovement.GetDraftMovementsResponse
import models.response.emcsTfe.{GetMovementListResponse, GetMovementResponse}
import org.scalamock.handlers.{CallHandler4, CallHandler5}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockEmcsTfeConnector extends MockFactory {
  lazy val mockGetMovementConnector: GetMovementConnector = mock[GetMovementConnector]
  lazy val mockGetMovementListConnector: GetMovementListConnector = mock[GetMovementListConnector]
  lazy val mockGetDraftMovementsConnector: GetDraftMovementsConnector = mock[GetDraftMovementsConnector]

  object MockEmcsTfeConnector {
    def getMovement(ern: String,
                    arc: String,
                    sequenceNumber: Option[Int] = None
                   ): CallHandler5[String, String, Option[Int], HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, GetMovementResponse]]] = {
      (mockGetMovementConnector.getMovement(_: String, _: String, _: Option[Int])(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, arc, sequenceNumber, *, *)
    }

    def getMovementList(ern: String,
                        search: Option[MovementListSearchOptions] = None
                       ): CallHandler4[String, Option[MovementListSearchOptions], HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, GetMovementListResponse]]] =
      (mockGetMovementListConnector.getMovementList(_: String, _: Option[MovementListSearchOptions])(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, search, *, *)

    def getDraftMovements(ern: String,
                          search: Option[GetDraftMovementsSearchOptions] = None
                         ): CallHandler4[String, Option[GetDraftMovementsSearchOptions], HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, GetDraftMovementsResponse]]] =
      (mockGetDraftMovementsConnector.getDraftMovements(_: String, _: Option[GetDraftMovementsSearchOptions])(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, search, *, *)
  }
}
