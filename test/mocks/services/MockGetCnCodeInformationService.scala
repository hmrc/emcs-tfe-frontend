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

import models.response.emcsTfe.MovementItem
import models.response.referenceData.CnCodeInformation
import org.scalamock.handlers.CallHandler2
import org.scalamock.scalatest.MockFactory
import services.GetCnCodeInformationService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

trait MockGetCnCodeInformationService extends MockFactory {

  lazy val mockGetCnCodeInformationService: GetCnCodeInformationService = mock[GetCnCodeInformationService]

  object MockGetCnCodeInformationService {
    def getCnCodeInformation(items: Seq[MovementItem]): CallHandler2[Seq[MovementItem], HeaderCarrier, Future[Seq[(MovementItem, CnCodeInformation)]]] =
      (mockGetCnCodeInformationService.getCnCodeInformation(_: Seq[MovementItem])(_: HeaderCarrier))
        .expects(items, *)
  }
}
