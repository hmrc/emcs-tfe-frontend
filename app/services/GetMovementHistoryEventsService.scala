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

import connectors.emcsTfe.GetMovementHistoryEventsConnector
import models.response.MovementHistoryEventsException
import models.response.emcsTfe.getMovementHistoryEvents.GetMovementHistoryEventsResponse
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GetMovementHistoryEventsService @Inject()(connector: GetMovementHistoryEventsConnector)
                                               (implicit ec: ExecutionContext) {

  def getMovementHistoryEvents(ern: String, arc: String)(implicit hc: HeaderCarrier): Future[GetMovementHistoryEventsResponse] = {
    connector.getMovementHistoryEvents(ern, arc).map {
      case Left(_) => throw MovementHistoryEventsException("No movement history events response")
      case Right(events) => events
    }
  }
}