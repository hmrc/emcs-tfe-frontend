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

import models.auth.UserRequest
import models.response.emcsTfe.GetMessageStatisticsResponse
import org.scalamock.handlers.CallHandler3
import org.scalamock.scalatest.MockFactory
import services.GetMessageStatisticsService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

trait MockGetMessageStatisticsService extends MockFactory {

  lazy val mockGetMessageStatisticsService: GetMessageStatisticsService = mock[GetMessageStatisticsService]

  object MockGetMessageStatisticsService {
    def getMessageStatistics(ern: String): CallHandler3[String, HeaderCarrier, UserRequest[_], Future[Option[GetMessageStatisticsResponse]]] =
      (mockGetMessageStatisticsService.getMessageStatistics(_: String)(_: HeaderCarrier, _: UserRequest[_])).expects(ern, *, *)
  }
}
