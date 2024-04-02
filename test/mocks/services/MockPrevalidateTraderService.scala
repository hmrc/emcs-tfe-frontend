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

package mocks.services

import models.response.emcsTfe.prevalidateTrader.PreValidateTraderApiResponse
import org.scalamock.handlers.CallHandler4
import org.scalamock.scalatest.MockFactory
import services.PrevalidateTraderService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future


trait MockPrevalidateTraderService extends MockFactory {

  lazy val mockPrevalidateTraderService: PrevalidateTraderService = mock[PrevalidateTraderService]

  object MockPrevalidateTraderService {
    def prevalidate(ern: String, ernToCheck: String, productCodesToCheck: Seq[String]): CallHandler4[String, String, Seq[String], HeaderCarrier, Future[PreValidateTraderApiResponse]] =
      (mockPrevalidateTraderService.prevalidate(_: String, _: String, _: Seq[String])(_: HeaderCarrier))
        .expects(ern, ernToCheck, productCodesToCheck, *)
  }
}
