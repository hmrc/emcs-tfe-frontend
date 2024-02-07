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

import connectors.betaAllowList.BetaAllowListConnector
import models.response.ErrorResponse
import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockBetaAllowListConnector extends MockFactory {

  lazy val mockBetaAllowListConnector: BetaAllowListConnector = mock[BetaAllowListConnector]

  object MockBetaAllowListConnector {

    def check(ern: String): CallHandler3[String, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, Boolean]]] =
      (mockBetaAllowListConnector.check(_: String)(_: HeaderCarrier, _: ExecutionContext))
        .expects(ern, *, *)
  }
}