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

import connectors.referenceData.GetCnCodeInformationConnector
import models.requests.CnCodeInformationRequest
import models.response.ErrorResponse
import models.response.referenceData.CnCodeInformationResponse
import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockGetCnCodeInformationConnector extends MockFactory {

  lazy val mockGetCnCodeInformationConnector: GetCnCodeInformationConnector = mock[GetCnCodeInformationConnector]

  object MockGetCnCodeInformationConnector {
    def getCnCodeInformation(request: CnCodeInformationRequest): CallHandler3[CnCodeInformationRequest, HeaderCarrier, ExecutionContext, Future[Either[ErrorResponse, CnCodeInformationResponse]]] =
      (mockGetCnCodeInformationConnector.getCnCodeInformation(_: CnCodeInformationRequest)(_: HeaderCarrier, _: ExecutionContext))
        .expects(request, *, *)
  }
}
