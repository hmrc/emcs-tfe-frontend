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

package uk.gov.hmrc.emcstfefrontend.mocks.connectors

import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.emcstfefrontend.connectors.referenceData.GetMemberStatesConnector
import uk.gov.hmrc.emcstfefrontend.models.MemberState
import uk.gov.hmrc.emcstfefrontend.models.response.ErrorResponse
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockGetMemberStatesConnector extends MockFactory {

  lazy val mockGetMemberStatesConnector: GetMemberStatesConnector = mock[GetMemberStatesConnector]

  object MockGetMemberStatesConnector {
    def getMemberStates(): CallHandler2[HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, Seq[MemberState]]]]  =
      (mockGetMemberStatesConnector.getMemberStates()(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *)
  }
}
