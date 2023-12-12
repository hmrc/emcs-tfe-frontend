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

import connectors.emcsTfe.{GetMovementConnector, GetMovementListConnector}
import models.response.ErrorResponse
import models.response.emcsTfe.{GetMovementListResponse, GetMovementResponse}
import org.scalamock.handlers.{CallHandler3, CallHandler4}
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockEmcsTfeConnector extends MockFactory {
  lazy val mockGetMovementConnector: GetMovementConnector = mock[GetMovementConnector]
  lazy val mockGetMovementListConnector: GetMovementListConnector = mock[GetMovementListConnector]

  object MockEmcsTfeConnector {
    def getMovement(ern: String, arc: String): CallHandler4[String, String, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, GetMovementResponse]]] = {
      (mockGetMovementConnector.getMovement(_: String, _: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, arc, *, *)
    }

    def getMovementList(ern: String): CallHandler3[String, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, GetMovementListResponse]]] =
      (mockGetMovementListConnector.getMovementList(_: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, *, *)
  }
}
